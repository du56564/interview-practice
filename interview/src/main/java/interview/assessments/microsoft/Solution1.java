package interview.assessments.microsoft;
/*
A cyber security expert has just intercepted a transmission from a group of cybercriminals. The transmission contains an array arr of binary codes, which represents some confidential information. However, after a thorough analysis, the expert discovered a vulnerability in the encryption method. An array is considered to be compromised if the bitwise OR of all elements in the subarray is present in the array itself.
The goal is to find the number of compromised subarrays in the intercepted transmission and prevent the cybercriminals from exploiting this weakness in their encryption.
Note: A subarray is defined as any contiguous segment of the array.
Example
Given, n = 3 and arr = [2, 4, 7).
 */

import java.util.*;

public class Solution1 {

    public static long getCompromisedSubarrayCount(int[] arr) {

        Set<Integer> arraySet = new HashSet<>();
        for (int num : arr) {
            arraySet.add(num);
        }
        long count = 0;
        Map<Integer, Integer> prev = new HashMap<>();

        for (int num : arr) {

            Map<Integer, Integer> curr = new HashMap<>();
            // single element subarray
            curr.put(num, curr.getOrDefault(num, 0) + 1);

            for (Map.Entry<Integer, Integer> e : prev.entrySet()) {

                int newOR = e.getKey() | num;
                int freq = e.getValue();
                curr.put(newOR, curr.getOrDefault(newOR, 0) + freq);
            }

            // count all valid subarrays
            for (Map.Entry<Integer, Integer> e : curr.entrySet()) {
                if (arraySet.contains(e.getKey())) {
                    count += e.getValue();
                }
            }

            prev = curr;
        }

        return count;
    }


    static void main(String args[]) {
        System.out.println(getCompromisedSubarrayCount(new int[]{2,4,7}));

    }
}