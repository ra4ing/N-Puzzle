package stud.g09.solver;

import core.problem.Problem;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Node;

import java.util.*;

public class IDAStar extends AbstractSearcher {
    private final Predictor predictor; // ���������ڵ������ʽ����
    private final PriorityQueue<Node> priorityQueue; // ���ڴ洢��������չ�ڵ�����ȼ�����
    private final Stack<Node> stack; // ���ڴ洢������ڵ��ջ

    /**
     * ���캯������ʼ�� IDAStar ����
     * @param predictor ���������ڵ������ʽ������
     */
    public IDAStar(Predictor predictor) {
        super(null); // �� IDA* �в���Ҫʹ�� frontier
        this.predictor = predictor;
        stack = new Stack<>();
        priorityQueue = new PriorityQueue<>((o1, o2) -> {
            int o1Evaluation = o1.evaluation();
            int o2Evaluation = o2.evaluation();
            if (o2Evaluation == o1Evaluation) {
                // ����ǳ���Node
                return o2.getPathCost() - o1.getPathCost();
            }
            return o2Evaluation - o1Evaluation;
        });
    }

    /**
     * ʹ�� IDA* �㷨����������������⡣
     * @param problem ��Ҫ��������⡣
     * @return ����ҵ�����������򷵻ر�ʾ��������Ľڵ���У����򷵻� null��
     */
    @Override
    public Deque<Node> search(Problem problem) {
        // ���ȼ�������Ƿ�ɽ⡣������ɽ⣬���� null��
        if (!problem.solvable()) {
            return null;
        }

        // ���ţ���ʼ��һЩ�������� bound��newBound ��
        stack.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;

        Node root = problem.root(predictor);
        int bound = root.evaluation(); // ��ǰ������ȵ�����
        int maxIteratorDepth = 256; // �������������
        int newBound; // ��һ�ε�����ȵ�����

        while (bound < maxIteratorDepth) {
            stack.push(root);
            newBound = bound;

            while (!stack.isEmpty()) {
                Node node = stack.pop();

                if (problem.goal(node.getState())) {
                    return generatePath(node);
                }

                // ���ɵ�ǰ�ڵ���ӽڵ㣬�������ӽڵ������ֵ�͵�ǰ bound �����жϡ�
                List<Node> childNodes = problem.childNodes(node, predictor);
                for (Node child : childNodes) {
                    nodesGenerated++;

                    // ����ӽڵ������ֵС�ڵ��� bound��˵���ӽڵ��ڵ�ǰ������ȵķ�Χ�ڣ�������ӵ����ȼ�����
                    if (child.evaluation() <= bound) {
                        // ���⽫��ǰ�ڵ�ĸ��ڵ�����ջ����Ϊ��ᵼ���ظ�����
                        if ((node.getParent() == null || !node.getParent().equals(child))) {
                            priorityQueue.add(child);
//                            stack.push(child);
                        }

                    } else { // ����ӽڵ������ֵ���� bound��˵���ӽڵ��ڵ�ǰ������ȵķ�Χ֮��
                        // Ϊ�˼��������ռ䣬��Ҫ���� newBound Ϊ�ӽڵ������ֵ�� newBound ֮��Ľ�Сֵ
                        newBound = (newBound > bound) ?
                                (Math.min(child.evaluation(), newBound)) : child.evaluation();
                    }
                }

                // �������ȼ������е������ӽڵ㣬�����ǰ����ȼ�˳������ջ�����ȼ����л��ڴ˹����б����
                while (!priorityQueue.isEmpty()) {
                    stack.push(priorityQueue.poll());
                }
                nodesExpanded++;
            }

            bound = newBound; // �ڵ�ǰ���������󣬸��� bound Ϊ newBound
        }

        // ����ڴﵽ��������ȣ�maxIteratorDepth��֮ǰ��δ�ҵ�����������򷵻� null
        return null;
    }
}


