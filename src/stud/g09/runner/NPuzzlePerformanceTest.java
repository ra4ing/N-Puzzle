package stud.g09.runner;

import core.runner.SearchTester;
import stud.g09.problem.npuzzle.DataBase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static core.runner.SearchTester.*;

public class NPuzzlePerformanceTest {
    private static final int TEST_CASES = 50; // �ܹ��Ĳ�����������

    // �����㷨������ʽ�����ı�ʶ��
    public static final int ASTAR = 4;
    public static final int IDASTAR = 8;
    public static final int MISPLACE = 16;
    public static final int MANHATTAN = 32;
    public static final int DISJOINT = 64;

    public static void main(String[] args) throws Exception {
        // ���������������
        ArrayList<String> problemLines3 = generateRandomTestCases(3);
        ArrayList<String> problemLines4 = generateRandomTestCases(4);

        // ���浽�ļ�
        saveProblems(problemLines3, 3);
        saveProblems(problemLines4, 4);

        // �������ܲ���
        runPerformanceTest();
    }

    /**
     * �������⵽�ļ�
     * @param problemLines ��������� ArrayList
     * @param size �����Ĵ�С
     */
    private static void saveProblems(ArrayList<String> problemLines, int size) {
        try {
            writeToFile(problemLines, "resources/problems_for_test_" + size + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * �������б�д�뵽ָ���ļ�
     * @param problemLines ��������� ArrayList
     * @param filePath �ļ�·��
     * @throws IOException ������� I/O ����
     */
    public static void writeToFile(ArrayList<String> problemLines, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter fileWriter = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            for (String line : problemLines) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
    }

    /**
     * ���������������
     * @param size �����Ĵ�С
     * @return ����������ɵĲ��������� ArrayList
     */
    private static ArrayList<String> generateRandomTestCases(int size) {
        ArrayList<String> problemLines = new ArrayList<>();

        for (int i = 0; i < NPuzzlePerformanceTest.TEST_CASES; i++) {
            problemLines.add(generateRandomTestCase(size));
        }

        return problemLines;
    }

    /**
     * ���ɵ��������������
     * @param size �����Ĵ�С
     * @return һ���ַ���������������ɵĲ�������
     */
    private static String generateRandomTestCase(int size) {
        StringBuilder testCase = new StringBuilder();
        testCase.append(size).append(" ");

        // ���ɳ�ʼ״̬
        boolean flag = true;
        while (flag) {
            ArrayList<Integer> initialState = new ArrayList<>();
            for (int i = 0; i < size * size; i++) {
                initialState.add(i);
            }
            Collections.shuffle(initialState);

            if (isSolvable(initialState, size)) {
                for (int i : initialState) {
                    testCase.append(i).append(" ");
                }
                flag = false;
            }
        }

        // ����Ŀ��״̬
        for (int i = 1; i <= size * size; i++) {
            testCase.append(i % (size * size)).append(" ");
        }

        return testCase.toString();
    }

    /**
     * �ж������Ƿ�ɽ�
     * @param puzzle ArrayList ���͵���������
     * @param size �����Ĵ�С
     * @return ��������ɽ⣬���� true�����򷵻� false
     */
    public static boolean isSolvable(ArrayList<Integer> puzzle, int size) {
        int inversionCount = 0;
        for (int i = 0; i < size * size - 1; i++) {
            if (puzzle.get(i) == 0) continue;

            for (int j = i + 1; j < size * size; j++) {
                if (puzzle.get(j) == 0) continue;
                if (puzzle.get(i) > puzzle.get(j)) inversionCount++;
            }
        }

        if (size % 2 == 1) {
            // �����С�������������inversionCount��ż������ɽ�
            return inversionCount % 2 == 0;
        } else {
            // �����С��ż���ģ��ҵ��հ�(0)���У��ӵײ���ʼ
            int blankRow = 0;
            for (int i = size * size - 1; i >= 0; i--) {
                if (puzzle.get(i) == 0) {
                    blankRow = (i / size) + 1;
                    break;
                }
            }
            // �����С��ż���������inversionCount�Ϳ��о�����ͬ����ż�ԣ���ɽ�
            return (inversionCount % 2) == (blankRow % 2);
        }
    }

    /**
     * �������ܲ���
     * @throws Exception ��������쳣
     */
    private static void runPerformanceTest() throws Exception {

        int[] testCombination = {ASTAR | MISPLACE, ASTAR | MANHATTAN, ASTAR | DISJOINT,
                IDASTAR | MISPLACE, IDASTAR | MANHATTAN};

        System.out.println("Performance Test Results:");
        for (int testCase : testCombination) {

            int size = 3;

            System.out.println("--------------------------------------------");

//            if (0 != (testCase & ASTAR)) {
//                System.out.print("AStar with ");
//                size = 3;
//            } else if (0 != (testCase & IDASTAR)) {
//                System.out.print("IDAStar with ");
//                size = 4;
//            }

            if (0 != (testCase & MISPLACE)) System.out.println("MisPlace");
            else if (0 != (testCase & MANHATTAN)) System.out.println("Manhattan");
            else if (0 != (testCase & DISJOINT)) {
                System.out.println("Disjoint Pattern");
                DataBase.load();
            }

            totalTime = 0;
            totalNodesGenerated = 0;
            totalNodesExpanded = 0;

            // Parse the input arguments
            String filePath = "resources/problems_for_test_" + size + ".txt";
            String[] args = new String[]{filePath, "NPUZZLE", String.valueOf(testCase), "stud.g09.runner.PuzzleFeeder"};

            // ���� searchTester
            SearchTester.main(args);

            // Print the performance results
            System.out.println("Total time: " + totalTime + "s");
            System.out.println("Average time: " + (totalTime / TEST_CASES) + "s");
            System.out.println("Total nodes generated: " + totalNodesGenerated);
            System.out.println("Average nodes generated: " + (totalNodesGenerated / TEST_CASES));
            System.out.println("Total nodes expanded: " + totalNodesExpanded);
            System.out.println("Average nodes expanded: " + (totalNodesExpanded / TEST_CASES));
            System.out.println("\n");
        }
    }

}
