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
 * 对学生的搜索算法进行检测的主程序
 * arg0: 问题输入样例      resources/problems.txt
 * arg1: 问题类型         NPUZZLE
 * arg2: 项目的哪个阶段    1
 * arg3: 各小组的Feeder   stud.g09.runner.PuzzleFeeder
 */
public final class SearchTester {
    public static double totalTime; // 一轮测试总体花费的时间
    public static int totalNodesGenerated; // 一轮测试总体生成的节点数量
    public static int totalNodesExpanded; // 一轮测试总体探索的节点数量
    //同学们可以根据自己的需要，随意修改。
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IOException {

        //根据args[3]提供的类名生成学生的EngineFeeder对象
        EngineFeeder feeder = (EngineFeeder) Class.forName(args[3]).getDeclaredConstructor().newInstance();

        //从文件读入所有输入样例的文本； args[0]：输入样例文件的相对路径
        Scanner scanner = new Scanner(new File(args[0]));
        ArrayList<String> problemLines = getProblemLines(scanner);

        //feeder从输入样例文本获取寻路问题的所有实例
        ArrayList<Problem> problems = feeder.getProblems(problemLines);

        //当前问题的类型 args[1]    寻路问题，数字推盘，野人传教士过河等
        ProblemType type = ProblemType.valueOf(args[1]);
        //任务第几阶段 args[2]
        int step = Integer.parseInt(args[2]);

        //根据问题类型和当前阶段，获取所有启发函数的类型
        //寻路问题分别使用Grid距离和Euclid距离作为启发函数
        ArrayList<HeuristicType> heuristics = getHeuristicTypes(step);

        for (HeuristicType heuristicType : heuristics) {
            //solveProblems方法根据不同启发函数生成不同的searcher
            //从Feeder获取所使用的搜索引擎（AStar，IDAStar等），
            if (step == 1) {
                solveProblems(problems, feeder.getAStar(heuristicType), heuristicType);
            } else if (step == 2) {
                solveProblems(problems, feeder.getIdaStar(heuristicType), heuristicType);
            } else if (step == 3) {
                solveProblems(problems, feeder.getIdaStarWithDisjoint(heuristicType), heuristicType);
//                solveProblems(problems, feeder.getBiAStar(heuristicType),heuristicType);
            } else {
                // 测试性能的组合
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
     * 根据问题类型和当前阶段，获取所有启发函数的类型
     *
     * @param step 阶段
     * @return 启发式函数
     */
    private static ArrayList<HeuristicType> getHeuristicTypes(int step) {
        //求解当前问题在当前阶段可用的启发函数类型列表
        ArrayList<HeuristicType> heuristics = new ArrayList<>();

        //NPuzzle问题的第一阶段和第二阶段，使用不在位将牌和曼哈顿距离
        if (step == 1 || step == 2) {
//            heuristics.add(MISPLACED);
            heuristics.add(MANHATTAN);
        }
        //NPuzzle问题的第三阶段，使用Disjoint Pattern
        else if (step == 3) {
            heuristics.add(DISJOINT_PATTERN);
//                heuristics.add(MANHATTAN);
        }
        // N-Puzzle 问题的测试
        else {
            // 测试性能的组合
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
     * 使用给定的searcher，求解问题集合中的所有问题，同时使用解检测器对求得的解进行检测
     *
     * @param problems      问题集合
     * @param searcher      searcher
     * @param heuristicType 使用哪种启发函数？
     */
    private static void solveProblems(ArrayList<Problem> problems, AbstractSearcher searcher, HeuristicType heuristicType) throws IOException {
        int index = 1;
        for (Problem problem : problems) {
            // 使用AStar引擎求解问题
            StopwatchCPU timer1 = new StopwatchCPU();
            Deque<Node> path = searcher.search(problem);
            double time1 = timer1.elapsedTime();

            // 用于测试的信息
            totalTime += time1;
            totalNodesGenerated += searcher.nodesGenerated();
            totalNodesExpanded += searcher.nodesExpanded();


            System.out.println("-------------------------");
            System.out.println("problem: " + index);

            if (path == null) {
                System.out.println("No Solution" + "，执行了" + time1 + "s，" + "共生成了" + searcher.nodesGenerated() + "个结点，" + "扩展了" + searcher.nodesExpanded() + "个结点");
                continue;
            }

////             解路径的可视化
//            problem.showSolution(path);

            System.out.println("启发函数：" + heuristicType + "，解路径长度：" + path.size() + "，执行了" + time1 + "s，" + "共生成了" + searcher.nodesGenerated() + "个结点，" + "扩展了" + searcher.nodesExpanded() + "个结点");

            // 为unity展示提供样例
            ((NPuzzleProblem)problem).showSolutionForUnity(path, index);
            index++;
        }
    }

    /**
     * 从文件读入问题实例的字符串，放入字符串数组里
     *
     * @param scanner 输入
     * @return 读入的字符串
     */
    public static ArrayList<String> getProblemLines(Scanner scanner) {
        ArrayList<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }
        return lines;
    }
}