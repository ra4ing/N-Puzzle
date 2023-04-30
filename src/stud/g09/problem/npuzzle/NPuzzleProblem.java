package stud.g09.problem.npuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;

import java.io.*;
import java.util.Deque;

public class NPuzzleProblem extends Problem {
    private final int size; // N-Puzzle问题的棋盘大小

    /**
     * NPuzzleProblem 构造函数
     * @param initialState 初始状态的拼图板
     * @param goalState 目标状态的拼图板
     * @param size 拼图板的大小
     */
    public NPuzzleProblem(PuzzleBoard initialState, PuzzleBoard goalState, int size) {
        super(initialState, goalState);
        this.size = size;
    }

    /**
     * 判断 N-Puzzle 问题是否可解
     * @return 若问题可解，返回 true；否则返回 false
     */
    @Override
    public boolean solvable() {
        int inversions = 0;
        int[] flattenedBoard = new int[size * size - 1];
        int index = 0;

        // 将拼图展成一维数组
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (((PuzzleBoard) initialState).board[i][j] != 0) {
                    flattenedBoard[index++] = ((PuzzleBoard) initialState).board[i][j];
                }
            }
        }

        // 计算逆序对
        for (int i = 0; i < flattenedBoard.length; i++) {
            for (int j = i + 1; j < flattenedBoard.length; j++) {
                if (flattenedBoard[i] > flattenedBoard[j]) {
                    inversions++;
                }
            }
        }

        if (size % 2 == 1) { // // 奇数大小的棋盘
            return inversions % 2 == 0;
        } else { // 偶数大小的棋盘
            int emptyRowFromBottom = size - ((PuzzleBoard) initialState).getEmptyRow();
            return (inversions + emptyRowFromBottom) % 2 == 1;
        }
    }


    /**
     * 计算从一个状态到另一个状态的步骤的成本
     * @param state 当前状态
     * @param action 执行的动作
     * @return 步骤成本，此处为 1
     */
    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    /**
     * 判断动作是否适用于当前状态
     * @param state 当前状态
     * @param action 要执行的动作
     * @return 若动作适用于当前状态，返回 true；否则返回 false
     */
    @Override
    public boolean applicable(State state, Action action) {
        return ((PuzzleBoard) state).canMove((Move) action);
    }

    /**
     * 显示解决方案
     * @param path 存储解决方案的节点的双端队列
     */
    @Override
    public void showSolution(Deque<Node> path) {
        int step = 0;
        for (Node node : path) {
            System.out.println("Step " + step + ":");
            if (node.getAction() != null) node.getAction().draw();
            node.getState().draw();
            step++;
        }
    }

    /**
     * 为 Unity 显示解决方案
     * @param path 存储解决方案的节点的双端队列
     * @param index 问题的编号
     */
    public void showSolutionForUnity(Deque<Node> path, int index) throws IOException {
        String filePath = "resources/path_for_show.txt";
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        PrintStream out = System.out;
        FileOutputStream fos = new FileOutputStream(file, true);
        PrintStream ps;

        if (index == 1)
            ps = new PrintStream(file);
        else
            ps = new PrintStream(fos);

        System.setOut(ps);

        System.out.println("-------------------------");
        System.out.println("problem: " + index);
        for (Node node : path) {
            if (node.getAction() != null) {
                node.getAction().draw();
            }
        }

        ps.close();
        fos.close();

        System.setOut(out);
    }

    /**
     * 反转动作，用于双向搜索的结果展示
     * @param previousNode 前一个节点
     * @param currentNode 当前节点
     * @return 包含反转动作的新节点
     */
    public static Node reverseAction(Node previousNode, Node currentNode) {
        if (currentNode.getAction() != null) {
            // 获取前一个节点和当前节点的空格位置
            PuzzleBoard previousState = (PuzzleBoard) previousNode.getState();
            PuzzleBoard currentState = (PuzzleBoard) currentNode.getState();
            int prevEmptyRow = previousState.getEmptyRow();
            int prevEmptyCol = previousState.getEmptyCol();
            int currentEmptyRow = currentState.getEmptyRow();
            int currentEmptyCol = currentState.getEmptyCol();

            // 根据空格位置的变化确定操作
            if (prevEmptyRow == currentEmptyRow) {
                if (prevEmptyCol - currentEmptyCol == 1) {
                    return new Node(currentNode.getState(), currentNode.getParent(), new Move(Direction.W), currentNode.getPathCost());
                } else if (prevEmptyCol - currentEmptyCol == -1) {
                    return new Node(currentNode.getState(), currentNode.getParent(), new Move(Direction.E), currentNode.getPathCost());
                }
            } else if (prevEmptyCol == currentEmptyCol) {
                if (prevEmptyRow - currentEmptyRow == 1) {
                    return new Node(currentNode.getState(), currentNode.getParent(), new Move(Direction.N), currentNode.getPathCost());
                } else if (prevEmptyRow - currentEmptyRow == -1) {
                    return new Node(currentNode.getState(), currentNode.getParent(), new Move(Direction.S), currentNode.getPathCost());
                }
            }
        }
        return currentNode;
    }


}
