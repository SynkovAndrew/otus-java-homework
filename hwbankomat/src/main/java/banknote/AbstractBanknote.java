package banknote;

import java.util.Objects;

public abstract class AbstractBanknote implements Banknote {
    private final BanknoteKindEnum value;

    protected AbstractBanknote(final BanknoteKindEnum value) {
        this.value = value;
    }

    public BanknoteKindEnum getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBanknote that = (AbstractBanknote) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
