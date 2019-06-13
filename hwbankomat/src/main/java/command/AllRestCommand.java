package command;

import atm.ATM;
import ui.UI;

import java.util.List;

import static java.util.Optional.ofNullable;

public class AllRestCommand extends AbstractCommand implements Command {
    private final List<ATM> atms;

    public AllRestCommand(final UI ui, final List<ATM> atms) {
        super(ui);
        this.atms = atms;
    }

    @Override
    public void execute() {
        ofNullable(ui).ifPresent(ui -> ui.showNotification(
                "\nAll Rest: " + atms.stream().mapToInt(ATM::getBalance).sum() + "\n"));
    }
}
