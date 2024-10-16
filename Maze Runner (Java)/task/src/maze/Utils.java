package maze;

import java.util.Comparator;
import java.util.List;

public class Utils {
    public static void sortCells(List<Cell> cells) {
        cells.sort(Comparator.comparing(Cell::getX)
                .thenComparing(Cell::getY));
    }
}
