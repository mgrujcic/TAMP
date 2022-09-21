import java.util.concurrent.atomic.AtomicReference;

public class MCS implements Lock{
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;

    class QNode{
        volatile boolean locked = true;
        volatile QNode next = null;
    }

    public MCS(){
        tail = new AtomicReference<>(null);
        myNode = new ThreadLocal<QNode>(){
            protected QNode initialValue(){
                return new QNode();
            }
        };
    }
    public void lock(){
        QNode node = myNode.get();
        QNode predecessor = tail.getAndSet(node);
        if(predecessor != null){
            node.locked = true;
            predecessor.next = node;
            while(node.locked){}
        }
    }
    public void unlock(){
        QNode node = myNode.get();
        if(node.next == null){
            if(tail.compareAndSet(node, null))
                return;
            while(node.next == null){}
        }
        node.next.locked = false;
        node.next = null;
    }
    public boolean isLocked(){
        return tail.get().locked;
    }
}