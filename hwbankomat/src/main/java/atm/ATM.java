package atm;

import banknote.Banknote;

import java.util.List;

public interface ATM {
    int getBalance();

    void put(Banknote bankNote) throws FailedToPutBanknoteException;

    List<Banknote> withdraw(int sum) throws FailedToWithdrawSumException;
}
