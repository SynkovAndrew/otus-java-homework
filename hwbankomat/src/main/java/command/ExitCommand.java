package command;

import ui.UI;

import static java.util.Optional.ofNullable;

public class ExitCommand extends AbstractCommand implements Command {

    public ExitCommand(final UI ui) {
        super(ui);
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification("\nATM's been turned off.\n"));
        System.exit(0);
    }
}
