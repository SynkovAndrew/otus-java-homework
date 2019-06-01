import handler.ATM;
import handler.StandardATM;

public class Main {
    public static void main(String[] args) {
        final ATM atm = new StandardATM();
        atm.start();
    }
}
