package cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheElement<K, V> {
    private K key;
    private V value;
    private long createdAtMs;
    private long lastAccessTimeMs;

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public void setAccessed() {
        lastAccessTimeMs = getCurrentTime();
    }
}
