import java.util.concurrent.locks.ReentrantLock;

public class CoarseList<T> implements Set<T> {
    private Node head;
    final private ReentrantLock lock = new ReentrantLock();
    public CoarseList(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T x) {
        Node pred, curr;
        int key = x.hashCode();
        lock.lock();
        try{
            pred = head;
            curr = pred.next;
            while(curr.key < key){
                pred = curr;
                curr = curr.next;
            }
            if(key == curr.key){
                return false;
            }else{
                Node newNode = new Node(x);
                newNode.next = curr;
                pred.next = newNode;
                return true;
            }
        }finally{
            lock.unlock();
        }
    }
    @Override
    public boolean contains(T x) {
        Node pred, curr;
        int key = x.hashCode();
        lock.lock();
        try{
            pred = head;
            curr = pred.next;
            while(curr.key < key){
                pred = curr;
                curr = curr.next;
            }
            if(key == curr.key){
                return false;
            }else{
                return true;
            }
        }finally{
            lock.unlock();
        }
    }

    @Override
    public boolean remove(T x) {
        Node pred, curr;
        int key = x.hashCode();
        lock.lock();
        try{
            pred = head;
            curr = pred.next;
            while(curr.key < key){
                pred = curr;
                curr = curr.next;
            }
            if(key == curr.key){
                pred.next = curr.next;
                return true;
            }else{
                return false;
            }
        }finally{
            lock.unlock();
        }
    }

    private class Node{
        int key;
        T item;
        Node next;

        Node(int key){
            this.key = key;
            this.item = null;
        }
        Node(T item){
            this.item = item;
            this.key = item.hashCode();
        }
    }
}
