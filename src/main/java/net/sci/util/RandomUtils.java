/**
 * 
 */
package net.sci.util;

import java.util.Random;

/**
 * Utilities to generate random numbers or sequences.
 * 
 * @author dlegland
 *
 */
public class RandomUtils
{
    /** Private constructor to prevent instantiation. */
    private RandomUtils()
    {        
    }
    
    public static final int[] randomPermutationIndices(int n)
    {
        // initialize array
        int[] indices = new int[n];
        for (int i = 0; i < n; i++)
        {
            indices[i] = i;
        }
        
        // performs n random permutations
        Random rand = new Random();
        for (int i = 0; i < n - 1; i++)
        {
            // choose index to permute
            int i2 = rand.nextInt(n-i) + i;
            
            // swap indices
            int tmp = indices[i];
            indices[i] = indices[i2];
            indices[i2] = tmp;
        }
        
        return indices;
    }
    
    /**
     * Chooses <it>k</it> indices between 0 and <it>n-1</it> without repetition.
     * 
     * @param n
     *            the number possible indices
     * @param k
     *            the number of indices in the subset
     * @return an array with <it>k</it> integer values between 0 and
     *         <it>n-1</it>
     */
    public static final int[] randomSubsetIndices(int n, int k)
    {
        // choose random individuals
        Random rand = new Random();
        int[] indices = new int[k];
        for (int i = 0; i < k; i++)
        {
            // check index is not already chosen
            do
            {
                indices[i] = rand.nextInt(n);
            } 
            while (containsIndex(indices, i, indices[i]));
        }
        return indices;
    }
    
    private static final boolean containsIndex(int[] indices, int n, int index)
    {
        for (int i = 0; i < n; i++)
        {
            if (indices[i] == index)
            {
                return true;
            }
        }
        return false;
    }
}
