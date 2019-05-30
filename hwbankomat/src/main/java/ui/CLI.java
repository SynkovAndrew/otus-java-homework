package ui;

import atm.ATM;
import atm.FailedToPutBanknoteException;
import atm.FailedToWithdrawSumException;
import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;

import java.util.List;
import java.util.Scanner;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.joining;

public class CLI implements UI {
    private final ATM atm;
    private final Scanner scanner;


    public CLI(final ATM atm) {
        this.atm = atm;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        while (true) {
            System.out.print(getMenuMessage());

            final String input = scanner.nextLine();
            final String[] parts = input.split(" ");

            if (parts.length == 0) {
                continue;
            }

            switch (parts[0]) {
                case ("balance"):
                    System.out.println("\nBalance: " + atm.getBalance() + "\n");
                    break;
                case ("put"):
                    final BanknoteKindEnum kind = BanknoteKindEnum.findByCode(parts[1]);
                    if (isNull(kind)) {
                        System.out.println("Error. ATM doesn't support such kind of banknote.");
                    } else {
                        try {
                            atm.put(new StandardBanknote(kind));
                            System.out.println("\n\"" + parts[1] + "\" is put successfully. Balance: " + atm.getBalance() + "\n");
                        } catch (FailedToPutBanknoteException e) {
                            System.out.println(e.getMessage());
                        }
                    }

                    break;
                case ("withdraw"):
                    try {
                        final List<Banknote> withdraw = atm.withdraw(Integer.valueOf(parts[1]));
                        System.out.println("\nWithdrew: " + withdraw.stream().map(Banknote::toString).collect(joining(", ")));
                        System.out.println("Balance: " + atm.getBalance() + "\n");
                    } catch (FailedToWithdrawSumException e) {
                        System.out.println(e.getMessage());

                    }
                    break;
                case ("exit"):
                    System.exit(0);
                    break;
            }
        }

    }

    private static String getMenuMessage() {
        return "Please input one of following options: \n\n" +
                "balance \n" +
                "put {TEN, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED, ONE_THOUSAND, FIVE_THOUSAND} \n" +
                "withdraw {number} \n" +
                "exit \n\n";
    }
}
