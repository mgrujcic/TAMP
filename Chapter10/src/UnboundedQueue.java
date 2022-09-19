import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UnboundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    volatile Node head, tail;
    final int capacity;
    public UnboundedQueue(int capacity){
        this.capacity = capacity;
        head = new Node(null);
        tail = head;
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        notEmptyCondition = deqLock.newCondition();
    }
    
    public void enq(T x){
        Node e = new Node(x);
        enqLock.lock();
        try{
            tail.next = e;
            tail = e;
        }finally{
            enqLock.unlock();
        }
    }

    public T deq(){
        T result;
        deqLock.lock();
        try{
            if(head.next == null)
                return null;
            else{
                result = head.next.value;
                head = head.next;
                return result;
            }
        }finally{
            deqLock.unlock();
        }
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
