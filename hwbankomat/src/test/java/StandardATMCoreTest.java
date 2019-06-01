import atm.ATMCore;
import atm.FailedToPutBanknoteException;
import atm.FailedToWithdrawSumException;
import atm.StandardATMCore;
import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        atmCore.put(new StandardBanknote(BanknoteKindEnum.TEN));
        atmCore.put(new StandardBanknote(BanknoteKindEnum.TEN));
        Assertions.assertEquals(20, atmCore.getBalance());

        atmCore.put(new StandardBanknote(BanknoteKindEnum.FIFTY));
        Assertions.assertEquals(70, atmCore.getBalance());

        atmCore.put(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED));
        atmCore.put(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED));
        Assertions.assertEquals(270, atmCore.getBalance());

        atmCore.put(new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND));
        atmCore.put(new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND));
        Assertions.assertEquals(10270, atmCore.getBalance());
    }

    @Test
    public void withdrawTest_failed() throws FailedToPutBanknoteException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atmCore.put(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED));
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(50));
    }

    @Test
    public void withdrawTest_failed_sum_too_small() throws FailedToPutBanknoteException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atmCore.put(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED));
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atmCore.withdraw(2));
    }

    @Test
    public void withdrawTest() throws FailedToPutBanknoteException, FailedToWithdrawSumException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 10; i++) {
                atmCore.put(new StandardBanknote(kind));
            }
        }

        List<Banknote> withdrew = atmCore.withdraw(100);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED)), withdrew);
        Assertions.assertEquals(68500, atmCore.getBalance());

        withdrew = atmCore.withdraw(200);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED)), withdrew);
        Assertions.assertEquals(68300, atmCore.getBalance());

        withdrew = atmCore.withdraw(2500);
        Assertions.assertEquals(3, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_HUNDRED)
        ), withdrew);
        Assertions.assertEquals(65800, atmCore.getBalance());

        withdrew = atmCore.withdraw(55550);
        Assertions.assertEquals(17, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_HUNDRED),
                new StandardBanknote(BanknoteKindEnum.FIFTY)

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
            atmCore.put(new StandardBanknote(BanknoteKindEnum.TEN));
            atmCore.put(new StandardBanknote(BanknoteKindEnum.FIVE_HUNDRED));
            atmCore.put(new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND));
        }
        Assertions.assertEquals(15100, atmCore.getBalance());
    }

    @Test
    public void putTest_isFull() throws FailedToPutBanknoteException {
        for (int i = 1; i <= 50; i++) {
            atmCore.put(new StandardBanknote(BanknoteKindEnum.TEN));
        }

        Assertions.assertThrows(FailedToPutBanknoteException.class, () -> atmCore.put(new StandardBanknote(BanknoteKindEnum.TEN)));
    }

}
