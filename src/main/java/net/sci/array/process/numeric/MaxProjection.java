/**
 * 
 */
package net.sci.array.process.numeric;

import java.util.Comparator;

import net.sci.array.Array;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes maximum intensity projection along a specified dimension.
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public class MaxProjection extends ProjectionOperator
{
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of MaxProjection operator, that specifies the
     * dimension to project along.
     * 
     * @param dim
     *            the dimension for projection
     */
    public MaxProjection(int dim)
    {
        super(dim);
    }

    
    // =============================================================
    // New methods
    
    public <C> Array<C> process(Array<C> array, Comparator<C> comparator)
    {
        int[] dims = computeOutputArrayDimensions(array);
        Array<C> res = array.newInstance(dims);
        process(array, res, comparator);
        return res;
    }
    
    public <C> void process(Array<? extends C> source, Array<? super C> target, Comparator<C> comparator)
    {
        // create position pointer for source image
        int nd = source.dimensionality();
        int[] srcPos = new int[nd];
        
        int indMax = source.size(this.dim);
        
        // iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
            C max = source.get(pos);
            
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, nd);
            
            // iterate over current line
            for (int i = 1; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                C value = source.get(srcPos);
                if (comparator.compare(value, max) > 0)
                {
                    max = value;
                }
            }
            
            // copy value of selected position
            target.set(max, pos);
        }
    }


    // =============================================================
    // Implementation of ProjectionOperator interface
    
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
            
            double maxValue = source.getValue(srcPos);
            
            // iterate over current line
            for (int i = 0; i < indMax; i++)
            {
                srcPos[this.dim] = i;
                maxValue = Math.max(maxValue, source.getValue(srcPos));
            }
            
            // copy value of selected position
            target.setValue(maxValue, pos);
        }
    }
}
