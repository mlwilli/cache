package com.github.mlwilli.cache;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Mutable stats, exposed via snapshot getters.
 */
public final class CacheStats {

    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();
    private final AtomicLong loads = new AtomicLong();
    private final AtomicLong loadFailures = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();

    void recordHit() {
        hits.incrementAndGet();
    }

    void recordMiss() {
        misses.incrementAndGet();
    }

    void recordLoadSuccess() {
        loads.incrementAndGet();
    }

    void recordLoadFailure() {
        loadFailures.incrementAndGet();
    }

    void recordEviction() {
        evictions.incrementAndGet();
    }

    public long getHits() {
        return hits.get();
    }

    public long getMisses() {
        return misses.get();
    }

    public long getLoads() {
        return loads.get();
    }

    public long getLoadFailures() {
        return loadFailures.get();
    }

    public long getEvictions() {
        return evictions.get();
    }

    @Override
    public String toString() {
        return "CacheStats{" +
                "hits=" + getHits() +
                ", misses=" + getMisses() +
                ", loads=" + getLoads() +
                ", loadFailures=" + getLoadFailures() +
                ", evictions=" + getEvictions() +
                '}';
    }
}
