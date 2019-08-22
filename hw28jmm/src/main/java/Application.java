import thread.SharedState;
import thread.ThreadTask;

public class Application {
    public static void main(String[] args) {
        final int MIN_AMPLITUDE = 1;
        final int MAX_AMPLITUDE = 10;
        final SharedState sharedState = new SharedState(MIN_AMPLITUDE, MAX_AMPLITUDE);
        final var firstThread = new Thread(new ThreadTask(sharedState, true));
        final var secondThread = new Thread(new ThreadTask(sharedState, false));
        firstThread.start();
        secondThread.start();
    }
}
