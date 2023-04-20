package stud.g01.problem.npuzzle;

import java.io.*;
import java.util.Scanner;

public final class DataBase {
//    public static final byte[] costTable_15_puzzle_0 = new byte[4096];
//    public static final byte[] costTable_15_puzzle_1 = new byte[16777216];
//    public static final byte[] costTable_15_puzzle_2 = new byte[16777216];
//    public static final byte[] costTable_8_puzzle_0 = new byte[6561];
//    public static final byte[] costTable_8_puzzle_1 = new byte[6561];
//
//    static {
////        loadStreamCostTable("resources/database4_0.db", costTable_15_puzzle_0);
////        loadStreamCostTable("resources/database4_1.db", costTable_15_puzzle_1);
////        loadStreamCostTable("resources/database4_2.db", costTable_15_puzzle_2);
////        loadStreamCostTable("resources/database3_0.db", costTable_8_puzzle_0);
////        loadStreamCostTable("resources/database3_1.db", costTable_8_puzzle_1);
//        loadStreamCostTable("resources/db4(1).txt", costTable_15_puzzle_0);
//        loadStreamCostTable("resources/db4(2).txt", costTable_15_puzzle_1);
//        loadStreamCostTable("resources/db4(3).txt", costTable_15_puzzle_2);
//        loadStreamCostTable("resources/db3(1).txt", costTable_8_puzzle_0);
//        loadStreamCostTable("resources/db3(2).txt", costTable_8_puzzle_1);
//    }

    private DataBase() {
    }

    public static int[][] puzzle3;
    public static int[][] puzzle4;

    public static void read2(String filename, int i, int size) {
        if (size == 3) {
            try {
                File file = new File(filename);
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split("\t");
                    for (int j = 0; j < line.length; j++) {
                        puzzle3[i][j] = Integer.parseInt(line[j]);
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (size == 4) {
            try {
                File file = new File(filename);
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextLine()) {
                    String[] line = scanner.nextLine().split("\t");
                    for (int j = 0; j < line.length; j++) {
                        puzzle4[i][j] = Integer.parseInt(line[j]);
                    }
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public static void load(){
        System.out.println("Loading Puzzle3");
        puzzle3=new int[2][(int) Math.pow(9, 4)];
        read2("resources/db3(1).txt", 0, 3);
        read2("resources/db3(2).txt", 1, 3);
        System.out.println("Load Puzzle3 Completed\n");

        System.out.println("Loading Puzzle4");
        puzzle4=new int[3][(int) Math.pow(16, 6)];
        read2("resources/db4(1).txt", 0,4);
        read2("resources/db4(2).txt", 1,4);
        read2("resources/db4(3).txt", 2,4);
        System.out.println("Load Puzzle4 Completed\n");
    }

    public static void loadStreamCostTable(String filename, byte[] costTable) {
        InputStream is = DataBase.class.getResourceAsStream(filename);
        DataInputStream dis = null;
        try {
            if (is != null) {
                dis = new DataInputStream(new BufferedInputStream(is));
                int i = 0;
                while (dis.available() > 0) {
                    costTable[i++] = dis.readByte();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Cannot find file " + filename + ".");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error: Cannot read from file " + filename + ".");
            System.exit(1);
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException ignored) {}
        }
    }
}

