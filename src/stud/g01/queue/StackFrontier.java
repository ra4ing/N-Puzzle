package stud.g01.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.*;

public class StackFrontier extends Stack<Node> implements Frontier {

    private final Comparator<Node> evaluator;
    private final Map<Integer, Integer> stateNodeMap;


    public StackFrontier(Comparator<Node> evaluator) {
        super();
        this.evaluator = evaluator;
        this.stateNodeMap = new HashMap<>();
    }

    @Override
    public Node poll() {
        Node node = super.pop();
        if (node != null) {
            stateNodeMap.remove(node.getState().hashCode());
        }
        return node;
    }

    @Override
    public boolean contains(Node node) {
        return stateNodeMap.containsKey(node.getState().hashCode());
    }

    @Override
    public boolean offer(Node node) {
//        Integer index = stateNodeMap.get(node.getState().hashCode());
//
//        if (index != null) {
//            return discardOrReplace(this.get(index), node);
//        } else {
//            stateNodeMap.put(node.getState().hashCode(), this.size());
//            super.add(node);
//            return true;
//        }
        return super.add(node);
    }

    private boolean discardOrReplace(Node oldNode, Node node) {
        if (evaluator.compare(oldNode, node) > 0) {
            replace(oldNode, node);
            return true;
        }
        return false;
    }

    private void replace(Node oldNode, Node newNode) {
        int index = stateNodeMap.get(oldNode.getState().hashCode());
        if (index >= 0 && index < this.size()) {
            stateNodeMap.put(newNode.getState().hashCode(), index);
            super.set(index, newNode);
        }
    }
}
