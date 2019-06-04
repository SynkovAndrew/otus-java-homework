package atm;

public class FailedToPutBanknoteException extends Exception {
    public FailedToPutBanknoteException(String message) {
        super(message);
    }
}
