import java.util.List;
import java.util.concurrent.RecursiveTask;

public abstract class Reducer<K, V, OUT> extends RecursiveTask<OUT> {
    protected K key;
    protected List<V> valueList;
    public void setInput(K key, List<V> valueList){
        this.key = key;
        this.valueList = valueList;
    }
}