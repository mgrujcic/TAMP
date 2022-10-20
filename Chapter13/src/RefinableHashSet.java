import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

public class RefinableHashSet<T> extends BaseHashSet<T>{
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[] locks;
    public RefinableHashSet(int capacity){
        super(capacity);
        locks = new ReentrantLock[capacity];
        for(int i = 0; i < capacity; i++){
            locks[i] = new ReentrantLock();
        }
        owner = new AtomicMarkableReference<Thread>(null, false);
    }

    @Override
    public final void acquire(T x){
        boolean[] mark = {true};
        Thread me = Thread.currentThread();
        Thread who;
        while(true){
            do{
                who = owner.get(mark);
            }while(mark[0] && who != me); //why is it ok if owner == me? can it ever happen? seems like it cannot.
            ReentrantLock[] oldLocks = locks;
            ReentrantLock oldLock = oldLocks[x.hashCode() % oldLocks.length];
            oldLock.lock();
            who = owner.get(mark);
            if((!(mark[0] && who != me)) && locks == oldLocks){
                return;
            }else{
                oldLock.unlock();
            }
        }
    }
    @Override
    public final void release(T x){
        locks[x.hashCode() % locks.length].unlock();
    }

    @Override
    public void resize(){
        boolean[] mark = {false};
        Thread me = Thread.currentThread();
        if(owner.compareAndSet(null, me, false, true)){
            try{
                if(!policy())
                    return;
                quiesce();
                int newCapacity = 2 * table.length;
                List<T>[] oldTable = table;
                table = (List<T>[]) new List[newCapacity];
                for(int i = 0; i < newCapacity; i++)
                    table[i] = new ArrayList<T>();
                locks = new ReentrantLock[newCapacity];
                for(int j = 0; j < locks.length; j++){
                    locks[j] = new ReentrantLock();
                }
                for(List<T> bucket: oldTable){
                    for(T x: bucket){
                        table[x.hashCode() % table.length].add(x);
                    }
                }
            }finally{
                owner.set(null, false);
            }
        }
    }

    private void quiesce(){
        for(ReentrantLock lock : locks){
            while(lock.isLocked()){}
        }
    }
    public boolean policy() {
        return setSize.get()/table.length > 4;
    }
}
