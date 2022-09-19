import java.util.concurrent.locks.ReentrantLock;

public class Optimistic<T> implements Set<T> {
    
    private Node head;
    public Optimistic(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    private boolean validate(Node pred, Node curr){
        Node it = head;
        while(it.key <= pred.key){
            if(it == pred)
                return pred.next == curr;
            it = it.next;
        }
           
        return false;

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
    public boolean remove(T x){
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
    public boolean contains(T x){
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
    private class Node{
        int key;
        T item;
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
