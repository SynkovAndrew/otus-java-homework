package atm;

import banknote.BanknoteEnum;
import cell.Cell;
import cell.CellIsEmptyException;
import cell.CellIsFullException;
import cell.StandardCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

public class StandardATMCore implements ATMCore {
    private final Map<BanknoteEnum, Cell> cells;

    public StandardATMCore() {
        this.cells = constructAtmCells();
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(Cell::getContentSum).sum();
    }

    @Override
    public void put(final BanknoteEnum banknote) throws FailedToPutBanknoteException {
        put(banknote, 1L);
    }

    @Override
    public void putMultiple(final List<BanknoteEnum> banknotes) throws FailedToPutBanknoteException {
        final Map<BanknoteEnum, Long> banknoteFrequency = banknotes.stream()
                .distinct()
                .collect(toMap(
                        Function.identity(),
                        banknote -> banknotes.stream().filter(bn -> bn.equals(banknote)).count()
                ));

        for (var entry : banknoteFrequency.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public List<BanknoteEnum> withdraw(final int sum) throws FailedToWithdrawSumException {
        if (getBalance() < sum) {
            throw new FailedToWithdrawSumException("Not enough banknotes to withdraw desired sum.");
        }

        final var toWithdraw = new ArrayList<BanknoteEnum>();
        int rest = sum;

        for (BanknoteEnum kind : BanknoteEnum.getReverseSorted()) {
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

    private void put(final BanknoteEnum banknote, final long banknoteCount) throws FailedToPutBanknoteException {
        final var cellToPut = ofNullable(cells.get(banknote))
                .orElseThrow(() -> new FailedToPutBanknoteException("Internal error. Please contact support."));
        try {
            for (int i = 0; i < banknoteCount; i++) {
                cellToPut.putBanknote(banknote);
            }
        } catch (CellIsFullException e) {
            throw new FailedToPutBanknoteException("ATM is full.");
        }
    }

    private int nextStep(final int sum,
                         final BanknoteEnum banknoteKind,
                         final List<BanknoteEnum> banknotes) {
        final var rest = new AtomicInteger(sum);
        final var count = sum / banknoteKind.getNominal();

        if (count > 0) {
            ofNullable(cells.get(banknoteKind))
                    .ifPresent(cell -> {
                        for (int i = 0; i < count; i++) {
                            try {
                                final BanknoteEnum banknote = cell.withdrawBanknote();
                                rest.set(rest.get() - banknote.getNominal());

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

    private Map<BanknoteEnum, Cell> constructAtmCells() {
        return Arrays.stream(BanknoteEnum.values())
                .collect(toMap(Function.identity(), StandardCell::new));
    }
}
