package command;

import ui.UI;

import static java.util.Optional.ofNullable;

public class NoSuchCommandCommand extends AbstractCommand implements Command {
    public NoSuchCommandCommand(final UI ui) {
        super(ui);
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification("\nError. No such command.\n"));
    }
}
