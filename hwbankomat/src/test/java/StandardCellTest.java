import banknote.Banknote;
import banknote.BanknoteKindEnum;
import banknote.StandardBanknote;
import cell.CellIsEmptyException;
import cell.CellIsFullException;
import cell.StandardCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

public class StandardCellTest {
    private StandardBanknote tenBanknote;
    private StandardCell tenCell;

    @BeforeEach
    public void beforeEach() {
        tenBanknote = new StandardBanknote(BanknoteKindEnum.TEN);
        tenCell = new StandardCell(tenBanknote);
    }

    @Test
    public void getBanknote_emptyCell() {
        Assertions.assertThrows(CellIsEmptyException.class, () -> tenCell.getBanknote());
    }

    @Test
    public void getBanknote() throws CellIsFullException, CellIsEmptyException {
        for (int i = 0; i < 30; i++) {
            tenCell.putBanknote(tenBanknote);
        }

        Assertions.assertEquals(30, tenCell.getOccupancy());

        final Banknote banknote = tenCell.getBanknote();

        Assertions.assertEquals(BanknoteKindEnum.TEN, banknote.getValue());
        Assertions.assertEquals(29, tenCell.getOccupancy());
    }

    @Test
    public void putBanknote() throws CellIsFullException {
        for (int i = 0; i < 35; i++) {
            tenCell.putBanknote(tenBanknote);
        }

        Assertions.assertEquals(35, tenCell.getOccupancy());
    }

    @Test
    public void putBanknote_isFull() throws CellIsFullException {
        for (int i = 0; i < 50; i++) {
            tenCell.putBanknote(tenBanknote);
        }
        Assertions.assertThrows(CellIsFullException.class, () -> tenCell.putBanknote(tenBanknote));
    }
}
