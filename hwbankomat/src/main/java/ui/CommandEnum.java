package ui;

import com.google.common.base.Strings;

import java.util.stream.Stream;

public enum CommandEnum {
    // atm commands
    BALANCE("BALANCE"),
    PUT("PUT"),
    PUT_MULTIPLE("PUT_MULTIPLE"),
    WITHDRAW("WITHDRAW"),
    // department commands
    ALL_REST("ALL_REST"),
    RESTORE_ALL_TO_DEFAULT("RESTORE_ALL_TO_DEFAULT"),
    // common commands
    EXIT("EXIT"),
    NO_SUCH_COMMAND("NO_SUCH_COMMAND");

    private final String code;

    CommandEnum(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static CommandEnum fromCode(final String code) {
        return Stream.of(values())
                .filter(command -> command.getCode().equals(Strings.nullToEmpty(code).trim().toUpperCase()))
                .findFirst()
                .orElse(NO_SUCH_COMMAND);
    }
}
