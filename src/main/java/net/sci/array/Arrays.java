/**
 * 
 */
package net.sci.array;

/**
 * A collection of utility methods for arrays.
 * 
 * @author dlegland
 */
public class Arrays
{
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Arrays()
    {
    }
    
    public static boolean isSameSize(Array<?> array1, Array<?> array2)
    {
        // first check dimensionality
        int nd1 = array1.dimensionality();
        if (array2.dimensionality() != nd1) return false;
        
        // check each dimension successively
        for (int d = 0; d < nd1; d++)
        {
            if (array1.getSize(d) != array2.getSize(d)) return false;
        }
        
        // otherwise return true
        return true;
    }
    
    public static boolean isSameDimensionality(Array<?> array1, Array<?> array2)
    {
        return array1.dimensionality() == array2.dimensionality();
    }
}
