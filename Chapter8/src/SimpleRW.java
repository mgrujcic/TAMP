import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class SimpleRW implements RWLock {
    int readers;
    boolean writer;
    ReentrantLock lock;
    Condition condition;
    Lock readLock, writeLock;

    public SimpleRW(){
        writer = false;
        readers = 0;
        lock = new ReentrantLock();
        readLock = new ReadLock();
        writeLock = new WriteLock();
        condition = lock.newCondition();
    }

    private class ReadLock implements Lock{
        @Override
        public void lock() {
            lock.lock();
            try{
                while(writer)
                    condition.await();
                readers++;
            }catch(InterruptedException exception){
                System.err.println("ReadLock: Unexpected InterruptedExpection: " + exception.getMessage());
                System.exit(-1);
            }finally{
                lock.unlock();
            }
        }
        @Override
        public void unlock() {
            lock.lock();
            try{
                readers--;
                if(readers == 0)
                    condition.signalAll();
            }finally{
                lock.unlock();
            }
        }
    }
    private class WriteLock implements Lock{
        @Override
        public void lock() {
            lock.lock();
            try{
                while(readers > 0)
                    condition.await();
                writer = true;
            }catch(InterruptedException ex){
                System.err.println("WriteLock: Unexpected InterruptedExpection: " + ex.getMessage());
                System.exit(-1);
            }finally{
                lock.unlock();
            }
        }
        @Override
        public void unlock() {
            lock.lock();
            try{
                writer = false;
                condition.signalAll();
            }finally{
                lock.unlock();
            }
        }
    }
    @Override
    public Lock readLock() {
        return readLock;
    }
    @Override
    public Lock writeLock() {
        return writeLock;
    }
}
