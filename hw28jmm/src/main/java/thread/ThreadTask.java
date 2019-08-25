package thread;

import static java.lang.Thread.currentThread;

public class ThreadTask implements Runnable {
    private final SharedState sharedState;
    private final boolean isFirst;

    public ThreadTask(final SharedState sharedState, final boolean isFirst) {
        this.sharedState = sharedState;
        this.isFirst = isFirst;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (sharedState.getMonitor()) {
                if (getRunCondition()) {
                    handleFlag();
                    System.out.printf("%s: %d\n", currentThread().getName(), getNextValue());
                    ThreadUtils.sleep(1000);
                    ThreadUtils.notify(sharedState.getMonitor());
                } else {
                    ThreadUtils.wait(sharedState.getMonitor());
                }
            }
        }
    }

    private int getNextValue() {
        return isFirst ?
                (
                        sharedState.getFirstUpOrDownFlag().get() ?
                                sharedState.getFirstCounter().getAndIncrement() :
                                sharedState.getFirstCounter().getAndDecrement()
                ) :
                (
                        sharedState.getSecondUpOrDownFlag().get() ?
                                sharedState.getSecondCounter().getAndIncrement() :
                                sharedState.getSecondCounter().getAndDecrement()
                );
    }

    private void handleFlag() {
        final boolean inRange = inRange();
        if (isFirst) {
            if (!inRange) {
                sharedState.getFirstUpOrDownFlag().set(!sharedState.getFirstUpOrDownFlag().get());
            }
        } else {
            if (!inRange) {
                sharedState.getSecondUpOrDownFlag().set(!sharedState.getSecondUpOrDownFlag().get());
            }
        }
    }

    private boolean inRange() {
        return isFirst ?
                IntegerUtils.inRange(sharedState.getMinAmplitude(), sharedState.getMaxAmplitude(), sharedState.getFirstCounter().get()) :
                IntegerUtils.inRange(sharedState.getMinAmplitude(), sharedState.getMaxAmplitude(), sharedState.getSecondCounter().get());
    }

    private boolean getRunCondition() {
        return isFirst ?
                sharedState.getFirstCounter().get() - sharedState.getSecondCounter().get() == 0 :
                Math.abs(sharedState.getFirstCounter().get() - sharedState.getSecondCounter().get()) == 1;
    }
}
