package com.github.mlwilli.cache;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;

/**
 * Configuration for the in-memory cache.
 */
public final class CacheConfig<K, V> {

    private final int maxSize;
    private final Duration timeToLive;
    private final EvictionPolicy evictionPolicy;
    private final CacheLoader<K, V> loader;
    private final Clock clock;

    private CacheConfig(Builder<K, V> builder) {
        this.maxSize = builder.maxSize;
        this.timeToLive = builder.timeToLive;
        this.evictionPolicy = builder.evictionPolicy;
        this.loader = builder.loader;
        this.clock = builder.clock != null ? builder.clock : Clock.systemUTC();

        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be > 0");
        }
        if (timeToLive != null && (timeToLive.isNegative() || timeToLive.isZero())) {
            throw new IllegalArgumentException("timeToLive must be positive if specified");
        }
        if (evictionPolicy == null) {
            throw new IllegalArgumentException("evictionPolicy must not be null");
        }
    }

    public int maxSize() {
        return maxSize;
    }

    public Duration timeToLive() {
        return timeToLive;
    }

    public EvictionPolicy evictionPolicy() {
        return evictionPolicy;
    }

    public CacheLoader<K, V> loader() {
        return loader;
    }

    public Clock clock() {
        return clock;
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static final class Builder<K, V> {
        private int maxSize = 1000;
        private Duration timeToLive;
        private EvictionPolicy evictionPolicy = EvictionPolicy.LRU;
        private CacheLoader<K, V> loader;
        private Clock clock;

        private Builder() {
        }

        public Builder<K, V> maxSize(int maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public Builder<K, V> timeToLive(Duration ttl) {
            this.timeToLive = ttl;
            return this;
        }

        public Builder<K, V> evictionPolicy(EvictionPolicy policy) {
            this.evictionPolicy = Objects.requireNonNull(policy, "policy");
            return this;
        }

        public Builder<K, V> loader(CacheLoader<K, V> loader) {
            this.loader = loader;
            return this;
        }

        public Builder<K, V> clock(Clock clock) {
            this.clock = Objects.requireNonNull(clock, "clock");
            return this;
        }

        public CacheConfig<K, V> build() {
            return new CacheConfig<>(this);
        }
    }

    @Override
    public String toString() {
        return "CacheConfig{" +
                "maxSize=" + maxSize +
                ", timeToLive=" + timeToLive +
                ", evictionPolicy=" + evictionPolicy +
                ", loader=" + (loader != null ? "present" : "absent") +
                ", clock=" + clock +
                '}';
    }
}
