package stud.g01.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g01.problem.npuzzle.NPuzzleProblem;
import stud.g01.problem.npuzzle.PuzzleBoard;
import stud.g01.queue.PqFrontier;

import java.util.ArrayList;
//Fix Me   //Fix Me
public class PuzzleFeeder extends EngineFeeder {

    /**
     * 根据存放问题输入样例的文本文件的内容，生成问题实例列表
     * @param problemLines  字符串数组，存放的是：问题输入样例的文本文件的内容
     * @return 生成的问题示例列表
     */
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {

        ArrayList<Problem> problems = new ArrayList<>();
        for (String problemLine : problemLines) {
            int index = 0;
            String[] cells = problemLine.split(" ");
            int size = Integer.parseInt(cells[index++]);

            int[][] map = getMap(cells, size, index);
            index += size * size;
            int[][] goal = getGoal(cells, size, index);

            NPuzzleProblem problem = getNPuzzle(map, goal, size);
            problems.add(problem);

        }
        return problems;
    }

    private NPuzzleProblem getNPuzzle(int[][] map, int[][]goal, int size) {
        PuzzleBoard initialState = new PuzzleBoard(size);
        initialState.setBoard(map);

        PuzzleBoard goalState = new PuzzleBoard(size);
        goalState.setBoard(goal);

//        for (int i=0; i<size; i++) {
//            for (int j=0; j<size; j++) {
//                System.out.print(initialState.getBoard()[i][j] + " ");
//            }
//            System.out.println();
//        }
//        System.out.println();

        return new NPuzzleProblem(initialState, goalState, size);
    }

    private int[][] getMap(String[] cells, int size, int index) {
        return getInts(cells, size, index);
    }

    private int[][] getInts(String[] cells, int size, int index) {
        int[][] map = new int[size][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                map[i][j] = Integer.parseInt(cells[index++]);
            }
        }
        return map;
    }

    private int[][] getGoal(String[] cells, int size, int index) {
        return getInts(cells, size, index);
    }

    /**
     * 生成采取某种估值机制的Frontier；与问题无关，
     *
     * @param type 结点评估器的类型
     * @return 使用评估机制的一个Frontier实例
     */
    @Override
    public Frontier getFrontier(EvaluationType type) {
        return new PqFrontier(Node.evaluator(type));
    }

    /**
     * 获得对状态进行估值的Predictor；不同问题有不同的估值函数
     *
     * @param type 不同问题的估值函数的类型
     * @return 启发函数
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return PuzzleBoard.predictor(type);
    }
}
