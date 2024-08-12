/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class MoveToFront
{
    private static final int R = 256;


    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode()
    {
        // 您的任务是维护256个扩展ASCII字符的有序序列。通过将序列中的第i个字符等于第i个扩展ASCII字符来初始化序列。
        LinkedList<Character> q = new LinkedList<Character>();
        for (int i = 0; i < R; i++)
        {
            q.addLast((char) i);
        }
        // 逐个从标准输入读取每个字符c; 输出c出现的序列中的8位索引；并将c移至前面。
        while (!BinaryStdIn.isEmpty())
        {
            char c = BinaryStdIn.readChar();
            int i = 0;
            for (char ch : q)
            {
                if (ch == c)
                {
                    break;
                }
                i++;
            }
            BinaryStdOut.write(i, 8);
            q.remove(i);
            q.addFirst(c);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode()
    {
        LinkedList<Character> q = new LinkedList<Character>();
        for (int i = 0; i < R; i++)
        {
            q.addLast((char) i);
        }
        // 逐个从标准输入读取每个位字符 i;写入序列的第 i 个字符；并将该字符移到最前面。检查解码器是否恢复了任何编码消息
        while (!BinaryStdIn.isEmpty())
        {
            int i = BinaryStdIn.readChar();
            char c = q.remove(i);
            BinaryStdOut.write(c);
            q.addFirst(c);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args)
    {
        if (args[0].equals("-"))
        {
            encode();
        }
        else if (args[0].equals("+"))
        {
            decode();
        }
        else
        {
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}
