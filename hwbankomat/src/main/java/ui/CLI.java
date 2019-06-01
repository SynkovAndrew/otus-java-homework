package ui;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CLI implements UI {
    private final Scanner scanner;

    public CLI() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void showNotification(String message) {
        System.out.println(message);
    }

    @Override
    public List<String> getNextCommand() {
        final String input = scanner.nextLine();
        final String[] parts = input.split(" ");
        return Arrays.asList(parts);
    }
}
