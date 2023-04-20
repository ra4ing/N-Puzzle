package stud.g01.problem.npuzzle;

import java.io.*;

public final class DataBase {
    public static byte[]
            costTable_15_puzzle_0 = new byte[4096],
            costTable_15_puzzle_1 = new byte[16777216],
            costTable_15_puzzle_2 = new byte[16777216],
            costTable_8_puzzle_0 = new byte[6561],
            costTable_8_puzzle_1 = new byte[6561];

    public static final int[][] subSets = {
            {-1, 0, 0, 0, 0, 1, 1, 1, 1},
            {-1, 1, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2}
    };
    public static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}
    };
//    public static final int[] classes = {2, 3};

    private DataBase() {
    }

    public static int[][] puzzle3;
    public static int[][] puzzle4;

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

    private static InputStream getInputStream(final String filename) throws FileNotFoundException {
        InputStream is = DataBase.class.getResourceAsStream(filename);
        if (is == null) {
            is = new FileInputStream(filename);
        }
        return is;
    }

    public static void load() {
        System.out.println("begin......");
        loadPuzzle_3();
        loadPuzzle_4();
        System.out.println("Load Puzzle4 Completed\n");
    }

    private static void loadPuzzle_3() {
        System.out.println("Loading Puzzle3");
        loadStreamCostTable("resources/db3_0.db", costTable_8_puzzle_0);
        loadStreamCostTable("resources/db3_1.db", costTable_8_puzzle_1);

        puzzle3 = new int[2][DataBase.costTable_8_puzzle_0.length];
        for (int i = 0; i < DataBase.costTable_8_puzzle_0.length; ++i) {
            puzzle3[0][i] = DataBase.costTable_8_puzzle_0[i];
        }
        for (int i = 0; i < DataBase.costTable_8_puzzle_1.length; ++i) {
            puzzle3[1][i] = DataBase.costTable_8_puzzle_1[i];
        }
        System.out.println("Load Puzzle3 Completed\n");
    }

    private static void loadPuzzle_4() {
        System.out.println("Loading Puzzle4");
//        loadStreamCostTable("resources/db4_0.db", costTable_15_puzzle_0);
//        loadStreamCostTable("resources/db4_1.db", costTable_15_puzzle_1);
//        loadStreamCostTable("resources/db4_2.db", costTable_15_puzzle_2);

        loadStreamCostTable("resources/database1.db", costTable_15_puzzle_0);
        loadStreamCostTable("resources/database2.db", costTable_15_puzzle_1);
        loadStreamCostTable("resources/database3.db", costTable_15_puzzle_2);
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

