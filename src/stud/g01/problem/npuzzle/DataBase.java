package stud.g01.problem.npuzzle;

import java.io.*;

public final class DataBase {
    public static final byte[] costTable_15_puzzle_0 = new byte[4096];
    public static final byte[] costTable_15_puzzle_1 = new byte[16777216];
    public static final byte[] costTable_15_puzzle_2 = new byte[16777216];

    static {
        loadStreamCostTable("resources/database1.db", costTable_15_puzzle_0);
        loadStreamCostTable("resources/database2.db", costTable_15_puzzle_1);
        loadStreamCostTable("resources/database3.db", costTable_15_puzzle_2);
    }

    private DataBase() {
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

