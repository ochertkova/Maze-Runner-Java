package maze;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    private static final String path = System.getProperty("user.dir") + File.separator + "Maze Runner (Java)" + File.separator + "task" + File.separator + "src" + File.separator + "maze";

    //private static final String fileName = dbFilePath + File.separator + "db.json";

    private static final int EXIT = 0;

    public static void main(String[] args) throws IOException {
        Maze currentMaze = null;
        Stack<Cell> currentEscape;
        int option = -1;
        do {
            option = showMenu(currentMaze);
            String fileName;
            switch (option) {
                case 1:
                    Scanner scan = new Scanner(System.in);
                    System.out.println("Please, enter the size of a maze");
                    int h = scan.nextInt();
                    int w = h;
                    currentMaze = Maze.generateMaze(h, w);
                    displayMaze(currentMaze);
                    break;
                case 2:
                    scan = new Scanner(System.in);
                    System.out.println("Please, enter the file name");
                    fileName = scan.next();
                    currentMaze = Maze.loadMaze(getFullPath(fileName));
                    break;
                case 3:
                    scan = new Scanner(System.in);
                    System.out.println("Please, enter the file name");
                    fileName = scan.next();
                    System.out.println(path);
                    assert currentMaze != null;
                    currentMaze.saveMaze(getFullPath(fileName));
                    break;
                case 4:
                    assert currentMaze != null;
                    displayMaze(currentMaze);
                    break;
                case 5:
                    assert currentMaze != null;
                    currentEscape = currentMaze.findEscape();
                    displayMaze(currentMaze, currentEscape);
                case 0:
                    exitMenu();
                    break;
                default:
                    System.out.println("Incorrect option. Please try again");
            }
        } while (option != EXIT);
    }

    private static String getFullPath(String fileName) {
        //return path + File.separator + fileName;
        return fileName;
    }

    private static int showMenu(Maze currentMaze) {
        Scanner scan = new Scanner(System.in);
        int option;
        //#TODO Menu display depends on whether the current maze exists in files
        System.out.println("=== Menu ===");
        System.out.println("1. Generate a new maze");
        System.out.println("2. Load a maze");
        if (currentMaze != null) {
            System.out.println("3. Save the maze");
            System.out.println("4. Display the maze");
            System.out.println("5. Find the escape");
        }
        System.out.println("0. Exit");
        option = scan.nextInt();
        return option;
    }

    private static void exitMenu() {

    }

    private static String[][] fillMazeMatrix(Maze maze, Stack<Cell> escape) {
        String[][] mazeMatrix = new String[maze.getH()][maze.getW()];
        List<Cell> mazeArray = maze.getMazeArray();
        for (Cell cell : mazeArray) {
            for (int i = 0; i < maze.getW(); i++) {
                for (int j = 0; j < maze.getH(); j++) {
                    if (i == cell.getY() && j == cell.getX()) {
                        if (cell.isBlocked()) {
                            mazeMatrix[i][j] = "\u2588\u2588";
                        } else if (escape != null && escape.contains(cell)) {
                            mazeMatrix[i][j] = "//";
                        } else {
                            mazeMatrix[i][j] = "  ";
                        }
                    }
                }
            }
        }
        return mazeMatrix;
    }

    public static void displayMaze(Maze maze, Stack<Cell> escape) {
        escape.forEach(System.out::println);

        String[][] filledMaze = fillMazeMatrix(maze, escape);
        for (int i = 0; i < filledMaze.length; i++) {
            for (int j = 0; j < filledMaze[i].length; j++) {
                System.out.print(filledMaze[i][j]);
            }
            System.out.println();
        }
    }

    public static void displayMaze(Maze maze) {
        displayMaze(maze, new Stack<>());
    }
}

