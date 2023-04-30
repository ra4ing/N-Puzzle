package stud.g09.problem.npuzzle;

public class SubBoard {
    public final Point[] points; // 当前子棋盘上点的数组
    private final int n; // 子棋盘上点的个数
    private final int size; // 原始棋盘的大小
    private int pathCost; // 子棋盘到达当前状态的路径成本

    // 固定的位置数组，用于计算哈希值
    public static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}};

    /**
     * SubBoard 构造函数
     * @param size 原始棋盘的大小
     * @param n 子棋盘上点的个数
     * @param points 子棋盘上点的数组
     */
    public SubBoard(int size, int n, Point[] points) {
        this.n = n;
        this.size = size;
        this.points = new Point[n];
        for (int i = 0; i < n; i++) {
            this.points[i] = new Point(points[i]);
        }
    }

    /**
     * 拷贝构造函数
     * @param oldSubBoard 被拷贝的子棋盘
     */
    public SubBoard(SubBoard oldSubBoard) {
        this.n = oldSubBoard.n;
        this.size = oldSubBoard.getSize();
        this.points = new Point[this.n];
        for (int i = 0; i < this.n; i++) {
            this.points[i] = new Point(oldSubBoard.points[i]);
        }
         this.pathCost = oldSubBoard.pathCost;
    }

    public int getN() {
        return this.n;
    }

    public int getSize() {
        return this.size;
    }

    public int getPathCost() {
        return this.pathCost;
    }

    /**
     * 判断在给定方向上是否可以移动
     * @param i 要移动的点的索引
     * @param d 要移动的方向
     * @return 若可以移动，返回 true；否则返回 false
     */
    public boolean applicable(int i, Direction d) {
        int[] offsets = Direction.offset(d);
        int goRow = points[i].getRow() + offsets[0];
        int goCol = points[i].getCol() + offsets[1];

        if (goRow < 0 || goRow >= size || goCol < 0 || goCol >= size)
            return false;
        for (int j = 0; j < n; j++) {
            if (points[j].getRow() == goRow && points[j].getCol() == goCol) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (int i = 0; i < this.n; i++) {
            hash += (points[i].getRow() * size + points[i].getCol()) *
                    Math.pow(size * size, positions[size - 3][points[i].getVal()]);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (obj instanceof SubBoard another) {
            for (int i = 0; i < n; i++) {
                if (!this.points[i].equals(another.points[i]))
                    return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 按给定方向移动点，并返回新的子棋盘状态
     * @param i 要移动的点的索引
     * @param d 要移动的方向
     * @return 新的子棋盘状态
     */
    public SubBoard move(int i, Direction d) {
        SubBoard state = new SubBoard(this);
        int[] offsets = Direction.offset(d);
        int goRow = points[i].getRow() + offsets[0];
        int goCol = points[i].getCol() + offsets[1];

        state.points[i].setRow(goRow);
        state.points[i].setCol(goCol);
        state.pathCost = this.pathCost + 1;
        return state;
    }


    // 不为0的值的位置信息
    public static class Point {
        private int row; // 行数
        private int col; // 列数
        private final int val; // 值

        public Point(int row, int col, int val) {
            this.row = row;
            this.col = col;
            this.val = val;
        }

        public Point(Point newPoint) {
            this.row = newPoint.getRow();
            this.col = newPoint.getCol();
            this.val = newPoint.getVal();
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getVal() {
            return val;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setCol(int col) {
            this.col = col;
        }
    }
}
