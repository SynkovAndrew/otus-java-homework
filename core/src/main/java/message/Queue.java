package message;

public enum Queue {
    INBOX_QUEUE("INBOX_QUEUE"),
    FRONT_END_QUEUE("FRONT_END_QUEUE"),
    DATABASE_QUEUE("DATABASE_QUEUE");

    private final String code;

    Queue(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
