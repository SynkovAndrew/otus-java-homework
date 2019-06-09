package command;

import core.ATMCore;
import ui.UI;

public abstract class AbstractCommand {
    protected final UI ui;
    protected final ATMCore atmCore;

    public AbstractCommand(final UI ui, final ATMCore atmCore) {
        this.ui = ui;
        this.atmCore = atmCore;
    }
}
