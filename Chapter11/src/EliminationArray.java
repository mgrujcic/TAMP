import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EliminationArray<T> {
    private static final int duration = 64;
    LockFreeExchanger<T>[] exchanger;
    private int capacity;
    public EliminationArray(int capacity){
        this.capacity = capacity;
        exchanger = (LockFreeExchanger<T>[]) new LockFreeExchanger[capacity];
        for(int i = 0; i < capacity; i++){
            exchanger[i] = new LockFreeExchanger<T>();
        }
    }
    public T visit(T value) throws TimeoutException {
        int slot = ThreadLocalRandom.current().nextInt(capacity); // cba with range policy
        return (exchanger[slot].exchange(value, duration, TimeUnit.MILLISECONDS));
    }
}
