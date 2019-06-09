import core.ATMCore;
import core.FailedToPutBanknoteException;
import core.FailedToWithdrawSumException;
import core.StandardATMCore;
import banknote.BanknoteEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class StandardATMCoreTest {
    private final int INIT_BALANCE = 0;
    private final int MAX_NUMBER_OF_BANKNOTES = 50;
    private ATMCore atmCore;

    @BeforeEach
    public void beforeEach() {
        atmCore = new StandardATMCore();
    }

    @Test
    public void putBanknoteTest() throws FailedToPutBanknoteException {
        Assertions.assertEquals(INIT_BALANCE, atmCore.getBalance());

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
        Assertions.assertEquals(INIT_BALANCE, atmCore.getBalance());
        atmCore.putMultiple(getFiveThousandFourHundredSeventy());
        Assertions.assertEquals(5470, atmCore.getBalance());
    }

    @Test
    public void withdrawTest_failed() throws FailedToPutBanknoteException {
        fulfillATM(10, newArrayList(BanknoteEnum.TWO_HUNDRED));
        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(50));
    }

    @Test
    public void withdrawTest_not_enough_money() throws FailedToPutBanknoteException {
        fulfillATM(10, newArrayList(BanknoteEnum.FIVE_THOUSAND));
        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(40050));
        Assertions.assertEquals(50000, atmCore.getBalance());

    }

    @Test
    public void withdrawTest_failed_sum_too_small() throws FailedToPutBanknoteException {
        fulfillATM(5, newArrayList(BanknoteEnum.TWO_HUNDRED));
        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(2));
        Assertions.assertEquals(1000, atmCore.getBalance());

    }

    @Test
    public void withdrawTest() throws FailedToPutBanknoteException, FailedToWithdrawSumException {
        fulfillATM(10, newArrayList(BanknoteEnum.values()));

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
        fulfillATM(10, newArrayList(BanknoteEnum.TEN, BanknoteEnum.FIVE_HUNDRED, BanknoteEnum.ONE_THOUSAND));
        Assertions.assertEquals(15100, atmCore.getBalance());
    }

    @Test
    public void putTest_isFull() throws FailedToPutBanknoteException {
        fulfillATM(MAX_NUMBER_OF_BANKNOTES, newArrayList(BanknoteEnum.TEN));
        Assertions.assertThrows(FailedToPutBanknoteException.class, () -> atmCore.put(BanknoteEnum.TEN));

    }

    @Test
    public void putMultipleTest_isFull() throws FailedToPutBanknoteException {
        fulfillATM(MAX_NUMBER_OF_BANKNOTES - 1, newArrayList(BanknoteEnum.TEN));
        Assertions.assertThrows(FailedToPutBanknoteException.class, () -> atmCore.putMultiple(
                newArrayList(BanknoteEnum.FIVE_THOUSAND, BanknoteEnum.ONE_THOUSAND, BanknoteEnum.TEN, BanknoteEnum.TEN)));
        Assertions.assertEquals((MAX_NUMBER_OF_BANKNOTES - 1) * BanknoteEnum.TEN.getNominal(), atmCore.getBalance());
    }

    @Test
    public void restoreInitState_empty() throws FailedToPutBanknoteException {
        fulfillATM(20, newArrayList(BanknoteEnum.TEN, BanknoteEnum.TWO_HUNDRED, BanknoteEnum.FIVE_THOUSAND));
        atmCore.restoreInitialState();
        Assertions.assertEquals(0, atmCore.getBalance());
    }

    @Test
    public void restoreInitState() throws FailedToPutBanknoteException {
        final ATMCore atmCore = new StandardATMCore(20);
        atmCore.putMultiple(newArrayList(BanknoteEnum.TEN, BanknoteEnum.TWO_HUNDRED, BanknoteEnum.FIVE_THOUSAND));
        Assertions.assertEquals(142410, atmCore.getBalance());
        atmCore.restoreInitialState();
        Assertions.assertEquals(137200, atmCore.getBalance());
    }

    private void fulfillATM(final int number, final List<BanknoteEnum> banknotes) throws FailedToPutBanknoteException {
        for (BanknoteEnum banknote : banknotes) {
            for (int i = 1; i <= number; i++) {
                atmCore.put(banknote);
            }
        }
    }

    private List<BanknoteEnum> getFiveThousandFourHundredSeventy() {
        return newArrayList(BanknoteEnum.TEN, BanknoteEnum.TEN, BanknoteEnum.FIFTY, BanknoteEnum.FIFTY, BanknoteEnum.FIFTY,
                BanknoteEnum.ONE_HUNDRED, BanknoteEnum.ONE_HUNDRED, BanknoteEnum.ONE_HUNDRED, BanknoteEnum.FIVE_THOUSAND);
    }
}
