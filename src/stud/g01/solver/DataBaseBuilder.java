package stud.g01.solver;


import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;

import core.solver.queue.Node;
import stud.g01.problem.npuzzle.Direction;
import stud.g01.problem.npuzzle.Move;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class DataBaseBuilder {

    private static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}};

    private final Queue<Node> frontier = new ArrayDeque<>();
    private final Set<Integer> explored = new HashSet<>();
    private final Predictor predictor = PuzzleBoard.predictor(HeuristicType.NOTHING);

    public DataBaseBuilder() {
    }

    private int[] build(Problem problem, Node root, int size) {
        int[] cost = new int[(int) Math.pow(size * size, 4)];
        frontier.clear();

        frontier.offer(root);
        while (!frontier.isEmpty()) {
            Node node = frontier.poll();

            for (Node child : getChildNodes(node)) {
                if (!explored.contains(hash(child.getState()))) {
                    frontier.offer(child);
                    explored.add(hash(node.getState()));
                    cost[hash(child.getState())] = child.getPathCost();
                }
            }
        }

        explored.clear();
        return cost;
    }

    private List<Node> getChildNodes(Node parent) {
        List<Node> nodes = new ArrayList<>();
        PuzzleBoard parentState = (PuzzleBoard) parent.getState();
        PuzzleBoard child;
        int size = parentState.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = parentState.getBoard()[row][col];
                if (value == 0) continue;
                for (Direction d : Direction.FOUR_DIRECTIONS) {
                    int[] offsets = Direction.offset(d);
                    int newRow = row + offsets[0];
                    int newCol = col + offsets[1];
                    child = move(parentState, row, col, newRow, newCol);
                    if (child != null) { // 可以移动
                        nodes.add(new Node(child, parent, null, parent.getPathCost() + 1));
                    }
                }

            }
        }
        return nodes;
    }

    private PuzzleBoard move(PuzzleBoard stated, int oldRow, int oldCol, int newRow, int newCol) {
        int[][] board = stated.getBoard();
        int size = stated.getSize();

        if (!(newRow >= 0 && newRow < size && newCol >= 0 && newCol < size)) { // 判断是否可以移动
            return null;
        }

        int temp = board[oldRow][oldCol];

        int[][] newBoard = new int[size][];
        for (int i = 0; i < size; i++) {
            newBoard[i] = board[i].clone();
        }
        newBoard[newRow][newCol] = temp;
        newBoard[oldRow][oldCol] = 0;

        return new PuzzleBoard(size, newBoard);
    }

    private static int hash(State state) {
        PuzzleBoard puzzleBoard = (PuzzleBoard) state;
        int hash = 0;
        int size = puzzleBoard.getSize();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int value = puzzleBoard.getBoard()[row][col];
                if (value != 0) {
                    hash += (row * size + col) * Math.pow(size * size, positions[size - 3][value]);
                }
            }
        }
        return hash;
    }

    private void save(int[] cost, String filename) {
        Path filePath = Paths.get("resources", filename);

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.WRITE)) {
                for (int val : cost) {
                    writer.write(val + "\t");
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildPuzzle3() {
        int size = 3;
        DataBaseBuilder dpd = new DataBaseBuilder();
        int[][] goalBoard1 = {
                {1, 2, 3},
                {4, 0, 0},
                {0, 0, 0}
        };
        PuzzleBoard goalState1 = new PuzzleBoard(size, goalBoard1);
        Problem subProblem1 = new NPuzzleProblem(goalState1, null, size);
        Node root1 = subProblem1.root(this.predictor);
        int[] cost1 = dpd.build(subProblem1, root1, size);
        dpd.save(cost1, "db3(1).txt");

        int[][] goalBoard2 = {
                {0, 0, 0},
                {0, 5, 6},
                {7, 8, 0}
        };
        PuzzleBoard goalState2 = new PuzzleBoard(size, goalBoard2);
        Problem subProblem2 = new NPuzzleProblem(goalState2, null, size);
        Node root2 = subProblem2.root(this.predictor);
        int[] cost2 = dpd.build(subProblem2, root2, size);
        dpd.save(cost2, "db3(2).txt");

        System.out.println("=========Puzzle3 Database Build Completed==========");
    }

    public void buildPuzzle4() {
        int size = 4;
        DataBaseBuilder dpd = new DataBaseBuilder();
        int[][] goalBoard1 = {
                {1, 0, 0, 0},
                {5, 6, 0, 0},
                {9, 10, 0, 0},
                {13, 0, 0, 0}
        };
        PuzzleBoard goalState1 = new PuzzleBoard(size, goalBoard1);
        Problem subProblem1 = new NPuzzleProblem(goalState1, null, size);
        Node root1 = subProblem1.root(this.predictor);
        int[] cost1 = dpd.build(subProblem1, root1, size);
        dpd.save(cost1, "db4(1).txt");
        System.out.println("---------Puzzle4 Part1 Completed---------");

        int[][] goalBoard2 = {
                {0, 0, 0, 0},
                {0, 0, 7, 8},
                {0, 0, 11, 12},
                {0, 14, 15, 0}
        };
        PuzzleBoard goalState2 = new PuzzleBoard(size, goalBoard2);
        Problem subProblem2 = new NPuzzleProblem(goalState2, null, size);
        Node root2 = subProblem2.root(this.predictor);
        int[] cost2 = dpd.build(subProblem2, root2, size);
        dpd.save(cost2, "db4(2).txt");
        System.out.println("---------Puzzle4 Part2 Completed---------");

        int[][] goalBoard3 = {
                {0, 2, 3, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        };
        PuzzleBoard goalState3 = new PuzzleBoard(size, goalBoard3);
        Problem subProblem3 = new NPuzzleProblem(goalState3, null, size);
        Node root3 = subProblem2.root(this.predictor);
        int[] cost3 = dpd.build(subProblem3, root3, size);
        dpd.save(cost3, "db4(3).txt");
        System.out.println("---------Puzzle4 Part3 Completed---------");


        System.out.println("=========Puzzle4 Database Build Completed==========");
    }

}
