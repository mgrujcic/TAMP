import java.util.concurrent.atomic.AtomicReference;

public class CLH implements Lock{
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myPred;
    ThreadLocal<QNode> myNode;

    public class QNode{
        public volatile boolean locked = false;
    }
    
    public CLH(){
        tail = new AtomicReference<QNode>(new QNode());
        myNode = new ThreadLocal<QNode>(){
            protected QNode initialValue(){
                return new QNode();
            }
        };
        myPred = new ThreadLocal<QNode>(){
            protected QNode initialValue(){
                return null;
            }
        };
    }
    public void lock(){
        QNode qnode = myNode.get();
        qnode.locked = true;
        QNode pred = tail.getAndSet(qnode);
        myPred.set(pred);
        while(pred.locked){}
    }
    public void unlock(){
        QNode qnode = myNode.get();
        qnode.locked = false;
        myNode.set(myPred.get());//awesome
    }
    //maybe its not locked but there is someone waiting? not sure if this was the intended solution
    public boolean isLocked(){
        return tail.get().locked;
    }
}
