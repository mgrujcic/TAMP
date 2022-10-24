package Matrices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class MatrixMulTask extends RecursiveAction{
    static final int THRESHOLD = 8;
    Matrix lhs, rhs, product;

    public MatrixMulTask(Matrix lhs, Matrix rhs, Matrix product){
        this.lhs = lhs;
        this.rhs = rhs;
        this.product = product;
    }
    @Override
    protected void compute() {
        int n = lhs.getDim();
        if(n <= THRESHOLD){
            Matrix.multiply(lhs, rhs, product);
        }else{
            Matrix[] term = new Matrix[]{
                new Matrix(n),
                new Matrix(n)
            };
            List<MatrixMulTask> tasks = new ArrayList<>(8);
            for(int i = 0; i < 2; i++){
                for(int j = 0; j < 2; j++){
                    for(int k = 0; k < 2; k++){
                        tasks.add(
                            new MatrixMulTask(
                                lhs.split(j, i),
                                rhs.split(i, k),
                                term[i].split(j, k)
                            )
                        );
                    }
                }
            }
            tasks.stream().forEach(ForkJoinTask::fork);
            tasks.stream().forEach(ForkJoinTask::join);
            new MatrixAddTask(term[0], term[1], product).compute();
        }
    }
}
