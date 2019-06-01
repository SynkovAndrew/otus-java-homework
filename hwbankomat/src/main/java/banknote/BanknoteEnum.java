package banknote;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BanknoteEnum {
    TEN("TEN", 10),
    FIFTY("FIFTY", 50),
    ONE_HUNDRED("ONE_HUNDRED", 100),
    TWO_HUNDRED("TWO_HUNDRED", 200),
    FIVE_HUNDRED("FIVE_HUNDRED", 500),
    ONE_THOUSAND("ONE_THOUSAND", 1000),
    FIVE_THOUSAND("FIVE_THOUSAND", 5000);

    private final int nominal;
    private final String code;

    BanknoteEnum(final String code, final int nominal) {
        this.nominal = nominal;
        this.code = code;
    }

    public int getNominal() {
        return nominal;
    }

    public String getCode() {
        return code;
    }

    public static BanknoteEnum findByCode(final String code) {
        return Stream.of(BanknoteEnum.values())
                .filter(kind -> kind.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "Banknote {" +
                "nominal=" + nominal +
                ", code='" + code + '\'' +
                '}';
    }

    public static Set<BanknoteEnum> getReverseSorted() {
        return Stream.of(BanknoteEnum.values())
                .sorted((o1, o2) -> Integer.compare(o2.getNominal(), o1.getNominal()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
