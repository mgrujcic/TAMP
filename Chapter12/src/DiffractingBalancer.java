import java.util.concurrent.TimeoutException;

public class DiffractingBalancer {
    Prism prism;
    Balancer toggle;
    public DiffractingBalancer(int capacity) {
        prism = new Prism(capacity);
        toggle = new Balancer();
    }

    public int traverse() throws InterruptedException{
        try{
            if(prism.visit())
                return 0;
            else 
                return 1;
        }catch(TimeoutException ex){
            return toggle.traverse();
        }
    }
}
