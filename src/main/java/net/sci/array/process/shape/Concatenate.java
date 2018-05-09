/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.Arrays;

/**
 * Concatenates two arrays of same type.
 * 
 * @author dlegland
 *
 */
public class Concatenate
{
    int dim = 0;
    
    /**
     * Default constructor, that concatenates along dimension 0. 
     */
    public Concatenate()
    {
    }
    
    /**
     * Concatenates along the specified dimension. 
     */
    public Concatenate(int dim)
    {
        this.dim = dim;
    }
    
    public <T> Array<T> process(Array<T> array1, Array<? extends T> array2)
    {
        int nd = Math.max(array1.dimensionality(), dim + 1);
        int[] newDims = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            if (d != dim)
            {
                // both arrays should have same size in the dimensions that are not concatenated
                if (array1.getSize(d) != array2.getSize(d))
                {
                    throw new IllegalArgumentException("Both arrays must have same size in dimension " + (d+1));
                }
                newDims[d] = array1.getSize(d);
            }
            else
            {
                // For the dimension of concatenation, simply add the size of each array
                int size =  array1.dimensionality() > d ? array1.getSize(d) : 1;
                size += array2.dimensionality() > d ? array2.getSize(d) : 1;
                newDims[d] = size;
            }
        }

        // Create new array
        Array<T> result = array1.newInstance(newDims);

        int[] offset = new int[nd];
        Arrays.copy(array1, result, offset);
        offset[this.dim] = array1.dimensionality() > this.dim ? array1.getSize(this.dim) : 1;
        Arrays.copy(array2, result, offset);
                
        return result;
    }
    
}
