import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class NaiveSynchronous<T> {
    T item = null;
    boolean enqueueing;
    ReentrantLock lock;
    Condition condition;

    public NaiveSynchronous(){
        enqueueing = false;
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void enq(T value){
        lock.lock();
        try{
            while(enqueueing)
                condition.await();
            enqueueing = true;
            item = value;
            condition.signalAll();
            while(item != null)
                condition.await();
            enqueueing = false;
            condition.signalAll();
        }catch (InterruptedException exception) {
            System.err.println("Error: " + exception);
            System.exit(-1);
        }finally{
            lock.unlock();
        }
    }
    public T deq(){
        lock.lock();
        try{
            while(!enqueueing)
                condition.await();
            T t = item;
            item = null;
            condition.signalAll();
            return t;
        }catch (InterruptedException exception) {
            System.err.println("Error: " + exception);
            System.exit(-1);
            return null;//XD
        }finally{
            lock.unlock();
        }
    }
    
}
