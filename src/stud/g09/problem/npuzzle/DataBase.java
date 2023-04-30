package stud.g09.problem.npuzzle;

import java.io.*;

// ���ݿ��࣬���ڼ��غʹ洢N-Puzzle����������
public final class DataBase {
    // ���ݱ����ڴ洢��ͬ��С��N-Puzzle����ĳɱ�����
    public static byte[]
            costTable_15_puzzle_0 = new byte[4096], // 6^3��״̬�������ֵ��
            costTable_15_puzzle_1 = new byte[16777216], //6^6��״̬�������ֵ��
            costTable_15_puzzle_2 = new byte[16777216], //6^6��״̬�������ֵ��
            costTable_8_puzzle_0 = new byte[6561], // 4^4��״̬�������ֵ��
            costTable_8_puzzle_1 = new byte[6561]; // 4^4��״̬�������ֵ��

    // �Ӽ����ݱ����ڱ�ʾ��ͬ��С��N-Puzzle������Ӽ���ϵ
    public static final int[][] subSets = {
            {-1, 0, 0, 0, 0, 1, 1, 1, 1},
            {-1, 1, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2}
    };

    // λ�����ݱ����ڱ�ʾ��ͬ��С��N-Puzzle�����λ�ù�ϵ
    public static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}
    };
//    public static final int[] classes = {2, 3};

    // ˽�й��캯������ֹʵ����
    private DataBase() {
    }

    // ���ڴ洢3x3��4x4 N-Puzzle����ĳɱ�����
    public static int[][] puzzle3;
    public static int[][] puzzle4;

    // ����ָ���ļ����ĳɱ����ݵ�ָ���ĳɱ���������
    private static void loadStreamCostTable(final String filename, final byte[] costTable) {
        try (InputStream is = getInputStream(filename);
             DataInputStream dis = new DataInputStream(new BufferedInputStream(is))) {

            for (int i = 0; i < costTable.length; i++) {
                int data = dis.read();
                if (data == -1) {
                    break;
                }
                costTable[i] = (byte) data;
            }
        } catch (final FileNotFoundException fnfe) {
            System.err.println("Error: Cannot find file " + filename + ".");
            System.exit(1);
        } catch (final IOException ioe) {
            System.err.println("Error: Cannot read from file " + filename + ".");
            System.exit(1);
        }
    }

    // ��ȡָ���ļ�����������
    private static InputStream getInputStream(final String filename) throws FileNotFoundException {
        InputStream is = DataBase.class.getResourceAsStream(filename);
        if (is == null) {
            is = new FileInputStream(filename);
        }
        return is;
    }

    // ����3x3��4x4 N-Puzzle����ĳɱ�����
    public static void load() {
        System.out.println("begin......");
        loadPuzzle_3();
        loadPuzzle_4();
        System.out.println("Load Puzzle4 Completed\n");
    }

    // ����3x3 N-Puzzle����ĳɱ�����
    private static void loadPuzzle_3() {
        System.out.println("Loading Puzzle3");

        // ����ָ���ļ��ĳɱ����ݵ���Ӧ�ĳɱ���������
        loadStreamCostTable("resources/db3_0.db", costTable_8_puzzle_0);
        loadStreamCostTable("resources/db3_1.db", costTable_8_puzzle_1);

        // �����صĳɱ����ݴ洢��puzzle3������
        puzzle3 = new int[2][DataBase.costTable_8_puzzle_0.length];
        for (int i = 0; i < DataBase.costTable_8_puzzle_0.length; ++i) {
            puzzle3[0][i] = DataBase.costTable_8_puzzle_0[i];
        }
        for (int i = 0; i < DataBase.costTable_8_puzzle_1.length; ++i) {
            puzzle3[1][i] = DataBase.costTable_8_puzzle_1[i];
        }
        System.out.println("Load Puzzle3 Completed\n");
    }

    // ����4x4 N-Puzzle����ĳɱ�����
    private static void loadPuzzle_4() {
        System.out.println("Loading Puzzle4");

        // ����ָ���ļ��ĳɱ����ݵ���Ӧ�ĳɱ���������
        loadStreamCostTable("resources/database1.db", costTable_15_puzzle_0);
        loadStreamCostTable("resources/database2.db", costTable_15_puzzle_1);
        loadStreamCostTable("resources/database3.db", costTable_15_puzzle_2);
        // �����صĳɱ����ݴ洢��puzzle4������
        puzzle4 = new int[3][DataBase.costTable_15_puzzle_1.length];
        for (int i = 0; i < DataBase.costTable_15_puzzle_0.length; ++i) {
            puzzle4[0][i] = DataBase.costTable_15_puzzle_0[i];
        }
        for (int i = 0; i < DataBase.costTable_15_puzzle_1.length; ++i) {
            puzzle4[1][i] = DataBase.costTable_15_puzzle_1[i];
        }
        for (int i = 0; i < DataBase.costTable_15_puzzle_2.length; ++i) {
            puzzle4[2][i] = DataBase.costTable_15_puzzle_2[i];
        }
    }

}

