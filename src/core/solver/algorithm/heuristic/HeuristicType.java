package core.solver.algorithm.heuristic;

public enum HeuristicType {
    //Npuzzle����������
    MISPLACED,  // ����λ����
    MANHATTAN,  // �����پ���
    MANLINEARCONFLICT, //������ + ���Գ�ͻ
    DISJOINT_PATTERN,
    EUCLID,
    MANHATTAN_FOR_BI,
    NOTHING,

    //PathFinding���������� (8��������)
    PF_EUCLID,      // ŷ����þ���
    PF_MANHATTAN,   // 8�����ƶ�ʱ������admissible��
    PF_GRID,        // �������߶Խ��ߣ�Ȼ��ƽ���ߣ�>= EUCLID, ��չ�����

    //Ұ�˴���ʿ����
    MC_HARMONY  //ȥ��Ұ�˻���˵�Լ��

}
