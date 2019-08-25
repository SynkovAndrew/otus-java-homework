package thread;

public class ThreadUtils {

    public static void sleep(final long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void wait(final Object object) {
        try {
            object.wait();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void notify(final Object object) {
        object.notify();
    }
}
