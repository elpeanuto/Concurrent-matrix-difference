package matrix;

public final class MatrixDifferenceByRows extends Thread {
    private final int[][] result;
    private final int[][] m1;
    private final int[][] m2;
    private final int start;
    private final int end;

    public MatrixDifferenceByRows(int[][] result, int[][] m1, int[][] m2, int start, int end) {
        this.result = result;
        this.m1 = m1;
        this.m2 = m2;
        this.start = start;
        this.end = Math.min(end, m1.length);
    }

    @Override
    public void run() {
        for (int i = start; i < end; i++) {
            for (int j = 0; j < m1.length; j++) {
                result[i][j] = m1[i][j] - m2[i][j];
            }
        }
    }
}