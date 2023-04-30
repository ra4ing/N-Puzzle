package stud.g09.problem.npuzzle;

import core.problem.Action;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;

import java.util.*;

import static core.solver.algorithm.heuristic.HeuristicType.*;
import static stud.g09.problem.npuzzle.DataBase.*;

/**
 * PuzzleBoard���ʾNPuzzle�����״̬���̳���State�ࡣ
 * �����˱�ʾN-Puzzle���̵Ķ�ά���飬�Լ���ص�״̬��Ϣ�Ͳ���������
 */
public class PuzzleBoard extends State {
    private static final int[][][][] zobristTable; // ���ڼ���zobrist
    private static final int[][][] goalTable; // ��ǰ����ÿ�����ֵ�Ŀ��λ��
    public final int[][] board; // ��ά�����ʾƴͼ���ϵ�����
    private int[] stateCode; // ���ڻ�ȡ���ݿ�λ��
    private final int size; // ƴͼ��Ĵ�С����3��ʾ3x3ƴͼ
    private int emptyRow; // ���ڴ洢�հ׸����λ��
    private int emptyCol; // ���ڴ洢�հ׸����λ��
    private static final double SCALE = 1.1; // ����ֵ��Ȩֵ
    private int hash_code = -1; // ״̬��zobrist��ϣֵ
    private int heuristic = -1; // ״̬������ֵ

    // ��ʼ��goalTable
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


    // ��ʼ��zobristTable
    static {
        int maxSize = 4;
        zobristTable = new int[2][maxSize][maxSize][maxSize * maxSize];
        Random random = new Random();

        // ΪzobristTable�����������ֵ
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

    /**
     * ����һ���µ�PuzzleBoard����
     *
     * @param size  N-Puzzle�ĳߴ�
     * @param board ��ʾN-Puzzle���̵Ķ�ά����
     */
    public PuzzleBoard(int size, int[][] board) {
        this.size = size;
        this.board = board;

        // ���ҿհ׸��λ��
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

    /**
     * ��ȡ�ո����������
     *
     * @return �ո��������
     */
    public int getEmptyRow() {
        return emptyRow;
    }

    /**
     * ��ȡ�ո����������
     *
     * @return �ո��������
     */
    public int getEmptyCol() {
        return emptyCol;
    }

//    public int getSize() {
//        return size;
//    }


    /**
     * ���������ƶ������Ƿ�����ڵ�ǰ������ִ�С�
     *
     * @param move ��һ�����ƶ�����
     */
    public boolean canMove(Move move) {
        Direction direction = move.getDirection();
        int[] offsets = Direction.offset(direction);
        int newRow = emptyRow + offsets[0];
        int newCol = emptyCol + offsets[1];

        return newRow >= 0 && newRow < size && newCol >= 0 && newCol < size;
    }

    /**
     * ���Ƶ�ǰ״̬
     */
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
     * ��ǰ״̬����action���������һ��״̬
     *
     * @param action ��ǰ״̬�£�һ�����е�action
     * @return ��һ��״̬
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

        // �ƶ��ո�λ��
        newBoard[emptyRow][emptyCol] = temp;
        newBoard[walkRow][walkCol] = 0;

        return new PuzzleBoard(size, newBoard);
    }

    /**
     * ��ǰ״̬�����п��ܵ�Action������һ��������
     *
     * @return ���п��ܵ�Action��List
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

    // ʹ��zobrist��ϣ�������ϣֵ
    @Override
    public int hashCode() {
        if (hash_code == -1) {
            int hash = 0;
            int[][][] zobristTableForSize = zobristTable[size-3];

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

    //ö��ӳ�䣬��Ų�ͬ���͵���������
    private static final EnumMap<HeuristicType, Predictor> predictors = new EnumMap<>(HeuristicType.class);

    static {
        predictors.put(MANHATTAN, (state, goal) -> ((PuzzleBoard) state).manhattan());
        predictors.put(DISJOINT_PATTERN, (state, goal) -> ((PuzzleBoard) state).disjoint());
        predictors.put(MANLINEAR_CONFLICT, (state, goal) -> ((PuzzleBoard) state).manLinearConflict());
        predictors.put(MISPLACED, (state, goal) -> ((PuzzleBoard) state).misplaced());
        predictors.put(EUCLID, (state, goal) -> ((PuzzleBoard) state).euclid());
    }

    public static Predictor predictor(HeuristicType type) {
        return predictors.get(type);
    }

    //������֮���mis����
    private int misplaced() {
        if (heuristic == -1) {
            int misplacedTiles = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size - 3][value][0];
                        int targetCol = goalTable[size - 3][value][1];
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


    //������֮���ŷ����¾���
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
            //��Ԫ��ı߳�
            heuristic = (int) (euclideanDistance * SCALE);
        }
        return heuristic;
    }

    // ���㵱ǰƴͼ״̬��Ŀ��״̬֮��������پ���
    private int manhattan() {
        if (heuristic == -1) {
            int manhattanDistance = 0;

            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];

                    if (value != 0) {
                        int targetRow = goalTable[size - 3][value][0];
                        int targetCol = goalTable[size - 3][value][1];
                        manhattanDistance += ((row - targetRow) > 0 ? row - targetRow : targetRow - row)
                                + ((col - targetCol) > 0 ? col - targetCol : targetCol - col);
                    }
                }
            }

            heuristic = (int) (manhattanDistance * SCALE);
        }
        return heuristic;
    }

    // ���������پ�������Գ�ͻ���������ֵ
    public int manLinearConflict() {
        if (this.heuristic == -1) {
            int heu = 0;
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    int value = board[row][col];
                    int targetRow = value / size;
                    int targetCol = value % size;

                    heu += Math.abs(row - targetRow) + Math.abs(col - targetCol);

                    if (col < size - 1 && row == targetRow) {
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                int otherValue = board[i][j];
                                int otherRow = otherValue / size;
                                int otherCol = otherValue % size;

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

    // ����ƴͼ���ϵĲ��ཻģʽ
    public int disjoint() {
        if (stateCode == null) {
            stateCode = getStateCode();
        }
        return disjointPatternDataBase();
    }

    // ���ཻģʽ���ݿ�ľ���ʵ��
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
            this.heuristic = (int) (disjointPattern * SCALE);
        }
        return this.heuristic;
    }

    // ���ݴ���ģʽ���ݿ�ʱ�Ĺ�ϣ��ʽ�����ϣֵ�����Եõ���״̬��ģʽ���ݿ��ж�Ӧ��PathCost
    private int[] getStateCode() {
        int[] stateCode = new int[3];
        if (size == 3) { // ����
            for (int i = 0; i < size; ++i) {
                for (int j = 0; j < size; ++j) {
                    if (board[i][j] == 0) continue;
                    stateCode[subSets[0][board[i][j]]] += (i * size + j) *
                            Math.pow(size * size, positions[0][board[i][j]]);
                }
            }
        } else { // �Ľ�
            for (int pos = size * size - 1; pos >= 0; --pos) {
                int value = board[pos / size][pos % size];

                if (value == 0) continue;

                // λ����ӿ������ٶ�
                stateCode[subSets[1][value]] |= pos << (positions[1][value] << 2);

            }
        }


        return stateCode;
    }


}
