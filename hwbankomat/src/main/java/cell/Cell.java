package cell;

import banknote.Banknote;
import banknote.BanknoteKindEnum;

public interface Cell {
    Banknote getBanknote() throws CellIsEmptyException;

    void putBanknote(Banknote banknote) throws CellIsFullException;

    int getContentSum();

    int getOccupancy();

    BanknoteKindEnum getBanknoteKind();
}
