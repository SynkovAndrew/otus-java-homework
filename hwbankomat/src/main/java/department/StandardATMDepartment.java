package department;

import atm.ATM;
import command.AllRestCommand;
import command.ExitCommand;
import command.NoSuchCommandCommand;
import command.RestoreAllToDefaultCommand;
import ui.AbstractUI;
import ui.CLI;
import ui.CommandEnum;

import java.util.List;

import static ui.CommandEnum.*;

public class StandardATMDepartment extends AbstractUI {
    private final List<ATM> atms;

    public StandardATMDepartment(final List<ATM> atms) {
        super(new CLI());
        this.atms = atms;
    }

    @Override
    protected String getMenuMessage() {
        return "Enter one of the following options: \n\n" +
                ALL_REST + " \n" +
                RESTORE_ALL_TO_DEFAULT + "\n" +
                EXIT + " \n";
    }

    @Override
    protected void runCommand(final List<String> command) {
        switch (CommandEnum.fromCode(command.get(0))) {
            case ALL_REST:
                new AllRestCommand(ui, atms)
                        .execute();
                break;
            case RESTORE_ALL_TO_DEFAULT:
                new RestoreAllToDefaultCommand(ui, atms)
                        .execute();
                break;
            case EXIT:
                new ExitCommand(ui)
                        .execute();
                break;
            case NO_SUCH_COMMAND:
                new NoSuchCommandCommand(ui)
                        .execute();
                break;
        }
    }
}
