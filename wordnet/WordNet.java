/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.HashSet;

public class WordNet
{
    private final HashSet<String> words;
    private final ArrayList<HashSet<String>> synsets;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms)
    {
        // corner case
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Null argument");
        In in1 = new In(synsets);
        In in2 = new In(hypernyms);
        // The input to the constructor does not correspond to a rooted DAG. ???
        words = new HashSet<>();
        String[] synsetsLines = in1.readAllLines();
        String[] hypernymsLines = in2.readAllLines();
        this.synsets = new ArrayList<>();
        Digraph hepernymsGraph = new Digraph(hypernymsLines.length);
        // according to the synsets file, create a map
        for (int i = 0; i < synsetsLines.length; i++)
        {
            String[] synsetsLine = synsetsLines[i].split(",");
            String[] nouns = synsetsLine[1].split(" ");
            this.synsets.add(i, new HashSet<>());
            for (int j = 0; j < nouns.length; j++)
            {
                words.add(nouns[j]);
                this.synsets.get(i).add(nouns[j]);
            }
        }
        // according to the hypernyms file, create a graph
        for (int i = 0; i < hypernymsLines.length; i++)
        {
            String[] hypernymsLine = hypernymsLines[i].split(",");
            int v = Integer.parseInt(hypernymsLine[0]);
            for (int j = 1; j < hypernymsLine.length; j++)
            {
                int w = Integer.parseInt(hypernymsLine[j]);
                if (w < 0 || w >= hypernymsLines.length)
                    throw new IllegalArgumentException("Out of range");
                hepernymsGraph.addEdge(v, w);
            }
        }
        validate(hepernymsGraph);
        sap = new SAP(hepernymsGraph);
    }

    private void validate(Digraph g)
    {
        assert g != null;
        int vertexNumber = g.V();
        int rootNumber = 0;
        for (int i = 0; i < vertexNumber; i++)
        {
            // 出度为 0 的点是根节点（没有上位词的同义词集）
            if (g.outdegree(i) == 0)
            {
                rootNumber++;
            }
        }
        // 根节点不足 1 或者大于 1 都不满足条件
        if (rootNumber != 1)
        {
            throw new IllegalArgumentException();
        }
        // The program uses neither 'DirectedCycle' nor 'Topological' to check whether the digraph is a DAG.
        DirectedCycle dc = new DirectedCycle(g);
        if (dc.hasCycle())
        {
            throw new IllegalArgumentException();
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns()
    {
        return words;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word)
    {
        if (word == null) throw new IllegalArgumentException("Null argument");
        return words.contains(word);
    }

    // find the noun is in which vertex
    private Iterable<Integer> findVertex(String noun)
    {
        HashSet<Integer> set = new HashSet<>();
        for (int i = 0; i < synsets.size(); i++)
        {
            if (synsets.get(i).contains(noun)) set.add(i);
        }
        return set;
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB)
    {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Null argument");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNet noun");
        return sap.length(findVertex(nounA), findVertex(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB)
    {
        if (nounA == null || nounB == null) throw new IllegalArgumentException("Null argument");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Not a WordNet noun");
        int ancestorVertex = sap.ancestor(findVertex(nounA), findVertex(nounB));
        String res = "";
        for (String s : synsets.get(ancestorVertex))
        {
            res += (s + " ");
        }
        return res.substring(0, res.length() - 1);
    }

    // do unit testing of this class
    public static void main(String[] args)
    {
        WordNet wordNet = new WordNet(args[0], args[1]);
        System.out.println(wordNet.isNoun("a"));
    }
}