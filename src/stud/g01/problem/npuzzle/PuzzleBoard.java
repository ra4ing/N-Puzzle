package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;

import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static core.solver.algorithm.heuristic.HeuristicType.*;

public class PuzzleBoard extends State {

    private int[][] board;
    private final int size;
    private int emptyRow;
    private int emptyCol;
    private static final double SCALE = 1.1;
    private int hash_code = 0;
    private int heuristic;

    private static final int[][][][] zobristTable;
    private static final int[][][] goalTable;

    static {
        int maxSize = 4;
        zobristTable = new int[maxSize + 1][maxSize][maxSize][maxSize * maxSize];
        Random random = new Random();

        for (int size = 3; size <= maxSize; size++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size * size; k++) {
                        zobristTable[size][i][j][k] = random.nextInt();
                    }
                }
            }
        }
    }

    static {
        int maxSize = 4;
        goalTable = new int[maxSize + 1][maxSize * maxSize][2];

        for (int size = 3; size <= maxSize; size++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = i * size + j + 1;
                    if (value < size * size) {
                        goalTable[size][value][0] = i;
                        goalTable[size][value][1] = j;
                    }
                }
            }
        }
    }

    public PuzzleBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
    }


    public void setBoard(int[][] board) {
        this.board = board;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                    break;
                }
            }
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public int getEmptyRow() {
        return emptyRow;
    }

//    public int getEmptyCol() {
//        return emptyCol;
//    }
//
//    public int getSize() {
//        return size;
//    }

    public boolean canMove(Move move) {
        Direction direction = move.getDirection();
        int[] offsets = Direction.offset(direction);
        int newRow = emptyRow + offsets[0];
        int newCol = emptyCol + offsets[1];

        return newRow >= 0 && newRow < size && newCol >= 0 && newCol < size;
    }

    @Override
    public void draw() {
        System.out.println("+-----" + "----".repeat(size - 1) + "+");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print("|");
                int value = board[i][j];
                if (value == 0) {
                    System.out.print("    ");
                } else {
                    System.out.printf("%4d", value);
                }
            }
            System.out.println("|");
            System.out.println("+-----" + "----".repeat(size - 1) + "+");
        }
    }

    /**
     * 当前状态采用action而进入的下一个状态
     *
     * @param action 当前状态下，一个可行的action
     * @return 下一个状态
     */
    @Override
    public State next(Action action) {
        Direction dir = ((Move) action).getDirection();
        int[] offsets = Direction.offset(dir);
        int walkRow = emptyRow + offsets[0];
        int walkCol = emptyCol + offsets[1];

        int temp = board[walkRow][walkCol];

        int[][] newBoard = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, size);
        }
        newBoard[emptyRow][emptyCol] = temp;
        newBoard[walkRow][walkCol] = 0;

//        System.out.println(((Move) action).getDirection().symbol());
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                System.out.print(board[i][j] + " ");
//            }
//            System.out.print("     ");
//            for (int j = 0; j < size; j++) {
//                System.out.print(newBoard[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//
//        System.out.println("--------------------------");

        PuzzleBoard newPuzzleBoard = new PuzzleBoard(size);
        newPuzzleBoard.setBoard(newBoard);

        return newPuzzleBoard;
    }

    /**
     * 当前状态下所有可能的Action，但不一定都可行
     *
     * @return 所有可能的Action的List
     */
    @Override
    public Iterable<? extends Action> actions() {
        Collection<Move> moves = new ArrayList<>();
        for (Direction d : Direction.FOUR_DIRECTIONS)
            moves.add(new Move(d));
        return moves;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof PuzzleBoard another) {
            return hashCode() == another.hashCode();
//            return Arrays.deepEquals(this.board, another.getBoard());
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hash_code == 0) {
            int hash = 0;
            int[][][] zobristTableForSize = zobristTable[size];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = board[i][j];
                    hash ^= zobristTableForSize[i][j][value];
                }
            }
            hash_code = hash;
        }
        return hash_code;
//        return Arrays.deepHashCode(this.board);
    }

    //枚举映射，存放不同类型的启发函数
    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);

    static {
        predictors.put(MANHATTAN, (state, goal) -> ((PuzzleBoard) state).manhattan());
        predictors.put(DISJOINT_PATTERN, (state, goal) -> ((PuzzleBoard) state).disjointPattern());
        predictors.put(MISPLACED, (state, goal) -> ((PuzzleBoard) state).misplaced());
        predictors.put(EUCLID, (state, goal) -> ((PuzzleBoard) state).euclid());
    }

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }


    // Add this inside the PuzzleBoard class
    private static final Map<String, Integer> patternData1 = new HashMap<>();
    private static final Map<String, Integer> patternData2 = new HashMap<>();

    static {
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader("resources/patterns1.csv"));
            String line;
            while ((line = reader1.readLine()) != null) {
                String[] tokens = line.split(",");
                patternData1.put(String.join("", tokens).substring(0, 8), Integer.parseInt(tokens[8]));
            }
            reader1.close();

            BufferedReader reader2 = new BufferedReader(new FileReader("resources/patterns2.csv"));
            while ((line = reader2.readLine()) != null) {
                String[] tokens = line.split(",");
                patternData2.put(String.join("", tokens).substring(0, 7), Integer.parseInt(tokens[7]));
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int disjointPattern() {
        if (heuristic == 0) {
            StringBuilder key1 = new StringBuilder();
            StringBuilder key2 = new StringBuilder();

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int currentValue = board[i][j];
                    if (currentValue >= 1 && currentValue <= 8) {
                        key1.append(currentValue);
                    } else if (currentValue >= 9 && currentValue <= 15) {
                        key2.append(currentValue);
                    }
                }
            }

            Integer cost1 = patternData1.get(key1.toString());
            Integer cost2 = patternData2.get(key2.toString().replaceAll("0", ""));
            heuristic = (int) ((cost1 != null ? cost1 : 0) + (cost2 != null ? cost2 : 0) * SCALE);
        }
        return heuristic;
    }


    //两个点之间的mis距离
    private int misplaced() {
        if (heuristic == 0) {
            int misplacedTiles = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];
                    int targetRow = goalTable[size][value][0];
                    int targetCol = goalTable[size][value][1];

                    if (value != 0) {
                        if ((row != targetRow || col != targetCol)) {
                            misplacedTiles++;
                        }
                    }
                }
            }
            heuristic = (int) (misplacedTiles * SCALE);
        }
        return heuristic;
    }


    //两个点之间的欧几里德距离
    private int euclid() {
        if (heuristic == 0) {
            double euclideanDistance = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size][value][0];
                        int targetCol = goalTable[size][value][1];

                        euclideanDistance += Math.sqrt(Math.pow((row - targetRow), 2) + Math.pow((col - targetCol), 2));
                    }
                }
            }
            //单元格的边长
            heuristic = (int) (euclideanDistance * SCALE);
        }
        return heuristic;
    }

    //两个点之间的曼哈顿距离
    private int manhattan() {
        if (heuristic == 0) {
            int manhattanDistance = 0;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size][value][0];
                        int targetCol = goalTable[size][value][1];

                        manhattanDistance += ((row - targetRow) > 0 ? row - targetRow : targetRow - row) + ((col - targetCol) > 0 ? col - targetCol : targetCol - col);
                    }
                }
            }

            heuristic = (int) (manhattanDistance * SCALE);
        }
        return heuristic;
    }


}
