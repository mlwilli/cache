package com.github.mlwilli.cache;

import java.time.Instant;
import java.util.Objects;

/**
 * Internal wrapper for cached values.
 */
final class ValueHolder<V> {

    private final V value;
    private final Instant createdAt;

    ValueHolder(V value, Instant createdAt) {
        this.value = Objects.requireNonNull(value, "value");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    V value() {
        return value;
    }

    Instant createdAt() {
        return createdAt;
    }
}
