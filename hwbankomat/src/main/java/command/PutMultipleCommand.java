package command;

import banknote.BanknoteEnum;
import core.ATMCore;
import core.FailedToPutBanknoteException;
import ui.UI;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

public class PutMultipleCommand extends AbstractCommand implements Command {
    private final ATMCore atmCore;
    private final List<String> banknotes;

    public PutMultipleCommand(final UI ui,
                              final ATMCore atmCore,
                              final List<String> banknotes) {
        super(ui);
        this.atmCore = atmCore;
        this.banknotes = banknotes;
    }

    @Override
    public void execute() {
        final var toPut = banknotes.stream()
                .map(BanknoteEnum::findByCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (toPut.isEmpty()) {
            ofNullable(ui).ifPresent(ui -> ui.showNotification(
                    "Error. ATM doesn't support such kind of banknote."));
        } else {
            try {
                atmCore.putMultiple(toPut);
                ofNullable(ui).ifPresent(ui -> ui.showNotification(
                        "\nAll banknotes have been put successfully. " +
                                "Balance: " + atmCore.getBalance() + "\n"));
            } catch (FailedToPutBanknoteException e) {
                ofNullable(ui).ifPresent(ui -> ui.showNotification(e.getMessage()));
            }
        }
    }
}
