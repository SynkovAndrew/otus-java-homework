import cache.CacheEngine;
import cache.CacheEngineImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class CacheEngineImplTest {
    private CacheEngine<String, Integer[]> cacheEngine;

    @BeforeEach
    public void beforeEach() {
        this.cacheEngine = new CacheEngineImpl<>(1950);
    }

    @AfterEach
    public void afterEach() {
        this.cacheEngine.dispose();
    }

    @Test
    public void cacheTest() {
        for (int i = 0; i < 1950; i++) {
            Integer[] array = new Integer[56500];
            cacheEngine.put(String.valueOf(i), array);
        }
        for (int i = 0; i < 1950; i++) {
            final Integer[] integers = cacheEngine.get(String.valueOf(i));
        }
        System.out.println("Hit: " + cacheEngine.getHitCount());
        System.out.println("Miss: " + cacheEngine.getMissCount());
    }
}
