import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockedQueue<T> {
    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    final T[] items;
    int tail, head, count;
    final int capacity;
    public LockedQueue(int capacity){
        items =(T[]) new Object[capacity];
        this.capacity = capacity;
    }

    public void enq(T x){
        lock.lock();
        try{
            while(count == capacity){
                notFull.await();
            }
            items[tail] = x;
            tail++;
            count++;
            if(tail == capacity)
                tail = 0;
            notEmpty.signal();

        }catch(Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }finally{
            lock.unlock();
        }
    }
    public T deq(){
        lock.lock();
        try{
            while(count == 0){
                notEmpty.await();
            }
            T ret = items[head];
            head++;
            count--;
            if(head == capacity)
                head = 0;
            notFull.signal();
            return ret;

        }catch(Exception ex){
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            return null;
        }finally{
            lock.unlock();
        }
    }
    
}