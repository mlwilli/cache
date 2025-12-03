package com.github.mlwilli.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory cache with TTL and LRU eviction.
 */
public final class InMemoryCache<K, V> implements Cache<K, V> {

    private final CacheConfig<K, V> config;
    private final CacheStats stats = new CacheStats();
    private final ConcurrentHashMap<K, ValueHolder<V>> store = new ConcurrentHashMap<>();

    // LRU access-order tracking
    private final LinkedHashMap<K, Boolean> accessOrder;
    private final Object lruLock = new Object();

    public InMemoryCache(CacheConfig<K, V> config) {
        this.config = Objects.requireNonNull(config, "config");
        this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public Optional<V> get(K key) {
        Objects.requireNonNull(key, "key");
        ValueHolder<V> holder = store.get(key);
        if (holder == null) {
            stats.recordMiss();
            return Optional.empty();
        }

        if (isExpired(holder)) {
            // remove expired
            invalidate(key);
            stats.recordMiss();
            return Optional.empty();
        }

        stats.recordHit();
        touchLru(key);
        return Optional.of(holder.value());
    }

    @Override
    public V getOrLoad(K key) {
        Optional<V> existing = get(key);
        if (existing.isPresent()) {
            return existing.get();
        }

        CacheLoader<K, V> loader = config.loader();
        if (loader == null) {
            throw new IllegalStateException("No CacheLoader configured and value not present for key: " + key);
        }

        try {
            V loaded = loader.load(key);
            stats.recordLoadSuccess();
            put(key, loaded);
            return loaded;
        } catch (Exception e) {
            stats.recordLoadFailure();
            throw new RuntimeException("Failed to load value for key: " + key, e);
        }
    }

    @Override
    public void put(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        Instant now = now();

        ValueHolder<V> previous = store.put(key, new ValueHolder<>(value, now));
        touchLru(key);

        if (previous == null) {
            enforceMaxSize();
        }
    }

    @Override
    public void invalidate(K key) {
        Objects.requireNonNull(key, "key");
        store.remove(key);
        synchronized (lruLock) {
            accessOrder.remove(key);
        }
    }

    @Override
    public void invalidateAll() {
        store.clear();
        synchronized (lruLock) {
            accessOrder.clear();
        }
    }

    @Override
    public CacheStats stats() {
        return stats;
    }

    private boolean isExpired(ValueHolder<V> holder) {
        Duration ttl = config.timeToLive();
        if (ttl == null) {
            return false;
        }
        Instant expiry = holder.createdAt().plus(ttl);
        return now().isAfter(expiry);
    }

    private Instant now() {
        Clock clock = config.clock();
        return clock.instant();
    }

    private void touchLru(K key) {
        if (config.evictionPolicy() != EvictionPolicy.LRU) {
            return;
        }
        synchronized (lruLock) {
            accessOrder.put(key, Boolean.TRUE);
        }
    }

    private void enforceMaxSize() {
        if (config.evictionPolicy() != EvictionPolicy.LRU) {
            return;
        }

        int maxSize = config.maxSize();
        synchronized (lruLock) {
            while (accessOrder.size() > maxSize) {
                Map.Entry<K, Boolean> eldest = accessOrder.entrySet().iterator().next();
                K eldestKey = eldest.getKey();
                accessOrder.remove(eldestKey);
                if (store.remove(eldestKey) != null) {
                    stats.recordEviction();
                }
            }
        }
    }
}
