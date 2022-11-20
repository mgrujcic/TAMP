package fibonacci;

import java.util.concurrent.RecursiveTask;

public class FibTask extends RecursiveTask<Integer>{
    int arg;
    public FibTask(int n){
        arg = n;
    }
    @Override
    protected Integer compute() {
        if(arg > 1){
            FibTask rightTask = new FibTask(arg-1);
            rightTask.fork();
            FibTask leftTask = new FibTask(arg-2);
            //wowza
            return rightTask.join() + leftTask.compute();
        }else{
            return arg;
        }
    }
}
