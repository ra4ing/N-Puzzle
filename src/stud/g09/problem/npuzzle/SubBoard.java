package stud.g09.problem.npuzzle;

public class SubBoard {
    public final Point[] points; // ��ǰ�������ϵ������
    private final int n; // �������ϵ�ĸ���
    private final int size; // ԭʼ���̵Ĵ�С
    private int pathCost; // �����̵��ﵱǰ״̬��·���ɱ�

    // �̶���λ�����飬���ڼ����ϣֵ
    public static final int[][] positions = {
            {-1, 0, 1, 2, 3, 0, 1, 2, 3},
            {-1, 0, 0, 1, 2, 1, 2, 0, 1, 3, 4, 2, 3, 5, 4, 5}};

    /**
     * SubBoard ���캯��
     * @param size ԭʼ���̵Ĵ�С
     * @param n �������ϵ�ĸ���
     * @param points �������ϵ������
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
     * �������캯��
     * @param oldSubBoard ��������������
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
     * �ж��ڸ����������Ƿ�����ƶ�
     * @param i Ҫ�ƶ��ĵ������
     * @param d Ҫ�ƶ��ķ���
     * @return �������ƶ������� true�����򷵻� false
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
     * �����������ƶ��㣬�������µ�������״̬
     * @param i Ҫ�ƶ��ĵ������
     * @param d Ҫ�ƶ��ķ���
     * @return �µ�������״̬
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


    // ��Ϊ0��ֵ��λ����Ϣ
    public static class Point {
        private int row; // ����
        private int col; // ����
        private final int val; // ֵ

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