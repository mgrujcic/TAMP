import java.util.Map;
import java.util.concurrent.RecursiveTask;

public abstract class Mapper<IN, K, V> extends RecursiveTask<Map<K, V>> {
    protected IN input;
    public void setInput(IN input){
        this.input = input;
    }
}
