package stud.g01.solver;

import java.util.*;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.heuristic.Predictor;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g01.problem.npuzzle.NPuzzleProblem;

public final class AStar extends AbstractSearcher {

    private final Frontier forwardFrontier;
    private final Frontier reverseFrontier;
    private final Predictor forwardPredictor;
    private final Predictor reversePredictor;
    private final Set<State> reverseExplored; // 反向搜索的已探索集合
    private final Set<State> forwardExplored; // 正向搜索的已探索集合
    private final Map<State, Node> stateNodeMap;
    private Problem problem;
    private int minIntersectionCost; // 最小交叉代价

    /**
     * 构造函数
     *
     * @param forwardFrontier  正向搜索的Node对象优先队列
     * @param reverseFrontier  反向搜索的Node对象优先队列
     * @param forwardPredictor 正向的预测器（不在位将牌，曼哈顿距离等）
     * @param reversePredictor 反向的预测器（不在位将牌，曼哈顿距离等）
     */
    public AStar(Frontier forwardFrontier, Frontier reverseFrontier, Predictor forwardPredictor, Predictor reversePredictor) {
        super(null);
        this.forwardFrontier = forwardFrontier;
        this.reverseFrontier = reverseFrontier;
        this.forwardPredictor = forwardPredictor;
        this.reversePredictor = reversePredictor;
        this.reverseExplored = new HashSet<>();
        this.forwardExplored = new HashSet<>();
        this.stateNodeMap = new HashMap<>();
        this.minIntersectionCost = Integer.MAX_VALUE;
    }

    @Override
    public Deque<Node> search(Problem problem) {
        // 先判断问题是否可解，无解时直接返回解路径为null
        if (!problem.solvable()) {
            return null;
        }
        this.problem = problem;

        // 每次新的搜索开始前，先清理掉正反向Frontier和Explored的内容
        forwardFrontier.clear();
        reverseFrontier.clear();
        forwardExplored.clear();
        reverseExplored.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;
        minIntersectionCost = Integer.MAX_VALUE;

        // 起始节点root
        Node root = problem.root(forwardPredictor);
        forwardFrontier.offer(root);

        Node goalNode = problem.goalNode(reversePredictor);
        reverseFrontier.offer(goalNode);

        // 搜索...
        while (!forwardFrontier.isEmpty() || !reverseFrontier.isEmpty()) {

            // 从正向优先队列frontier中取出估值最小的节点
            Node forwardNode = forwardFrontier.poll();
            if (forwardNode != null) {
                Deque<Node> forwardPath = searchHelper(forwardNode, reverseExplored, forwardExplored, forwardFrontier, forwardPredictor);
                if (forwardPath != null) {
                    return forwardPath;
                }
            }

            // 从反向优先队列reverseFrontier中取出估值最小的节点
            Node reverseNode = reverseFrontier.poll();
            if (reverseNode != null) {
                Deque<Node> reversePath = searchHelper(reverseNode, forwardExplored, reverseExplored, reverseFrontier, reversePredictor);
                if (reversePath != null) {
                    return reversePath;
                }
            }
        }

        // 如果正反向优先队列都为空，说明找不到最优解
        return null;
    }

    private Deque<Node> searchHelper(Node node, Set<State> oppositeExplored, Set<State> thisExplored, Frontier thisFrontier, Predictor thisPredictor) {
        // 检查是否进入目标状态或与另一个方向的搜索相交
        if (oppositeExplored.contains(node.getState())) {
            Node intersectingNode = stateNodeMap.get(node.getState());
            int intersectionCost = node.getPathCost() + intersectingNode.getPathCost();

            if (intersectionCost < minIntersectionCost) {
                minIntersectionCost = intersectionCost;
                return generatePath(node, intersectingNode);
            }
        }

        thisExplored.add(node.getState());
        stateNodeMap.put(node.getState(), node);

        // 对节点node进行扩展 Expansion
        for (Node child : problem.childNodes(node, thisPredictor)) {
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