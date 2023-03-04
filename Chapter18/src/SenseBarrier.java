import java.util.concurrent.atomic.AtomicInteger;

public class SenseBarrier {
    AtomicInteger count;
    int size;
    boolean sense;
    ThreadLocal<Boolean> threadSense;

    public SenseBarrier(int size) {
        this.size = size;
        count = new AtomicInteger(size);
        sense = false;
        threadSense = new ThreadLocal<Boolean>() {
            protected Boolean initialValue(){
                return !sense;
            }
        };
    }

    public void await(){
        boolean mySense = threadSense.get();
        int position = count.getAndDecrement();
        if(position == 1){
            count.set(size);
            sense = mySense;
        }else{
            while(sense != mySense) {}
        }
        threadSense.set(!mySense);
    }
}