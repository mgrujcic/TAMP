import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    private AtomicInteger count;
    private Node parent;
    private volatile boolean sense;
    private ThreadLocal<Boolean> threadSense;
    private int radix;

    public Node(int radix, ThreadLocal<Boolean> threadSense) {
        sense = false;
        parent = null;
        count = new AtomicInteger(radix);
        this.radix = radix;
        this.threadSense = threadSense;
    }
    
    public Node(Node parent, int radix, ThreadLocal<Boolean> threadSense){
        this(radix, threadSense);
        this.parent = parent;
    }

    public void await(){
        boolean mySense = threadSense.get();
        int position = count.getAndDecrement();
        if(position == 1){
            if(parent != null){
                parent.await();
            }
            count.set(radix);
            sense = mySense;
        }else{
            while(sense != mySense) {}
        }
        threadSense.set(!mySense);
    }
    
}
