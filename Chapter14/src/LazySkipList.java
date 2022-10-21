import java.util.concurrent.ThreadLocalRandom;
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

    boolean add(T x){
        int toplevel = randomLevel();
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true){
            int lFound = find(x, preds, succs);
            if(lFound != -1){ //if x exists in the skiplist
                if(lFound != -1){
                    Node<T> nodeFound = succs[lFound];
                    if(!nodeFound.marked){//it wasn't removed
                        while(!nodeFound.fullyLinked);//the value is already being added. 
                        //Have to wait for it so it cannot happen that there isn't a fully
                        //linked node without a remove call in the meantime
                        return false;
                    }
                    continue;
                }
                int highestLocked = -1;
                try{
                    Node<T> pred, succ;
                    boolean valid = true;
                    for(int level = 0; valid && (level <= toplevel); level++){
                        pred = preds[level];
                        succ = succs[level];
                        pred.lock();
                        highestLocked = level;
                        //succ isn't locked!
                        valid = !pred.marked && !succ.marked && pred.next[level] == succ;
                    }
                    if(!valid)continue;
                    Node<T> newNode = new Node(x, toplevel);
                    for(int level = 0; level <= toplevel; level++)
                        newNode.next[level] = succs[level];
                    for(int level = 0; level <= toplevel; level++)
                        preds[level].next[level] = newNode;
                    newNode.fullyLinked = true;
                    return true;
                }finally{
                    for(int level = 0; level <= highestLocked; level++)
                        preds[level].unlock();
                }

            }
        }
    }

    boolean remove(T x){
        Node<T> victim = null;
        boolean isMarked = false;
        int topLevel = -1;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while(true){
            int lFound = find(x, preds, succs);
            if(lFound != -1) victim = succs[lFound];
            if(isMarked ||
                (lFound != -1 && 
                 victim.fullyLinked &&
                 victim.topLevel == lFound && //How can it even happen that it is fully linked, unmarked, and yet has missing levels??
                 !victim.marked)){
                
                if(!isMarked){
                    topLevel = victim.topLevel;
                    victim.lock();
                    if(victim.marked){//check if someone marked it between checking if it is marekd and locking it
                        victim.unlock();
                        return false;
                    }
                    victim.marked = true;
                    isMarked = true;
                }
                int highestLocked = -1;
                try{
                    Node<T> pred, succ;
                    boolean valid = true;
                    for(int level = 0; valid && (level <= topLevel); level++){
                        pred = preds[level];
                        pred.lock();
                        highestLocked = level;
                        valid = !pred.marked && pred.next[level] == victim;
                    }
                    if(!valid)
                        continue;
                    for(int level = topLevel; level >= 0; level--)
                        preds[level].next[level] = victim.next[level];
                    victim.unlock();
                    return true;
                }finally{
                    for(int i = 0; i <= highestLocked; i++){
                        preds[i].unlock();
                    }
                }
            }else{
                return false;
            }
        }
    }

    boolean contains(T x){
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        int lFound = find(x, preds, succs);
        return lFound != -1 
            && succs[lFound].fullyLinked
            && !succs[lFound].marked;
    }

    int randomLevel(){
        int rand = ThreadLocalRandom.current().nextInt(1, 1<<MAX_LEVEL);
        int i;
        for(i = 1<<(MAX_LEVEL-1); (i&rand) == 0; i>>=2);
        return i+1; 
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
