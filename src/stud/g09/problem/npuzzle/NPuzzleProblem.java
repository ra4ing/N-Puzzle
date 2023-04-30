package stud.g09.problem.npuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;

import java.io.*;
import java.util.Deque;

public class NPuzzleProblem extends Problem {
    private final int size; // N-Puzzle��������̴�С

    /**
     * NPuzzleProblem ���캯��
     * @param initialState ��ʼ״̬��ƴͼ��
     * @param goalState Ŀ��״̬��ƴͼ��
     * @param size ƴͼ��Ĵ�С
     */
    public NPuzzleProblem(PuzzleBoard initialState, PuzzleBoard goalState, int size) {
        super(initialState, goalState);
        this.size = size;
    }

    /**
     * �ж� N-Puzzle �����Ƿ�ɽ�
     * @return ������ɽ⣬���� true�����򷵻� false
     */
    @Override
    public boolean solvable() {
        int inversions = 0;
        int[] flattenedBoard = new int[size * size - 1];
        int index = 0;

        // ��ƴͼչ��һά����
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (((PuzzleBoard) initialState).board[i][j] != 0) {
                    flattenedBoard[index++] = ((PuzzleBoard) initialState).board[i][j];
                }
            }
        }

        // ���������
        for (int i = 0; i < flattenedBoard.length; i++) {
            for (int j = i + 1; j < flattenedBoard.length; j++) {
                if (flattenedBoard[i] > flattenedBoard[j]) {
                    inversions++;
                }
            }
        }

        if (size % 2 == 1) { // // ������С������
            return inversions % 2 == 0;
        } else { // ż����С������
            int emptyRowFromBottom = size - ((PuzzleBoard) initialState).getEmptyRow();
            return (inversions + emptyRowFromBottom) % 2 == 1;
        }
    }


    /**
     * �����һ��״̬����һ��״̬�Ĳ���ĳɱ�
     * @param state ��ǰ״̬
     * @param action ִ�еĶ���
     * @return ����ɱ����˴�Ϊ 1
     */
    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    /**
     * �ж϶����Ƿ������ڵ�ǰ״̬
     * @param state ��ǰ״̬
     * @param action Ҫִ�еĶ���
     * @return �����������ڵ�ǰ״̬������ true�����򷵻� false
     */
    @Override
    public boolean applicable(State state, Action action) {
        return ((PuzzleBoard) state).canMove((Move) action);
    }

    /**
     * ��ʾ�������
     * @param path �洢��������Ľڵ��˫�˶���
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
     * Ϊ Unity ��ʾ�������
     * @param path �洢��������Ľڵ��˫�˶���
     * @param index ����ı��
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
     * ��ת����������˫�������Ľ��չʾ
     * @param previousNode ǰһ���ڵ�
     * @param currentNode ��ǰ�ڵ�
     * @return ������ת�������½ڵ�
     */
    public static Node reverseAction(Node previousNode, Node currentNode) {
        if (currentNode.getAction() != null) {
            // ��ȡǰһ���ڵ�͵�ǰ�ڵ�Ŀո�λ��
            PuzzleBoard previousState = (PuzzleBoard) previousNode.getState();
            PuzzleBoard currentState = (PuzzleBoard) currentNode.getState();
            int prevEmptyRow = previousState.getEmptyRow();
            int prevEmptyCol = previousState.getEmptyCol();
            int currentEmptyRow = currentState.getEmptyRow();
            int currentEmptyCol = currentState.getEmptyCol();

            // ���ݿո�λ�õı仯ȷ������
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
