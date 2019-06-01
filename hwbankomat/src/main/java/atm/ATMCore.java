package atm;

import banknote.BanknoteEnum;

import java.util.List;

public interface ATMCore {
    int getBalance();

    void put(BanknoteEnum banknoteKind) throws FailedToPutBanknoteException;

    List<BanknoteEnum> withdraw(int sum) throws FailedToWithdrawSumException;
}
