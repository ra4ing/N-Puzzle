package stud.g09.solver;

import core.problem.Problem;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Node;

import java.util.*;

public class IDAStar extends AbstractSearcher {
    private final Predictor predictor; // 用于评估节点的启发式函数
    private final PriorityQueue<Node> priorityQueue; // 用于存储和排序扩展节点的优先级队列
    private final Stack<Node> stack; // 用于存储待处理节点的栈

    /**
     * 构造函数，初始化 IDAStar 对象。
     * @param predictor 用于评估节点的启发式函数。
     */
    public IDAStar(Predictor predictor) {
        super(null); // 在 IDA* 中不需要使用 frontier
        this.predictor = predictor;
        stack = new Stack<>();
        priorityQueue = new PriorityQueue<>((o1, o2) -> {
            int o1Evaluation = o1.evaluation();
            int o2Evaluation = o2.evaluation();
            if (o2Evaluation == o1Evaluation) {
                // 返回浅层的Node
                return o2.getPathCost() - o1.getPathCost();
            }
            return o2Evaluation - o1Evaluation;
        });
    }

    /**
     * 使用 IDA* 算法搜索解决给定的问题。
     * @param problem 需要解决的问题。
     * @return 如果找到解决方案，则返回表示解决方案的节点队列；否则返回 null。
     */
    @Override
    public Deque<Node> search(Problem problem) {
        // 首先检查问题是否可解。如果不可解，返回 null。
        if (!problem.solvable()) {
            return null;
        }

        // 接着，初始化一些变量，如 bound、newBound 等
        stack.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;

        Node root = problem.root(predictor);
        int bound = root.evaluation(); // 当前迭代深度的限制
        int maxIteratorDepth = 256; // 最大迭代深度限制
        int newBound; // 下一次迭代深度的限制

        while (bound < maxIteratorDepth) {
            stack.push(root);
            newBound = bound;

            while (!stack.isEmpty()) {
                Node node = stack.pop();

                if (problem.goal(node.getState())) {
                    return generatePath(node);
                }

                // 生成当前节点的子节点，并根据子节点的评估值和当前 bound 进行判断。
                List<Node> childNodes = problem.childNodes(node, predictor);
                for (Node child : childNodes) {
                    nodesGenerated++;

                    // 如果子节点的评估值小于等于 bound，说明子节点在当前迭代深度的范围内，将其添加到优先级队列
                    if (child.evaluation() <= bound) {
                        // 避免将当前节点的父节点推入栈，因为这会导致重复访问
                        if ((node.getParent() == null || !node.getParent().equals(child))) {
                            priorityQueue.add(child);
//                            stack.push(child);
                        }

                    } else { // 如果子节点的评估值大于 bound，说明子节点在当前迭代深度的范围之外
                        // 为了减少搜索空间，需要更新 newBound 为子节点的评估值和 newBound 之间的较小值
                        newBound = (newBound > bound) ?
                                (Math.min(child.evaluation(), newBound)) : child.evaluation();
                    }
                }

                // 对于优先级队列中的所有子节点，将它们按优先级顺序推入栈。优先级队列会在此过程中被清空
                while (!priorityQueue.isEmpty()) {
                    stack.push(priorityQueue.poll());
                }
                nodesExpanded++;
            }

            bound = newBound; // 在当前迭代结束后，更新 bound 为 newBound
        }

        // 如果在达到最大迭代深度（maxIteratorDepth）之前仍未找到解决方案，则返回 null
        return null;
    }
}


