package cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;
import static java.util.Optional.ofNullable;

public class CacheEngineImpl<K, V> implements CacheEngine<K, V> {
    private final int maxElements;
    private final Map<K, SoftReference<CacheElement<K, V>>> elements;
    private final AtomicInteger miss;
    private final AtomicInteger hit;

    public CacheEngineImpl(final int maxElements) {
        this.maxElements = maxElements;
        this.elements = new LinkedHashMap<>();
        this.miss = new AtomicInteger(0);
        this.hit = new AtomicInteger(0);
    }


    @Override
    public void put(K key, V value) {
        if (elements.size() == maxElements) {
            elements.keySet().stream().findFirst().ifPresent(elements::remove);
        }
        final CacheElement<K, V> element = CacheElement.<K, V>builder()
                .createdAtMs(currentTimeMillis())
                .lastAccessTimeMs(currentTimeMillis())
                .key(key)
                .value(value)
                .build();
        final var referenceQueue = new ReferenceQueue<CacheElement<K, V>>();
        final SoftReference<CacheElement<K, V>> softReference = new SoftReference<>(element);
        elements.put(key, softReference);
        System.out.println("------------------------- cache: " + element.getKey() + " 's been cached!");
    }

    @Override
    public V get(K key) {
        return ofNullable(elements.get(key))
                .map(cacheElement -> ofNullable(cacheElement.get())
                        .map(e -> {
                            hit.incrementAndGet();
                            e.setAccessed();
                            System.out.println("------------------------- cache: " + key + " 's been hit!");
                            return e.getValue();
                        })
                        .orElseGet(() -> {
                            miss.incrementAndGet();
                            elements.remove(key);
                            System.out.println("------------------------- cache: " + key + " 's been missed!");
                            return null;
                        }))
                .orElseGet(() -> {
                    System.out.println("------------------------- cache: " + key + " 's been missed!");
                    miss.incrementAndGet();
                    return null;
                });
    }

    @Override
    public int getHitCount() {
        return hit.get();
    }

    @Override
    public int getMissCount() {
        return miss.get();
    }

    @Override
    public void dispose() {
        elements.clear();
        miss.set(0);
        hit.set(0);
    }
}
