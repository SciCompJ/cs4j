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
 
    /**
     * Checks if two arrays have the same size along each dimension.
     * 
     * Both arrays must have same dimensionality.
     * 
     * @param array1
     *            the first array to check
     * @param array2
     *            the second array to check
     * @return true if both arrays have same size
     * 
     * @see Array#getSize()
     * @see Array#getSize(int)
     */
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
    
    /**
     * Checks if two arrays have the same number of dimensions.
     * 
     * Both arrays must have same dimensionality.
     * 
     * @param array1
     *            the first array to check
     * @param array2
     *            the second array to check
     * @return true if both arrays have same number of dimensions
     * 
     * @see Array#dimensionality()
     */
    public static boolean isSameDimensionality(Array<?> array1, Array<?> array2)
    {
        return array1.dimensionality() == array2.dimensionality();
    }
}
