import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class WordCount {
    static List<String> text;
    static int numThreads = 8;
    
    public static void main(String[] args) {
        text = readFile("src/revalation.txt");
        List<List<String>> inputs = splitInputs(text);
        MapReduce<List<String>, String, Long, Long> mapReduce = new MapReduce<>();
        mapReduce.setMapperSupplier(WordCount.WCMapper::new);
        mapReduce.setReducerSupplpier(WordCount.WCReducer::new);
        mapReduce.setInput(inputs);
        Map<String, Long> map = mapReduce.call();
        /*
        RecursiveWordCountTask wCountTask = new RecursiveWordCountTask(
            text.spliterator()
        );
        Map<String, Long> map = wCountTask.compute();
        */
        displayOutout(map);
    }

    private static void displayOutout(Map<String, Long> map) {
        map.entrySet().stream().sorted(
            (x1, x2) -> {return Long.compare(x2.getValue(), x1.getValue());}
        ).limit(10)
         .forEach((kvp) -> {System.out.println(kvp.getKey() + ": " + kvp.getValue());});
    }

    private static List<List<String>> splitInputs(List<String> words) {
        ArrayList<List<String>> divided = new ArrayList<>(numThreads);
        for(int i = 0; i < numThreads; i++) 
            divided.add(new LinkedList<>());
        int nextList = 0;
        for(var word: words){
            divided.get(nextList).add(word);
            nextList = (nextList + 1) % numThreads;
        }
        return divided;
    }

    private static List<String> readFile(String file) {
        List<String> words = new LinkedList<>();
        try(Scanner sc = new Scanner(Files.newInputStream(Path.of(file)))){

            while(sc.hasNext()){
                sc.useDelimiter("\\b");
                String word = sc.next();
                if(word.chars().allMatch(Character::isAlphabetic))
                    words.add(word.toLowerCase());
            }
        }catch(IOException ex){
            ex.printStackTrace();
            System.exit(-1);
        }
        return words;
    }

    static private class WCMapper extends Mapper<List<String>, String, Long> {
        @Override
        protected Map<String, Long> compute() {
            Map<String, Long> map = new HashMap<>();
            input.forEach(
                (w) -> map.merge(w, 1L, (x, y) -> x + y)
            );
            return map;
        }
    }

    static private class WCReducer extends Reducer<String, Long, Long>{
        @Override
        protected Long compute() {
            long count = 0;
            for(long c: valueList){
                count += c;
            }
            return count;
        }
    }
}
