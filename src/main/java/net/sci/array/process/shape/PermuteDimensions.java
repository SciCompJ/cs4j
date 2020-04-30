/**
 * 
 */
package net.sci.array.process.shape;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * Permutes the dimensions of the array to process.
 * 
 * <p>Example:
 * <pre>{@code
    // create input 5x4x3 array
    int[] dims = new int[] {5, 4, 3};
    Array<?> array = UInt8Array.create(dims);
    // create permute dimensions operator 
    int[] newOrder = new int[] {2, 0, 1};
    PermuteDimensions op = new PermuteDimensions(newOrder);
    // apply operator to array
    Array<?> result = op.process(array);
    // resulting dimensions should be: int[] {3, 5, 4};
    int[] newDims = result.size(); 
 * }</pre>
 * @author dlegland
 *
 */
public class PermuteDimensions implements ArrayOperator
{
    /** 
     * The indices of the dimensions in the new array. Shoud be a permutation of the integers between 0 and nd.
     */
    int[] dimOrder;

    /**
     * Creates a new instance of the PermuteDimensions operator.
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
        int[] dims = array.size();
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
            for (int d = 0; d < nd; d++)
            {
                newPos[d] = pos[dimOrder[d]];
            }
            result.set(array.get(pos), newPos);
        }
        
        return result;
    }
    
    /**
     * Creates a view on the given array that permutes the dimensions.
     * 
     * @param array
     *            the reference array.
     * @return a view on the reference array with permuted dimensions.
     */
    public <T> Array<?> createView(Array<T> array)
    {
        if (array.dimensionality() > dimOrder.length)
        {
            throw new IllegalArgumentException(String.format(
                    "Requires an array of dimensionality %d, not %d",
                    dimOrder.length, array.dimensionality()));
        }

        int[] newDims = computeOutputArraySize(array);

        int nd = dimOrder.length;
        Function<int[], int[]> mapping = (int[] pos) ->
        {
            int[] srcPos = new int[pos.length];
            for (int d = 0; d < nd; d++)
            {
                srcPos[dimOrder[d]] = pos[d];
            }
            return srcPos;
        };
        
        return array.view(newDims, mapping);
    }

    private int[] computeOutputArraySize(Array<?> inputArray)
    {
        // number of dimensions of new array
        int nd = dimOrder.length;
        
        int[] dims = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            dims[d] = inputArray.size(dimOrder[d]);
        }

        return dims;
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        return array.dimensionality() == dimOrder.length;
    }
}
