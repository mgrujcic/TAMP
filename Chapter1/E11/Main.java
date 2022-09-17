package Chapter1.E11;

public class Main{
    static Philosopher[] philosophers = new Philosopher[5];
    static boolean[] forks = new boolean[5];
    static boolean[] workingOnForks = new boolean[5];
    public static void main(String[] args) {
         // used to protect the critical section

        for(int i = 0; i < 5; i++)
            philosophers[i] = new Philosopher(i, forks, workingOnForks);

        Thread[] philThreads = new Thread[5];
        for(int i=0; i< 5; i++){
            philThreads[i] = new Thread(philosophers[i]);
            philThreads[i].start();
        }
    }
}