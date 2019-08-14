package utils;

import static java.util.Optional.ofNullable;

public class StringUtils {
    public static String capitalize(final String string) {
        return ofNullable(string)
                .map(s -> s.length() > 1 ? s.substring(0, 1).toUpperCase() + s.substring(1) : s.toUpperCase())
                .orElse("");
    }
}
