package com.sisimpur.library.util;

public class StringMatchingUtil {

    // Combine substring match and edit distance into a final score
    public static double calculateCombinedScore(String query, String target) {
        // Clean the strings by removing non-alphabetic characters and trimming spaces
        String cleanedQuery = cleanString(query);
        String cleanedTarget = cleanString(target);

        System.out.println("query "+cleanedQuery+" target: "+cleanedTarget);

        // Substring score: check if cleaned query is a substring of cleaned target
        double substringScore = getSubstringScore(cleanedQuery, cleanedTarget);

        System.out.println("substring score: "+substringScore);
        // Edit distance score
        double editDistanceScore = getEditDistanceScore(query, target);

        System.out.println("editDistance score "+editDistanceScore);

        // Combined score: an weighted average of substring score and edit distance score
        //substring is given more priority as it is semantically more meaningful
        double score = 0.5*substringScore + 0.5*editDistanceScore ;
        System.out.println("final score "+score);
        return score;
    }

    private static String cleanString(String input) {

        input = input.trim();
        input = input.replaceAll("[^a-zA-Z0-9]", "");
        return input.toLowerCase();
    }



    private static double getSubstringScore(String query, String target) {
        return target.contains(query) ? 1.0 : 0.0;
    }

    private static double getEditDistanceScore(String query, String target) {
        int distance = editDistance(query, target);
        int maxLength = Math.max(query.length(), target.length());
        return maxLength == 0 ? 0.0 : 1.0 - (double) distance / maxLength;
    }

    private static int editDistance(String str1, String str2) {
        int m = str1.length();
        int n = str2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1] + (str1.charAt(i - 1) == str2.charAt(j - 1) ? 0 : 1),
                                    dp[i - 1][j] + 1),
                            dp[i][j - 1] + 1);
                }
            }
        }
        return dp[m][n];
    }

}

