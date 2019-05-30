package banknote;

import java.util.Objects;

public abstract class AbstractBanknote implements Banknote {
    private final BanknoteKindEnum kind;

    protected AbstractBanknote(final BanknoteKindEnum kind) {
        this.kind = kind;
    }

    @Override
    public BanknoteKindEnum getKind() {
        return kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBanknote that = (AbstractBanknote) o;
        return kind == that.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind);
    }

    @Override
    public String toString() {
        return "\"" + kind.getCode() + "\" Banknote";
    }
}
