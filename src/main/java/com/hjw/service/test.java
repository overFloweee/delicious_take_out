package com.hjw.service;

import java.util.Arrays;

/**
 * @author qifei
 * @since 2024-04-17
 */
public class test
{
    public static void main(String[] args)
    {

        // Scanner input = new Scanner(System.in);
        // int amount = input.nextInt();

        int amount = 11;
        int[] coins = new int[]{1, 2, 5};
        // int[] coins = new int[]{3};
        // int[] coins = new int[]{1};

        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        System.out.println(Arrays.toString(dp));
        for (int i = 1; i <= amount; i++)
        {
            for (int j = 0; j < coins.length; j++)
            {
                if (i >= coins[j])
                {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }

        int min = dp[amount] == amount + 1 ? -1 : dp[amount];

        System.out.println(min);
    }
}
