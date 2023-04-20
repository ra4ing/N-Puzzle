package stud.g01.solver;

import stud.g01.problem.npuzzle.Direction;
import stud.g01.problem.npuzzle.SubBoard;

import java.io.*;
import java.util.*;

public class DatabaseBuilder {

    private final int size;
    private final Queue<SubBoard> frontier = new ArrayDeque<>();

    public DatabaseBuilder(int size) {
        this.size = size;
    }

    public int[] build(SubBoard root) {
        frontier.clear();
        frontier.add(root);

        HashSet<Integer> explored = new HashSet<>();
        explored.add(root.hashCode());

        SubBoard node, child;
        int[] cost = new int[(int) Math.pow(size * size, root.getN())];
        Arrays.fill(cost, -1);

        while (!frontier.isEmpty()) {
            node = frontier.poll();

            for (int i = 0; i < root.getN(); i++) {
                for (Direction d : Direction.FOUR_DIRECTIONS) {
                    if (node.applicable(i, d)) {
                        child = node.move(i, d);
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
        OutputStream os;
        DataOutputStream dos = null;
        try {
            os = new FileOutputStream(filename);
            dos = new DataOutputStream(new BufferedOutputStream(os));
            for (int value : cost) {
                dos.writeByte(value);
            }
        } catch (IOException ioe) {
            System.err.println("Error: Cannot write to file " + filename + ".");
            System.exit(1);
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static void buildPuzzle3() {
        DatabaseBuilder dataBaseBuilder = new DatabaseBuilder(3);
        SubBoard.Point[] points1 = {
                new SubBoard.Point(0, 0, 1),
                new SubBoard.Point(0, 1, 2),
                new SubBoard.Point(0, 2, 3),
                new SubBoard.Point(1, 0, 4)
        };
        SubBoard subBoard1 = new SubBoard(3, 4, points1);
        int[] cost1 = dataBaseBuilder.build(subBoard1);
//        dataBaseBuilder.save(cost1, "db3_0.txt");
        dataBaseBuilder.save(cost1, "resources/db3_0.db");
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
//        dataBaseBuilder.save(cost2, "db3_1.txt");
        dataBaseBuilder.save(cost2, "resources/db3_1.db");
        System.out.println("---------Puzzle3 Part2 Completed---------");
        System.out.println();

        System.out.println("=========Puzzle3 Database Build Completed==========");
    }

    public static void buildPuzzle4() {
        DatabaseBuilder dataBaseBuilder = new DatabaseBuilder(4);
        SubBoard.Point[] points1 = {
                new SubBoard.Point(0, 1, 2),
                new SubBoard.Point(0, 2, 3),
                new SubBoard.Point(0, 3, 4),
        };
        SubBoard subBoard1 = new SubBoard(4, 3, points1);
        int[] cost1 = dataBaseBuilder.build(subBoard1);
        dataBaseBuilder.save(cost1, "resources/db4_0.db");
        System.out.println("---------Puzzle4 Part1 Completed---------");
        System.out.println();

        SubBoard.Point[] points2 = {
                new SubBoard.Point(0, 0, 1),
                new SubBoard.Point(1, 0, 5),
                new SubBoard.Point(1, 1, 6),
                new SubBoard.Point(2, 0, 9),
                new SubBoard.Point(2, 1, 10),
                new SubBoard.Point(3, 0, 13)
        };
        SubBoard subBoard2 = new SubBoard(4, 6, points2);
        int[] cost2 = dataBaseBuilder.build(subBoard2);
        dataBaseBuilder.save(cost2, "resources/db4_1.db");
        System.out.println("---------Puzzle4 Part2 Completed---------");
        System.out.println();

        SubBoard.Point[] points3 = {
                new SubBoard.Point(1, 2, 7),
                new SubBoard.Point(1, 3, 8),
                new SubBoard.Point(2, 2, 11),
                new SubBoard.Point(2, 3, 12),
                new SubBoard.Point(3, 1, 14),
                new SubBoard.Point(3, 2, 15)
        };
        SubBoard subBoard3 = new SubBoard(4, 6, points3);
        int[] cost3 = dataBaseBuilder.build(subBoard3);
        dataBaseBuilder.save(cost3, "resources/db4_2.db");
        System.out.println("---------Puzzle4 Part3 Completed---------");
        System.out.println();


        System.out.println("=========Puzzle4 Database Build Completed==========");
    }

    public static void main(String[] args) {
        buildPuzzle3();
        System.out.println();
        buildPuzzle4();
    }
}
