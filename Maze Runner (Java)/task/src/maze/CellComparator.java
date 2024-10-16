package maze;

import java.util.Comparator;

public class CellComparator implements Comparator<Cell> {

    @Override
    public int compare(Cell c1, Cell c2) {
        int result = Integer.compare(c1.getX(), c2.getY());
        if (result != 0) {return -result;}
        return Integer.compare(c1.getY(), c2.getY());
    }
}
