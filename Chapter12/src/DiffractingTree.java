public class DiffractingTree {
    DiffractingBalancer root;
    DiffractingTree[] child;
    int size;
    public DiffractingTree(int mySize){
        size = mySize;
        root = new DiffractingBalancer(size);
        if(size > 2){
            child = new DiffractingTree[] {
                new DiffractingTree(size/2),
                new DiffractingTree(size/2)
            };
        }
    }

    public int traverse() throws InterruptedException{
        int half = root.traverse();
        if(size > 2){
            return 2 * child[half].traverse() + half;//perfect shuffle;
        }else{
            return half;
        }
    }
}
