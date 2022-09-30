import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {
    AtomicReference<Node> head,tail;
    public LockFreeQueue(){
        Node node = new Node(null);
        head = new AtomicReference<>(node);
        tail = new AtomicReference<>(node);
    }

    public class Node {
        public T value;
        public AtomicReference<Node> next;
        public Node(T value){
            this.value = value;
            next = new AtomicReference<Node>(null);
        }
    }
    public void enq(T value){
        Node node = new Node(value);
        while(true){
            Node last = tail.get();
            Node next = last.next.get();
            if(last == tail.get()){
                if(next == null){
                    if(tail.compareAndSet(next, node)){
                        tail.compareAndSet(last, node);
                        return;
                    }
                }else{
                    tail.compareAndSet(last, next);
                }
            }
        }
    }
    public T deq(){
        while(true){
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get();
            if(first == head.get()){
                if(first == last){
                    if(next == null){
                        return null;//empty
                    }
                    tail.compareAndSet(last,next);
                }else{
                    T value = next.value;
                    if(head.compareAndSet(first, next))
                        return value;
                }
            }
        }
    }
}
