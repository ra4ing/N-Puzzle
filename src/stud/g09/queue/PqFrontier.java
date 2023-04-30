package stud.g09.queue;

import core.solver.queue.Frontier;
import core.solver.queue.Node;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class PqFrontier extends PriorityQueue<Node> implements Frontier {
    private final Comparator<Node> evaluator; // 用于比较节点优先级的评估器
    private final Map<Integer, Node> stateNodeMap; // 用于存储状态节点的映射关系，其中键为状态的哈希值，值为对应的节点

    /**
     * PqFrontier 构造函数
     * @param evaluator 用于比较节点优先级的评估器
     */
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

    /**
     * 判断队列中是否有该节点，若没有则直接插入，若有则判断是否丢弃或替换
     * @param node 传入的节点
     */
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

    /**
     * 选择是否丢弃或替换节点
     * @param oldNode 已经存在的节点
     * @param node 新的节点
     * @return 若替换节点，返回 true；否则返回 false
     */
    private boolean discardOrReplace(Node oldNode, Node node) {
        if (evaluator.compare(oldNode, node) > 0) {
            replace(oldNode, node);
            return true;
        }
        return false;
    }

    /**
     * 替换节点
     * @param oldNode 被替换的节点
     * @param newNode 替换的新节点
     */
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
