package stud.g01.problem.npuzzle;

import core.problem.Action;

/**
 *
 * N-Puzzle的移动动作，根据情况，可以NSEW四个方向。
 */
public class Move extends Action {

    private final Direction direction;

    public Move(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void draw() {
        System.out.println(this.direction.symbol());
    }

    @Override
    public int stepCost() {
        return Direction.cost();
    }

    @Override
    public String toString() {
        return direction.name();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof Move another) {
            //两个Node对象的状态相同，则认为是相同的
            return this.direction.equals(another.direction);
        }
        return false;
    }
}
