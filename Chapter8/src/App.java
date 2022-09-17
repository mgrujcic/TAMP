import java.util.concurrent.ThreadLocalRandom;

public class App {
    private static class Worker implements Runnable {

        private LockedQueue<Integer> queue;
        private boolean producer;
        public Worker(LockedQueue<Integer> queue){
            this.queue = queue;
            producer = ThreadLocalRandom.current().nextBoolean();
            if(producer)
                System.out.println("Producer");
            else
                System.out.println("Consoomer");
        }
        @Override
        public void run(){
            while(true){
                int sleepDur = ThreadLocalRandom.current().nextInt(100);
                try{
                    Thread.sleep(sleepDur);
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.exit(-1);
                }
                if(producer){
                    queue.enq(sleepDur);
                    System.out.printf("Producer %d just added %d. %d in queue\n", Thread.currentThread().getId(), sleepDur, queue.count);
                }else{
                    int res = queue.deq();
                    System.out.printf("Consoomer %d just dequeued %d. %d in queue\n",Thread.currentThread().getId(), res, queue.count);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        LockedQueue<Integer> queue = new LockedQueue<>(10);
        int noWorkers = 4;
        Thread[] threads = new Thread[noWorkers];

        for(int i = 0; i < noWorkers; i++){
            threads[i] = new Thread(new Worker(queue));
        }
        for(int i = 0; i < noWorkers; i++){
            threads[i].start();
        }
    }
}
