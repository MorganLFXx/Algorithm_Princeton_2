/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver
{
    private final AZTrieST<Integer> dict;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary)
    {
        if (dictionary == null) throw new IllegalArgumentException("dictionary is null");
        dict = new AZTrieST<>();
        for (int i = 0; i < dictionary.length; i++)
        {
            dict.put(dictionary[i], i);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board)
    {
        if (board == null) throw new IllegalArgumentException("board is null");
        HashSet<String> validWords = new HashSet<>();

        for (int i = 0; i < board.rows(); i++)
        {
            for (int j = 0; j < board.cols(); j++)
            {
                boolean[][] marked = new boolean[board.rows()][board.cols()];
                HashSet<String> set = dfs(board, i, j, new StringBuilder(), marked);
                validWords.addAll(set);
            }
        }

        return validWords;
    }

    private HashSet<String> dfs(BoggleBoard board, int i, int j, StringBuilder sb,
                                boolean[][] marked)
    {
        int row = board.rows();
        int col = board.cols();
        HashSet<String> set = new HashSet<>();

        if (marked[i][j]) return set;
        char current = board.getLetter(i, j);
        marked[i][j] = true;
        sb.append(current);
        if (current == 'Q')
        {
            sb.append('U');
        }

        // check if the prefix is in the dictionary
        if (!dict.hasPrefix(sb.toString()))
        {
            sb.deleteCharAt(sb.length() - 1);
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == 'Q')
            {
                sb.deleteCharAt(sb.length() - 1);
            }
            marked[i][j] = false;
            return set;
        }

        if (sb.length() > 2 && dict.contains(sb.toString()))
        {
            set.add(sb.toString());
        }

        if (isValid(row, col, i, j + 1)) // right
            set.addAll(dfs(board, i, j + 1, sb, marked));
        if (isValid(row, col, i, j - 1)) // left
            set.addAll(dfs(board, i, j - 1, sb, marked));
        if (isValid(row, col, i + 1, j)) // down
            set.addAll(dfs(board, i + 1, j, sb, marked));
        if (isValid(row, col, i - 1, j)) // up
            set.addAll(dfs(board, i - 1, j, sb, marked));
        // up-right 顺时针
        if (isValid(row, col, i - 1, j + 1)) set.addAll(dfs(board, i - 1, j + 1, sb, marked));
        if (isValid(row, col, i + 1, j + 1)) set.addAll(dfs(board, i + 1, j + 1, sb, marked));
        if (isValid(row, col, i + 1, j - 1)) set.addAll(dfs(board, i + 1, j - 1, sb, marked));
        if (isValid(row, col, i - 1, j - 1)) set.addAll(dfs(board, i - 1, j - 1, sb, marked));
        marked[i][j] = false;
        sb.deleteCharAt(sb.length() - 1);
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == 'Q')
        {
            sb.deleteCharAt(sb.length() - 1);
        }
        return set;
    }

    private boolean isValid(int row, int col, int i, int j)
    {
        if (i < 0 || i >= row) return false;
        if (j < 0 || j >= col) return false;
        return true;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    // 3-4 1, 5 2, 6 3, 7 5, 8+ 11
    public int scoreOf(String word)
    {
        if (word == null) throw new IllegalArgumentException("word is null");
        if (!dict.contains(word)) return 0;
        switch (word.length())
        {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    public static void main(String[] args)
    {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        Set<String> set = (Set<String>) solver.getAllValidWords(board);
        set.stream().sorted().forEach(StdOut::println);
        for (String word : set)
        {
            // StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
