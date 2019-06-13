package atm;

import command.*;
import core.ATMCore;
import core.StandardATMCore;
import ui.AbstractUI;
import ui.CLI;
import ui.CommandEnum;

import java.util.List;

import static ui.CommandEnum.*;

public class StandardATM extends AbstractUI implements ATM {
    private final ATMCore atmCore;

    public StandardATM() {
        super(new CLI());
        this.atmCore = new StandardATMCore();
    }

    public StandardATM(final int initOccupancy) {
        super(new CLI());
        this.atmCore = new StandardATMCore(initOccupancy);
    }

    @Override
    protected void runCommand(final List<String> command) {
        switch (CommandEnum.fromCode(command.get(0))) {
            case BALANCE:
                new ShowBalanceCommand(ui, atmCore)
                        .execute();
                break;
            case PUT:
                new PutCommand(ui, atmCore, command.get(1))
                        .execute();
                break;
            case PUT_MULTIPLE:
                new PutMultipleCommand(ui, atmCore, command.subList(1, command.size()))
                        .execute();
                break;
            case WITHDRAW:
                new WithdrawCommand(ui, atmCore, command.get(1))
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

    @Override
    public int getBalance() {
        return atmCore.getBalance();
    }

    @Override
    public void restoreToDefault() {
        atmCore.restoreInitialState();
    }

    @Override
    protected String getMenuMessage() {
        return "Enter one of the following options: \n\n" +
                BALANCE + " \n" +
                PUT + " {TEN, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_THOUSAND} \n" +
                PUT_MULTIPLE + " {TEN, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_THOUSAND} \n" +
                WITHDRAW + " {number} \n" +
                EXIT + " \n";
    }
}
