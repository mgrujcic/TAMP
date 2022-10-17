import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LazySkipList<T>{
    static final int MAX_LEVEL = 30;
    final Node<T> head = new Node<T>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<T>(Integer.MAX_VALUE);
    public LazySkipList(){
        for(int i = 0; i < head.next.length; i++){
            head.next[i] = tail;
        }
    }

    public int find(T x, Node<T>[] preds, Node<T>[] succs){
        int key = x.hashCode();
        int levelFound = -1;
        Node<T> pred = head;
        for(int level = MAX_LEVEL; level >= 0; level--){
            Node<T> curr = pred.next[level];//volatile not permitted? how would compiler even optimise it?
            while(key > curr.key){
                /*
                 * no need to check if curr contains the next key
                 * because it will overshoot if it doesn't (guarded vy the tail sentinel)
                 */
                pred = curr; curr = pred.next[level];
            }
            if(levelFound == -1 && key == curr.key){
                levelFound = level;
            }
            preds[levelFound] = pred;
            succs[levelFound] = curr;

        }




        return levelFound;
    }

    private static final class Node<T>{
        final Lock lock = new ReentrantLock();
        final T item;
        final int key;
        final Node<T>[] next;
        volatile boolean marked = false;
        volatile boolean fullyLinked = false;
        private int topLevel;
        
        public Node(int key){
            this.item = null;
            this.key = key;
            this.next = new Node[MAX_LEVEL + 1];
            topLevel = MAX_LEVEL;
        }

        public Node (T x, int height){
            item = x;
            key = x.hashCode();
            next = new Node[height+1];
            topLevel = height;
        }
        public void lock(){
            lock.lock();
        }
        public void unlock(){
            lock.unlock();
        }
    }
}
