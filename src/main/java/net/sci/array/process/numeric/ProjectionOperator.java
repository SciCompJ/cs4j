/**
 * 
 */
package net.sci.array.process.numeric;

import net.sci.array.Array;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Computes intensity projection along a specified dimension.
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public abstract class ProjectionOperator implements ScalarArrayOperator
{
    // =============================================================
    // Inner members
    
    protected int dim;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of OrthogonalProjection operator, that specifies
     * the dimension to project along.
     * 
     * @param dim the dimension for projection
     */
    public ProjectionOperator(int dim)
    {
        this.dim = dim;
    }

    
    // =============================================================
    // New methods
    
    public abstract void processScalar(ScalarArray<?> source, ScalarArray<?> target);


    // =============================================================
    // New methods
    
    /**
     * Creates a new array that can be used as output for processing the given
     * input array.
     * 
     * @param array
     *            the reference array
     * @return a new instance of Array that can be used for processing input
     *         array.
     */
    public ScalarArray<?> createEmptyOutputArray(ScalarArray<?> array)
    {
        int[] dims = computeOutputArrayDimensions(array);
        return array.newInstance(dims);
    }
    
    /**
     * Computes the dimensions of the output array. It has same dimensionality
     * as input array, and the same size except for the projection dimension,
     * where the output size is set to 1.
     * 
     * @param array
     *            the reference array
     * @return the dimensions of the output array.
     */
    protected int[] computeOutputArrayDimensions(Array<?> array)
    {
        // number of dimensions of new array
        int nd = array.dimensionality();
        
        // check validity of input
        if (dim >= nd)
        {
            throw new IllegalArgumentException(String.format(
                    "Projection along dim %d can not process array with dimensionality %d", dim,
                    nd+1));
        }
        
        // create array containing new dimensions
        int[] dims = new int[nd];
        System.arraycopy(array.size(), 0, dims, 0, nd);
        dims[this.dim] = 1;
        
        // return output array dimensions
        return dims;
    }
    
    
    // =============================================================
    // Implementation of ScalarArrayOperator interface
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> input)
    {
        ScalarArray<?> output = createEmptyOutputArray(input);
        processScalar(input, output);
        return output;
    }
}
