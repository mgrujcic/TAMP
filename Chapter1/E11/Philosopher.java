package Chapter1.E11;

import java.lang.Math;


public class Philosopher implements Runnable{

    private State state;
    private int philNum;
    private boolean[] forks, workingOnForks; // forks adjacent to philNum are philnum and (philNum+1) % n
    private int left, right;

    public Philosopher(int philNum, boolean[] forks, boolean[] workingOnForks){
        state = State.THINKING;
        this.philNum = philNum;
        this.forks = forks;
        this.workingOnForks = workingOnForks;

        int n = forks.length;

        left = (n + philNum - 1) % n;
        right = (philNum + 1) % n;
        System.out.println(left + " " + right);
    }

    @Override
    public void run(){
        System.out.printf("Philosopher %d: started running.\n", this.philNum);
        while(true){
            // THINKING
            try{
                Thread.sleep(rng());
            }catch(InterruptedException exception){
                System.out.printf("InterruptedException in philosopher %d. Exiting...", this.philNum);
            }
            //HUNGER
            System.out.printf("Philosopher %d: I am hungry. Picking up forks\n", philNum);
            pickUpForks();

            
            //EATING
            System.out.printf("Philosopher %d: Eating\n", philNum);
            try{
                Thread.sleep(rng());
            }catch(InterruptedException exception){
                System.out.printf("InterruptedException in philosopher %d. Exiting...", this.philNum);
            }

            //PUT FORKS DOWN
            System.out.printf("Philosopher %d: Done eating. Putting down forks\n", philNum);
            putForksDown();

        }
        
    }
    ///////////////////////////////////
    // SUBTASK 1 SOLUTION
    ///////////////////////////////////

    /* 
        My attempt at generalising the flag algorithm from the first chapter. 
        I am trying to avoid using well known spinlock solutions that i have seen before (Lamport).
        One reason for that is that I am trying to come up with a solution such that philosophers that don't share forks dont block each other.
        
        Mutual exclusion: Last two operations will always be:
        1) Set the flag
        2) Check if neighbours both have their flags down

        Its not possible for neighbouring philosophers to both pass. 
        When one of them completes step 1 it is not possible for the other one to pass.
        The state that both of them are checking, both of them see that they can work on forks is therefor impossible.
        Lets look at adjecent philosophers A and B both trying to work on forks, and lets say A completed step 1 first.
        It is now impossible for both of them to acquire the lock. If A completes his check before B raises flag, he will get access to the critical section.
        If B raises the flag before A completes the check, either A will access the critical section because he didn't see that B set his flag,
        and B will lower the flag, or neither of them has checked the other one and one or both of them will release their flag and go back to step 1.

        This solution is not fair.
        Nothing about it is fair. Two philosophers get to eat while others starve.
        I assume that those two, because of their waiting, get higher priorities, and other threads run while they are eating.
        I will try to fix it by adding random waiting in the nested infinite loop.

        This seems to fix it but now its wasting time and blocking philosophers who probably could eat.
        Also livelock ???
    */
    private void getCriticalAccess1(){
        

        workingOnForks[philNum] = true;

        while(workingOnForks[left] || workingOnForks[right]){
            workingOnForks[philNum] = false;
            while(workingOnForks[left] || workingOnForks[right]){
                try{
                    Thread.sleep(rng());
                }catch(InterruptedException exception){
                    System.out.printf("InterruptedException in philosopher %d. Exiting...", this.philNum);
                }
            }
            workingOnForks[philNum] = true;
        }
    }

    private void releaseCriticalAccess1(){
        workingOnForks[philNum] = false;
    }
    ///////////////////////////////////
    // END OF SUBTASK 1 SOLUTION
    ///////////////////////////////////
    private void putForksDown(){
        

    
        //Exclusive acces to the critical section secured
        forks[philNum] = false;
        forks[right] = false;
        state = State.THINKING;

        //Done eating

        releaseCriticalAccess1();
        
    }

    private void pickUpForks(){
        state = State.HUNGRY;
        getCriticalAccess1();

        state = State.EATING;
    }

    private long rng(){
        return Math.round(Math.random()*500.0);
    }

    public State getState() {
        return state;
    }

}
