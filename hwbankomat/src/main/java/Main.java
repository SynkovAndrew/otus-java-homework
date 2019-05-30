import atm.ATM;
import atm.StandardATM;
import ui.CLI;
import ui.UI;

public class Main {
    public static void main(String[] args) {
        final ATM atm = new StandardATM();
        ATMHelper.fullfillATM(atm);

        final UI ui = new CLI(atm);
        ui.run();
    }
}
