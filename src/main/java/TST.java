import java.util.*;

public class TST<Value> {
    public Node<Value> root;

    public static class Node<Value> {
        public char c;
        public Node<Value> left, mid, right;
        public Value val;
    }

    // Inserts the key value pair into ternary search tree
    public void put(String key, Value val) {
        if (key == null || key.isEmpty())
            return;
        root = place(root, key, val, 0);
    }

    private Node place(Node node, String key, Value val, int i){
        char c = key.charAt(i);
        if(node == null){
            node = new Node();
            node.c = c;
        }
        if(c < node.c)
            node.left = place(node.left,key,val,i);
        else if(c > node.c)
            node.right = place(node.right, key, val, i);
        else if(i < key.length()-1)
            node.mid = place(node.mid, key, val, i+1);
        else
            node.val = val;

        return node;
    }

    // Returns a list of values using the given prefix
    public List<Value> valuesWithPrefix(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("calls keysWithPrefix() with null argument");
        }

        ArrayList<String> list = new ArrayList<>();
        ArrayList<Value> vList = new ArrayList<>();

        Node<Value> x = get(root, prefix, 0);
        if (x == null)
            return null;
        collect(x.mid, new StringBuilder(prefix), list);
        for(String str : list){
            vList.add(get(str));
        }
        return vList;
    }

    //Get the value of the key
    public Value get(String key) {
        if (key == null)
            return null;

        if (key.length() == 0)
            throw new IllegalArgumentException("Key must have length >= 1");

        Node<Value> x = get(root, key, 0);
        if (x == null)
            return null;
        return x.val;
    }

    //Traverse the tree and find retrieve the keys
    private Node<Value> get(Node<Value> x, String key, int i) {
        if (x == null)
            return null;
        char c = key.charAt(i);
        if (c < x.c)
            return get(x.left,  key, i);
        else if (c > x.c)
            return get(x.right, key, i);
        else if (i < key.length() - 1)
            return get(x.mid,   key, i+1);
        else
            return x;
    }

    // all keys in subtrie rooted at x with given prefix
    private void collect(Node<Value> x, StringBuilder prefix, List<String> list) {
        if (x == null)
            return;
        collect(x.left,  prefix, list);
        if (x.val != null)
            list.add(prefix.toString() + x.c);
        collect(x.mid,   prefix.append(x.c), list);
        prefix.deleteCharAt(prefix.length() - 1);
        collect(x.right, prefix, list);
    }

}