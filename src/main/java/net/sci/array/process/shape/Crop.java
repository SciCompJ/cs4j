/**
 * 
 */
package net.sci.array.process.shape;

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
    /**
     * The minimum index to keep (inclusive) for each dimension
     */
    int[] minIndices;

    /**
     * The size of the cropped array in each dimension.
     */
    int[] sizes;
    
    /**
     * 
     * @param minIndices
     *            the minimum index to keep (inclusive) for each dimension
     * @param maxIndices
     *            the maximum index to keep (exclusive) for each dimension
     */
    public Crop(int[] minIndices, int[] maxIndices)
    {
        if (minIndices.length != maxIndices.length)
        {
            throw new IllegalArgumentException("both rrays mus have same length");
        }
        this.minIndices = minIndices;

        this.sizes = computeSizes(minIndices, maxIndices);
    }
    
    private int[] computeSizes(int[] minIndices, int[] maxIndices)
    {
        int nd = minIndices.length;
        int[] sizes = new int[nd];
        for (int d = 0; d < nd; d++)
        {
            int siz = maxIndices[d] - minIndices[d];
            if (siz < 1)
            {
                throw new IllegalArgumentException("size of crop array must be reater that 0");
            }
            sizes[d] = siz;
        }
        return sizes;
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        // create result array
        Array<T> res = array.newInstance(sizes);

        return process(array, res);
    }

    /**
     * Specific implementation for 2D arrays
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
            throw new RuntimeException("min and max indices arrays must have length of " + nd);
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
        for (int d = 0;d < nd; d++)
        {
            if (target.getSize(d) != sizes[d])
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
}
