import edu.princeton.cs.algs4.Queue;

public class AZTrieST<Value>
{
    private static final int R = 26;       // only A-Z

    private Node root;
    private int n;

    private static class Node
    {
        private Object val;
        private Node[] next = new Node[R];
    }

    public AZTrieST()
    {

    }

    public Value get(String key)
    {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    public boolean contains(String key)
    {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    private Node get(Node x, String key, int d)
    {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next[c - 'A'], key, d + 1);
    }


    public void put(String key, Value val)
    {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) delete(key);
        else root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d)
    {
        if (x == null) x = new Node();
        if (d == key.length())
        {
            if (x.val == null) n++;
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next[c - 'A'] = put(x.next[c - 'A'], key, val, d + 1);
        return x;
    }

    public int size()
    {
        return n;
    }

    public boolean isEmpty()
    {
        return size() == 0;
    }

    public Iterable<String> keys()
    {
        return keysWithPrefix("");
    }

    public Iterable<String> keysWithPrefix(String prefix)
    {
        Queue<String> results = new Queue<String>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results)
    {
        if (x == null) return;
        if (x.val != null) results.enqueue(prefix.toString());
        for (char c = 0; c < R; c++)
        {
            prefix.append(c);
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    public void delete(String key)
    {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d)
    {
        if (x == null) return null;
        if (d == key.length())
        {
            if (x.val != null) n--;
            x.val = null;
        }
        else
        {
            char c = key.charAt(d);
            x.next[c - 'A'] = delete(x.next[c - 'A'], key, d + 1);
        }
        if (x.val != null) return x;
        for (char c = 0; c < R; c++)
            if (x.next[c] != null) return x;
        return null;
    }

    public boolean hasPrefix(String prefix)
    {
        Node x = get(root, prefix, 0);
        return x != null;
    }


    public static void main(String[] args)
    {

    }
}
