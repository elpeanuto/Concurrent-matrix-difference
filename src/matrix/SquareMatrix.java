package matrix;

import exceptions.SquareMatrixException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SquareMatrix {
    private final int[][] matrix;

    SquareMatrix(int[][] matrix) {
        if (matrix == null)
            throw new SquareMatrixException("Matrix is null");
        if (matrix.length != matrix[0].length)
            throw new SquareMatrixException("This is not a square matrix");

        this.matrix = matrix.clone();
    }

    public static SquareMatrix generateMatrix(int length, int bottomBound, int upperBound) {
        if (length < 1)
            throw new SquareMatrixException(String.format("Length must be greater then 0, length: %d", length));

        return new SquareMatrix(randomMatrix(length, bottomBound, upperBound));
    }

    public static SquareMatrix matrixDifference(SquareMatrix matrixA, SquareMatrix matrixB) {
        if (matrixA.getSize() != matrixB.getSize())
            throw new SquareMatrixException("Lengths must be equals");

        int length = matrixA.getSize();
        int[][] resultMatrix = new int[length][length];

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                resultMatrix[i][j] = matrixA.matrix[i][j] - matrixB.matrix[i][j];
            }
        }

        return new SquareMatrix(resultMatrix);
    }

    public static SquareMatrix matrixDifferenceConcurrent(SquareMatrix matrixA, SquareMatrix matrixB, int numOfThread)
            throws InterruptedException {
        List<Thread> list = new ArrayList<>();

        int length = matrixA.getSize();
        int threadStep = length % numOfThread == 0 ? length / numOfThread : length / numOfThread + length % numOfThread;

        int[][] resultMatrix = new int[length][length];

        for (int i = 0; i < numOfThread * threadStep; i += threadStep) {
            Thread thread = new MatrixDifferenceByRows(resultMatrix, matrixA.matrix, matrixB.matrix, i, i + threadStep);
            thread.start();
            list.add(thread);
        }

        for (Thread thread : list) {
            thread.join();
        }

        return new SquareMatrix(resultMatrix);
    }

    public int[][] getMatrix() {
        return matrix.clone();
    }

    public int getSize() {
        return matrix.length;
    }

    private static int[][] randomMatrix(int length, int bottomBound, int upperBound) {
        int[][] matrix = new int[length][length];

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = (int) (Math.random() * (upperBound - bottomBound)) + bottomBound;
            }
        }

        return matrix;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(Arrays.toString(matrix.getClass().getFields())).append(":\n\n");

        for (int[] integers : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                str.append("\t").append(integers[j]).append(" ");
            }
            str.append("\n");
        }

        return str.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SquareMatrix that = (SquareMatrix) o;
        return Arrays.deepEquals(matrix, that.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(matrix);
    }
}
