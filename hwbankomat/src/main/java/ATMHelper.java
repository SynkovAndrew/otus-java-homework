import atm.ATM;
import atm.FailedToPutBanknoteException;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;

public class ATMHelper {
    public static void fullfillATM(final ATM atm) {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 20; i++) {
                try {
                    atm.put(new StandardBanknote(kind));
                } catch (FailedToPutBanknoteException ignored) {}
            }
        }
    }
}
