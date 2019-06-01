package ui;

import java.util.List;

public interface UI {
    void showNotification(String message);

    List<String> getNextCommand();
}
