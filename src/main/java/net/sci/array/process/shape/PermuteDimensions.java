/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * @author dlegland
 *
 */
public class PermuteDimensions implements ArrayOperator
{
    int[] dimOrder;

    /**
     * 
     * @param dimOrder the new order of dimensions 
     */
    public PermuteDimensions(int[] dimOrder)
    {
        if (!checkDimOrderValidity(dimOrder))
        {
            throw new IllegalArgumentException("input array is not a valid permutation");
        }
        this.dimOrder = dimOrder;
    }

    /**
     * Should have one instance of each integer between 0 and nd-1
     * 
     * @param dimOrder
     *            the dim order to test
     * @return true if each integer between 0 and dimOrder.length exists in
     *         dimOrder
     */
    private static final boolean checkDimOrderValidity(int[] dimOrder)
    {
        // number of dimensions
        int nd = dimOrder.length;
        
        boolean[] valid = new boolean[nd];
        
        
        // iterate over array elements
        for (int d : dimOrder)
        {
            if (d < 0 || d >= nd)
            {
                throw new IllegalArgumentException("Requires dim values between 0 and " + (nd-1));
            }
            
            valid[d] = true;
        }
        
        for (boolean v : valid)
        {
            if (!v) 
                return false;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> Array<T> process(Array<T> array)
    {
        if (array.dimensionality() != dimOrder.length)
        {
            throw new IllegalArgumentException("Requires an array with " + dimOrder.length + " dimensions");
        }

        // compute new size
        int nd = dimOrder.length;
        int[] dims = array.getSize();
        int[] newDims = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            newDims[d] = dims[dimOrder[d]];
        }
        
        // create result
        Array<T> result = array.newInstance(newDims);
        
        // iterate over positions of new array
        int[] newPos = new int[nd];
        for (int[] pos : array.positions())
        {
            for (int d = 0; d <nd; d++)
            {
                newPos[d] = pos[dimOrder[d]];
            }
            result.set(newPos, array.get(pos));
        }
        
        return result;
    }
    
    @Override
    public boolean canProcess(Array<?> array)
    {
        return array.dimensionality() == dimOrder.length;
    }
}