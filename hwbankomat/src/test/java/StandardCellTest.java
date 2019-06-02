import banknote.BanknoteEnum;
import cell.Cell;
import cell.CellIsEmptyException;
import cell.CellIsFullException;
import cell.StandardCell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StandardCellTest {
    private final int INIT_OCCUPANCY = 20;
    private BanknoteEnum tenBanknote = BanknoteEnum.TEN;
    private Cell tenCell;

    @BeforeEach
    public void beforeEach() throws CellIsFullException {
        tenCell = new StandardCell(tenBanknote);
        for (int i = 0; i < INIT_OCCUPANCY; i++) {
            tenCell.putBanknote(tenBanknote);
        }
    }

    @Test
    public void getBanknote_emptyCell() throws CellIsEmptyException {
        for (int i = 1; i <= INIT_OCCUPANCY; i++) {
            tenCell.withdrawBanknote();
            Assertions.assertEquals(INIT_OCCUPANCY - i, tenCell.getOccupancy());
        }
        Assertions.assertTrue(tenCell.isEmpty());
        Assertions.assertThrows(CellIsEmptyException.class, () -> tenCell.withdrawBanknote());
    }

    @Test
    public void getBanknote() throws CellIsFullException, CellIsEmptyException {
        for (int i = 1; i <= 10; i++) {
            tenCell.putBanknote(tenBanknote);
            Assertions.assertEquals(INIT_OCCUPANCY + i, tenCell.getOccupancy());
        }
        final BanknoteEnum banknote = tenCell.withdrawBanknote();
        Assertions.assertEquals(BanknoteEnum.TEN, banknote);
        Assertions.assertEquals(29, tenCell.getOccupancy());
    }

    @Test
    public void putBanknote() throws CellIsFullException {
        for (int i = 1; i < +15; i++) {
            tenCell.putBanknote(tenBanknote);
            Assertions.assertFalse(tenCell.isEmpty());
            Assertions.assertEquals(INIT_OCCUPANCY + i, tenCell.getOccupancy());
        }
    }

    @Test
    public void putBanknote_isFull() throws CellIsFullException {
        for (int i = 1; i <= 30; i++) {
            tenCell.putBanknote(tenBanknote);
            Assertions.assertEquals(INIT_OCCUPANCY + i, tenCell.getOccupancy());

        }
        Assertions.assertThrows(CellIsFullException.class, () -> tenCell.putBanknote(tenBanknote));
    }

    @Test
    public void getOccupancySum() {
        Assertions.assertEquals(INIT_OCCUPANCY, tenCell.getOccupancy());
    }

    @Test
    public void getBanknoteKind() {
        Assertions.assertEquals(BanknoteEnum.TEN, tenCell.getBanknoteKind());
    }

    @Test
    public void getContentSum() {
        Assertions.assertEquals(INIT_OCCUPANCY * 10, tenCell.getContentSum());
    }
}
