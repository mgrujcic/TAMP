import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class FifoRW implements RWLock{
    int readAcquires, readReleases;
    boolean writer;
    ReentrantLock lock;
    Condition condition;
    Lock readLock, writeLock;
    
    public FifoRW(){
        readAcquires = 0;
        readReleases = 0;
        writer = false;
        lock = new ReentrantLock();
        condition = lock.newCondition();
        readLock = new ReadLock();
        writeLock = new WriteLock();
    }
    @Override
    public Lock readLock() {
        return readLock;
    }
    @Override
    public Lock writeLock() {
        return writeLock;
    }

    private class ReadLock implements Lock{
        @Override
        public void lock() {
            lock.lock();
            try{
                while(writer)
                    condition.await();
                readAcquires++;
            }catch (InterruptedException exception){
                System.err.println("ReadLock: Unexpected InterruptedExpection: " + exception.getMessage());
                System.exit(-1);
            }finally{
                lock.unlock();
            }
        }
        public void unlock(){
            lock.lock();
            try{
                readReleases++;
                if(readAcquires == readReleases)
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
                while(writer)
                    condition.await();
                writer = true;
                while(readAcquires != readReleases)
                    condition.wait();
            }catch (InterruptedException exception){
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
                writer = false;
                condition.signalAll();
            }finally{
                lock.unlock();
            }
        }
    }
}
