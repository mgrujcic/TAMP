package Matrices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class MatrixAddTask extends RecursiveAction{
    static final int THRESHOLD = 8;
    Matrix lhs, rhs, sum;
    public MatrixAddTask(Matrix lhs, Matrix rhs, Matrix sum){
        this.lhs = lhs;
        this.rhs = rhs;
        this.sum = sum;
    }
    
    @Override
    protected void compute() {
        int n = lhs.getDim();
        if(n <= THRESHOLD){
            Matrix.add(lhs, rhs, sum);
        }else{
            List<MatrixAddTask> tasks = new ArrayList<>(4);
            for(int i = 0; i < 2; i++){
                for(int j = 0; j < 2; j++){
                    tasks.add(
                        new MatrixAddTask(
                            lhs.split(i, j), 
                            rhs.split(i, j), 
                            sum.split(i, j))
                    );
                }
            }
            
            tasks.stream().forEach((task) -> {
                task.fork();
            });
            tasks.stream().forEach((task) -> {
                task.join();
            });
        }
        
    }
}
