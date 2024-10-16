package maze;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Maze {
    List<Cell> mazeArray;
    Integer h;
    Integer w;
    private Cell entrance;
    private Cell exit;

    public Maze(List<Cell> maze, Integer h, Integer w) {
        this.mazeArray = maze;
        this.h = h;
        this.w = w;
    }

    public List<Cell> getMazeArray() {
        return mazeArray;
    }

    public void setMaze(ArrayList<Cell> mazeArray) {
        this.mazeArray = mazeArray;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }


    private Cell getEntranceCell() {
        return this.entrance;
    }

    private Cell getExitCell() {
        return this.exit;
    }

    public void saveMaze(String fullFileName) {
        File file = new File(fullFileName);
        try (FileWriter writer = new FileWriter(file)) {
            String firstLine = "%d %d".formatted(this.h, this.w);
            writer.write(firstLine);
            writer.write(System.getProperty("line.separator"));
            System.out.println(firstLine);
            for (Cell cell : this.getMazeArray()) {
                writer.write(cell.toString());
                writer.write(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Maze loadMaze(String fullFileName) {
        String[] fileContentArray;
        try (BufferedReader br = new BufferedReader(new FileReader(fullFileName))) {
            List<String> fileContents = Files.readAllLines(Path.of(fullFileName));
            fileContentArray = fileContents.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        String firstLine = fileContentArray[0];
        System.out.println("First line is:");
        System.out.println(firstLine);
        String[] firstLineArray = firstLine.split(" ");
        int h = Integer.parseInt(firstLineArray[0]);
        int w = Integer.parseInt(firstLineArray[1]);

        List<Cell> cells = Arrays
                .stream(fileContentArray).skip(1)
                .map(Maze::lineToCell)
                .collect(Collectors.toList());
        Maze maze = new Maze(cells, h, w);
        maze.exit = maze.setNewExit(h, w, cells);
        maze.entrance = maze.setNewEntrance(h, w, cells);
        return maze;
    }

    private static Cell lineToCell(String string) {
        String[] stringArray = string.split(" ");
        int stringH = Integer.parseInt(stringArray[0]);
        int stringW = Integer.parseInt(stringArray[1]);
        boolean stringBlocked = Boolean.parseBoolean(stringArray[2]);
        Cell cell = new Cell(stringH, stringW, stringBlocked);
        return cell;
    }


    public static Maze generateMaze(int h, int w) {
        ArrayList<Cell> mazeArray = new ArrayList<>();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Cell cell = new Cell(i, j);
                mazeArray.add(cell);
            }
        }
        Cell startCell = getStartCell(mazeArray, h, w);
        //Generating maze with Prim's algorithm
        ArrayList<Cell> frontierArray = findFrontierCells(startCell, mazeArray, w, h);
        do {
            Cell randomFrontierCell = frontierArray.get((int) (Math.random() * (frontierArray.size() - 1)));
            randomFrontierCell.setBlocked(false);
            setPassage(randomFrontierCell, mazeArray);
            ArrayList<Cell> newFrontier = findFrontierCells(randomFrontierCell, mazeArray, w, h);
            for (Cell cell : newFrontier) {
                if (!frontierArray.contains(cell) && cell.isBlocked()) {
                    frontierArray.add(cell);
                }
            }
            frontierArray.remove(randomFrontierCell);
        } while (!frontierArray.isEmpty());
        Maze maze = new Maze(mazeArray, h, w);
        //maze.setEntrance(h, w, mazeArray);
        // maze.setExit(h, w, mazeArray);
        maze.exit = maze.setNewExit(h, w, mazeArray);
        maze.entrance = maze.setNewEntrance(h, w, mazeArray);
        return maze;
    }

    private static Cell getStartCell(ArrayList<Cell> mazeArray, int h, int w) {
        Cell startCell;
        do {// get random cell except for border cells
            startCell = mazeArray.get((int) (Math.random() * (mazeArray.size() - 1)));
        } while (startCell.isBorder(h, w));
        startCell.setBlocked(false);
        return startCell;
    }

    private Cell findCell(int x, int y) {
        for (Cell cell : this.getMazeArray()) {
            if (cell.getX() == x && cell.getY() == y) {
                return cell;
            }
        }
        return null;
    }

    private Cell findIfNotBlocked(int x, int y) {
        Cell cell = findCell(x, y);
        assert cell != null;
        return cell.isBlocked() ? null : cell;
    }

    private static void setPassage(Cell randomFrontierCell, List<Cell> mazeArray) {
        for (Cell cell : mazeArray) {
            boolean matchingX = cell.getX() == (randomFrontierCell.getParent().getX() + randomFrontierCell.getX()) / 2;
            boolean matchingY = cell.getY() == (randomFrontierCell.getY() + randomFrontierCell.getParent().getY()) / 2;
            if (matchingX && matchingY) {
                cell.setBlocked(false);
            }
        }
    }


    private Cell setNewEntrance(int h, int w,   List<Cell> mazeArray) {
        List<Cell> entranceColumn = mazeArray.stream()
                .filter(cell -> cell.getX() == 0)
                .sorted(Comparator.comparing(Cell::getY))
                .toList();

        int nearestOpenX = 2;

        Optional<Cell> neighborOfEntrance = entranceColumn.stream()
                .map(cell -> findCell(nearestOpenX, cell.getY()))
                .filter(cell -> !cell.isBlocked())
                .findFirst();

        Cell neighbor = neighborOfEntrance.get();
        Cell entranceCell = entranceColumn.get(neighbor.getY());
        entranceCell.setBlocked(false);
        entranceCell.setParent(neighbor);
        setPassage(entranceCell, mazeArray);
        return entranceCell;
    }


    private Cell setNewExit(int h, int w, List<Cell> mazeArray) {
        List<Cell> exitColumn = mazeArray.stream()
                .filter(cell -> cell.getX() == w - 1)
                .sorted(Comparator.comparing(Cell::getY))
                .toList();
        int nearestOpenX = w - 3;

        Optional<Cell> neighborOfExit = exitColumn.stream()
                .map(cell -> findCell(nearestOpenX, exitColumn.size() - cell.getY() - 1))
                .filter(cell -> !cell.isBlocked())
                .sorted(Comparator.comparing(Cell::getY).reversed())
                .findFirst();

        Cell neighbor = neighborOfExit.get();
        Cell exitCell = exitColumn.get(neighbor.getY());
        exitCell.setBlocked(false);
        exitCell.setParent(neighbor);
        setPassage(exitCell, mazeArray);
        return exitCell;
    }

    public Stack<Cell> findEscape() {
        Stack<Cell> escape = this.traverseMaze();
        System.out.println("Traversed the maze!");
        return escape;
    }


    private Stack<Cell> traverseMaze() {
        Stack<Cell> path = traverseRec(getEntranceCell(), getExitCell(), new HashSet<>());

        return path;
    }

    private Stack<Cell> traverseRec(Cell currentCell, Cell targetCell, Set<Cell> visited) {
        if (targetCell.equals(currentCell)) {
            Stack<Cell> path = new Stack<>();
            path.push(currentCell);
            return path;
        }
        List<Cell> newNeighbors = findNewNeighbors(currentCell, visited);

        for (Cell neighbor : newNeighbors) {
            visited.add(neighbor);
            Stack<Cell> possiblePath = traverseRec(neighbor, targetCell, visited);
            if (possiblePath != null) {
                possiblePath.push(currentCell);
                return possiblePath;
            }
        }
        // this cell is dead end
        return null;
    }

    private List<Cell> findNewNeighbors(Cell cell, Set<Cell> visited) {
        List<Cell> neighbors = new ArrayList<>();

        int up = cell.getY() - 1;
        int right = cell.getX() + 1;
        int down = cell.getY() + 1;
        int left = cell.getX() - 1;

        if (up >= 0) {
            Cell upCell = findIfNotBlocked(cell.getX(), up);
            if (upCell != null && !visited.contains(upCell)) {
                neighbors.add(upCell);
            }
        }

        if (right <= this.getW()) {
            Cell rightCell = findIfNotBlocked(right, cell.getY());
            if (rightCell != null && !visited.contains(rightCell)) {
                neighbors.add(rightCell);
            }
        }

        if (down < this.getH()) {
            Cell downCell = findIfNotBlocked(cell.getX(), down);
            if (downCell != null && !visited.contains(downCell)) {
                neighbors.add(downCell);
            }
        }

        if (left >= 0) {
            Cell leftCell = findIfNotBlocked(left, cell.getY());
            if (leftCell != null && !visited.contains(leftCell)) {
                neighbors.add(leftCell);
            }
        }
        return neighbors;
    }

    private static ArrayList<Cell> findFrontierCells(Cell currentCell, ArrayList<Cell> mazeArray, final int w, final int h) {
        ArrayList<Cell> frontierArray = new ArrayList<>();
        mazeArray.stream()
                .filter(cell -> cell.isBlocked() && !(cell.isBorder(h, w)))
                .filter(currentCell::isAdjacent)
                .forEach(cell -> {
                    frontierArray.add(cell);
                    cell.setParent(currentCell);
                });

        return frontierArray;
    }
}
