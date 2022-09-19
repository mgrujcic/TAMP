import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue <T>{
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;
    volatile Node head, tail;
    final int capacity;
    public BoundedQueue(int capacity){
        this.capacity = capacity;
        head = new Node(null);
        tail = head;
        size = new AtomicInteger(0);
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x){
        boolean mustWakeDequeuers = false;
        Node e = new Node(x);
        enqLock.lock();
        try{
            while(size.get() == capacity)
                notFullCondition.await();
            tail.next = e;
            tail = e;
            if(size.getAndIncrement() == 0)
                mustWakeDequeuers = true;
        }catch(InterruptedException ex){
            System.err.println(ex);
            System.exit(-1);
        }finally{
            enqLock.unlock();
        }
        if(mustWakeDequeuers){
            deqLock.lock();
            try{
                notEmptyCondition.signalAll();
            }finally{
                deqLock.unlock();
            }
        }
    }

    public T deq(){
        boolean mustWakeEnqueuers = false;
        T ret = null;
        deqLock.lock();
        try{
            while(size.get() == 0){
                notEmptyCondition.await();
            }
            ret = head.next.value;
            head = head.next;
            if(size.getAndDecrement() == capacity){
                mustWakeEnqueuers = true;
            }
        }catch(InterruptedException ex){
            System.err.println(ex);
            System.exit(-1);
        }finally{
            deqLock.unlock();
        }
        if(mustWakeEnqueuers){
            enqLock.lock();
            try{
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return ret;
    }
    class Node {
        public T value;
        public volatile Node next;
        public Node(T x){
            value = x;
            next = null;
        }
    }
}
