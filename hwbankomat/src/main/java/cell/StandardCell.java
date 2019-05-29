package cell;

import banknote.Banknote;

public class StandardCell extends AbstractCell {
    public static int MAX = 50;

    public StandardCell(final Banknote banknoteType) {
        super(banknoteType, MAX);
    }
}
