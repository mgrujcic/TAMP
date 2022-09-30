public class Node {
    enum CStatus{IDLE, FIRST, SECOND, RESULT, ROOT};
    boolean locked;
    CStatus cStatus;
    int firstValue, secondValue;
    int result;
    Node parent;
    public Node(){
        cStatus = CStatus.ROOT;
        locked = false;
    }
    public Node(Node myParent){
        parent = myParent;
        cStatus = CStatus.IDLE;
        locked = false;
    }

    synchronized boolean precombine() throws PanicException, InterruptedException{
        while(locked) wait();
        switch(cStatus) {
            case IDLE:
                cStatus = CStatus.FIRST;
                return true;
            case FIRST:
                locked = true;
                cStatus = CStatus.SECOND;
                return false;
            case ROOT:
                return false;
            default:
                throw new PanicException("unexpected Node state" + cStatus);
        }
    }

    synchronized int combine(int combined) throws PanicException, InterruptedException{
        while(locked) wait();
        locked = true;
        firstValue = combined;
        switch(cStatus){
            case FIRST:
                return firstValue;
            case SECOND:
                return firstValue + secondValue;
            default:
                throw new PanicException("unexpected Node state " + cStatus);
        }
    }
    synchronized int op(int combined) throws PanicException, InterruptedException{
        switch (cStatus) {
            case ROOT:
                int prior = result;
                result += combined;
                return prior;
            case SECOND:
                secondValue = combined;
                locked = false;
                notifyAll();
                while(cStatus != CStatus.RESULT) wait();
                locked = false;
                notifyAll();
                cStatus = CStatus.IDLE;
                return result;
            default:
                throw new PanicException("unexpected Node state " + cStatus);
        }
    }
    synchronized void distribute(int prior) throws PanicException{
        switch (cStatus) {
            case FIRST:
                cStatus = CStatus.IDLE;
                locked = false;
                break;
            case SECOND:
                result = prior + firstValue;
                cStatus = CStatus.RESULT;
                break;
            default:
                throw new PanicException("unexpected Node state" + cStatus);
        }
        notifyAll();
    }
}
