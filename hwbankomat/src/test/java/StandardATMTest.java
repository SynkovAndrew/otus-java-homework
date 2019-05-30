import atm.ATM;
import atm.FailedToPutBanknoteException;
import atm.FailedToWithdrawSumException;
import atm.StandardATM;
import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class StandardATMTest {
    private ATM atm;

    @BeforeEach
    public void beforeEach() {
        atm = new StandardATM();
    }

    @Test
    public void putBanknoteTest() throws FailedToPutBanknoteException {
        Assertions.assertEquals(0, atm.getBalance());

        atm.put(new StandardBanknote(BanknoteKindEnum.TEN));
        atm.put(new StandardBanknote(BanknoteKindEnum.TEN));
        Assertions.assertEquals(20, atm.getBalance());

        atm.put(new StandardBanknote(BanknoteKindEnum.FIFTY));
        Assertions.assertEquals(70, atm.getBalance());

        atm.put(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED));
        atm.put(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED));
        Assertions.assertEquals(270, atm.getBalance());

        atm.put(new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND));
        atm.put(new StandardBanknote(BanknoteKindEnum.FIVE_THOUSAND));
        Assertions.assertEquals(10270, atm.getBalance());
    }

    @Test
    public void withdrawTest_failed() throws FailedToPutBanknoteException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atm.put(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED));
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atm.withdraw(50));
    }

    @Test
    public void withdrawTest_failed_sum_too_small() throws FailedToPutBanknoteException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 5; i++) {
                atm.put(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED));
            }
        }

        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atm.withdraw(2));
    }

    @Test
    public void withdrawTest() throws FailedToPutBanknoteException, FailedToWithdrawSumException {
        for (BanknoteKindEnum kind : BanknoteKindEnum.values()) {
            for (int i = 1; i <= 10; i++) {
                atm.put(new StandardBanknote(kind));
            }
        }

        List<Banknote> withdrew = atm.withdraw(100);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(new StandardBanknote(BanknoteKindEnum.ONE_HUNDRED)), withdrew);
        Assertions.assertEquals(68500, atm.getBalance());

        withdrew = atm.withdraw(200);
        Assertions.assertEquals(1, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(new StandardBanknote(BanknoteKindEnum.TWO_HUNDRED)), withdrew);
        Assertions.assertEquals(68300, atm.getBalance());

        withdrew = atm.withdraw(2500);
        Assertions.assertEquals(3, withdrew.size());
        Assertions.assertIterableEquals(newArrayList(
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND),
                new StandardBanknote(BanknoteKindEnum.FIVE_HUNDRED)
        ), withdrew);
        Assertions.assertEquals(65800, atm.getBalance());

        withdrew = atm.withdraw(55550);
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
        Assertions.assertEquals(10250, atm.getBalance());
    }


    @Test
    public void withdraw_empty() {
        Assertions.assertThrows(FailedToWithdrawSumException.class, () -> atm.withdraw(100));
    }

    @Test
    public void getBalanceTest() throws FailedToPutBanknoteException {
        for (int i = 1; i <= 10; i++) {
            atm.put(new StandardBanknote(BanknoteKindEnum.TEN));
            atm.put(new StandardBanknote(BanknoteKindEnum.FIVE_HUNDRED));
            atm.put(new StandardBanknote(BanknoteKindEnum.ONE_THOUSAND));
        }
        Assertions.assertEquals(15100, atm.getBalance());
    }

    @Test
    public void putTest_isFull() throws FailedToPutBanknoteException {
        for (int i = 1; i <= 50; i++) {
            atm.put(new StandardBanknote(BanknoteKindEnum.TEN));
        }

        Assertions.assertThrows(FailedToPutBanknoteException.class, () -> atm.put(new StandardBanknote(BanknoteKindEnum.TEN)));
    }

}
