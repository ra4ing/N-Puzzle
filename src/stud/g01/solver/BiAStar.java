package stud.g01.solver;

import java.util.*;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g01.problem.npuzzle.NPuzzleProblem;

// ******************************* //
public final class BiAStar extends AbstractSearcher {

    private final Frontier forwardFrontier;
    private final Frontier reverseFrontier;
    private final Set<State> reverseExplored;
    private final Set<State> forwardExplored;
    private final Map<State, Node> forwardStateNodeMap;
    private final Map<State, Node> reverseStateNodeMap;
    private Problem problem;
    private int minIntersectionCost;

    public BiAStar(Frontier forwardFrontier, Frontier reverseFrontier) {
        super(null);
        this.forwardFrontier = forwardFrontier;
        this.reverseFrontier = reverseFrontier;
        this.reverseExplored = new HashSet<>();
        this.forwardExplored = new HashSet<>();
        this.forwardStateNodeMap = new HashMap<>();
        this.reverseStateNodeMap = new HashMap<>();
        this.minIntersectionCost = Integer.MAX_VALUE;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        if (!problem.solvable()) {
            return null;
        }
        this.problem = problem;

        forwardFrontier.clear();
        reverseFrontier.clear();
        forwardExplored.clear();
        reverseExplored.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;
        minIntersectionCost = Integer.MAX_VALUE;

        Node root = problem.root();
        forwardFrontier.offer(root);

        Node goalNode = problem.goalNode();
        reverseFrontier.offer(goalNode);

        while (!forwardFrontier.isEmpty() || !reverseFrontier.isEmpty()) {
            Node forwardNode = forwardFrontier.poll();
            Node reverseNode = reverseFrontier.poll();

            if (forwardNode != null && forwardNode.equals(reverseNode)) {
                return generatePath(forwardNode, reverseNode);
            }

            if (forwardNode != null) {
                Deque<Node> forwardPath = searchHelper(forwardNode, reverseExplored, reverseStateNodeMap, forwardStateNodeMap, forwardExplored, forwardFrontier);
                if (forwardPath != null) {
                    return forwardPath;
                }
            }

            if (reverseNode != null) {
                Deque<Node> reversePath = searchHelper(reverseNode, reverseExplored, forwardStateNodeMap, reverseStateNodeMap, reverseExplored, reverseFrontier);
                if (reversePath != null) {
                    return reversePath;
                }
            }
        }

//        return generatePath(minIntersectionNode1, minIntersectionNode2);
        return null;
    }

    private Deque<Node> searchHelper(Node node, Set<State> oppositeExplored, Map<State, Node> oppositeStateNodeMap, Map<State, Node> thisStateNodeMap, Set<State> thisExplored, Frontier thisFrontier) {
        if (oppositeExplored.contains(node.getState())) {
            Node intersectingNode = oppositeStateNodeMap.get(node.getState());
            int currentIntersectionCost = node.getPathCost() + intersectingNode.getPathCost();
            if (currentIntersectionCost <= minIntersectionCost) {
                return generatePath(node, intersectingNode);
            } else {
                if (minIntersectionCost < Math.min(node.evaluation() + intersectingNode.evaluation(), minIntersectionCost)) {
                    minIntersectionCost = node.evaluation() + intersectingNode.evaluation();
                }

            }
        }


        thisExplored.add(node.getState());
        thisStateNodeMap.put(node.getState(), node);
        // 对节点node进行扩展 Expansion
        for (Node child : problem.childNodes(node, (state, goal) -> 0)) {
            nodesGenerated++;
            if (!thisExplored.contains(child.getState())) { // 如果新生成的节点（新扩展出的节点）还没有被扩展，则插入到frontier中。
                thisFrontier.offer(child);

            }
            // 如果已经扩展过，就舍弃掉。
        }
        nodesExpanded++;

        return null;
    }

    private Deque<Node> generatePath(Node forwardNode, Node reverseNode) {
        Deque<Node> path = new LinkedList<>();

        // 从正向节点回溯到起点，构建路径的前半部分
        Node currentNode = forwardNode;
        while (currentNode != null) {
            path.addFirst(currentNode);
            currentNode = currentNode.getParent();
        }

        // 从反向节点回溯到终点，构建路径的后半部分
        currentNode = reverseNode.getParent();
        Node previousNode = path.peekLast();

        while (currentNode != null) {
            // 反转操作
            path.addLast(NPuzzleProblem.reverseAction(previousNode, currentNode));
            previousNode = currentNode;
            currentNode = currentNode.getParent();
        }

        return path;
    }
}