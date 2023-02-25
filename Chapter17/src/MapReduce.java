import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

public class MapReduce<IN, K, V, OUT> implements Callable<Map<K, OUT>>  {
    private List<IN> inputList;
    private Supplier<Mapper<IN, K, V>> mapperSupplier;
    private Supplier<Reducer<K, V, OUT>> reducerSupplier;
    private static ForkJoinPool pool;

    public MapReduce(){
        pool = new ForkJoinPool();
        mapperSupplier = () -> {throw new UnsupportedOperationException("no mapper supplier");};
        reducerSupplier = () -> {throw new UnsupportedOperationException("no reducer supplier");};
    }
    public Map<K, OUT> call(){
        Set<Mapper<IN, K, V>> mappers = new HashSet<>();
        for(IN input: inputList){
            Mapper<IN, K, V> mapper = mapperSupplier.get();
            mapper.setInput(input);
            pool.execute(mapper);
            mappers.add(mapper);
        }
        Map<K, List<V>> mapResults = new HashMap<>();
        for(Mapper<IN, K, V> mapper: mappers){
            Map<K, V> map = mapper.join();
            for(K key: map.keySet()){
                mapResults.putIfAbsent(key, new LinkedList<>());
                mapResults.get(key).add(map.get(key));
            }
        }
        Map<K, Reducer<K, V, OUT>> reducers = new HashMap<>();
        mapResults.forEach(
            (k, v) -> {
                Reducer<K, V, OUT> reducer = reducerSupplier.get();
                reducer.setInput(k, v);
                pool.execute(reducer);
                reducers.put(k, reducer);
            }
        );
        Map<K, OUT> result = new HashMap<>();//
        reducers.forEach(
            (k, reducer) -> {
                result.put(k, reducer.join());
            }
        );
        return result;
    }
    public void setMapperSupplier(Supplier<Mapper<IN, K, V>> mapperSupplier){
        this.mapperSupplier = mapperSupplier;
    }
    public void setReducerSupplpier(Supplier<Reducer<K, V, OUT>> reducerSupplier){
        this.reducerSupplier = reducerSupplier;
    }
    public void setInput(List<IN> inputList){
        this.inputList = inputList;
    }
}
