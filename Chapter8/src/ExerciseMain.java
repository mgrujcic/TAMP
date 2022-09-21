import java.util.concurrent.ThreadLocalRandom;

public class ExerciseMain {
    int redCount = 0, blueCount = 0;

    private static class Worker implements Runnable {
        private Exercise83 RBLock;
        Worker(Exercise83 RBLock){
            this.RBLock = RBLock;
        }
        @Override
        public void run() {
            int randLimit = 10;
            while(true){
                try{
                    Thread.sleep(ThreadLocalRandom.current().nextInt(randLimit));
                }catch(InterruptedException ex){
                    System.err.println("InterruptedException: " + ex);
                    System.exit(-1);
                }
                if(ThreadLocalRandom.current().nextBoolean()){
                    RBLock.lockBlue();//try finally??
                    System.out.printf("%d %d (b)\n", RBLock.blue, RBLock.red);
                    if(RBLock.red != 0){
                        System.err.printf("not correct");
                        System.exit(-1);
                    }
                    try{
                        Thread.sleep(ThreadLocalRandom.current().nextInt(randLimit));
                    }catch(InterruptedException ex){
                        System.err.println("InterruptedException: " + ex);
                        System.exit(-1);
                    }
                    RBLock.unlockBlue();
                }else{
                    RBLock.lockRed();//try finally??
                    System.out.printf("%d %d (r)\n", RBLock.blue, RBLock.red);
                    if(RBLock.blue != 0){
                        System.err.printf("not correct");
                        System.exit(-1);
                    }
                    try{
                        Thread.sleep(ThreadLocalRandom.current().nextInt(randLimit));
                    }catch(InterruptedException ex){
                        System.err.println("InterruptedException: " + ex);
                        System.exit(-1);
                    }
                    RBLock.unlockRed();
                }
                
            }
        }
    }

    public static void main(String args[]){
        Exercise83 RBLock = new Exercise83();
        int noWorkers = 16;
        Thread[] threads = new Thread[noWorkers];

        for(int i = 0; i < noWorkers; i++){
            threads[i] = new Thread(new Worker(RBLock));
        }
        for(int i = 0; i < noWorkers; i++){
            threads[i].start();
        }
    }
}
