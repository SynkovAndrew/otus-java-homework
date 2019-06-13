package core;

import banknote.BanknoteEnum;

import java.util.List;

public interface ATMCore {
    int getBalance();

    void put(BanknoteEnum banknote) throws FailedToPutBanknoteException;

    void putMultiple(List<BanknoteEnum> banknotes) throws FailedToPutBanknoteException;

    List<BanknoteEnum> withdraw(int sum) throws FailedToWithdrawSumException;

    void restoreInitialState();
}
