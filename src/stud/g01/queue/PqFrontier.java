package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PqFrontier extends PriorityQueue<Node> implements Frontier {
    private final Comparator<Node> evaluator;
    private final Map<Integer, Node> stateNodeMap;

    public PqFrontier(Comparator<Node> evaluator) {
        super(evaluator);
        this.evaluator = evaluator;
        this.stateNodeMap = new HashMap<>();
    }

    @Override
    public Node poll() {
        Node node = super.poll();
        if (node != null) {
            stateNodeMap.remove(node.getState().hashCode());
        }
        return node;
    }

    @Override
    public boolean offer(Node node) {
        Node oldNode = stateNodeMap.get(node.getState().hashCode());
        if (oldNode != null) {
            return discardOrReplace(oldNode, node);
        } else {
            super.offer(node);
            stateNodeMap.put(node.getState().hashCode(), node);
            return true;
        }
//        return super.add(node);
    }

    private boolean discardOrReplace(Node oldNode, Node node) {
        if (evaluator.compare(oldNode, node) > 0) {
            replace(oldNode, node);
            return true;
        }
        return false;
    }

    private void replace(Node oldNode, Node newNode) {
        super.remove(oldNode);
        super.offer(newNode);
        stateNodeMap.put(newNode.getState().hashCode(), newNode);
    }

    @Override
    public boolean contains(Node node) {
        return stateNodeMap.containsKey(node.getState().hashCode());
    }


}
