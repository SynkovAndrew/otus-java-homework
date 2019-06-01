package cell;

import banknote.BanknoteEnum;

public interface Cell {
    BanknoteEnum getBanknote() throws CellIsEmptyException;

    void putBanknote(BanknoteEnum banknote) throws CellIsFullException;

    int getContentSum();

    int getOccupancy();

    BanknoteEnum getBanknoteKind();
}
