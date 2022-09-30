public class Merger {
    Merger[] half;
    Balancer[] layer;
    final int width;
    public Merger(int myWidth){
        width = myWidth;
        layer = new Balancer[width / 2];
        for(int i = 0; i < width / 2; i++){
            layer[i] = new Balancer();
        }
        if(width > 2){
            half = new Merger[]{
                new Merger(width/2), 
                new Merger(width/2)
            };
        }
    }
    
    public int traverse(int input) { //book method missed the case where width is 2?
        int output = 0;
        if(width > 2){
            if(input < width/2){
                output = half[input % 2].traverse(input / 2);
            }else{
                output = half[1 - (input % 2)].traverse(input / 2);
            }
        }
        
        return (2 * output) + layer[output].traverse();
    }
}
