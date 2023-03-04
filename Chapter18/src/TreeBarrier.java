import java.util.concurrent.atomic.AtomicInteger;

public class TreeBarrier {
    int radix;
    Node[] leaf;
    int leaves;
    ThreadLocal<Boolean> threadSense;
    ThreadLocal<Integer> threadNo;
    AtomicInteger threadNoCounter;

    public TreeBarrier(int n, int r){
        radix = r;
        leaves = 0;
        leaf = new Node[n/r];
        int depth = 0;
        threadNoCounter.set(0);
        threadSense = new ThreadLocal<Boolean>(){
            protected Boolean initialValie(){//why the warning?
                return true;
            }
        };
        threadNo = new ThreadLocal<Integer>(){
            protected Integer initialValue(){
                return threadNoCounter.getAndIncrement();
            }
        };
        
        while(n > 1){
            depth++;
            n = n / r;
        }
        Node root = new Node(radix, threadSense);
        build(root, depth - 1);
        
    }

    private void build(Node parent, int depth) {
        if(depth == 0) {
            leaf[leaves] = parent;
            leaves++;
        }else{
            for(int i = 0; i < radix; i++){
                Node child = new Node(parent, radix, threadSense);
                build(child, depth - 1);
            }
        }
    }

    public void await() {
        int me = threadNo.get();
        leaf[me/radix].await();
    }
    
}
