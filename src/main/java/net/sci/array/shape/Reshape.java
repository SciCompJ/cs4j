/**
 * 
 */
package net.sci.array.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.util.MathUtils;

/**
 * Reshapes the dimension of an array, initialized with values of input array.
 * 
 * The product of dimensions of both arrays should be the same.
 * 
 * @author dlegland
 *
 */
public class Reshape extends AlgoStub implements ArrayOperator
{
    int[] newDims;

    /**
     * Creates a reshape operator.
     * 
     * @param newDims
     *            the dimensions of the array after reshaping
     */
    public Reshape(int[] newDims)
    {
        this.newDims = newDims;
    }
    
    public <T> Array<T> view(Array<T> array)
    {
        long[] cumDims = MathUtils.cumProdLong(array.size());
        long[] targetCumDims = MathUtils.cumProdLong(newDims);
        
        return array.reshapeView(newDims, pos -> 
        {
            // first convert view coordinates into linear index 
            long index = pos[0];
            for (int d = 1; d < pos.length; d++)
            {
                index += targetCumDims[d - 1] * pos[d];
            }
            
            // convert linear index into coordinates into original array 
            int[] res = new int[array.dimensionality()];
            for (int d = res.length - 1; d > 0; d--)
            {
                long numel = cumDims[d - 1];
                res[d] = (int) (index / numel);
                index -= (res[d] * numel);
            }
            res[0] = (int) index;
            
            return res;
        });
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> Array<T> process(Array<T> array)
    {
        // compute element number of input array
        long count = array.elementCount();

        // compute element number of output array
        long count2 = MathUtils.prod(newDims);

        // check element numbers are the same
        if (count != count2)
        {
            throw new IllegalArgumentException("Input array should have same number of elements as product of dimensions");
        }

        // allocate memory
        Array<T> result = array.newInstance(newDims);

        // copy elements of input array to result
        Array.Iterator<T> iter1 = array.iterator();
        Array.Iterator<T> iter2 = result.iterator();
        while (iter1.hasNext())
        {
            iter2.setNext(iter1.next());
        }
        
        return result;
    }
    
    /**
     * Checks if the operator can be applied to the specified array, by
     * comparing the number of elements before and after reshape.
     * 
     * @param array
     *            the array to reshape
     * @return true if array as same number of elements as the number of
     *         dimensions stored in this operator.
     */
    @Override
    public boolean canProcess(Array<?> array)
    {
        // compute element number of input array
        long prodDims = array.elementCount();
        
        // compute element number of output array
        long prodDims2 = MathUtils.prod(newDims);
        
        // check element numbers are the same
        return prodDims == prodDims2;
    }
}
