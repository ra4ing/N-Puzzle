package core.runner;

import algs4.util.StopwatchCPU;
import core.problem.Problem;
import core.problem.ProblemType;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Node;
import core.solver.algorithm.heuristic.HeuristicType;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Scanner;

import static core.solver.algorithm.heuristic.HeuristicType.*;

import stud.g09.problem.npuzzle.NPuzzleProblem;
import stud.g09.runner.NPuzzlePerformanceTest;

/**
 * ��ѧ���������㷨���м���������
 * arg0: ������������      resources/problems.txt
 * arg1: ��������         NPUZZLE
 * arg2: ��Ŀ���ĸ��׶�    1
 * arg3: ��С���Feeder   stud.g09.runner.PuzzleFeeder
 */
public final class SearchTester {
    public static double totalTime; // һ�ֲ������廨�ѵ�ʱ��
    public static int totalNodesGenerated; // һ�ֲ����������ɵĽڵ�����
    public static int totalNodesExpanded; // һ�ֲ�������̽���Ľڵ�����
    //ͬѧ�ǿ��Ը����Լ�����Ҫ�������޸ġ�
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        //����args[3]�ṩ����������ѧ����EngineFeeder����
        EngineFeeder feeder = (EngineFeeder) Class.forName(args[3]).getDeclaredConstructor().newInstance();

        //���ļ��������������������ı��� args[0]�����������ļ������·��
        Scanner scanner = new Scanner(new File(args[0]));
        ArrayList<String> problemLines = getProblemLines(scanner);

        //feeder�����������ı���ȡѰ·���������ʵ��
        ArrayList<Problem> problems = feeder.getProblems(problemLines);

        //��ǰ��������� args[1]    Ѱ·���⣬�������̣�Ұ�˴���ʿ���ӵ�
        ProblemType type = ProblemType.valueOf(args[1]);
        //����ڼ��׶� args[2]
        int step = Integer.parseInt(args[2]);

        //�����������ͺ͵�ǰ�׶Σ���ȡ������������������
        //Ѱ·����ֱ�ʹ��Grid�����Euclid������Ϊ��������
        ArrayList<HeuristicType> heuristics = getHeuristicTypes(step);

        for (HeuristicType heuristicType : heuristics) {
            //solveProblems�������ݲ�ͬ�����������ɲ�ͬ��searcher
            //��Feeder��ȡ��ʹ�õ��������棨AStar��IDAStar�ȣ���
            if (step == 1) {
                solveProblems(problems, feeder.getAStar(heuristicType), heuristicType);
            } else if (step == 2) {
                solveProblems(problems, feeder.getIdaStar(heuristicType), heuristicType);
            } else if (step == 3) {
                solveProblems(problems, feeder.getIdaStarWithDisjoint(heuristicType), heuristicType);
//                solveProblems(problems, feeder.getBiAStar(heuristicType),heuristicType);
            } else {
                // �������ܵ����
                if (0 != (step & NPuzzlePerformanceTest.ASTAR)) {
                    solveProblems(problems, feeder.getAStar(heuristicType), heuristicType);
                }
                if (0 != (step & NPuzzlePerformanceTest.IDASTAR)){
                    solveProblems(problems, feeder.getIdaStar(heuristicType), heuristicType);
                }
            }
            System.out.println();
        }
    }

    /**
     * �����������ͺ͵�ǰ�׶Σ���ȡ������������������
     *
     * @param step �׶�
     * @return ����ʽ����
     */
    private static ArrayList<HeuristicType> getHeuristicTypes(int step) {
        //��⵱ǰ�����ڵ�ǰ�׶ο��õ��������������б�
        ArrayList<HeuristicType> heuristics = new ArrayList<>();

        //NPuzzle����ĵ�һ�׶κ͵ڶ��׶Σ�ʹ�ò���λ���ƺ������پ���
        if (step == 1 || step == 2) {
//            heuristics.add(MISPLACED);
            heuristics.add(MANHATTAN);
        }
        //NPuzzle����ĵ����׶Σ�ʹ��Disjoint Pattern
        else if (step == 3) {
            heuristics.add(DISJOINT_PATTERN);
//                heuristics.add(MANHATTAN);
        }
        // N-Puzzle ����Ĳ���
        else {
            // �������ܵ����
            if (0 != (step & NPuzzlePerformanceTest.MISPLACE)) {
                heuristics.add(MISPLACED);
            }
            if (0 != (step & NPuzzlePerformanceTest.MANHATTAN)){
                heuristics.add(MANHATTAN);
            }
            if (0 != (step & NPuzzlePerformanceTest.DISJOINT)){
                heuristics.add(DISJOINT_PATTERN);
            }
        }

        return heuristics;
    }

    /**
     * ʹ�ø�����searcher��������⼯���е��������⣬ͬʱʹ�ý���������õĽ���м��
     *
     * @param problems      ���⼯��
     * @param searcher      searcher
     * @param heuristicType ʹ����������������
     */
    private static void solveProblems(ArrayList<Problem> problems, AbstractSearcher searcher, HeuristicType heuristicType) throws IOException {
        int index = 1;
        for (Problem problem : problems) {
            // ʹ��AStar�����������
            StopwatchCPU timer1 = new StopwatchCPU();
            Deque<Node> path = searcher.search(problem);
            double time1 = timer1.elapsedTime();

            // ���ڲ��Ե���Ϣ
            totalTime += time1;
            totalNodesGenerated += searcher.nodesGenerated();
            totalNodesExpanded += searcher.nodesExpanded();


            System.out.println("-------------------------");
            System.out.println("problem: " + index);

            if (path == null) {
                System.out.println("No Solution" + "��ִ����" + time1 + "s��" + "��������" + searcher.nodesGenerated() + "����㣬" + "��չ��" + searcher.nodesExpanded() + "�����");
                continue;
            }

////             ��·���Ŀ��ӻ�
//            problem.showSolution(path);

            System.out.println("����������" + heuristicType + "����·�����ȣ�" + path.size() + "��ִ����" + time1 + "s��" + "��������" + searcher.nodesGenerated() + "����㣬" + "��չ��" + searcher.nodesExpanded() + "�����");

            // Ϊunityչʾ�ṩ����
            ((NPuzzleProblem)problem).showSolutionForUnity(path, index);
            index++;
        }
    }

    /**
     * ���ļ���������ʵ�����ַ����������ַ���������
     *
     * @param scanner ����
     * @return ������ַ���
     */
    public static ArrayList<String> getProblemLines(Scanner scanner) {
        ArrayList<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }
        return lines;
    }
}