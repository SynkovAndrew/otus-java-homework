package atm;

import banknote.*;
import cell.Cell;
import cell.CellIsFullException;
import cell.StandardCell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ATMImpl implements ATM {
    private final Set<Cell> cells;

    public ATMImpl() {
        this.cells = constructAtmCells();
    }

    public int getBalance() {
        return cells.stream().mapToInt(Cell::getContentSum).sum();
    }

    public void put(final Banknote bankNote) throws FailedToPutBanknoteException {
        final var cellToPut = cells.stream()
                .filter(cell -> cell.getBanknoteKind().equals(bankNote.getValue()))
                .findFirst()
                .orElseThrow(() -> new FailedToPutBanknoteException("Internal error. Please contact support."));

        try {
            cellToPut.putBanknote(bankNote);
        } catch (CellIsFullException e) {
            throw new FailedToPutBanknoteException("Bankomat is full.");
        }
    }

    public List<Banknote> withdraw(final int sum) throws FailedToWithdrawSumException {
        return null;
    }

    private Set<Cell> constructAtmCells() {
        final var cells = new HashSet<Cell>();

        cells.add(new StandardCell(new TenBanknote()));
        cells.add(new StandardCell(new FiftyBanknote()));
        cells.add(new StandardCell(new OneHundredBanknote()));
        cells.add(new StandardCell(new TwoHundredBanknote()));
        cells.add(new StandardCell(new FiveHundredBanknote()));
        cells.add(new StandardCell(new OneThousandBanknote()));
        cells.add(new StandardCell(new FiveThousandBanknote()));

        return cells;
    }
}
