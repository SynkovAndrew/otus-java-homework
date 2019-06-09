package command;

import core.ATMCore;
import ui.UI;

import static java.util.Optional.ofNullable;

public class ExitCommand extends AbstractCommand implements Command {

    public ExitCommand(final UI ui,
                       final ATMCore atmCore) {
        super(ui, atmCore);
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification("\nATM's been turned off.\n"));
        System.exit(0);
    }
}
