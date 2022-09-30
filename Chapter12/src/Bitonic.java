public class Bitonic {
    Bitonic[] half;
    Merger merger;
    final int width;
    public Bitonic(int myWidth) {
        width = myWidth;
        merger = new Merger(width);
        if(width > 2){
            half = new Bitonic[] {
                new Bitonic(width/2),
                new Bitonic(width/2)
            };
        }
    }

    public int traverse(int input) {
        int output = 0;
        int subnet = input / (width / 2);
        if(width > 2){
            output = half[subnet].traverse(input - subnet*(width/2));
        }
        return merger.traverse(output+subnet*(width/2));
    }
}
