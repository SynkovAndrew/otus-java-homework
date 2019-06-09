import atm.ATM;
import atm.StandardATM;

public class Main {
    public static void main(String[] args) {
        final ATM atm = new StandardATM();
        atm.startUI();
    }
}
