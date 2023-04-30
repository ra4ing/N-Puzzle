package stud.g09.runner;

import core.problem.Problem;
import core.runner.EngineFeeder;
import core.solver.algorithm.heuristic.HeuristicType;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.queue.EvaluationType;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g09.problem.npuzzle.NPuzzleProblem;
import stud.g09.problem.npuzzle.PuzzleBoard;
import stud.g09.queue.PqFrontier;

import java.util.ArrayList;

/**
 * PuzzleFeeder �����ڸ����������ݴ�����������ʵ������������Ӧ�� Frontier �� Predictor��
 */
public class PuzzleFeeder extends EngineFeeder {

    /**
     * ���ݴ�����������������ı��ļ������ݣ���������ʵ���б�
     * @param problemLines �ַ������飬��ŵ��ǣ����������������ı��ļ������ݡ�
     * @return ���ɵ�����ʵ���б�
     */
    @Override
    public ArrayList<Problem> getProblems(ArrayList<String> problemLines) {

        ArrayList<Problem> problems = new ArrayList<>();
        // ���� problemLines����ÿһ��ת��Ϊһ�� NPuzzleProblem ʵ�������Ƚ��� size��
        // Ȼ���ȡ��ʼ״̬��Ŀ��״̬�ľ�����󣬴���һ�� NPuzzleProblem ������ӵ������б��С�
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
        PuzzleBoard initialState = new PuzzleBoard(size, map);
        PuzzleBoard goalState = new PuzzleBoard(size, goal);

        return new NPuzzleProblem(initialState, goalState, size);
    }

    private int[][] getMap(String[] cells, int size, int index) {
        return getInts(cells, size, index);
    }

    private int[][] getGoal(String[] cells, int size, int index) {
        return getInts(cells, size, index);
    }

    private int[][] getInts(String[] cells, int size, int index) {

        // �������ַ������� cells �У����ո����� size ����ʼ���� index����ȡ��ת�������������Ǵ洢��һ����ά���������С�
        int[][] map = new int[size][size];
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                map[i][j] = Integer.parseInt(cells[index++]);
            }
        }
        return map;
    }



    /**
     * ���ɲ�ȡĳ�ֹ�ֵ���Ƶ�Frontier���������޹أ�
     *
     * @param type ���������������
     * @return ʹ���������Ƶ�һ��Frontierʵ��
     */
    @Override
    public Frontier getFrontier(EvaluationType type) {
        return new PqFrontier(Node.evaluator(type));
    }

    /**
     * ��ö�״̬���й�ֵ��Predictor����ͬ�����в�ͬ�Ĺ�ֵ����
     *
     * @param type ��ͬ����Ĺ�ֵ����������
     * @return ��������
     */
    @Override
    public Predictor getPredictor(HeuristicType type) {
        return PuzzleBoard.predictor(type);
    }
}
