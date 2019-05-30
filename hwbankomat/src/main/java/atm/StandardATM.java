package atm;

import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;
import cell.Cell;
import cell.CellIsFullException;
import cell.StandardCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class StandardATM implements ATM {
    private final Set<Cell> cells;

    public StandardATM() {
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
        final var toWithdraw = new ArrayList<Banknote>();
        int rest = sum;

        for (BanknoteKindEnum kind: BanknoteKindEnum.getReverseSorted()) {
            rest = nextStep(rest, kind, toWithdraw);
        }

        if (rest != 0) {
            throw new FailedToWithdrawSumException("Not enough banknotes to withdraw desired sum.");
        }

        return toWithdraw;
    }

    private int nextStep(final int rest,
                         final BanknoteKindEnum banknoteKind,
                         final List<Banknote> banknotes) {
        final var count = rest / banknoteKind.getValue();

        if (count > 0) {
            banknotes.addAll(IntStream.rangeClosed(1, count)
                    .mapToObj(num -> new StandardBanknote(banknoteKind))
                    .collect(Collectors.toList()));

            return rest - banknoteKind.getValue() * count;
        }

        return rest;
    }

    private Set<Cell> constructAtmCells() {
        return Arrays.stream(BanknoteKindEnum.values())
                .map(kind -> new StandardCell(new StandardBanknote(kind)))
                .collect(toSet());
    }
}
