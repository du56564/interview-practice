//Minimum Edge Reversals So Every Node Is Reachable
/*
You are given a directed graph with n nodes (labeled from 0 to n - 1) that would form a tree if all edges were bidirectional. The graph is represented by a 2D array edges, where edges[i] = [ui, vi] indicates a directed edge from node ui to node vi.
An edge reversal means changing the direction of an edge - turning an edge from ui → vi into vi → ui.
Your task is to find, for each node i (where 0 ≤ i ≤ n - 1), the minimum number of edge reversals needed so that starting from node i, you can reach all other nodes in the graph by following directed edges.
Return an array answer where answer[i] represents the minimum number of edge reversals required when starting from node i.
Key Points:
The graph forms a tree structure (connected and acyclic) if edges were bidirectional
Each edge is initially directed (one-way)
You need to calculate the answer independently for each starting node
The goal is to make all nodes reachable from a given starting node with minimum edge reversals
Example Understanding: If you have edges [[0,1], [2,0], [3,2]], this creates a directed path: 3 → 2 → 0 → 1.
Starting from node 0: You can reach node 1 directly, but to reach nodes 2 and 3, you'd need to reverse edges 2→0 and 3→2, requiring 2 reversals.
Starting from node 3: You can already reach all nodes following the existing directions, requiring 0 reversals.
 */
package interview.assessments.microsoft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    // Adjacency list where each edge stores [destination, direction]
    // direction: 1 means original direction, -1 means reversed direction
    private List<int[]>[] adjacencyList;
  
    // Result array storing minimum reversals needed from each node
    private int[] minReversals;

    public int[] minEdgeReversals(int n, int[][] edges) {
        // Initialize result array
        minReversals = new int[n];
      
        // Initialize adjacency list
        adjacencyList = new List[n];
        Arrays.setAll(adjacencyList, i -> new ArrayList<>());
      
        // Build bidirectional graph with edge directions
        // For edge (x -> y): x to y has weight 1 (no reversal needed)
        //                     y to x has weight -1 (reversal needed)
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacencyList[from].add(new int[] {to, 1});    // Original direction
            adjacencyList[to].add(new int[] {from, -1});   // Reversed direction
        }
      
        // First DFS: Calculate reversals needed from node 0
        dfs(0, -1);
      
        // Second DFS: Use rerooting to calculate reversals for all other nodes
        dfs2(0, -1);
      
        return minReversals;
    }

    /**
     * First DFS to calculate the number of edge reversals needed
     * to reach all nodes from node 0
     * @param currentNode - current node being visited
     * @param parent - parent node to avoid revisiting
     */
    private void dfs(int currentNode, int parent) {
        // Traverse all neighbors
        for (int[] neighbor : adjacencyList[currentNode]) {
            int nextNode = neighbor[0];
            int direction = neighbor[1];
          
            // Skip parent to avoid cycles
            if (nextNode != parent) {
                // If direction is -1, we need to reverse this edge
                // to go from node 0 to nextNode
                if (direction < 0) {
                    minReversals[0] += 1;
                }
              
                // Continue DFS traversal
                dfs(nextNode, currentNode);
            }
        }
    }

    /**
     * Second DFS using rerooting technique to calculate reversals
     * for all nodes based on the result from node 0
     * @param currentNode - current node being visited
     * @param parent - parent node to avoid revisiting
     */
    private void dfs2(int currentNode, int parent) {
        // Traverse all neighbors
        for (int[] neighbor : adjacencyList[currentNode]) {
            int nextNode = neighbor[0];
            int direction = neighbor[1];
          
            // Skip parent to avoid cycles
            if (nextNode != parent) {
                // Calculate reversals for nextNode based on currentNode
                // If direction is 1: moving root from currentNode to nextNode
                //                     doesn't need this edge reversed
                // If direction is -1: moving root from currentNode to nextNode
                //                      needs this edge reversed
                minReversals[nextNode] = minReversals[currentNode] + direction;
              
                // Continue rerooting for subtree rooted at nextNode
                dfs2(nextNode, currentNode);
            }
        }
    }
}