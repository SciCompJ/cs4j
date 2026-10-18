/**
 * 
 */
package net.sci.array.numeric.process;

import java.util.Comparator;

import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes minimum intensity projection along a specified dimension. 
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @see MaxProjection
 * 
 * @author dlegland
 *
 */
public class MinProjection extends ProjectionOperator
{
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of MinProjection operator, that specifies the
     * dimension to project along.
     * 
     * @param dim
     *            the dimension for projection
     */
    public MinProjection(int dim)
    {
        super(dim);
    }

    
    // =============================================================
    // New methods
    
    /**
     * Processes the specified array, by providing the comparator to use.
     * 
     * @param <C>
     *            the type of elements within the array
     * @param array
     *            the array to process
     * @param comparator
     *            the comparator of elements
     * @return the min-projection along the specified dimension
     */
    public <C> Array<C> process(Array<C> array, Comparator<C> comparator)
    {
        int[] dims = computeOutputArrayDimensions(array);
        Array<C> res = array.newInstance(dims);
        process(array, res, comparator);
        return res;
    }
    
    /**
     * Processes the specified input array, using the second argument to store
     * the result.
     * 
     * @param <C>
     *            the type of elements within the array
     * @param source
     *            the array to process
     * @param target
     *            the array used to store the result
     * @param comparator
     *            the comparator of elements
     */
    public <C> void process(Array<? extends C> source, Array<? super C> target, Comparator<C> comparator)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
            C min = source.get(pos);
            
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            // iterate over current line
            for (int i = 1; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                C value = source.get(srcPos);
                if (comparator.compare(value, min) < 0)
                {
                    min = value;
                }
            }
            
            // copy value of selected position
            target.set(pos, min);
        }
    }


    // =============================================================
    // Implementation of ProjectionOperator interface
    
    @Override
    public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            double minValue = source.getValue(srcPos);
            
            // iterate over current line
            for (int i = 1; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                minValue = Math.min(minValue, source.getValue(srcPos));
            }
            
            // copy value of selected position
            target.setValue(pos, minValue);
        }
    }
}
