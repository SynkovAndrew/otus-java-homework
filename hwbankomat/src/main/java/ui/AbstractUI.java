package ui;

import java.util.List;

public abstract class AbstractUI {
    protected final UI ui;

    protected AbstractUI(final UI ui) {
        this.ui = ui;
    }

    public void startUI() {
        while (true) {
            ui.showNotification(getMenuMessage());

            final var nextCommand = ui.getNextCommand();

            if (nextCommand.size() == 0) {
                continue;
            }

            runCommand(nextCommand);
        }
    }

    protected abstract String getMenuMessage();

    protected abstract void runCommand(final List<String> command);
}
