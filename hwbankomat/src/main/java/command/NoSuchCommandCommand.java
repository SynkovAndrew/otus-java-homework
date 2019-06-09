package command;

import core.ATMCore;
import ui.UI;

import static java.util.Optional.ofNullable;

public class NoSuchCommandCommand extends AbstractCommand implements Command {
    public NoSuchCommandCommand(final UI ui,
                                final ATMCore atmCore) {
        super(ui, atmCore);
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification("\nError. No such command.\n"));
    }
}
