import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collector;

public class App {
    public static void main(String[] args) throws Exception {
        String niska;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(Path.of("src/podaci.txt"))))){
            String sledeca;
            while((sledeca = in.readLine()) != null){
                String ociscena = sledeca.chars().filter((c) -> !Character.isWhitespace(c)).collect(StringBuilder::new,
                    StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
                System.out.println(ociscena);
                
            }
        }
        
    }
}
