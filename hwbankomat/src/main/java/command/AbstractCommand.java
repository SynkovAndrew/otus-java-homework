package command;

import ui.UI;

public abstract class AbstractCommand {
    protected final UI ui;

    public AbstractCommand(final UI ui) {
        this.ui = ui;
    }
}
