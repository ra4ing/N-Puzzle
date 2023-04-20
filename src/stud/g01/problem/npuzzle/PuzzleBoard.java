package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;

import java.util.*;

import static core.solver.algorithm.heuristic.HeuristicType.*;

public class PuzzleBoard extends State {
    private static final int[][][] goalTable;
    private static final int[][][][] zobristTable;
    private static final int[] classes;
    private static final int[][] subSets = {
            {-1, 0, 0, 0, 0, 1, 1, 1, 1},
            {-1, 1, 0, 0, 0, 1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2}
    };
    public static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}
    };

    public final int[][] board;
    private int[] stateCode;
    private final int size;
    private int emptyRow;
    private int emptyCol;
    private static final double SCALE = 1;
    private int hash_code = -1;
    private int heuristic = -1;


    //init classes
    static {
        classes = new int[]{2, 3};
    }

    // init zobristTable
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

    // init goalTable
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

//        System.out.println(((Move) action).getDirection().symbol());
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                System.out.print(board[i][j] + " ");
//            }
//            System.out.print("     ");
//            for (int j = 0; j < size; j++) {
//                System.out.print(board[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("--------------------------");
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
        predictors.put(DISJOINT_PATTERN, (state, goal) -> ((PuzzleBoard) state).disjoint());
        predictors.put(MANLINEAR_CONFLICT, (state, goal) -> ((PuzzleBoard) state).manLinearConflict());
        predictors.put(MISPLACED, (state, goal) -> ((PuzzleBoard) state).misplaced());
        predictors.put(EUCLID, (state, goal) -> ((PuzzleBoard) state).euclid());
        predictors.put(MANHATTAN_FOR_BI, (state, goal) -> ((PuzzleBoard) state).manhattanForBi(goal));
    }

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }

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
                            if (((PuzzleBoard) goal).board[gi][gj] == this.board[i][j]) {
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

    public int disjoint() {
        if (stateCode == null) {
            stateCode = getStateCode();
        }
        return disjointPatternDataBase();
    }

    private int disjointPatternDataBase() {
        if (this.heuristic == -1) {
            int disjointPattern = 0;
            if (size == 3) {
                disjointPattern += DataBase.puzzle3[0][stateCode[0]];
                disjointPattern += DataBase.puzzle3[1][stateCode[1]];
            } else if (size == 4) {
                disjointPattern += DataBase.puzzle4[0][stateCode[0]];
                disjointPattern += DataBase.puzzle4[1][stateCode[1]];
                disjointPattern += DataBase.puzzle4[2][stateCode[2]];
            }
            this.heuristic = disjointPattern;
        }
        return this.heuristic;
    }

    private int[] getStateCode() {
        int[] stateCode = new int[classes[size - 3]];
        if (size == 3) {
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (board[i][j] == 0) continue;
                    stateCode[subSets[size - 3][board[i][j]]] += (i * size + j) *
                            Math.pow(size * size, positions[size - 3][board[i][j]]);
                }
            }
        } else {
            for (int pos = size * size - 1; pos >= 0; --pos) {
                final int i = goalTable[size-3][pos][0];
                final int j = goalTable[size-3][pos][1];
                final int value = board[i][j];

                if (value != 0) {
                    final int subsetNumber = subSets[size-3][value];

                    switch (subsetNumber) {
                        case 2 -> stateCode[2] |= pos << (positions[size - 3][value] << 2);
                        case 1 -> stateCode[1] |= pos << (positions[size - 3][value] << 2);
                        default -> stateCode[0] |= pos << (positions[size - 3][value] << 2);
                    }
                }
            }
        }



        return stateCode;
    }


}
