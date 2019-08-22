public class IntegerUtils {
    public static boolean inRange(final int from,
                                  final int to,
                                  final int value) {
        return from <= value && value < to;
    }
}
