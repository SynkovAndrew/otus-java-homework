package banknote;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BanknoteKindEnum {
    TEN("TEN", 10),
    FIFTY("FIFTY", 50),
    ONE_HUNDRED("ONE_HUNDRED", 100),
    TWO_HUNDRED("TWO_HUNDRED", 200),
    FIVE_HUNDRED("FIVE_HUNDRED", 500),
    ONE_THOUSAND("ONE_THOUSAND", 1000),
    FIVE_THOUSAND("FIVE_THOUSAND", 5000);

    private final int value;
    private final String code;

    BanknoteKindEnum(final String code, final int value) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static BanknoteKindEnum findByCode(final String code) {
        return Stream.of(BanknoteKindEnum.values())
                .filter(kind -> kind.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static Set<BanknoteKindEnum> getReverseSorted() {
        return Stream.of(BanknoteKindEnum.values())
                .sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
