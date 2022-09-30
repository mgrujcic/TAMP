import java.util.EmptyStackException;
import java.util.concurrent.TimeoutException;

import javax.xml.catalog.CatalogException;

public class EliminationBackoffStack<T> extends LockFreeStack<T> {
    static final int capacity = 8;
    EliminationArray<T> eliminationArray = new EliminationArray<T>(capacity);
    
    public void push(T value){
        Node node = new Node(value);
        while(true){
            if(tryPush(node)){
                return;
            }else try{
                T otherValue = eliminationArray.visit(value);
                if(otherValue == null)
                    return;
            }catch (TimeoutException ex){
                //pass
            }
        }
    }
    public T pop() throws EmptyStackException {

        while (true) {
            Node returnNode = tryPop();
            if(returnNode != null){
                return returnNode.value;
            }else try{
                T otherValue = eliminationArray.visit(null);
                if(otherValue != null) {
                    return otherValue;
                }
            }catch(TimeoutException ex){
                //pass
            }
        }
    }
}
