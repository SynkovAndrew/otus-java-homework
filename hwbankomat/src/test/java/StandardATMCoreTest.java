import atm.ATMCore;
import atm.FailedToPutBanknoteException;
import atm.FailedToWithdrawSumException;
import atm.StandardATMCore;
import banknote.BanknoteEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class StandardATMCoreTest {
    private ATMCore atmCore;

    @BeforeEach
    public void beforeEach() {
        atmCore = new StandardATMCore();
    }

    @Test
    public void putBanknoteTest() throws FailedToPutBanknoteException {
        Assertions.assertEquals(0, atmCore.getBalance());

        atmCore.put(BanknoteEnum.TEN);
        atmCore.put(BanknoteEnum.TEN);
        Assertions.assertEquals(20, atmCore.getBalance());

        atmCore.put(BanknoteEnum.FIFTY);
        Assertions.assertEquals(70, atmCore.getBalance());

        atmCore.put(BanknoteEnum.ONE_HUNDRED);
        atmCore.put(BanknoteEnum.ONE_HUNDRED);
        Assertions.assertEquals(270, atmCore.getBalance());

        atmCore.put(BanknoteEnum.FIVE_THOUSAND);
        atmCore.put(BanknoteEnum.FIVE_THOUSAND);
        Assertions.assertEquals(10270, atmCore.getBalance());
    }

    @Test
    public void putMultipleBanknoteTest() throws FailedToPutBanknoteException {
        Assertions.assertEquals(0, atmCore.getBalance());
        final var banknotes = newArrayList(BanknoteEnum.TEN, BanknoteEnum.TEN, BanknoteEnum.FIFTY, BanknoteEnum.FIFTY, BanknoteEnum.FIFTY,
                BanknoteEnum.ONE_HUNDRED, BanknoteEnum.ONE_HUNDRED, BanknoteEnum.ONE_HUNDRED, BanknoteEnum.FIVE_THOUSAND);
        atmCore.putMultiple(banknotes);
        Assertions.assertEquals(5470, atmCore.getBalance());
    }

    @Test
    public void withdrawTest_failed() throws FailedToPutBanknoteException {
        for (BanknoteEnum kind : BanknoteEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atmCore.put(BanknoteEnum.TWO_HUNDRED);
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(50));
    }

    @Test
    public void withdrawTest_failed_sum_too_small() throws FailedToPutBanknoteException {
        for (BanknoteEnum kind : BanknoteEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atmCore.put(BanknoteEnum.TWO_HUNDRED);
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(2));
    }

    @Test
    public void withdrawTest() throws FailedToPutBanknoteException, FailedToWithdrawSumException {
        for (BanknoteEnum kind : BanknoteEnum.values()) {
            for (int i = 1; i <= 10; i++) {
                atmCore.put(kind);
            }
        }

        List<BanknoteEnum> withdrew = atmCore.withdraw(100);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(BanknoteEnum.ONE_HUNDRED), withdrew);
        Assertions.assertEquals(68500, atmCore.getBalance());

        withdrew = atmCore.withdraw(200);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(BanknoteEnum.TWO_HUNDRED), withdrew);
        Assertions.assertEquals(68300, atmCore.getBalance());

        withdrew = atmCore.withdraw(2500);
        Assertions.assertEquals(3, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.FIVE_HUNDRED
        ), withdrew);
        Assertions.assertEquals(65800, atmCore.getBalance());

        withdrew = atmCore.withdraw(55550);
        Assertions.assertEquals(17, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.FIVE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.ONE_THOUSAND,
                BanknoteEnum.FIVE_HUNDRED,
                BanknoteEnum.FIFTY

        ), withdrew);
        Assertions.assertEquals(10250, atmCore.getBalance());
    }


    @Test
    public void withdraw_empty() {
        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(100));
    }

    @Test
    public void getBalanceTest() throws FailedToPutBanknoteException {
        for (int i = 1; i <= 10; i++) {
            atmCore.put(BanknoteEnum.TEN);
            atmCore.put(BanknoteEnum.FIVE_HUNDRED);
            atmCore.put(BanknoteEnum.ONE_THOUSAND);
        }
        Assertions.assertEquals(15100, atmCore.getBalance());
    }

    @Test
    public void putTest_isFull() throws FailedToPutBanknoteException {
        for (int i = 1; i <= 50; i++) {
            atmCore.put(BanknoteEnum.TEN);
        }

        Assertions.assertThrows(FailedToPutBanknoteException.class, () -> atmCore.put(BanknoteEnum.TEN));
    }

}
