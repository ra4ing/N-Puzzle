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
                if (((PuzzleBoard) initialState).getBoard()[i][j] != 0) {
                    flattenedBoard[index++] = ((PuzzleBoard) initialState).getBoard()[i][j];
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
            node.getAction().draw();
            node.getState().draw();
            step++;
        }
    }


}
