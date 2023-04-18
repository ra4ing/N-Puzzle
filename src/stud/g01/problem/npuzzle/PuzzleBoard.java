package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;

import java.util.*;

import static core.solver.algorithm.heuristic.HeuristicType.*;

public class PuzzleBoard extends State {

    private final int[][] board;
    private static final int[][][] goalTable;
    private static final int[][][][] zobristTable;
    private final int size;
    private int emptyRow;
    private int emptyCol;
    private static final double SCALE = 1.1;
    private int hash_code = -1;
    private int heuristic = -1;


    static {
        int maxSize = 4;
        zobristTable = new int[2][maxSize][maxSize][maxSize * maxSize];
        Random random = new Random();

        for (int size = 3; size <= maxSize; size++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    for (int k = 0; k < size * size; k++) {
                        zobristTable[size - 3][i][j][k] = random.nextInt();
                    }
                }
            }
        }
    }

    static {
        int maxSize = 4;
        goalTable = new int[2][maxSize * maxSize][2];

        for (int size = 3; size <= maxSize; size++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = i * size + j + 1;
                    if (value < size * size) {
                        goalTable[size - 3][value][0] = i;
                        goalTable[size - 3][value][1] = j;
                    }
                }
            }
        }
    }

    public PuzzleBoard(int size, int[][] board) {
        this.size = size;
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

    public int getEmptyCol() {
        return emptyCol;
    }

    public int getSize() {
        return size;
    }

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
                    System.out.printf("%4c", '#');
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

        int[][] newBoard = new int[size][];
        for (int i = 0; i < size; i++) {
            newBoard[i] = board[i].clone();
        }
        newBoard[emptyRow][emptyCol] = temp;
        newBoard[walkRow][walkCol] = 0;

        System.out.println(((Move) action).getDirection().symbol());
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.print("     ");
            for (int j = 0; j < size; j++) {
                System.out.print(newBoard[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("--------------------------");

        return new PuzzleBoard(size, newBoard);
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
        }

        return false;
    }

    @Override
    public int hashCode() {
        if (hash_code == -1) {
            int hash = 0;
            int[][][] zobristTableForSize = zobristTable[size - 3];

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    int value = board[i][j];
                    hash ^= zobristTableForSize[i][j][value];
                }
            }
            hash_code = hash;
        }
        return hash_code;
    }

    //枚举映射，存放不同类型的启发函数
    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);

    static {
        predictors.put(MANHATTAN, (state, goal) -> ((PuzzleBoard) state).manhattan());
//        predictors.put(DISJOINT_PATTERN, (state, goal) -> ((PuzzleBoard) state).disjointPattern());
        predictors.put(MANLINEARCONFLICT, (state, goal) -> ((PuzzleBoard) state).manLinearConflict());
        predictors.put(MISPLACED, (state, goal) -> ((PuzzleBoard) state).misplaced());
        predictors.put(EUCLID, (state, goal) -> ((PuzzleBoard) state).euclid());
        predictors.put(MANHATTAN_FOR_BI, (state, goal) -> ((PuzzleBoard) state).manhattanForBi(goal));
        predictors.put(NOTHING, (state, goal) -> ((PuzzleBoard) state).noHeuristic());
    }

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }


//    static {
//        int maxSize = 4;
//        for (int size = 0; size <= 4; size++) {
//            db[size] = PathFinding.puzzle3;
//            subsets[size] = PathFinding.subsets3;
//            positions[size] = PathFinding.positions3;
//            classes[size] = size - 1;
//        }
//    }
//
//    private int disjointPattern() {
//        if (heuristic == -1) {
//
//        }
//        return heuristic;
//    }


    //两个点之间的mis距离
    private int misplaced() {
        if (heuristic == -1) {
            int misplacedTiles = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];
                    int targetRow = goalTable[size - 3][value][0];
                    int targetCol = goalTable[size - 3][value][1];

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
        if (heuristic == -1) {
            double euclideanDistance = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size - 3][value][0];
                        int targetCol = goalTable[size - 3][value][1];

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
        if (heuristic == -1) {
            int manhattanDistance = 0;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size - 3][value][0];
                        int targetCol = goalTable[size - 3][value][1];

                        manhattanDistance += ((row - targetRow) > 0 ? row - targetRow : targetRow - row) + ((col - targetCol) > 0 ? col - targetCol : targetCol - col);
                    }
                }
            }

            heuristic = (int) (manhattanDistance * SCALE);
        }
        return heuristic;
    }

    private int manhattanForBi(State goal) {
        if (heuristic == -1) {
            int mht = 0;
            for (int i = 0; i < this.size; ++i) {
                for (int j = 0; j < this.size; ++j) {
                    if (this.board[i][j] == 0) continue;
                    int gi = 0, gj = 0;
                    for (boolean flag = false; gi < this.size; ++gi) {
                        for (gj = 0; gj < this.size; ++gj) {
                            if (((PuzzleBoard) goal).getBoard()[gi][gj] == this.board[i][j]) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) break;
                    }
                    int t = ((i - gi) > 0 ? i - gi : gi - i) + ((j - gj) > 0 ? j - gj : gj - j);
                    mht += t;
                }
            }
            heuristic = mht;
        }

        return heuristic;
    }

    public int manLinearConflict() {
        if (this.heuristic == -1) {
            int heu = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    int targetRow = goalTable[size - 3][value][0];
                    int targetCol = goalTable[size - 3][value][1];

                    heu += Math.abs(row - targetRow) + Math.abs(col - targetCol);

                    if (col < size - 1 && row == targetRow) {
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                int otherValue = board[i][j];
                                int otherRow = goalTable[size - 3][otherValue][0];
                                int otherCol = goalTable[size - 3][otherValue][1];

                                if (row == otherRow && col == otherCol - 1 && value > otherValue) {
                                    heu += 2;
                                }
                            }
                        }
                    }
                }
            }
            this.heuristic = (int) (heu * SCALE);
        }
        return this.heuristic;
    }

    int noHeuristic() {
        return 0;
    }


}
