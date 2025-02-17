/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.ScalarArray;

/**
 * Computes intensity projection along a specified dimension.
 * 
 * Returns an array with the same number of dimension of the input. The size of
 * the output along the projection dimension is equal to 1.
 * 
 * @author dlegland
 *
 */
public abstract class ProjectionOperator extends AlgoStub implements ScalarArrayOperator
{
    // =============================================================
    // Inner members
    
    protected int dim;
    
    /**
     * The factory used to create output array. If set to null (the default), use the factory
     * of the input array.
     */
    protected ScalarArray.Factory<?> factory = null;
    
    
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
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        // choose the ScalarArray factory for creating result
        ScalarArray.Factory<?> factory = this.factory;
        if (factory == null)
        {
            factory = array.factory();
        }
        
        // create the output array
        int[] dims = computeOutputArrayDimensions(array);
        ScalarArray<?> output = factory.create(dims);
        
        // call the processing method
        processScalar(array, output);
        return output;
    }


    /**
     * Sets up the factory used to create output arrays.
     * 
     * @param factory the factory to set
     */
    public void setFactory(ScalarArray.Factory<?> factory)
    {
        this.factory = factory;
    }
}
