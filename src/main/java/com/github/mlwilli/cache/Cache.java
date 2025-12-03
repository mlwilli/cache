package com.github.mlwilli.cache;

import java.util.Optional;

/**
 * Generic cache interface.
 */
public interface Cache<K, V> extends AutoCloseable {

    /**
     * Retrieves a value, if present and not expired.
     */
    Optional<V> get(K key);

    /**
     * Retrieves a value, loading it through the configured CacheLoader if necessary.
     *
     * @throws IllegalStateException if no loader is configured and value is missing
     */
    V getOrLoad(K key);

    /**
     * Inserts or updates a value in the cache.
     */
    void put(K key, V value);

    /**
     * Removes a single entry.
     */
    void invalidate(K key);

    /**
     * Clears all entries.
     */
    void invalidateAll();

    /**
     * Returns a snapshot of current stats.
     */
    CacheStats stats();

    @Override
    default void close() {
        // nothing to close in this implementation, but kept for future extensibility
    }
}
