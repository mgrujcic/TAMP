public class Exercise81 implements RWLock{
    private int readers;
    private boolean writer;
    Lock readLock, writeLock;
    public Exercise81(){
        writer = false;
        readers = 0;
        readLock = new readLock(this);
        writeLock = new writeLock(this);
    }
    @Override
    public Lock readLock() {
        return readLock;
    }
    @Override
    public Lock writeLock() {
        return writeLock;
    }
    private class readLock implements Lock{
        Exercise81 parent;
        readLock(Exercise81 parent){
            this.parent = parent;
        }
        @Override
        public void lock(){
            synchronized(parent){
                try{
                    while(writer)
                        parent.wait();
                }catch(InterruptedException ex){
                    System.err.println("error: " + ex);
                    System.exit(-1);
                }
                readers++;
                
            }
        }
        @Override
        public void unlock() {
            synchronized(parent){
                try{
                    while(readers>0)
                        parent.wait();
                }catch(InterruptedException ex){
                    System.err.println("error: " + ex);
                    System.exit(-1);
                }
                readers--;
                if(readers == 0)
                    parent.notifyAll();
                
            }
            
        }
    }

    private class writeLock implements Lock{
        Exercise81 parent;
        writeLock(Exercise81 parent){
            this.parent = parent;
        }
        @Override
        public void lock() {
            synchronized(parent){
                try{
                    while(readers>0 || writer)
                        parent.wait();
                    writer = true;
                }catch(InterruptedException ex){
                    System.err.println("error: " + ex);
                    System.exit(-1);
                }
            }
        }
        @Override
        public void unlock() {
            synchronized(parent){
                writer = false;
                parent.notifyAll();
            }
        }
    }

}
