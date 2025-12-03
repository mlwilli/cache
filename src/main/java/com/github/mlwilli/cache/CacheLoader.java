package com.github.mlwilli.cache;

/**
 * Loader for read-through caching.
 */
@FunctionalInterface
public interface CacheLoader<K, V> {

    V load(K key) throws Exception;
}
