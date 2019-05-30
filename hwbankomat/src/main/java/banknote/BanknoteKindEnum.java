package banknote;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static Set<BanknoteKindEnum> getReverseSorted() {
        return Stream.of(BanknoteKindEnum.values())
                .sorted((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
