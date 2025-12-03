package com.github.mlwilli.cache;

import java.time.Duration;
import java.util.Random;

/**
 * Minimal demo of the cache in action.
 */
public final class Demo {

    public static void main(String[] args) {
        CacheConfig<String, String> config = CacheConfig.<String, String>builder()
                .maxSize(3)
                .timeToLive(Duration.ofSeconds(10))
                .evictionPolicy(EvictionPolicy.LRU)
                .loader(key -> {
                    // Simulated expensive computation / IO
                    sleep(100);
                    return "value-for-" + key;
                })
                .build();

        Cache<String, String> cache = new InMemoryCache<>(config);

        System.out.println("Putting a couple of manual entries...");
        cache.put("manual-1", "hello");
        cache.put("manual-2", "world");

        System.out.println("manual-1 -> " + cache.get("manual-1").orElse("<missing>"));
        System.out.println("manual-2 -> " + cache.get("manual-2").orElse("<missing>"));

        System.out.println("\nLoading entries through loader (read-through)...");
        for (int i = 1; i <= 5; i++) {
            String key = "k" + i;
            String value = cache.getOrLoad(key);
            System.out.printf("getOrLoad(%s) = %s%n", key, value);
        }

        System.out.println("\nAccess pattern to exercise LRU...");
        Random rnd = new Random();
        for (int i = 0; i < 10; i++) {
            String key = "k" + (1 + rnd.nextInt(5));
            cache.get(key);
        }

        System.out.println("\nFinal stats: " + cache.stats());
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private Demo() {
    }
}
