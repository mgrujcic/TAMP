import java.util.concurrent.locks.ReentrantLock;

public class Lazy<T> implements Set<T>{

    private Node head;
    public Lazy(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T x){
        int key = x.hashCode();
        while(true){
            Node pred = head;
            Node curr = head.next;
            while(curr.key < key){
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try{
                curr.lock();
                try{
                    if(validate(pred, curr)){
                        if(curr.key == key){
                            return false;
                        }else{
                            Node node = new Node(x);
                            // !!!!!!!!!!!!!!!!!!!!!! order
                            node.next = curr;
                            pred.next = node;
                            return true;
                        }
                    }
                }finally{
                    curr.unlock();
                }
            }finally{
                pred.unlock();
            }
        }
    }

    @Override
    public boolean remove(T x) {
        int key = x.hashCode();
        while(true){
            Node pred = head;
            Node curr = head.next;
            while(curr.key < key){
                pred = curr;
                curr = curr.next;
            }
            pred.lock();
            try{
                curr.lock();
                try{
                    if(validate(pred, curr)){
                        if(curr.key == key){
                            curr.marked = true;
                            pred.next = curr.next;
                            return true;
                        }else{
                            
                            return false;
                        }
                    }
                }finally{
                    curr.unlock();
                }
            }finally{
                pred.unlock();
            }
        }
    }

    @Override
    public boolean contains(T x) {
        int key = x.hashCode();
        Node curr = head;
        while(key < curr.key)
            curr = curr.next;
        return curr.key == key && !curr.marked;
    }

    private boolean validate(Node pred, Node curr){
        return !pred.marked && !curr.marked && pred.next == curr; 
    }

    private class Node{
        int key;
        T item;
        volatile boolean marked = false;
        volatile Node next;
        ReentrantLock lock = new ReentrantLock();

        Node(int key){
            this.key = key;
            this.item = null;
        }
        Node(T item){
            this.item = item;
            this.key = item.hashCode();
        }
        void lock(){
            this.lock.lock();
        }
        void unlock(){
            this.lock.unlock();
        }
    }
}
