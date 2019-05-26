package common;

import java.lang.reflect.Method;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class LogUtils {
    public static String createMessage(final Method method, final List<Object> args) {
        return "Executed method: " + method.getName() + " ( " +
                args.stream()
                        .map(arg -> "param" + args.indexOf(arg) + ": " + arg)
                        .collect(joining(", ")) + " )";
    }
}
