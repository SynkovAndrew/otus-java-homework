package cell;

import banknote.BanknoteEnum;

public interface Cell {
    BanknoteEnum withdrawBanknote() throws CellIsEmptyException;

    void putBanknote(BanknoteEnum banknote) throws CellIsFullException;

    int getContentSum();

    int getOccupancy();

    BanknoteEnum getBanknoteKind();

    boolean isEmpty();

    Cell copy();
}
