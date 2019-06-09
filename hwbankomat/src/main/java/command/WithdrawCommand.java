package command;

import banknote.BanknoteEnum;
import core.ATMCore;
import core.FailedToWithdrawSumException;
import ui.UI;

import java.util.List;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public class WithdrawCommand extends AbstractCommand implements Command {
    private final String sum;

    public WithdrawCommand(final UI ui,
                           final ATMCore atmCore,
                           final String sum) {
        super(ui, atmCore);
        this.sum = sum;
    }

    @Override
    public void execute() {
        try {
            final List<BanknoteEnum> withdraw = atmCore.withdraw(Integer.valueOf(sum));
            ofNullable(ui).ifPresent(ui -> ui.showNotification(
                    "\nWithdrew: " + withdraw.stream().map(BanknoteEnum::toString).collect(joining(", "))));
        } catch (FailedToWithdrawSumException e) {
            ofNullable(ui).ifPresent(ui -> ui.showNotification(e.getMessage()));
        } catch (NumberFormatException e) {
            ofNullable(ui).ifPresent(ui -> ui.showNotification("Entered sum is not correct! Use numbers only."));
        }
    }
}
