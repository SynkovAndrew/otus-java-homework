package atm;

public class FailedToWithdrawSumException extends Exception {
    public FailedToWithdrawSumException(String message) {
        super(message);
    }
}
