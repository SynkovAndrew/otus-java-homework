package cell;

import banknote.Banknote;
import banknote.BanknoteKindEnum;

import java.util.Objects;

public class AbstractCell implements Cell {
    private final int max;
    private final Banknote banknoteType;
    private int occupancy;

    public AbstractCell(final Banknote banknoteType,
                        final int max) {
        this.max = max;
        this.banknoteType = banknoteType;
        this.occupancy = 0;
    }

    @Override
    public int getOccupancy() {
        return occupancy;
    }

    @Override
    public Banknote getBanknote() throws CellIsEmptyException {
        if (occupancy == 0) {
            throw new CellIsEmptyException();
        }
        occupancy--;

        return banknoteType;
    }

    @Override
    public void putBanknote(Banknote banknote) throws CellIsFullException {
        if (occupancy == max) {
            throw new CellIsFullException();
        }
        occupancy++;
    }

    @Override
    public int getContentSum() {
        return occupancy * banknoteType.getValue().getValue();
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
