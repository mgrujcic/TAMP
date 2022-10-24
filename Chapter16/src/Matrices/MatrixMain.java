package Matrices;

import java.util.concurrent.ForkJoinPool;

public class MatrixMain {
    public static void main(String[] args) {
        int N = 16;

        double[][] matrixA = new double[N][N];
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                matrixA[i][j] = j;
            }
        }

        Matrix lhs = new Matrix(matrixA, 0, 0, N);
        Matrix rhs = new Matrix(matrixA, 0, 0, N);
        Matrix sum = new Matrix(N);

        MatrixAddTask matrixAddTask = new MatrixAddTask(lhs, rhs, sum);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(matrixAddTask);
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                System.out.printf("%f ", sum.get(i, j));
            }
            System.out.println();
        }

    }
}
