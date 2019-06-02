package handler;

import atm.*;
import banknote.BanknoteEnum;
import ui.CLI;
import ui.UI;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static atm.CommandEnum.*;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

public class StandardATM implements ATM {
    private final ATMCore atmCore;
    private final UI ui;

    public StandardATM() {
        this.atmCore = new StandardATMCore();
        this.ui = new CLI();
    }

    public void start() {
        while (true) {
            ui.showNotification(getMenuMessage());

            final var nextCommand = ui.getNextCommand();

            if (nextCommand.size() == 0) {
                continue;
            }

            switch (CommandEnum.fromCode(nextCommand.get(0))) {
                case BALANCE:
                    handleShowBalance();
                    break;
                case PUT:
                    handlePutBanknote(nextCommand.get(1));
                    break;
                case PUT_MULTIPLE:
                    handlePutMultipleBanknotes(nextCommand.subList(1, nextCommand.size()));
                    break;
                case WITHDRAW:
                    handleWithdraw(nextCommand.get(1));
                    break;
                case EXIT:
                    handleExit();
                    break;
                case NO_SUCH_COMMAND:
                    handleNoSuchCommand();
                    break;
            }
        }
    }

    private void handleShowBalance() {
        ui.showNotification("\nBalance: " + atmCore.getBalance() + "\n");
    }

    private void handleNoSuchCommand() {
        ui.showNotification("\nError. No such command.\n");
    }

    private void handlePutBanknote(final String banknote) {
        final BanknoteEnum kind = BanknoteEnum.findByCode(banknote);
        if (isNull(kind)) {
            ui.showNotification("Error. ATM doesn't support such kind of banknote.");
        } else {
            try {
                atmCore.put(kind);
                ui.showNotification("\n\"" + banknote + "\" has been put successfully. Balance: " + atmCore.getBalance() + "\n");
            } catch (FailedToPutBanknoteException e) {
                ui.showNotification(e.getMessage());
            }
        }

    }

    private void handlePutMultipleBanknotes(final List<String> banknotes) {
        final var toPut = banknotes.stream()
                .map(BanknoteEnum::findByCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (toPut.isEmpty()) {
            ui.showNotification("Error. ATM doesn't support such kind of banknote.");
        } else {
            try {
                atmCore.putMultiple(toPut);
                ui.showNotification("\nAll banknotes have been put successfully. Balance: " + atmCore.getBalance() + "\n");
            } catch (FailedToPutBanknoteException e) {
                ui.showNotification(e.getMessage());
            }
        }

    }

    private void handleWithdraw(final String sum) {
        try {
            final List<BanknoteEnum> withdraw = atmCore.withdraw(Integer.valueOf(sum));
            ui.showNotification("\nWithdrew: " + withdraw.stream().map(BanknoteEnum::toString).collect(joining(", ")));
        } catch (FailedToWithdrawSumException  e) {
            ui.showNotification(e.getMessage());
        } catch (NumberFormatException e) {
            ui.showNotification("Entered sum is not correct! Use numbers only.");
        }
    }

    private void handleExit() {
        ui.showNotification("\nATM's been turned off.\n");
        System.exit(0);
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
