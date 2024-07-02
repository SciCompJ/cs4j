/**
 * 
 */
package net.sci.array.shape;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.ArrayOperator;

/**
 * Perform crop on an array.
 * 
 * Several ways for using it:
 * <ul>
 * <li> <code> Array output = crop.process(input);</code> </li>
 * <li> <code> crop.process(input, output);</code> </li>
 * </ul>
 * @author dlegland
 *
 */
public class Crop extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Static factories
    
    /**
     * Creates a new Crop operator by specifying the min and max bounds along
     * each dimension. The bounds are inclusive in the direction of the minimum,
     * and exclusive in the direction of the maximum.
     * 
     * @param minIndices
     *            the minimum index to keep (inclusive) for each dimension
     * @param maxIndices
     *            the maximum index to keep (exclusive) for each dimension
     */
    public final static  Crop fromMinMax(int[] minIndices, int[] maxIndices)
    {
        if (minIndices.length != maxIndices.length)
        {
            throw new IllegalArgumentException("both arrays must have same length");
        }
        int[] sizes = computeSizes(minIndices, maxIndices);
        return new Crop(minIndices, sizes);
    }
    
    /**
     * Utility function that computes the value of the size of a Crop operator
     * based on the minimal and maximal indices.
     * 
     * @param minIndices
     *            the minimum index to keep (inclusive) for each dimension
     * @param maxIndices
     *            the maximum index to keep (exclusive) for each dimension
     * @return the size of the Crop operator along each dimension.
     */
    public final static int[] computeSizes(int[] minIndices, int[] maxIndices)
    {
        int nd = minIndices.length;
        int[] sizes = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            int siz = maxIndices[d] - minIndices[d];
            if (siz < 1)
            {
                throw new IllegalArgumentException("size of crop array must be Greater that 0");
            }
            sizes[d] = siz;
        }
        return sizes;
    }

    
    // =============================================================
    // Class members
    
    /**
     * The minimum index to keep (inclusive) for each dimension
     */
    int[] minIndices;

    /**
     * The size of the cropped array in each dimension.
     */
    int[] sizes;
    
    
    // =============================================================
    // Constructors
    
    /**
     * 
     * @param minIndices
     *            the minimum index to keep (inclusive) for each dimension
     * @param maxIndices
     *            the maximum index to keep (exclusive) for each dimension
     */
    private Crop(int[] minIndices, int[] sizes)
    {
        if (minIndices.length != sizes.length)
        {
            throw new IllegalArgumentException("both arrays must have same length");
        }
        this.minIndices = minIndices;
        this.sizes = sizes;
    }
    
    
    // =============================================================
    // Implementation of the  ArrayOperator interface

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        // create result array
        Array<T> res = array.newInstance(sizes);

        return process(array, res);
    }

    /**
     * Specific implementation for 2D arrays.
     * 
     * @param array
     *            the input array
     * @return the result of crop
     */
    public <T> Array2D<?> process2d(Array2D<T> array)
    {
        if (minIndices.length != 2 || sizes.length != 2)
        {
            throw new RuntimeException("min and max indices arrays must have length of 2");
        }
        
        // compute dimensions of new array
        int size0 = this.sizes[0];
        int size1 = this.sizes[1];
        
        // create result array
        Array2D<T> res = Array2D.wrap(array.newInstance(size0, size1));
        
        // iterate over position of result
        for (int y = 0; y < size1; y++)
        {
            int y0 = this.minIndices[1];
            for (int x = 0; x < size0; x++)
            {
                res.set(x, y, array.get(x + minIndices[0], y + y0));
            }
        }
        
        return res;
    }

    public <T> Array<?> processNd(Array<T> array)
    {
        int nd = array.dimensionality();
        if (minIndices.length != nd || sizes.length != nd)
        {
            throw new RuntimeException("min indices and size arrays must have length of " + nd);
        }

        // create result array
        Array<T> res = array.newInstance(sizes);

        // iterate over position of result
        return process(array, res);
    }

    public <T> Array<T> process(Array<T> source, Array<T> target)
    {
        int nd = source.dimensionality();
        if (minIndices.length != nd || sizes.length != nd)
        {
            throw new RuntimeException("min indices and size arrays must have length of " + nd);
        }
        if (target.dimensionality() != nd)
        {
            throw new RuntimeException("Source and target arrays musthave same dimensionality");
        }

        // Check target array dimensions
        for (int d = 0; d < nd; d++)
        {
            if (target.size(d) != sizes[d])
            {
                throw new IllegalArgumentException(
                        "Dimensions of target array does not match crop dimensions");
            }
        }
        
        // iterate over position of result
        int[] pos2 = new int[nd];
        for (int[] pos : target.positions())
        {
            for (int d = 0; d <nd; d++)
            {
                pos2[d] = minIndices[d] + pos[d];
            }
            target.set(pos, source.get(pos2));
        }
        
        return target;
    }
    
    public <T> Array<T> createView(Array<T> array)
    {
        int nd = sizes.length;
        if (array.dimensionality() != nd)
        {
            throw new IllegalArgumentException("Input array must have dimensionality " + nd);
        }

        // convert position in view to position in source image
        Function<int[], int[]> mapping = (int[] pos) -> {
            int[] srcPos = new int[nd];
            for (int d = 0; d < nd; d++)
            {
                srcPos[d] = pos[d] + minIndices[d];
            }
            return srcPos;
        };
        
        return array.reshapeView(this.sizes, mapping);
    }
   

}
