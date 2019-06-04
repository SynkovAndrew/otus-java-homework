package cell;

import banknote.BanknoteEnum;

import java.util.Objects;

public class AbstractCell implements Cell {
    private final int max;
    private final BanknoteEnum banknoteKind;
    private int occupancy;

    public AbstractCell(final BanknoteEnum banknoteKind,
                        final int max) {
        this.max = max;
        this.banknoteKind = banknoteKind;
        this.occupancy = 0;
    }

    @Override
    public int getOccupancy() {
        return occupancy;
    }

    @Override
    public boolean isEmpty() {
        return occupancy == 0;
    }

    @Override
    public BanknoteEnum withdrawBanknote() throws CellIsEmptyException {
        if (occupancy == 0) {
            throw new CellIsEmptyException();
        }
        occupancy--;

        return banknoteKind;
    }

    @Override
    public void putBanknote(BanknoteEnum banknote) throws CellIsFullException {
        if (occupancy == max) {
            throw new CellIsFullException();
        }
        occupancy++;
    }

    @Override
    public int getContentSum() {
        return occupancy * banknoteKind.getNominal();
    }

    @Override
    public BanknoteEnum getBanknoteKind() {
        return banknoteKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractCell that = (AbstractCell) o;
        return banknoteKind == that.banknoteKind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(banknoteKind);
    }
}
