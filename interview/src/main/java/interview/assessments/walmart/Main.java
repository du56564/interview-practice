package interview.assessments.walmart;
/*
Q1.
Given a set of songs, N songs play them all provided below conditions satisfy.
1. We will play the songs in random order
2. While we play none of the songs will repeat. all the songs will play only once.

Input: 1,2,3,4,5,6,7,8,9,10
Output1: 3,5,1,4,8,9,10,2,6,7
Output2: 8,2,1,9,10,3,5,6,7,4

- random fun 1 -> n
- play = randomNum
- alreadyPlayed = if set(randomNum)

TC : O(N)
SC: O(N) -> O(1)

[3]
visited =


Q2.
Given the root of a binary tree, returns its diameter.
The diameter of a binary tree is defined as the length of the longest path (in terms of the number of edges) between any two nodes in the tree.
This path may or may not pass through the root

diameter is 3.
4-2-1-3 (3 edges)
5-2-1-3 (3 edges)

    1
   / \
  2   3
/ \
4   5
   / \
   6  7

4=[1], 5= [1]
2= [2,3]
1[]

1+max(left, right)
d = max(d, left+right+nodeItself)

 */
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    public TreeNode (int val) {
        this.val = val;
        this.left = null;
        this.right = null;
    }
}
class Solution {
   int diameter = 0;
   public int getDiameter (TreeNode root) {
       dfs (root);
       return diameter;
   }
   private int dfs (TreeNode node) {
       // base case
       if (node == null) {
           return 0;
       }
       int left = dfs (node.left);
       int right = dfs (node.right);
       diameter = Math.max(diameter, left + right);
       return 1 + Math.max(left, right);
   }
}

class MainSol {

    public static void main(String[] args){
        TreeNode root = new TreeNode(1);
        TreeNode nodeTwo = new TreeNode(2);
        TreeNode nodeThree = new TreeNode(3);


        TreeNode nodeFour = new TreeNode(4);
        TreeNode nodeFive = new TreeNode(5);

        TreeNode nodeSix = new TreeNode(6);
        TreeNode nodeSeven = new TreeNode(7);

        nodeFive.left = nodeSix;
        nodeFive.right = nodeSeven;

        nodeTwo.left = nodeFour;
        nodeTwo.right = nodeFive;

        root.left = nodeTwo;
        root.right = nodeThree;

        Solution solution = new Solution();
        int res = solution.getDiameter(root);
        System.out.println(res);
    }

}
