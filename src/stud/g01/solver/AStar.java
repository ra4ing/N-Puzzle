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
    private final Set<State> reverseExplored; // ������������̽������
    private final Set<State> forwardExplored; // ������������̽������
    private final Map<State, Node> stateNodeMap;
    private Problem problem;
    private int minIntersectionCost; // ��С�������

    /**
     * ���캯��
     *
     * @param forwardFrontier  ����������Node�������ȶ���
     * @param reverseFrontier  ����������Node�������ȶ���
     * @param forwardPredictor �����Ԥ����������λ���ƣ������پ���ȣ�
     * @param reversePredictor �����Ԥ����������λ���ƣ������پ���ȣ�
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
        // ���ж������Ƿ�ɽ⣬�޽�ʱֱ�ӷ��ؽ�·��Ϊnull
        if (!problem.solvable()) {
            return null;
        }
        this.problem = problem;

        // ÿ���µ�������ʼǰ���������������Frontier��Explored������
        forwardFrontier.clear();
        reverseFrontier.clear();
        forwardExplored.clear();
        reverseExplored.clear();
        nodesExpanded = 0;
        nodesGenerated = 0;
        minIntersectionCost = Integer.MAX_VALUE;

        // ��ʼ�ڵ�root
        Node root = problem.root(forwardPredictor);
        forwardFrontier.offer(root);

        Node goalNode = problem.goalNode(reversePredictor);
        reverseFrontier.offer(goalNode);

        // ����...
        while (!forwardFrontier.isEmpty() || !reverseFrontier.isEmpty()) {

            // ���������ȶ���frontier��ȡ����ֵ��С�Ľڵ�
            Node forwardNode = forwardFrontier.poll();
            if (forwardNode != null) {
                Deque<Node> forwardPath = searchHelper(forwardNode, reverseExplored, forwardExplored, forwardFrontier, forwardPredictor);
                if (forwardPath != null) {
                    return forwardPath;
                }
            }

            // �ӷ������ȶ���reverseFrontier��ȡ����ֵ��С�Ľڵ�
            Node reverseNode = reverseFrontier.poll();
            if (reverseNode != null) {
                Deque<Node> reversePath = searchHelper(reverseNode, forwardExplored, reverseExplored, reverseFrontier, reversePredictor);
                if (reversePath != null) {
                    return reversePath;
                }
            }
        }

        // ������������ȶ��ж�Ϊ�գ�˵���Ҳ������Ž�
        return null;
    }

    private Deque<Node> searchHelper(Node node, Set<State> oppositeExplored, Set<State> thisExplored, Frontier thisFrontier, Predictor thisPredictor) {
        // ����Ƿ����Ŀ��״̬������һ������������ཻ
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

        // �Խڵ�node������չ Expansion
        for (Node child : problem.childNodes(node, thisPredictor)) {
            nodesGenerated++;
            if (!thisExplored.contains(child.getState())) { // ��������ɵĽڵ㣨����չ���Ľڵ㣩��û�б���չ������뵽frontier�С�
                thisFrontier.offer(child);
            }
            // ����Ѿ���չ��������������
        }
        nodesExpanded++;

        return null;
    }

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