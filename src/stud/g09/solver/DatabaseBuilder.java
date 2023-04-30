package stud.g09.solver;

import stud.g09.problem.npuzzle.Direction;
import stud.g09.problem.npuzzle.SubBoard;

import java.io.*;
import java.util.*;

/**
 * DatabaseBuilder 类用于构建数独问题的数据库，用于求解过程中加速启发式搜索。
 */
public class DatabaseBuilder {

    private final int size; // 问题的大小（如 3x3 或 4x4）
    private final Queue<SubBoard> frontier = new ArrayDeque<>(); // 用于搜索的边界队列

    /**
     * 构造函数，初始化 DatabaseBuilder 对象。
     * @param size 数独问题的大小。
     */
    public DatabaseBuilder(int size) {
        this.size = size;
    }

    /**
     * 从给定的 root 节点开始，构建子数独问题的数据库。
     * @param root 子数独问题的根节点。
     * @return 包含不同子问题的花费的数组。
     */
    public int[] build(SubBoard root) {

        // 使用广度优先搜索（BFS）算法遍历子数独问题树，首先将根节点加入 frontier 队列。
        frontier.clear();
        frontier.add(root);

        HashSet<Integer> explored = new HashSet<>();
        explored.add(root.hashCode());

        SubBoard node, child;
        int[] cost = new int[(int) Math.pow(size * size, root.getN())];
        Arrays.fill(cost, -1);

        // 在循环中，从 frontier 队列中取出一个节点，然后遍历其所有可行的子节点
        while (!frontier.isEmpty()) {
            node = frontier.poll();

            for (int i = 0; i < root.getN(); i++) {
                for (Direction d : Direction.FOUR_DIRECTIONS) {
                    if (node.applicable(i, d)) {
                        child = node.move(i, d);

                        // 对于每个可行的子节点，如果它尚未被探索，则将其加入 frontier 队列，并将其花费存储在 cost 数组中。
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

    /**
     * 将子数独问题的成本数据保存到指定的文件中。
     * @param cost 子数独问题的成本数组。
     * @param filename 用于保存数据的文件名。
     */
    private void save(int[] cost, String filename) {

        // 将子数独问题的成本数据写入指定的文件。这里使用 DataOutputStream 将数据写入文件。
        OutputStream os;
        DataOutputStream dos = null;
        try {
            os = new FileOutputStream(filename);
            dos = new DataOutputStream(new BufferedOutputStream(os));

            // 在循环中，将 cost 数组中的每个值作为字节写入文件。
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
        // 先创建一个 DatabaseBuilder 对象，然后创建]多个 SubBoard 对象，代表不同的子问题。
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

        System.out.println("=========Puzzle3 Database Build Completed==========\n");
    }

    public static void buildPuzzle4() {
        // 先创建一个 DatabaseBuilder 对象，然后创建多个 SubBoard 对象，代表不同的子问题。
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


        System.out.println("=========Puzzle4 Database Build Completed==========\n");
    }

    public static void main(String[] args) {
        buildPuzzle3(); // 构建三阶模式数据库
        buildPuzzle4(); // 构建四阶模式数据库
    }
}
