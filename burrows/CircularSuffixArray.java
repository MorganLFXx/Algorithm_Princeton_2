// Hint:
// Be sure not to create new String objects when you sort the suffixes.
// 一种自然的方法是定义一个嵌套类 CircularSuffix，它隐式表示一个循环后缀（通过引用输入字符串和指向循环后缀中第一个字符的指针）。

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray
{
    private static final int R = 256; // extended ASCII alphabet
    private final int length;
    private final String str;
    private final CircularSuffix[] suffixes;

    private class CircularSuffix implements Comparable<CircularSuffix>
    {
        private final int offset;

        public CircularSuffix(int offset)
        {
            this.offset = offset;
        }

        public int compareTo(CircularSuffix that)
        {
            for (int i = 0; i < length; i++)
            {
                char c1 = str.charAt((this.offset + i) % length);
                char c2 = str.charAt((that.offset + i) % length);
                if (c1 < c2) return -1;
                if (c1 > c2) return 1;
            }
            return 0;
        }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s)
    {
        if (s == null) throw new IllegalArgumentException();
        str = s;
        length = s.length();
        suffixes = new CircularSuffix[length];
        for (int i = 0; i < length; i++)
        {
            suffixes[i] = new CircularSuffix(i);
        }
        sort(s, suffixes);
        // if (length <= 10)
        // {
        //     insertionSort(s, suffixes, 0, length - 1, 0);
        // }
        // else
        // {
        //     sort(s, suffixes);
        // }
    }

    // length of s
    public int length()
    {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i)
    {
        if (i < 0 || i >= length) throw new IllegalArgumentException();
        return suffixes[i].offset;
    }

    private static void sort(String str, CircularSuffix[] a)
    {
        CircularSuffix[] aux = new CircularSuffix[str.length()];
        sort(str, a, aux, 0, a.length - 1, 0);
    }

    private static void sort(String str, CircularSuffix[] a, CircularSuffix[] aux, int lo, int hi,
                             int d)
    {
        if (hi <= lo) return;
        // key-indexed counting
        int[] count = new int[R + 2];
        for (int i = lo; i <= hi; i++)
            count[charAt(str, a[i], d) + 2]++;
        for (int r = 0; r < R + 1; r++)
            count[r + 1] += count[r];
        for (int i = lo; i <= hi; i++)
            aux[count[charAt(str, a[i], d) + 1]++] = a[i];
        for (int i = lo; i <= hi; i++)
            a[i] = aux[i - lo];
        // sort R subarrays recursively
        for (int r = 0; r < R; r++)
            sort(str, a, aux, lo + count[r], lo + count[r + 1] - 1, d + 1);

    }

    private static int charAt(String str, CircularSuffix s, int d)
    {
        if (d < str.length()) return str.charAt((d + s.offset) % str.length());
        else return -1;
    }


    // unit testing (required)
    public static void main(String[] args)
    {
        CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println("length: " + csa.length());
        for (int i = 0; i < csa.length(); i++)
        {
            StdOut.println("original:" + " " + i + " " + "sorted:" + csa.index(i));
        }
    }


    // private static void insertionSort(String str, CircularSuffix[] a, int lo, int hi, int d)
    // {
    //     for (int i = lo; i <= hi; i++)
    //         for (int j = i; j > lo && less(str, a[j], a[j - 1], d); j--)
    //             exch(a, j, j - 1);
    // }
    //
    // private static boolean less(String str, CircularSuffix v, CircularSuffix w, int d)
    // {
    //     for (int i = d; i < str.length(); i++)
    //     {
    //         char c1 = str.charAt((v.offset + i) % str.length());
    //         char c2 = str.charAt((w.offset + i) % str.length());
    //         if (c1 < c2) return true;
    //         if (c1 > c2) return false;
    //     }
    //     return false;
    // }
    //
    // private static void exch(CircularSuffix[] a, int i, int j)
    // {
    //     CircularSuffix temp = a[i];
    //     a[i] = a[j];
    //     a[j] = temp;
    // }
}
