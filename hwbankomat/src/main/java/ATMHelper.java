import atm.ATMCore;
import atm.FailedToPutBanknoteException;
import atm.StandardATMCore;
import banknote.BanknoteEnum;

public class ATMHelper {
    public static void fulfillATM(final ATMCore atmCore) {
        for (BanknoteEnum kind : BanknoteEnum.values()) {
            for (int i = 1; i <= 20; i++) {
                try {
                    atmCore.put(kind);
                } catch (FailedToPutBanknoteException ignored) {
                }
            }
        }
    }
}
