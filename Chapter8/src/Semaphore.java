import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Semaphore {
    int state;
    ReentrantLock lock;
    Condition condition;

    public Semaphore(int capacity){
        this.state = capacity;
        state = 0;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void down(){
        lock.lock();
        try{
            while(state <= 0)
                condition.wait();
            state--;
        }catch(InterruptedException ex){
            System.exit(-1);
        }finally{
            lock.unlock();
        }
    }
    public void up(){
        lock.lock();
        try{
            state++;
            condition.signalAll();
        }finally{
            lock.unlock();
        }
    }
}
