import java.util.EmptyStackException;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack <T>{
    AtomicReference<Node> top = new AtomicReference<>(null);
    static final int MIN_DELAY = 1;
    static final int MAX_DELAY = 16;
    Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);

    protected boolean tryPush(Node node){
        Node oldTop = top.get();
        node.next = oldTop;
        return top.compareAndSet(oldTop, node);
    }

    public void push(T value){
        Node node = new Node(value);
        while(true){
            if(tryPush(node)){
                return;
            }else{
                try{
                    backoff.backoff();
                }catch(InterruptedException ex){
                    System.err.println("Error: " + ex);
                    System.exit(-1);
                }
            }
        }
    }

    protected Node tryPop() throws EmptyStackException{
        Node oldTop = top.get();
        if(oldTop == null) {
            throw new EmptyStackException();
        }
        Node newTop = oldTop.next;
        if(top.compareAndSet(oldTop, newTop)){
            return oldTop;
        }else{
            return null;
        }
    }

    public T pop() throws EmptyStackException {
        while(true){
            Node returnNode = tryPop();
            if(returnNode != null){
                return returnNode.value;
            }else{
                try{
                    backoff.backoff();
                }catch(InterruptedException ex){
                    System.err.println("Error: " + ex);
                    System.exit(-1);
                }
            }
        }
    }
    protected class Node {
        public Node next = null;
        public T value;
        public Node(T value){
            this.value = value;
        }
    }

}
