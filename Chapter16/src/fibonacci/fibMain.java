package fibonacci;

import java.util.concurrent.ForkJoinPool;

public class fibMain {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int res = forkJoinPool.invoke(new FibTask(30));
        System.out.println(res);
    }
}
