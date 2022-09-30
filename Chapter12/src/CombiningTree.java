import java.util.Stack;

public class CombiningTree {
    Node[] leaf;
    public CombiningTree(int width){
        Node[] nodes = new Node[2 * width - 1];
        nodes[0] = new Node();
        for(int i = 1; i < nodes.length; i++){
            nodes[i] = new Node(nodes[(i-1)/2]);
        }
        leaf = new Node[width];
        for(int i = 0; i < leaf.length; i++){
            leaf[i] = nodes[nodes.length - i - 1];
        }
    }

    public int getAndIncrement() throws PanicException, InterruptedException{
        Stack<Node> stack = new Stack<Node>();
        Node myLeaf = leaf[(int) Thread.currentThread().getId()/2];
        Node node = myLeaf;

        while(node.precombine()) {
            node = node.parent;
        }
        Node stop = node;
        int combined = 1;
        for(node = myLeaf; node != stop; node = node.parent){
            combined = node.combine(combined);
            stack.push(node);
        }
        int prior = stop.op(combined);
        while(!stack.empty()){
            node = stack.pop();
            node.distribute(prior);
        }
        return prior;
    }
}
