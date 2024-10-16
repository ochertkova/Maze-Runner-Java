package maze;

import java.util.Objects;

public class Cell {
    int x;
    int y;
    Cell parent;
    private boolean blocked;


    public Cell(int x, int y) {
        this(x, y, true);
    }

    public Cell(int x, int y, boolean blocked) {
        this.x = x;
        this.y = y;
        this.blocked = blocked;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Cell getParent() {
        return parent;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }


    @Override
    public String toString() {
        return getX() + " " + getY() + " " + blocked;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Cell asCell)) return false;

        return Objects.equals(this.x, asCell.getX()) && Objects.equals(this.y, asCell.getY());
    }

    protected boolean isBorder(int h, int w) {
        var isXonBorder = getX() == 0 || getX() == h - 1;
        var isYonBorder = getY() == 0 || getY() == w - 1;
        return isXonBorder || isYonBorder;
    }

    protected boolean isAdjacent(Cell otherCell) {
        int up = this.getY() - 2;
        int right = this.getX() + 2;
        int down = this.getY() + 2;
        int left = this.getX() - 2;

        boolean isUpAdjacent = otherCell.getX() == this.getX() && otherCell.getY() == up;
        if (isUpAdjacent) return true;

        boolean isLeftAdjacent = otherCell.getX() == left && otherCell.getY() == this.getY();
        if (isLeftAdjacent) return true;

        boolean isRightAdjacent = otherCell.getX() == right && otherCell.getY() == this.getY();
        if (isRightAdjacent) return true;

        boolean isDownAdjacent = otherCell.getX() == this.getX() && otherCell.getY() == down;
        return isDownAdjacent;
    }
}