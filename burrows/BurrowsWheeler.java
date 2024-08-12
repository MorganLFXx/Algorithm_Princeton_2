/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;


public class BurrowsWheeler
{

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform()
    {
        // 对长度为n的字符串s进行Burrows–Wheeler变换定义如下：
        // 考虑对s的n个环状后缀进行排序的结果。Burrows–Wheeler变换是排序后的后缀数组t[]中的最后一列，该列之前是原始字符串所在的第一行。
        while (!BinaryStdIn.isEmpty())
        {
            int first = 0;
            String s = BinaryStdIn.readString();
            StringBuilder sb = new StringBuilder();
            int length = s.length();
            CircularSuffixArray csa = new CircularSuffixArray(s);
            for (int i = 0; i < s.length(); i++)
            {
                if (csa.index(i) == 0)
                {
                    first = i;
                }
                int offset = csa.index(i) + length;
                sb.append(s.charAt((offset - 1) % length));
            }
            BinaryStdOut.write(first);
            for (int i = 0; i < length; i++)
            {
                BinaryStdOut.write(sb.charAt(i));
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform()
    {
        while (!BinaryStdIn.isEmpty())
        {
            int first = BinaryStdIn.readInt();
            char[] t = BinaryStdIn.readString().toCharArray();
            int length = t.length;
            // 从Burrows–Wheeler变换的最后一列t[]重构排序后的首列
            char[] arr = Arrays.copyOf(t, length);
            Arrays.sort(arr);
            String firstColumn = new String(arr);

            // 从首列和最后一列构造next[]数组
            int[] next = new int[length];
            int[] count = new int[257];
            // The trickiest part   该段几乎与key-indexed counting中的部分相同
            for (int i = 0; i < length; i++)
                count[t[i] + 1]++;
            for (int i = 0; i < 256; i++)
                count[i + 1] += count[i];
            for (int i = 0; i < length; i++)
                next[count[t[i]]++] = i;


            // 通过next[]数组和first的值重构原始字符串
            StringBuilder sb = new StringBuilder();
            sb.append(firstColumn.charAt(first));
            for (int i = 1; i < length; i++)
            {
                sb.append(firstColumn.charAt(next[first]));
                first = next[first];
            }
            BinaryStdOut.write(sb.toString());
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args)
    {
        if (args[0].equals("-"))
        {
            transform();
        }
        else if (args[0].equals("+"))
        {
            inverseTransform();
        }
    }
}
