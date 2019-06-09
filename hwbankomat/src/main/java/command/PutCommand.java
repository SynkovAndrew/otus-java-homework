package command;

import banknote.BanknoteEnum;
import core.ATMCore;
import core.FailedToPutBanknoteException;
import ui.UI;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

public class PutCommand extends AbstractCommand implements Command {
    private final String banknote;

    public PutCommand(final UI ui,
                      final ATMCore atmCore,
                      final String banknote) {
        super(ui, atmCore);
        this.banknote = banknote;
    }

    @Override
    public void execute() {
        final BanknoteEnum kind = BanknoteEnum.findByCode(banknote);
        if (isNull(kind)) {
            ofNullable(ui).ifPresent(ui -> ui.showNotification("Error. ATM doesn't support such kind of banknote."));
        } else {
            try {
                atmCore.put(kind);
                ofNullable(ui).ifPresent(ui -> ui.showNotification(
                        "\n\"" + banknote + "\" has been put successfully. " +
                                "Balance: " + atmCore.getBalance() + "\n"));
            } catch (FailedToPutBanknoteException e) {
                ofNullable(ui).ifPresent(ui -> ui.showNotification(e.getMessage()));
            }
        }
    }
}
