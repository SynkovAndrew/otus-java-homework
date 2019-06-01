package atm;

import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;
import cell.Cell;
import cell.CellIsEmptyException;
import cell.CellIsFullException;
import cell.StandardCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toSet;

public class StandardATMCore implements ATMCore {
    private final Set<Cell> cells;

    public StandardATMCore() {
        this.cells = constructAtmCells();
    }

    public int getBalance() {
        return cells.stream().mapToInt(Cell::getContentSum).sum();
    }

    public void put(final Banknote bankNote) throws FailedToPutBanknoteException {
        final var cellToPut = cells.stream()
                .filter(cell -> cell.getBanknoteKind().equals(bankNote.getKind()))
                .findFirst()
                .orElseThrow(() -> new FailedToPutBanknoteException("Internal error. Please contact support."));

        try {
            cellToPut.putBanknote(bankNote);
        } catch (CellIsFullException e) {
            throw new FailedToPutBanknoteException("ATMCore is full.");
        }
    }

    public List<Banknote> withdraw(final int sum) throws FailedToWithdrawSumException {
        if (getBalance() < sum) {
            throw new FailedToWithdrawSumException("Not enough banknotes to withdraw desired sum.");
        }

        final var toWithdraw = new ArrayList<Banknote>();
        int rest = sum;

        for (BanknoteKindEnum kind : BanknoteKindEnum.getReverseSorted()) {
            if (rest == 0) {
                break;
            }
            rest = nextStep(rest, kind, toWithdraw);
        }

        if (rest != 0) {
            throw new FailedToWithdrawSumException("Not enough banknotes to withdraw desired sum.");
        }

        return toWithdraw;
    }

    private int nextStep(final int sum,
                         final BanknoteKindEnum banknoteKind,
                         final List<Banknote> banknotes) {
        final AtomicInteger rest = new AtomicInteger(sum);
        final var count = sum / banknoteKind.getValue();

        if (count > 0) {
            ofNullable(findCell(banknoteKind))
                    .ifPresent(cell -> {
                        for (int i = 0; i < count; i++) {
                            try {
                                final Banknote banknote = cell.getBanknote();
                                rest.set(rest.get() - banknote.getKind().getValue());

                                banknotes.add(banknote);
                            } catch (CellIsEmptyException e) {
                                break;
                            }
                        }
                    });

            return rest.get();
        }

        return sum;
    }

    private Set<Cell> constructAtmCells() {
        return Arrays.stream(BanknoteKindEnum.values())
                .map(kind -> new StandardCell(new StandardBanknote(kind)))
                .collect(toSet());
    }

    private Cell findCell(final BanknoteKindEnum banknoteKind) {
        return cells.stream()
                .filter(cell -> cell.getBanknoteKind().equals(banknoteKind))
                .findFirst()
                .orElse(null);
    }
}
