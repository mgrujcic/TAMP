import java.util.concurrent.atomic.AtomicMarkableReference;

public class Nonblocking<T> implements Set<T>{
    private Node head;

    @Override
    public boolean add(T x) {
        int key = x.hashCode();
        while(true){
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if(curr.key == key){
                return false;
            }else{
                Node node = new Node(x);
                node.next = new AtomicMarkableReference<Nonblocking<T>.Node>(curr, false);
                node.next.set(curr, false);
                boolean valid = pred.next.compareAndSet(curr, node, false, false);
                if(valid)
                    return true;
            }
        }
    }
    @Override
    public boolean contains(T x) {
        int key = x.hashCode();
        Node curr = head;
        while(curr.key < key){
            curr = curr.next.getReference();
        }
        return curr.key == key && !curr.next.isMarked();
    }
    @Override
    public boolean remove(T x) {
        int key = x.hashCode();
        while(true){
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if(curr.key == key){
                Node succ = curr.next.getReference();
                boolean snip = curr.next.compareAndSet(succ, succ, false, true);
                if(!snip)
                    continue;
                //no check if this is successful
                //curr is marked so it doesnt matter? why even attempt deleting it now?
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }else{
                return false;
            }
        }
    }
    private class Window {
        public Node pred, curr;
        Window(Node pred, Node curr){
            this.pred = pred;
            this.curr = curr;
        }
    }
    Window find(Node head, int key){
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry: while(true){
            pred = head;
            curr = pred.next.getReference();
            while(true){
                succ = curr.next.get(marked);
                while(marked[0]){//if not valid
                    //try to physically remove curr, fail if pred was changed/deleted
                    snip = pred.next.compareAndSet(curr, succ, false, false);
                    if(!snip)
                        continue retry; //start over
                    curr = succ;
                    succ = curr.next.get(marked);
                }
                if(curr.key >= key)
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    private class Node{
        int key;
        T item;
        AtomicMarkableReference<Node> next;

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
