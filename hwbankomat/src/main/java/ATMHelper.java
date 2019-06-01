import atm.ATMCore;
import atm.FailedToPutBanknoteException;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;

public class ATMHelper {
    public static void fulfillATM(final ATMCore atmCore) {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 20; i++) {
                try {
                    atmCore.put(new StandardBanknote(kind));
                } catch (FailedToPutBanknoteException ignored) {}
            }
        }
    }
}
