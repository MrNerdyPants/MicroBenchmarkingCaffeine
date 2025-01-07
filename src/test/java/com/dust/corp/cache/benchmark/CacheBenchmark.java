package com.dust.corp.cache.benchmark;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.openjdk.jmh.annotations.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@BenchmarkMode(Mode.Throughput) // Measures how many operations per second
@State(Scope.Thread)           // Each thread gets its own state
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Threads(4) // Simulate 4 threads
@Warmup(iterations = 2)
@Measurement(iterations = 5)
@Fork(2)
public class CacheBenchmark {

    private Map<String, String> concurrentHashMap;
    private Cache<String, String> caffeineCache;

    @Param({"100", "1000", "10000"}) // Varying data sizes
    private int dataSize;

    @Setup
    public void setup() {
        // Initialize ConcurrentHashMap
        concurrentHashMap = new ConcurrentHashMap<>();

        // Initialize Caffeine Cache
        caffeineCache = Caffeine.newBuilder()
                .maximumSize(10_000)
                .build();

        // Populate data
        for (int i = 0; i < dataSize; i++) {
            String key = "key" + i;
            String value = "value" + i;
            concurrentHashMap.put(key, value);
            caffeineCache.put(key, value);
        }
    }

    @Benchmark
    public String testConcurrentHashMapGet() {
        return concurrentHashMap.get("key50");
    }

    @Benchmark
    public String testCaffeineCacheGet() {
        return caffeineCache.getIfPresent("key50");
    }

    @Benchmark
    public void testConcurrentHashMapPut() {
        concurrentHashMap.put("newKey", "newValue");
    }

    @Benchmark
    public void testCaffeineCachePut() {
        caffeineCache.put("newKey", "newValue");
    }

    @Benchmark
    public void testConcurrentHashMapRemove() {
        concurrentHashMap.remove("key50");
    }

    @Benchmark
    public void testCaffeineCacheInvalidate() {
        caffeineCache.invalidate("key50");
    }
}

