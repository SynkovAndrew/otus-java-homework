package messageV2;

public enum Queue {
    INPUT_QUEUE("INPUT_QUEUE"),
    OUTPUT_QUEUE("OUTPUT_QUEUE");

    private final String code;

    Queue(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
