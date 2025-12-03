Tiny Cache

A small, thread-safe in-memory cache with TTL, LRU eviction, and optional read-through loading.
Lightweight, dependency-free, and built for cases where you don’t want to drag in something huge just to cache a few values.

Features

Thread-safe in-memory storage

TTL support (entries expire automatically)

LRU eviction when size is exceeded

Optional CacheLoader for read-through caching

Basic stats: hits, misses, loads, failures, evictions

Zero dependencies — just Java 25

It’s intentionally small so you can skim 

Usage Example
CacheConfig<String, String> config = CacheConfig.<String, String>builder()
    .maxSize(100)
    .timeToLive(Duration.ofSeconds(30))
    .loader(key -> "value-for-" + key)
    .build();

Cache<String, String> cache = new InMemoryCache<>(config);

String v1 = cache.getOrLoad("user:42");
String v2 = cache.get("user:42").orElse("<missing>");

System.out.println(cache.stats());

Project Layout
src/main/java/com/github/mlwilli/cache/
 ├─ Cache.java               (public API)
 ├─ CacheConfig.java         (settings)
 ├─ CacheStats.java          (runtime stats)
 ├─ CacheLoader.java         (optional loader)
 ├─ InMemoryCache.java       (LRU + TTL implementation)
 ├─ ValueHolder.java         (internal wrapper)
 └─ Demo.java                (example usage)

Running the Demo
mvn exec:java

This will load some values, exercise LRU eviction, and print out the final cache stats.
