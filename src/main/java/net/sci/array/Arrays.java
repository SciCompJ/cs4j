/**
 * 
 */
package net.sci.array;

import net.sci.array.generic.GenericArray;

/**
 * A collection of utility methods for arrays.
 * 
 * @author dlegland
 */
public class Arrays
{
    /**
     * Static factory for creating generic arrays.
     * 
     * @param <T>
     *            the type of the array.
     * @param sizes
     *            the dimensions of the array
     * @param initValue
     *            the initialization value used to fill the array
     * @return a new array with specified dimensions
     */
    public static final <T> Array<T> create(int[] sizes, T initValue)
    {
        return GenericArray.create(sizes, initValue);
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
     * @see Array#size()
     * @see Array#size(int)
     */
    public static boolean isSameSize(Array<?> array1, Array<?> array2)
    {
        // first check dimensionality
        int nd1 = array1.dimensionality();
        if (array2.dimensionality() != nd1) return false;
        
        // check each dimension successively
        for (int d = 0; d < nd1; d++)
        {
            if (array1.size(d) != array2.size(d)) return false;
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
    
    /**
     * Copy the contents of the source array into the target array, with an
     * optional offset. Both array must have same dimensionality, but target
     * array may have a larger size.
     * 
     * @param <T>
     *            the type of the data to copy.
     * @param source
     *            the source array.
     * @param target
     *            the target array.
     * @param offset
     *            the position of the first element to copy in the target array.
     *            Must have a length equal to dimensionality of input arrays.
     */
    public static <T> void copy(Array<? extends T> source, Array<? super T> target, int[] offset)
    {
        // dimensionality of source array
        int nd = source.dimensionality();
        
        // initializes a default target position from the offset
        int[] pos2 = new int[nd];
        System.arraycopy(offset, 0, pos2, 0, nd);
        
        for (int[] pos : source.positions())
        {
            // update the first nd positions
            for (int d = 0; d  < nd; d++)
            {
                pos2[d] = pos[d] + offset[d];
            }
            target.set(pos2, source.get(pos));
        }
    }
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Arrays()
    {
    } 
}
