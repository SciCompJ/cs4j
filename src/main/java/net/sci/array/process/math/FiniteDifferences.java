/**
 * 
 */
package net.sci.array.process.math;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * @author dlegland
 *
 */
public class FiniteDifferences extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Class variables

    /**
     * The dimension in which the finite difference need to be computed
     */
    int dim = 0;
    
    /**  
     * The spacing between array elements in the specified dimension
     */
    double spacing = 1.0;
   
    /**
     * The factory used to create output array. If set to null (the default), use the factory
     * of the input array.
     */
    protected ScalarArray.Factory<? extends Scalar> factory = null;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new operator for computing finite differences along dimension
     * 0.
     */
    public FiniteDifferences()
    {
    }

    /**
     * Creates a new operator for computing finite differences along the
     * specified dimension.
     * 
     * @param dim
     *            the dimension to operate on
     */
    public FiniteDifferences(int dim)
    {
        this.dim = dim;
    }

    /**
     * Creates a new operator for computing finite differences along specified
     * dimension and using the specified spacing.
     * 
     * @param dim
     *            The dimension in which the finite difference need to be
     *            computed
     * @param spacing
     *            the spacing between array elements in the specified dimension
     */
    public FiniteDifferences(int dim, double spacing)
    {
        this.dim = dim;
        this.spacing = spacing;
    }

    
    // =============================================================
    // Processing Methods

    /* (non-Javadoc)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        // check validity of input
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires an instance of ScalarArray as input");
        }
        
        // class cast source 
        ScalarArray<?> source = (ScalarArray<?>) array;

        // choose the ScalarArray factory for creating result
        ScalarArray.Factory<? extends Scalar> factory = this.factory;
        if (factory == null)
        {
            factory = source.factory();
        }
        
        // create the output array
        ScalarArray<?> target = factory.create(array.size());
        
        // call the processing method        
        processScalar(source, target);
        
        return target;
    }

    public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
    {
        // check dimensionality of inputs
        checkArrayDimensions(source, target);

        // allocate array for positioning in source array
        int nd = target.dimensionality();
        int[] srcPos = new int[nd];
        
        int posMax = target.size(dim) - 1;
  
        // iterate over positions in target array
        for (int[] pos : target.positions())
        {
            // compute value of pixel before current element
            System.arraycopy(pos, 0, srcPos, 0, nd);
            srcPos[dim] = Math.max(pos[dim] - 1, 0);
            double v0 = source.getValue(srcPos);
                    
            // compute value of pixel after current element
            System.arraycopy(pos, 0, srcPos, 0, nd);
            srcPos[dim] = Math.min(pos[dim] + 1, posMax);
            double v1 = source.getValue(srcPos);
            
            // compute local finite difference value
            target.setValue(pos, (v1 - v0) * 0.5 / spacing);
        }
    }
    
    private void checkArrayDimensions(ScalarArray<?> source, ScalarArray<?> target)
    {
        // check dimensionality of inputs
        if (source.dimensionality() <= dim)
        {
            throw new IllegalArgumentException("Source array must have dimensionality at least equal to " + (dim+1));
        }
        if (target.dimensionality() <= dim)
        {
            throw new IllegalArgumentException("Target array must have dimensionality at least equal to " + (dim+1));
        }
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Source and Target arrays must have same dimensions");
        }
    }
    
    /**
     * Sets up the factory used to create output arrays.
     * 
     * @param factory the factory to set
     */
    public void setFactory(ScalarArray.Factory<? extends Scalar> factory)
    {
        this.factory = factory;
    }
}
