package stud.g01.solver;

import core.problem.Problem;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Node;

import java.util.*;

public class IDAStar extends AbstractSearcher {
    private final Predictor predictor;
    private final PriorityQueue<Node> priorityQueue;
    private final Stack<Node> stack;

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

    @Override
    public Deque<Node> search(Problem problem) {
        if (!problem.solvable()) {
            return null;
        }

        stack.clear();
//        nodesExpanded = 0;
//        nodesGenerated = 0;

        Node root = problem.root(predictor);
        int bound = root.evaluation();
        int maxIteratorDepth = 256;
        int newBound;

        while (bound < maxIteratorDepth) {
            stack.push(root);
            newBound = bound;

            while (!stack.isEmpty()) {
                Node node = stack.pop();

                if (problem.goal(node.getState())) {
                    return generatePath(node);
                }

                List<Node> childNodes = problem.childNodes(node, predictor);
                for (Node child : childNodes) {
//                    nodesGenerated++;
                    if (child.evaluation() <= bound) {
                        if ((node.getParent() == null || !node.getParent().equals(child))) {
                            priorityQueue.add(child);
                        }

                    } else {
                        newBound = (newBound > bound) ? (Math.min(child.evaluation(), newBound)) : child.evaluation();
                    }
                }
                while (!priorityQueue.isEmpty()) {
                    stack.push(priorityQueue.poll());
                }
//                nodesExpanded++;
            }
            bound = newBound;
        }
        return null;
    }
}


