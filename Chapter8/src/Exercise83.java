import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Exercise83 {
    public int red = 0; //public for testing
    public int blue = 0;
    private boolean redWaiting, blueWaiting;
    private ReentrantLock lock;
    private Condition cond;

    public Exercise83(){
        lock = new ReentrantLock();
        cond = lock.newCondition();
    }

    public void lockRed(){
        lock.lock();
        try{
            
            while(blueWaiting)//wait for blue to acquire if waiting
                cond.await();
            if(red == 0)//dont allow more blue locks, fairness?
                redWaiting = true;
            while(blue > 0)
                cond.await();
            redWaiting = false;
            /*if(redWaiting){// better?
                redWaiting = false;
                cond.signalAll();
            }*/
            red++;            
        }catch(InterruptedException ex){
            System.err.println("Error: " + ex);
            System.exit(-1);
        }finally{
            lock.unlock();
        }
    }
    public void unlockRed(){
        lock.lock();
        try{
            red--;
            if(red == 0)
                cond.signalAll();
        }finally{
            lock.unlock();
        }
    }
    public void lockBlue(){
        lock.lock();
        try{
            
            while(redWaiting)
                cond.await();
            if(blue == 0)
                blueWaiting = true;
            while(red > 0)
                cond.await();
                blueWaiting = false;
                /*if(blueWaiting){ // better?
                    blueWaiting = false;
                    cond.signalAll();
                }*/
            blue++;            
        }catch(InterruptedException ex){
            System.err.println("Error: " + ex);
            System.exit(-1);
        }finally{
            lock.unlock();
        }
    }
    public void unlockBlue(){
        lock.lock();
        try{
            blue--;
            if(blue == 0)
                cond.signalAll();
            
        }finally{
            lock.unlock();
        }
    }
}
