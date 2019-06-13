package command;

import core.ATMCore;
import ui.UI;

import static java.util.Optional.ofNullable;

public class ShowBalanceCommand extends AbstractCommand implements Command {
    private final ATMCore atmCore;

    public ShowBalanceCommand(final UI ui, final ATMCore atmCore) {
        super(ui);
        this.atmCore = atmCore;
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification("\nBalance: " + atmCore.getBalance() + "\n"));
    }
}
