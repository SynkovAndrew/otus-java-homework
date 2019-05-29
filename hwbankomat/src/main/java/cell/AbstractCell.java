package cell;

import banknote.Banknote;
import banknote.BanknoteKindEnum;

import java.util.Objects;

public class AbstractCell implements Cell {
    private final int max;
    private final Banknote banknoteType;
    private int current;

    public AbstractCell(final Banknote banknoteType,
                        final int max) {
        this.max = max;
        this.banknoteType = banknoteType;
        this.current = 0;
    }

    @Override
    public Banknote getBanknote() throws CellIsEmptyException {
        if (current == 0) {
            throw new CellIsEmptyException();
        }
        current--;

        return banknoteType;
    }

    @Override
    public void putBanknote(Banknote banknote) throws CellIsFullException {
        if (current == max) {
            throw new CellIsFullException();
        }
        current++;
    }

    @Override
    public int getContentSum() {
        return current * banknoteType.getValue().getValue();
    }

    @Override
    public BanknoteKindEnum getBanknoteKind() {
        return banknoteType.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCell that = (AbstractCell) o;
        return banknoteType == that.banknoteType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(banknoteType);
    }
}
