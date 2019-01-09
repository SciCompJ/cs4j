/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.ArrayOperator;

/**
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
     * The maximum index to keep (exclusive) for each dimension
     */
    int[] maxIndices;
    
    /**
     * 
     * @param minIndices
     *            the minimum index to keep (inclusive) for each dimension
     * @param maxIndices
     *            the maximum index to keep (exclusive) for each dimension
     */
    public Crop(int[] minIndices, int[] maxIndices)
    {
        this.minIndices = minIndices;
        this.maxIndices = maxIndices;
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array instanceof Array2D)
        {
            return process2d((Array2D<?>) array);
        }
        
        // use generic method
        return processNd(array);
    }

    public <T> Array2D<?> process2d(Array2D<T> array)
    {
        if (minIndices.length != 2 || maxIndices.length != 2)
        {
            throw new RuntimeException("min and max indices arrays must have length of 2");
        }
        
        // compute dimensions of new array
        int size0 = this.maxIndices[0] - this.minIndices[0];
        int size1 = this.maxIndices[1] - this.minIndices[1];
        
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
        if (minIndices.length != nd || maxIndices.length != nd)
        {
            throw new RuntimeException("min and max indices arrays must have length of " + nd);
        }

        // compute dimensions of new array
        int[] dims = new int[nd];
        for (int d = 0;d < nd; d++)
        {
            dims[d] = this.maxIndices[d] - this.minIndices[d];
        }
        
        // create result array
        Array<T> res = array.newInstance(dims);

        // iterate over position of result
        int[] pos2 = new int[nd];
        for (int[] pos : res.positions())
        {
            for (int d = 0; d <nd; d++)
            {
                pos2[d] = minIndices[d] + pos[d];
            }
            res.set(pos, array.get(pos2));
        }
        
        return res;
    }
}
