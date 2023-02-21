import matrix.SquareMatrix;
import statistic.StatisticWriter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int BOTTOM_BOUND_CONTENT = 0;
    private static final int UPPER_BOUND_CONTENT = 100;
    private static final int NUM_OF_PHYSICAL_CORES = 4;
    private static final int NUM_OF_LOGICAL_CORES = 8;
    private static final String STATISTIC_DIR = "src/out";
    private static final String STATISTIC_FILENAME_PATTERN = "STATISTIC_FOR_MATRIX_SIZE_%d.txt";

    private static class ExecutionStatistics {
        private final long parallelTimeAsNano;
        private final long oneThreadTimeAsNano;
        private final double parallelTimeAsSec;
        private final double oneThreadTimeAsSec;
        private final long timeDifferenceAsNano;
        private final int numOfThreads;

        public ExecutionStatistics(long parallelTimeAsNano, long oneThreadTimeAsNano, int numOfThreads) {
            this.parallelTimeAsNano = parallelTimeAsNano;
            this.oneThreadTimeAsNano = oneThreadTimeAsNano;
            this.numOfThreads = numOfThreads;
            parallelTimeAsSec = parallelTimeAsNano / 1000_000_000.0;
            oneThreadTimeAsSec = oneThreadTimeAsNano / 1000_000_000.0;
            timeDifferenceAsNano = oneThreadTimeAsNano - parallelTimeAsNano;
        }

        public long getParallelTimeAsNano() {
            return parallelTimeAsNano;
        }

        public long getOneThreadTimeAsNano() {
            return oneThreadTimeAsNano;
        }

        public double getParallelTimeAsSec() {
            return parallelTimeAsSec;
        }

        public double getOneThreadTimeAsSec() {
            return oneThreadTimeAsSec;
        }

        public long getTimeDifferenceAsNano() {
            return timeDifferenceAsNano;
        }

        @Override
        public String toString() {
            return "Num of threads: " + numOfThreads +
                    "\nParallel time as nano:" + parallelTimeAsNano +
                    "\nOne thread time as nano: " + oneThreadTimeAsNano +
                    "\nParallel time as sec: " + parallelTimeAsSec +
                    "\nOne thread time as sec: " + oneThreadTimeAsSec +
                    "\nTime difference: " + timeDifferenceAsNano;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        executeLabTasks(10, 10, 100);
    }

    private static void executeLabTasks(int startMatrixSize, int matrixSizeStep, int endMatrixSize) throws IOException, InterruptedException {
        startMatrixSize = Math.abs(startMatrixSize);
        matrixSizeStep = Math.abs(matrixSizeStep);
        endMatrixSize = Math.abs(endMatrixSize);

        if(matrixSizeStep == 0)
            throw new RuntimeException("MatrixSizeStep must be greater than 0");

        if(endMatrixSize / matrixSizeStep > 20)
            throw new RuntimeException("Allowed number of steps 20, yours is: " + endMatrixSize / matrixSizeStep);

        File dir = new File(STATISTIC_DIR);

        StatisticWriter.clearDirectory(dir);

        for (int matrixSize = startMatrixSize; matrixSize <= endMatrixSize; matrixSize += matrixSizeStep) {
            if (!new File(dir, String.format(STATISTIC_FILENAME_PATTERN, matrixSize)).createNewFile())
                throw new RuntimeException("Failed to create file");
        }

        String fileName = String.format(STATISTIC_FILENAME_PATTERN, startMatrixSize);

        for (int matrixSize = startMatrixSize; matrixSize <= endMatrixSize; matrixSize += matrixSizeStep) {
            for (int numOfThreads = NUM_OF_PHYSICAL_CORES / 2; numOfThreads <= NUM_OF_LOGICAL_CORES * 16; numOfThreads *= 2) {
                StatisticWriter.writeToFile(dir.getPath(), fileName, statisticToString(matrixDifferenceTimeStatistic(numOfThreads, matrixSize)));
            }

            fileName = String.format(STATISTIC_FILENAME_PATTERN, matrixSize + matrixSizeStep);
        }
    }

    private static Boolean correctnessOfCalculations(int numOfThreads, int matrixSize) throws InterruptedException {
        SquareMatrix matrixA = SquareMatrix.generateMatrix(matrixSize, BOTTOM_BOUND_CONTENT, UPPER_BOUND_CONTENT);
        SquareMatrix matrixB = SquareMatrix.generateMatrix(matrixSize, BOTTOM_BOUND_CONTENT, UPPER_BOUND_CONTENT);

        SquareMatrix parallelRes = SquareMatrix.matrixDifferenceConcurrent(matrixA, matrixB, numOfThreads);
        SquareMatrix oneThreadRes = SquareMatrix.matrixDifference(matrixA, matrixB);

        return parallelRes.equals(oneThreadRes);
    }

    private static ExecutionStatistics matrixDifferenceTimeStatistic(int numOfThreads, int matrixSize) throws InterruptedException {
        SquareMatrix matrixA = SquareMatrix.generateMatrix(matrixSize, BOTTOM_BOUND_CONTENT, UPPER_BOUND_CONTENT);
        SquareMatrix matrixB = SquareMatrix.generateMatrix(matrixSize, BOTTOM_BOUND_CONTENT, UPPER_BOUND_CONTENT);

        long startTime = System.nanoTime();

        SquareMatrix.matrixDifferenceConcurrent(matrixA, matrixB, numOfThreads);

        long endTime = System.nanoTime();
        long result1 = endTime - startTime;

        long startTime1 = System.nanoTime();

        SquareMatrix.matrixDifference(matrixA, matrixB);

        long endTime1 = System.nanoTime();
        long result2 = endTime1 - startTime1;

        return new ExecutionStatistics(result1, result2, numOfThreads);
    }

    private static String statisticToString(ExecutionStatistics executionStatistics) {

        return executionStatistics.toString() +
                "\nBottom bound: " + BOTTOM_BOUND_CONTENT +
                "\nUpper bound: " + UPPER_BOUND_CONTENT +
                "\n----------------------------\n";
    }
}