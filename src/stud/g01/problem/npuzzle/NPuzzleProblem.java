package stud.g01.problem.npuzzle;

import core.problem.Action;
import core.problem.Problem;
import core.problem.State;
import core.solver.queue.Node;

import java.util.Deque;

public class NPuzzleProblem extends Problem {
    private final int size;

    public NPuzzleProblem(PuzzleBoard initialState, PuzzleBoard goalState, int size) {
        super(initialState, goalState);
        this.size = size;
    }

    @Override
    public boolean solvable() {
        int inversions = 0;
        int[] flattenedBoard = new int[size * size - 1];
        int index = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (((PuzzleBoard) initialState).board[i][j] != 0) {
                    flattenedBoard[index++] = ((PuzzleBoard) initialState).board[i][j];
                }
            }
        }

        for (int i = 0; i < flattenedBoard.length; i++) {
            for (int j = i + 1; j < flattenedBoard.length; j++) {
                if (flattenedBoard[i] > flattenedBoard[j]) {
                    inversions++;
                }
            }
        }

        if (size % 2 == 1) { // Odd board size
            return inversions % 2 == 0;
        } else { // Even board size
            int emptyRowFromBottom = size - ((PuzzleBoard) initialState).getEmptyRow();
            return (inversions + emptyRowFromBottom) % 2 == 1;
        }
    }


    @Override
    public int stepCost(State state, Action action) {
        return 1;
    }

    @Override
    public boolean applicable(State state, Action action) {
        return ((PuzzleBoard) state).canMove((Move) action);
    }

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
