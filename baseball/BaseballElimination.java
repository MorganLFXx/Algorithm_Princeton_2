/* *****************************************************************************
 *  Name:xinxiang
 *  Date:2024.7.27
 *  Description:
 **************************************************************************** */

// Analysis:
// 先计算平凡消除，即直接算就能发现必然不会胜利的队伍
// 然后计算非平凡消除，对于每个队伍，用剩下的队伍构建一个流网络，然后求最小切，在最小切s侧中的队伍，就是能消除该队伍的R-set
// 因为能在s侧最小切，表示该队伍的胜利次数和剩余比赛次数的和小于等于这些队伍的胜利次数，所以这些队伍必然能消除该队伍

// 流网络建模如下：
// source s to every game i-j as vertexes and its capacity is g[i][j]
// then, from game to each opponent team(no restrict capacity)
// finally, from every team i to sink t and its capacity is w[x]+r[x]-w[i]

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination
{
    private final int numTeams;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] lefts;
    private final int[][] games;

    private boolean[] isEliminates;
    private boolean[] expectedEliminates; // 在构造函数中，用于存储检查完平凡消除后的情况
    private HashMap<Integer, List<String>> eliminateTeams = new HashMap<Integer, List<String>>();
    // private List<String>[] eliminateTeams;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename)
    {
        // Initial
        In in = new In(filename);
        numTeams = in.readInt();
        if (numTeams <= 0) throw new IllegalArgumentException("number of teams is negative");

        isEliminates = new boolean[numTeams];
        expectedEliminates = new boolean[numTeams];

        teams = new String[numTeams];
        wins = new int[numTeams];
        losses = new int[numTeams];
        lefts = new int[numTeams];
        games = new int[numTeams][numTeams];
        for (int i = 0; i < numTeams; i++)
        {
            teams[i] = in.readString();
            wins[i] = in.readInt();
            losses[i] = in.readInt();
            lefts[i] = in.readInt();
            for (int j = 0; j < numTeams; j++)
                games[i][j] = in.readInt();
        }

        // Check Trivial Elimination
        checkTrivialElimination();

        // Check Non-Trivial Elimination
        int numToCheck = 0;
        for (int i = 0; i < numTeams; i++)
        {
            if (!isEliminates[i])
            {
                numToCheck++;
            }
        }
        // special
        if (numToCheck == 0 || numToCheck == 1 || numToCheck == 2) return;

        for (int i = 0; i < numTeams; i++)
        {
            expectedEliminates[i] = isEliminates[i];
        }
        for (int i = 0; i < numTeams; i++)
        {
            if (isEliminates[i]) continue;
            checkNonTrivialElimination(i, numToCheck);
        }
    }

    // number of teams
    public int numberOfTeams()
    {
        return numTeams;
    }

    // all teams
    public Iterable<String> teams()
    {
        return Arrays.asList(teams);
    }

    // number of wins for given team
    public int wins(String team)
    {
        if (hasNoThisTeam(team)) throw new IllegalArgumentException();
        return wins[Arrays.asList(teams).indexOf(team)];
    }

    // number of losses for given team
    public int losses(String team)
    {
        if (hasNoThisTeam(team)) throw new IllegalArgumentException();
        return losses[Arrays.asList(teams).indexOf(team)];
    }

    // number of remaining games for given team
    public int remaining(String team)
    {
        if (hasNoThisTeam(team)) throw new IllegalArgumentException();
        return lefts[Arrays.asList(teams).indexOf(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2)
    {
        if (hasNoThisTeam(team1)) throw new IllegalArgumentException();
        if (hasNoThisTeam(team2)) throw new IllegalArgumentException();
        return games[Arrays.asList(teams).indexOf(team1)][Arrays.asList(teams).indexOf(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team)
    {
        if (hasNoThisTeam(team)) throw new IllegalArgumentException();
        return isEliminates[Arrays.asList(teams).indexOf(team)];
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team)
    {
        if (hasNoThisTeam(team)) throw new IllegalArgumentException();
        return eliminateTeams.get(Arrays.asList(teams).indexOf(team));
    }

    public static void main(String[] args)
    {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams())
        {
            if (division.isEliminated(team))
            {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else
            {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    private boolean hasNoThisTeam(String team)
    {
        return !Arrays.asList(teams).contains(team);
    }

    private int calCombination(int n, int k)
    {
        // 边界条件检查
        if (k < 0 || k > n) return 0; // 如果 k 不合法，则组合数为 0
        if (k == 0 || k == n) return 1; // 如果 k 为 0 或者 n，则组合数为 1

        // 利用对称性减少递归次数
        k = Math.min(k, n - k);

        // 递归计算
        return calCombination(n - 1, k) + calCombination(n - 1, k - 1);
    }

    private void checkTrivialElimination()
    {
        for (int i = 0; i < numTeams; i++)
        {
            int maxWins = wins[i] + lefts[i];
            for (int j = 0; j < numTeams; j++)
            {
                if (maxWins < wins[j])
                {
                    isEliminates[i] = true;
                    eliminateTeams.put(i, Arrays.asList(teams[j]));
                    break;
                }
            }
        }
    }

    private void checkNonTrivialElimination(int curTeam, int numToCheck)
    {
        // Construct the flow network
        int gamesV = calCombination(numToCheck - 1, 2);
        // s + games to play + teams remaining + t
        int total = 1 + gamesV + numToCheck - 1 + 1;
        FlowNetwork flowNetwork = new FlowNetwork(total);

        // HashMap<String, Integer> teamToVertex = new HashMap<>(); // 将队伍映射到顶点
        // s--0 t--total-1
        // 1--gamesV games
        // gameV+1--gameV+numToCheck-1 teams
        // teamToVertex.put("s", 0);
        // teamToVertex.put("t", total - 1);

        ArrayList<Integer> leftTeamToWin = new ArrayList<>(); // 还剩下哪些队伍要和当前队伍竞争
        for (int i = 0; i < numTeams; i++)
        {
            if (curTeam == i) continue;
            if (expectedEliminates[i]) continue;
            leftTeamToWin.add(i);
        }

        int curVertex = 1;
        for (int i = 0; i < numToCheck - 1; i++)
        {
            for (int j = i + 1; j < numToCheck - 1; j++)
            {
                int team1 = leftTeamToWin.get(i);
                int team2 = leftTeamToWin.get(j);

                // teamToVertex.put(team1 + "-" + team2, curVertex);
                // teamToVertex.put(teams[team1], 1 + gamesV + j);
                // teamToVertex.put(teams[team2], 1 + gamesV + k);

                // from s to games
                flowNetwork.addEdge(new FlowEdge(0, curVertex, games[team1][team2]));
                // from games to teams
                flowNetwork.addEdge(
                        new FlowEdge(curVertex, 1 + gamesV + i, Double.POSITIVE_INFINITY));
                flowNetwork.addEdge(
                        new FlowEdge(curVertex, 1 + gamesV + j, Double.POSITIVE_INFINITY));
                curVertex++;
            }
        }
        for (int i = 0; i < numToCheck - 1; i++)
        {
            // from teams to t
            int curTeamTryWin = wins[curTeam] + lefts[curTeam] - wins[leftTeamToWin.get(i)];
            flowNetwork.addEdge(new FlowEdge(1 + gamesV + i, total - 1, curTeamTryWin));
        }

        // Compute the max flow using Ford-Fulkerson algorithm
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, 0, total - 1);
        for (int i = 1 + gamesV; i < total - 1; i++)
        {
            if (fordFulkerson.inCut(i))
            {
                isEliminates[curTeam] = true;
                if (eliminateTeams.get(curTeam) == null)
                {
                    eliminateTeams.put(curTeam, new ArrayList<>());
                }
                eliminateTeams.get(curTeam).add(teams[leftTeamToWin.get(i - 1 - gamesV)]);
            }
        }

    }

}

