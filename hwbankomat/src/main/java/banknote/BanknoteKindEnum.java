package banknote;

public enum BanknoteKindEnum {
    TEN(10),
    FIFTY(50),
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    ONE_THOUSAND(1000),
    FIVE_THOUSAND(5000);

    private final int value;

    BanknoteKindEnum(final int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
