import java.util.Scanner;

public class App {
    private static int sharedInteger = 0;
    private static class Worker implements Runnable {
        Lock lock;

        public Worker(Lock lock){
            this.lock = lock;
        }

        @Override
        public void run() {
            for(int i=0; i<10000; i++){
                lock.lock();
                try{
                    sharedInteger += 1;
                    if(sharedInteger%1000 == 0){
                        System.out.println(sharedInteger);
                    }
                }finally{
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {

        // broken lock
        /*
        Lock lock = new Lock(){
            @Override
            public void lock() {
            
            }
            @Override
            public void unlock() { 
            }
        };*/

        int noWorkers;

        try( Scanner sc = new Scanner(System.in)){
            noWorkers = sc.nextInt();
            Lock lock = new MCS();

            Thread[] threads = new Thread[noWorkers];

            for(int i = 0; i < noWorkers; i++){
                threads[i] = new Thread(new Worker(lock));
            }
            for(int i = 0; i < noWorkers; i++){
                threads[i].start();
            }
            for(int i = 0; i < noWorkers; i++){
                threads[i].join();
            }
            System.out.println(sharedInteger);
        } catch (InterruptedException ex){
            System.out.println(ex);
        }
        

    }

}