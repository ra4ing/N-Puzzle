package stud.g09.solver;

import stud.g09.problem.npuzzle.Direction;
import stud.g09.problem.npuzzle.SubBoard;

import java.io.*;
import java.util.*;

/**
 * DatabaseBuilder �����ڹ���������������ݿ⣬�����������м�������ʽ������
 */
public class DatabaseBuilder {

    private final int size; // ����Ĵ�С���� 3x3 �� 4x4��
    private final Queue<SubBoard> frontier = new ArrayDeque<>(); // ���������ı߽����

    /**
     * ���캯������ʼ�� DatabaseBuilder ����
     * @param size ��������Ĵ�С��
     */
    public DatabaseBuilder(int size) {
        this.size = size;
    }

    /**
     * �Ӹ����� root �ڵ㿪ʼ��������������������ݿ⡣
     * @param root ����������ĸ��ڵ㡣
     * @return ������ͬ������Ļ��ѵ����顣
     */
    public int[] build(SubBoard root) {

        // ʹ�ù������������BFS���㷨���������������������Ƚ����ڵ���� frontier ���С�
        frontier.clear();
        frontier.add(root);

        HashSet<Integer> explored = new HashSet<>();
        explored.add(root.hashCode());

        SubBoard node, child;
        int[] cost = new int[(int) Math.pow(size * size, root.getN())];
        Arrays.fill(cost, -1);

        // ��ѭ���У��� frontier ������ȡ��һ���ڵ㣬Ȼ����������п��е��ӽڵ�
        while (!frontier.isEmpty()) {
            node = frontier.poll();

            for (int i = 0; i < root.getN(); i++) {
                for (Direction d : Direction.FOUR_DIRECTIONS) {
                    if (node.applicable(i, d)) {
                        child = node.move(i, d);

                        // ����ÿ�����е��ӽڵ㣬�������δ��̽����������� frontier ���У������仨�Ѵ洢�� cost �����С�
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
     * ������������ĳɱ����ݱ��浽ָ�����ļ��С�
     * @param cost ����������ĳɱ����顣
     * @param filename ���ڱ������ݵ��ļ�����
     */
    private void save(int[] cost, String filename) {

        // ������������ĳɱ�����д��ָ�����ļ�������ʹ�� DataOutputStream ������д���ļ���
        OutputStream os;
        DataOutputStream dos = null;
        try {
            os = new FileOutputStream(filename);
            dos = new DataOutputStream(new BufferedOutputStream(os));

            // ��ѭ���У��� cost �����е�ÿ��ֵ��Ϊ�ֽ�д���ļ���
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
        // �ȴ���һ�� DatabaseBuilder ����Ȼ�󴴽�]��� SubBoard ���󣬴���ͬ�������⡣
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
        // �ȴ���һ�� DatabaseBuilder ����Ȼ�󴴽���� SubBoard ���󣬴���ͬ�������⡣
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
        buildPuzzle3(); // ��������ģʽ���ݿ�
        buildPuzzle4(); // �����Ľ�ģʽ���ݿ�
    }
}
