package cell;

import banknote.BanknoteEnum;

import java.util.Objects;

public class StandardCell implements Cell {
    public static int STANDARD_MAX = 50;
    private final int max;
    private final BanknoteEnum banknoteKind;
    private int occupancy;

    public StandardCell(final BanknoteEnum banknoteKind) {
        this.max = STANDARD_MAX;
        this.banknoteKind = banknoteKind;
        this.occupancy = 0;
    }

    public StandardCell(final BanknoteEnum banknoteKind, final int occupancy) {
        this.max = STANDARD_MAX;
        this.banknoteKind = banknoteKind;
        this.occupancy = occupancy;
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
    public Cell copy() {
        return new StandardCell(this.banknoteKind, this.occupancy);
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
        StandardCell that = (StandardCell) o;
        return banknoteKind == that.banknoteKind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(banknoteKind);
    }
}
