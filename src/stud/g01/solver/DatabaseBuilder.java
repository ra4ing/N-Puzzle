package stud.g01.solver;

import stud.g01.problem.npuzzle.Direction;
import stud.g01.problem.npuzzle.SubBoard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class DatabaseBuilder {

    private final int size;
    private final int n;
    private final Queue<SubBoard> frontier = new ArrayDeque<>();

    public DatabaseBuilder(int size, int n) {
        this.size = size;
        this.n = n;
    }

    public int[] build(SubBoard root) {
        frontier.clear();
        frontier.add(root);

        HashSet<Integer> explored = new HashSet<>();
        explored.add(root.hashCode());

        SubBoard node, child;
        int[] cost = new int[(int) Math.pow(size * size, n)];

        while (!frontier.isEmpty()) {
            node = frontier.poll();

            for (int i=0; i<root.getN(); i++) {
                for (Direction d : Direction.FOUR_DIRECTIONS) {
                    if (node.applicable(i,d)) {
                        child = node.move(i,d);
                        if (!explored.contains(child.hashCode())) {
                            frontier.add(child);
                            explored.add(child.hashCode());
                            cost[child.hashCode()] = child.getPathCost();
                        }
                    }
                }
            }
        }

        explored.clear();
        return cost;
    }

    private void save(int[] cost, String filename) {
        Path filePath = Paths.get("resources", filename);

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE)) {
                for (int val : cost) {
                    writer.write(val + "\t");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void buildPuzzle3() {
        DatabaseBuilder dataBaseBuilder = new DatabaseBuilder(3, 4);
        SubBoard.Point[] points1 = {
                new SubBoard.Point(0, 0, 1),
                new SubBoard.Point(0, 1, 2),
                new SubBoard.Point(0, 2, 3),
                new SubBoard.Point(1, 0, 4)
        };
        SubBoard subBoard1 = new SubBoard(3, 4, points1);
        int[] cost1 = dataBaseBuilder.build(subBoard1);
        dataBaseBuilder.save(cost1, "db3(1).txt");
        System.out.println("---------Puzzle3 Part1 Completed---------");
        System.out.println();

        SubBoard.Point[] points2 = {
                new SubBoard.Point(1, 1, 5),
                new SubBoard.Point(1, 2, 6),
                new SubBoard.Point(2, 0, 7),
                new SubBoard.Point(2, 1, 8)
        };
        SubBoard subBoard2 = new SubBoard(3, 4, points2);
        int[] cost2 = dataBaseBuilder.build(subBoard2);
        dataBaseBuilder.save(cost2, "db3(2).txt");
        System.out.println("---------Puzzle3 Part2 Completed---------");
        System.out.println();

        System.out.println("=========Puzzle3 Database Build Completed==========");
    }

    public static void buildPuzzle4() {
        DatabaseBuilder dataBaseBuilder = new DatabaseBuilder(4, 6);
        SubBoard.Point[] points1 = {
                new SubBoard.Point(0, 0, 1),
                new SubBoard.Point(1, 0, 5),
                new SubBoard.Point(1, 1, 6),
                new SubBoard.Point(2, 0, 9),
                new SubBoard.Point(2, 1, 10),
                new SubBoard.Point(3, 0, 13)
        };
        SubBoard subBoard1 = new SubBoard(4, 6, points1);
        int[] cost1 = dataBaseBuilder.build(subBoard1);
        dataBaseBuilder.save(cost1, "db4(1).txt");
        System.out.println("---------Puzzle4 Part1 Completed---------");
        System.out.println();

        SubBoard.Point[] points2 = {
                new SubBoard.Point(1, 2, 7),
                new SubBoard.Point(1, 3, 8),
                new SubBoard.Point(2, 2, 11),
                new SubBoard.Point(2, 3, 12),
                new SubBoard.Point(3, 1, 14),
                new SubBoard.Point(3, 2, 15)
        };
        SubBoard subBoard2 = new SubBoard(4, 6, points2);
        int[] cost2 = dataBaseBuilder.build(subBoard2);
        dataBaseBuilder.save(cost2, "db4(2).txt");
        System.out.println("---------Puzzle4 Part2 Completed---------");
        System.out.println();

        SubBoard.Point[] points3 = {
                new SubBoard.Point(0, 1, 2),
                new SubBoard.Point(0, 2, 3),
                new SubBoard.Point(0, 3, 4),
        };
        SubBoard subBoard3 = new SubBoard(4, 3, points3);
        int[] cost3 = dataBaseBuilder.build(subBoard3);
        dataBaseBuilder.save(cost3, "db4(3).txt");
        System.out.println("---------Puzzle4 Part1 Completed---------");
        System.out.println();


        System.out.println("=========Puzzle4 Database Build Completed==========");
    }

    public static void main(String[] args) {
        buildPuzzle3();
        System.out.println();
        buildPuzzle4();
    }
}
