import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StripedHashSet<T> extends BaseHashSet<T> {
    final Lock[] locks;
    public StripedHashSet(int capacity){
        super(capacity);
        locks = new Lock[capacity];
        for(int i = 0; i < locks.length; i++)
            locks[i] = new ReentrantLock();
    }

    public final void acquire(T x){
        locks[x.hashCode() % locks.length].lock();
    }

    public void release(T x){
        locks[x.hashCode() % locks.length].unlock();
    }
    public void resize(){
        for(Lock lock: locks){
            lock.lock();
        }
        try{
            int newCapacity = 2 * table.length;
            List<T>[] oldTable = table;
            table = (List<T>[]) new List[newCapacity];
            for(int i = 0; i < newCapacity; i++)
                table[i] = new ArrayList<T>();
            for(List<T> bucket: oldTable){
                for(T x: bucket){
                    table[x.hashCode() % table.length].add(x);
                }
            }
        }finally{
            for(Lock lock: locks){
                lock.unlock();
            }
        }
        
    }
    @Override
    public boolean policy() {
        return setSize.get()/table.length > 4;
    }
    
}
