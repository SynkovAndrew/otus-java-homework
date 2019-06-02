package cell;

import banknote.BanknoteEnum;

public class StandardCell extends AbstractCell {
    public static int MAX = 50;

    public StandardCell(final BanknoteEnum banknote) {
        super(banknote, MAX);
    }
}
