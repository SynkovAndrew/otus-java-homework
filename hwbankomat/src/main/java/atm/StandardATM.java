package atm;

import command.*;
import core.ATMCore;
import ui.CommandEnum;
import core.StandardATMCore;
import ui.CLI;
import ui.UI;

import static ui.CommandEnum.*;

public class StandardATM implements ATM {
    private final ATMCore atmCore;
    private final UI ui;

    public StandardATM() {
        this.atmCore = new StandardATMCore();
        this.ui = new CLI();
    }

    public StandardATM(final int initOccupancy) {
        this.atmCore = new StandardATMCore(initOccupancy);
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
                case BALANCE:
                    new ShowBalanceCommand(ui, atmCore)
                            .execute();
                    break;
                case PUT:
                    new PutCommand(ui, atmCore, nextCommand.get(1))
                            .execute();
                    break;
                case PUT_MULTIPLE:
                    new PutMultipleCommand(ui, atmCore, nextCommand.subList(1, nextCommand.size()))
                            .execute();
                    break;
                case WITHDRAW:
                    new WithdrawCommand(ui, atmCore, nextCommand.get(1))
                            .execute();
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
    public int getBalance() {
        return atmCore.getBalance();
    }

    @Override
    public void restoreToDefault() {
        atmCore.restoreInitialState();
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
