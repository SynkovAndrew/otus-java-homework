import atm.ATM;
import atm.StandardATM;
import banknote.BanknoteEnum;
import com.google.common.reflect.Reflection;
import core.ATMCore;
import core.FailedToPutBanknoteException;
import department.StandardATMDepartment;

import java.lang.reflect.Field;

import static com.google.common.collect.Lists.newArrayList;

public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, FailedToPutBanknoteException {
        final ATM atm1 = new StandardATM(1);
        final ATM atm2 = new StandardATM(1);

        final Field atmCoreField = StandardATM.class.getDeclaredField("atmCore");
        atmCoreField.setAccessible(true);

        final var atmCore1 = (ATMCore) atmCoreField.get(atm1);
        final var atmCore2 = (ATMCore) atmCoreField.get(atm2);

        atmCore1.putMultiple(newArrayList(BanknoteEnum.FIVE_THOUSAND, BanknoteEnum.TEN));
        atmCore2.putMultiple(newArrayList(BanknoteEnum.ONE_THOUSAND, BanknoteEnum.TEN));

        final var department = new StandardATMDepartment(newArrayList(atm1, atm2));
        department.startUI();
    }
}
