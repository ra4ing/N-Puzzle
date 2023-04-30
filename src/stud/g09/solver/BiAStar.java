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

    private final Frontier forwardFrontier; // �������������� Frontier
    private final Frontier reverseFrontier; // ���ڷ��������� Frontier
    private final Set<State> reverseExplored; // �洢������������̽����״̬
    private final Set<State> forwardExplored; // �洢������������̽����״̬
    private final Map<State, Node> forwardStateNodeMap; // �洢����������״̬���ڵ��ӳ��
    private final Map<State, Node> reverseStateNodeMap; // �洢����������״̬���ڵ��ӳ��
    private Problem problem; // ��ǰ��Ҫ���������
    private int minIntersectionCost; // ����������������С����Ĵ���

    /**
     * ���캯������ʼ�� BiAStar ����
     * @param forwardFrontier �������������� Frontier��
     * @param reverseFrontier ���ڷ��������� Frontier��
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
     * ʹ��˫�� A* �㷨����������������⡣
     * @param problem ��Ҫ��������⡣
     * @return ����ҵ�����������򷵻ر�ʾ��������Ľڵ���У����򷵻� null��
     */
    @Override
    public Deque<Node> search(Problem problem) {
        // ��������Ƿ�ɽ⡣������ɽ⣬���� null
        if (!problem.solvable()) {
            return null;
        }
        this.problem = problem;

        // ��ʼ��һЩ����
        forwardFrontier.clear();
        reverseFrontier.clear();
        forwardExplored.clear();
        reverseExplored.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;
        minIntersectionCost = Integer.MAX_VALUE;

        // ������ĸ��ڵ�������������� frontier����Ŀ��ڵ���뷴�������� frontier
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

            // ��ÿ�ε����У��ֱ������������frontier �ͷ��������� frontier ��ȡ��һ���ڵ㡣����������ڵ��Ƿ���ȣ�
            // �����ȣ�˵���ҵ��˽��㣬���� generatePath ��������·��
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
     * ˫�������������������ڴ�����������������̡�
     * @param node ��ǰ����Ľڵ㡣
     * @param oppositeExplored ��Է������̽��״̬���ϡ�
     * @param oppositeStateNodeMap ��Է����״̬���ڵ��ӳ�䡣
     * @param thisStateNodeMap ��ǰ�����״̬���ڵ��ӳ�䡣
     * @param thisExplored ��ǰ�������̽��״̬���ϡ�
     * @param thisFrontier ��ǰ����� Frontier��
     * @return ����ҵ�����������򷵻ر�ʾ��������Ľڵ���У����򷵻� null��
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
        // �Խڵ�node������չ Expansion
        for (Node child : problem.childNodes(node, (state, goal) -> 0)) {
            nodesGenerated++;
            if (!thisExplored.contains(child.getState())) { // ��������ɵĽڵ㣨����չ���Ľڵ㣩��û�б���չ������뵽frontier�С�
                thisFrontier.offer(child);

            }
            // ����Ѿ���չ��������������
        }
        nodesExpanded++;

        return null;
    }

    /**
     * ���ɴ���ʼ�ڵ㵽Ŀ��ڵ��·����
     * @param forwardNode ���������ҵ��Ľ���ڵ㡣
     * @param reverseNode ���������ҵ��Ľ���ڵ㡣
     * @return ��ʾ����ʼ�ڵ㵽Ŀ��ڵ��·���Ľڵ���С�
     */
    private Deque<Node> generatePath(Node forwardNode, Node reverseNode) {
        Deque<Node> path = new LinkedList<>();

        // ������ڵ���ݵ���㣬����·����ǰ�벿��
        Node currentNode = forwardNode;
        while (currentNode != null) {
            path.addFirst(currentNode);
            currentNode = currentNode.getParent();
        }

        // �ӷ���ڵ���ݵ��յ㣬����·���ĺ�벿��
        currentNode = reverseNode.getParent();
        Node previousNode = path.peekLast();

        while (currentNode != null) {
            // ��ת����
            path.addLast(NPuzzleProblem.reverseAction(previousNode, currentNode));
            previousNode = currentNode;
            currentNode = currentNode.getParent();
        }

        return path;
    }
}