package department;

import atm.ATM;
import command.*;
import ui.CommandEnum;
import ui.CLI;
import ui.UI;

import java.util.List;

import static ui.CommandEnum.*;

public class StandardATMDepartment implements ATMDepartment {
    private final List<ATM> atms;
    private final UI ui;

    public StandardATMDepartment(List<ATM> atms) {
        this.atms = atms;
        this.ui = new CLI();
    }


    public void startUI() {
        while (true) {
            ui.showNotification(getMenuMessage());

            final var nextCommand = ui.getNextCommand();

            if (nextCommand.size() == 0) {
                continue;
            }

            switch (CommandEnum.fromCode(nextCommand.get(0))) {
                case ALL_REST:
                    break;
                case RESTORE_ALL_TO_DEFAULT:
                    break;
                case EXIT:
                    new ExitCommand(ui, atmCore)
                            .execute();
                    break;
                case NO_SUCH_COMMAND:
                    new NoSuchCommandCommand(ui, atmCore)
                            .execute();
                    break;
            }
        }
    }

    @Override
    public int getSumRest() {
        return 0;
    }

    @Override
    public void restoreAllToDefault() {

    }

    private String getMenuMessage() {
        return "Enter one of the following options: \n\n" +
                BALANCE + " \n" +
                PUT + " {TEN, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_THOUSAND} \n" +
                PUT_MULTIPLE + " {TEN, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_THOUSAND} \n" +
                WITHDRAW + " {number} \n" +
                EXIT + " \n";
    }
}
