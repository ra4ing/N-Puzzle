package stud.g01.problem.npuzzle;

import java.util.EnumMap;
import java.util.List;

/**
 * ��ͼ�п����ƶ���8�����򣬼����ͷ����
 */
public enum Direction {
    N('��'),  //��
    E('��'),  //��
    S('��'),  //��
    W('��');  //��

    private final char symbol;

    /**
     * ���캯��
     *
     * @param symbol ö����Ĵ������--��ͷ
     */
    Direction(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }

    /**
     * �ƶ���������ֲ�ͬ�����4������8�����򣩡�
     */
    public static final List<Direction> FOUR_DIRECTIONS = List.of(Direction.N, Direction.E, Direction.S, Direction.W);

    /**
     * ��ͬ����ĺ�ɢֵ
     */
    public static int cost() {
        return 1;
    }

    //���������ƶ�������λ����
    private static final EnumMap<Direction, int[]> DIRECTION_OFFSET = new EnumMap<>(Direction.class);

    static {
        //�кţ�������꣩���������кţ��������꣩������
        DIRECTION_OFFSET.put(N, new int[]{-1, 0});
        DIRECTION_OFFSET.put(E, new int[]{0, 1});
        DIRECTION_OFFSET.put(S, new int[]{1, 0});
        DIRECTION_OFFSET.put(W, new int[]{0, -1});
    }

    public static int[] offset(Direction dir) {
        return DIRECTION_OFFSET.get(dir);
    }
}
