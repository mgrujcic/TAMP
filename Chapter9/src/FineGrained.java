import java.util.concurrent.locks.ReentrantLock;

public class FineGrained<T> implements Set<T>{
    
    private Node head;
    public FineGrained(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T x) {
        int key = x.hashCode();
        Node pred = head;
        Node curr;
        pred.lock.lock();
        try{
            curr = pred.next;
            curr.lock.lock();
            try{
                while(curr.key < key){
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if(curr.key == key){
                    return false;
                }else{
                    Node newNode = new Node(x);
                    newNode.next = curr;
                    pred.next = newNode;
                    return true;
                }
            }finally{
                curr.lock.unlock();
            }
        }finally{
            pred.lock.unlock();
        }
    }

    public boolean contains(T x) {
        int key = x.hashCode();
        Node pred = head;
        Node curr;
        pred.lock.lock();
        try{
            curr = pred.next;
            curr.lock.lock();
            try{
                while(curr.key < key){
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if(curr.key == key){
                    return true;
                }else{
                    return false;
                }
            }finally{
                curr.lock.unlock();
            }
        }finally{
            pred.lock.unlock();
        }
    }

    public boolean remove(T x) {
        int key = x.hashCode();
        Node pred = head;
        Node curr;
        pred.lock.lock();
        try{
            curr = pred.next;
            curr.lock.lock();
            try{
                while(curr.key < key){
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if(curr.key == key){
                    pred.next = curr.next;
                    return true;
                }else{
                    return false;
                }
            }finally{
                curr.lock.unlock();
            }
        }finally{
            pred.lock.unlock();
        }
    }

    private class Node{
        int key;
        T item;
        Node next;
        ReentrantLock lock = new ReentrantLock();

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
