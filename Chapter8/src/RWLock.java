public interface RWLock {
    Lock readLock();
    Lock writeLock();
}
