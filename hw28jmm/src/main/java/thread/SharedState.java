package thread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedState {
    private final int minAmplitude;
    private final int maxAmplitude;
    private final AtomicInteger firstCounter;
    private final AtomicInteger secondCounter;
    private final AtomicBoolean firstUpOrDownFlag;
    private final AtomicBoolean secondUpOrDownFlag;
    private final Object monitor;

    public SharedState(final int minAmplitude, final int maxAmplitude) {
        this.minAmplitude = minAmplitude;
        this.maxAmplitude = maxAmplitude;
        this.firstCounter = new AtomicInteger(minAmplitude);
        this.secondCounter = new AtomicInteger(minAmplitude);
        this.firstUpOrDownFlag = new AtomicBoolean(false);
        this.secondUpOrDownFlag = new AtomicBoolean(false);
        this.monitor = new Object();
    }

    public int getMinAmplitude() {
        return minAmplitude;
    }

    public int getMaxAmplitude() {
        return maxAmplitude;
    }

    public AtomicInteger getFirstCounter() {
        return firstCounter;
    }

    public AtomicInteger getSecondCounter() {
        return secondCounter;
    }

    public AtomicBoolean getFirstUpOrDownFlag() {
        return firstUpOrDownFlag;
    }

    public AtomicBoolean getSecondUpOrDownFlag() {
        return secondUpOrDownFlag;
    }

    public Object getMonitor() {
        return monitor;
    }
}
