/**
 * 
 */
package net.sci.array.process.binary;

import net.sci.array.Array;
import net.sci.array.Arrays;
import net.sci.array.binary.BinaryArray;

/**
 * Creates new array by keeping only values for which binary mask is set to TRUE.
 * 
 * @author dlegland
 *
 */
public class BinaryMask
{
    public <T> Array<T> process(Array<T> array, BinaryArray mask)
    {
        // check input validity
        if (!Arrays.isSameSize(array, mask))
        {
            throw new IllegalArgumentException("Both arrays must have same size");
        }
        
        // allocate result array
        Array<T> res = array.newInstance(array.size());
        
        // iterate over positions
        for (int[] pos : res.positions())
        {
            if (mask.getBoolean(pos))
            {
                res.set(pos, array.get(pos));
            }
        }
        
        // return result
        return res;
    }

}
