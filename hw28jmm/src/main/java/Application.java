import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.currentThread;

public class Application {

    public static void main(String[] args) {
        final int MIN_AMPLITUDE = 1;
        final int MAX_AMPLITUDE = 3;
        final var firstCounter = new AtomicInteger(1);
        final var secondCounter = new AtomicInteger(1);
        final var firstUpOrDownFlag = new AtomicBoolean(true);
        final var secondUpOrDownFlag = new AtomicBoolean(true);
        final var monitor = new Object();

        final Runnable firstThreadRunnable = () -> {
            while (true) {
                synchronized (monitor) {
                    if (firstCounter.get() - secondCounter.get() == 0) {
                        final var inRange = IntegerUtils.inRange(MIN_AMPLITUDE, MAX_AMPLITUDE, firstCounter.get());
                        if (!inRange) {
                            firstUpOrDownFlag.set(!firstUpOrDownFlag.get());
                        }
                        final var value = firstUpOrDownFlag.get() ? firstCounter.getAndIncrement() : firstCounter.getAndDecrement();
                        System.out.printf("%s: %d\n", currentThread().getName(), value);
                        ThreadUtils.sleep(300);
                        ThreadUtils.notify(monitor);
                    } else {
                        ThreadUtils.wait(monitor);
                    }
                }
            }
        };

        final Runnable secondThreadRunnable = () -> {
            ThreadUtils.sleep(500);
            while (true) {
                synchronized (monitor) {
                    if (Math.abs(firstCounter.get() - secondCounter.get()) == 1) {
                        final var inRange = IntegerUtils.inRange(MIN_AMPLITUDE, MAX_AMPLITUDE, secondCounter.get());
                        if (!inRange) {
                            secondUpOrDownFlag.set(!secondUpOrDownFlag.get());
                        }
                        final var value = secondUpOrDownFlag.get() ? secondCounter.getAndIncrement() : secondCounter.getAndDecrement();
                        System.out.printf("%s: %d\n", currentThread().getName(), value);
                        ThreadUtils.sleep(300);
                        ThreadUtils.notify(monitor);
                    } else {
                        ThreadUtils.wait(monitor);
                    }
                }
            }
        };

        final var firstThread = new Thread(firstThreadRunnable);
        final var secondThread = new Thread(secondThreadRunnable);
        firstThread.start();
        secondThread.start();
    }
}
