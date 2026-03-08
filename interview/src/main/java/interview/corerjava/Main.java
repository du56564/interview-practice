package interview.corerjava;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
         System.out.println(rollString("abz", new int[]{3,2,1}));
    }

    private static String rollString(String s, int[] roll) {
            int n = s.length();
            int[] count = new int[n + 1];

            // Mark increments
            for (int r : roll) {
                count[0] += 1;
                if (r < n) {
                    count[r] -= 1;
                }
            }

            System.out.println(Arrays.toString(count));
            
            // Prefix sum to calculate total rolls per index
            for (int i = 1; i < n; i++) {
                count[i] += count[i - 1];
            }

            System.out.println(Arrays.toString(count));

            char[] chars = s.toCharArray();

            for (int i = 0; i < n; i++) {
                int shift = count[i] % 26;
                chars[i] = (char) ((chars[i] - 'a' + shift) % 26 + 'a');
            }

            return new String(chars);
    }
}