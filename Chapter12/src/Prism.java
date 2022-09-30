import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Prism {
    private static final int duration = 100;
    Exchanger<Long>[] exchanger;
    public Prism(int capacity) {
        exchanger = (Exchanger<Long>[]) new Exchanger[capacity];
        for(int i = 0; i < capacity; i++){
            exchanger[i] = new Exchanger<Long>();
        }
    }
    public boolean visit() throws InterruptedException, TimeoutException {
        long me = Thread.currentThread().getId();
        int slot = ThreadLocalRandom.current().nextInt(exchanger.length);
        long other = exchanger[slot].exchange(me, duration, TimeUnit.MILLISECONDS);
        return me < other;
    }
}
