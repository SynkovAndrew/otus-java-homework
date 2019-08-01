package cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;
import static java.util.Optional.ofNullable;

public class CacheEngineImpl<K, V> implements CacheEngine<K, V> {
    private final int maxElements;
    private final long lifeTimeMs;
    private final long idleTimeMs;
    private final boolean isEternal;
    private final Map<K, CacheElement<K, V>> elements;
    private final Timer timer;
    private final AtomicInteger hit;
    private final AtomicInteger miss;

    public CacheEngineImpl(final int maxElements,
                           final long lifeTimeMs,
                           final long idleTimeMs,
                           final boolean isEternal) {
        this.maxElements = maxElements;
        this.elements = new LinkedHashMap<>();
        this.lifeTimeMs = lifeTimeMs > 0 ? lifeTimeMs : 0;
        this.idleTimeMs = idleTimeMs > 0 ? idleTimeMs : 0;
        this.isEternal = lifeTimeMs == 0 && idleTimeMs == 0 || isEternal;
        this.timer = new Timer();
        this.hit = new AtomicInteger(0);
        this.miss = new AtomicInteger(0);
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
        elements.put(key, element);
        if (!isEternal) {
            if (lifeTimeMs != 0) {
                timer.schedule(getTimerTask(key, e -> e.getCreatedAtMs() + lifeTimeMs), lifeTimeMs);
            }
            if (idleTimeMs != 0) {
                timer.schedule(getTimerTask(key, e -> e.getLastAccessTimeMs() + idleTimeMs), idleTimeMs);
            }
        }
    }

    @Override
    public V get(K key) {
        return ofNullable(elements.get(key))
                .map(cacheElement -> {
                    hit.incrementAndGet();
                    cacheElement.setAccessed();
                    return cacheElement.getValue();
                }).orElseGet(() -> {
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
        timer.cancel();

    }

    private TimerTask getTimerTask(final K key, final Function<CacheElement<K, V>, Long> timeFunction) {
        return new TimerTask() {
            @Override
            public void run() {
                elements.computeIfPresent(key, (key, element) -> {
                    if (timeFunction.apply(element).compareTo(currentTimeMillis()) > 0) {
                        elements.remove(key);
                        this.cancel();
                    }
                    return null;
                });
            }
        };
    }
}
