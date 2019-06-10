package command;

import atm.ATM;
import ui.UI;

import java.util.List;

import static java.util.Optional.ofNullable;

public class RestoreAllToDefaultCommand extends AbstractCommand implements Command {
    private final List<ATM> atms;

    public RestoreAllToDefaultCommand(final UI ui, final List<ATM> atms) {
        super(ui);
        this.atms = atms;
    }

    @Override
    public void execute() {
        atms.forEach(ATM::restoreToDefault);
        ofNullable(ui).ifPresent(ui -> ui.showNotification(
                "\nAll ATM's ve been restored to default\n" +
                        "All Rest: " + atms.stream().mapToInt(ATM::getBalance).sum() + "\n"));
    }
}
