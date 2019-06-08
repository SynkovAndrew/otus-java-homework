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
    private final State currentState;
    private final State initialState;
    private final State previousState;

    public StandardATMCore() {
        this.initialState = new State(constructAtmCells());
        this.currentState = new State(constructAtmCells());
        this.previousState = new State(constructAtmCells());
    }

    @Override
    public int getBalance() {
        return currentState.getCells().values().stream().mapToInt(Cell::getContentSum).sum();
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
        this.previousState.setState(currentState);

        final var toWithdraw = new ArrayList<BanknoteEnum>();
        int rest = sum;

        for (BanknoteEnum kind : BanknoteEnum.getReverseSorted()) {
            if (rest == 0) {
                break;
            }
            rest = nextStep(rest, kind, toWithdraw);
        }

        if (rest != 0) {
            this.currentState.setState(previousState);
            throw new FailedToWithdrawSumException("Not enough banknotes to withdraw desired sum.");
        }

        return toWithdraw;
    }

    private void put(final BanknoteEnum banknote, final long banknoteCount) throws FailedToPutBanknoteException {
        final var cellToPut = ofNullable(currentState.getCells().get(banknote))
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
            ofNullable(currentState.getCells().get(banknoteKind))
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

    private static class State {
        private final Map<BanknoteEnum, Cell> cells;

        private State(final Map<BanknoteEnum, Cell> cells) {
            this.cells = cells;
        }

        private Map<BanknoteEnum, Cell> getCells() {
            return cells;
        }

        private void setState(final State state) {
            state.getCells().entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> e.getValue().copy()))
                    .forEach(cells::put);
        }
    }
}
