import java.util.HashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class RecursiveWordCountTask extends RecursiveTask<Map<String, Long>> {
    final int THRESHOLD = 8;
    Spliterator<String> rightSplit;

    RecursiveWordCountTask(Spliterator<String> aSpliterator) {
        rightSplit = aSpliterator;
    }
    @Override
    protected Map<String, Long> compute() {
        Map<String, Long> result = new HashMap<>();
        Spliterator<String> leftSplit;
        if(rightSplit.estimateSize() > THRESHOLD && (leftSplit = rightSplit.trySplit()) != null){
            RecursiveWordCountTask left = new RecursiveWordCountTask(leftSplit);
            RecursiveWordCountTask right = new RecursiveWordCountTask(rightSplit);
            left.fork();
            right.compute().forEach(
                (k, v) -> result.merge(k, v, (x, y) -> x + y)
            );
            left.join().forEach(
                (k, v) -> result.merge(k, v, (x, y) -> x + y)
            );
        }else{
            rightSplit.forEachRemaining(
                word -> result.merge(word, 1L, (x, y) -> x + y)
            );
        }
        return result;
    }
}
