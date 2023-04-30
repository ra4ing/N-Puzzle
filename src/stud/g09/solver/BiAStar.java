package stud.g09.solver;

import java.util.*;

import core.problem.Problem;
import core.problem.State;
import core.solver.algorithm.searcher.AbstractSearcher;
import core.solver.queue.Frontier;
import core.solver.queue.Node;
import stud.g09.problem.npuzzle.NPuzzleProblem;

// ******************************* //
public final class BiAStar extends AbstractSearcher {

    private final Frontier forwardFrontier; // 用于正向搜索的 Frontier
    private final Frontier reverseFrontier; // 用于反向搜索的 Frontier
    private final Set<State> reverseExplored; // 存储反向搜索中已探索的状态
    private final Set<State> forwardExplored; // 存储正向搜索中已探索的状态
    private final Map<State, Node> forwardStateNodeMap; // 存储正向搜索中状态到节点的映射
    private final Map<State, Node> reverseStateNodeMap; // 存储反向搜索中状态到节点的映射
    private Problem problem; // 当前需要解决的问题
    private int minIntersectionCost; // 两个搜索方向中最小交点的代价

    /**
     * 构造函数，初始化 BiAStar 对象。
     * @param forwardFrontier 用于正向搜索的 Frontier。
     * @param reverseFrontier 用于反向搜索的 Frontier。
     */
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

    /**
     * 使用双向 A* 算法搜索解决给定的问题。
     * @param problem 需要解决的问题。
     * @return 如果找到解决方案，则返回表示解决方案的节点队列；否则返回 null。
     */
    @Override
    public Deque<Node> search(Problem problem) {
        // 检查问题是否可解。如果不可解，返回 null
        if (!problem.solvable()) {
            return null;
        }
        this.problem = problem;

        // 初始化一些变量
        forwardFrontier.clear();
        reverseFrontier.clear();
        forwardExplored.clear();
        reverseExplored.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;
        minIntersectionCost = Integer.MAX_VALUE;

        // 将问题的根节点加入正向搜索的 frontier，将目标节点加入反向搜索的 frontier
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

            // 在每次迭代中，分别从正向搜索的frontier 和反向搜索的 frontier 中取出一个节点。检查这两个节点是否相等，
            // 如果相等，说明找到了交点，调用 generatePath 方法返回路径
            if (forwardNode != null) {
                Deque<Node> forwardPath = searchHelper(forwardNode, reverseExplored, reverseStateNodeMap,
                        forwardStateNodeMap, forwardExplored, forwardFrontier);
                if (forwardPath != null) {
                    return forwardPath;
                }
            }

            if (reverseNode != null) {
                Deque<Node> reversePath = searchHelper(reverseNode, reverseExplored, forwardStateNodeMap,
                        reverseStateNodeMap, reverseExplored, reverseFrontier);
                if (reversePath != null) {
                    return reversePath;
                }
            }
        }

//        return generatePath(minIntersectionNode1, minIntersectionNode2);
        return null;
    }

    /**
     * 双向搜索辅助函数，用于处理正向或反向搜索过程。
     * @param node 当前处理的节点。
     * @param oppositeExplored 相对方向的已探索状态集合。
     * @param oppositeStateNodeMap 相对方向的状态到节点的映射。
     * @param thisStateNodeMap 当前方向的状态到节点的映射。
     * @param thisExplored 当前方向的已探索状态集合。
     * @param thisFrontier 当前方向的 Frontier。
     * @return 如果找到解决方案，则返回表示解决方案的节点队列；否则返回 null。
     */
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

    /**
     * 生成从起始节点到目标节点的路径。
     * @param forwardNode 正向搜索找到的交点节点。
     * @param reverseNode 反向搜索找到的交点节点。
     * @return 表示从起始节点到目标节点的路径的节点队列。
     */
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