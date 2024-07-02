/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.numeric.ScalarArray;

/**
 * Performs smoothing of an array along a single dimension.
 * 
 * @see FiniteDifferences
 * 
 * @author dlegland
 *
 */
public class Smoothing1D extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Class variables

    /**
     * The dimension in which the smoothing needs to be performed.
     * Default is 0.
     */
    int dim = 0;
    
    /**  
     * The size of the smoothing "box". Default is 3.
     */
    int size = 3;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new operator for computing smoothing along dimension 0.
     */
    public Smoothing1D()
    {
    }

    /**
     * Creates a new operator for computing smoothing along the specified
     * dimension.
     * 
     * @param dim
     *            the dimension to operate on
     */
    public Smoothing1D(int dim)
    {
        this.dim = dim;
    }
    
    /**
     * Creates a new operator for computing smoothing of a given size along the specified
     * dimension.
     * 
     * @param dim
     *            the dimension to operate on
     */
    // TODO: choose name -> UniformSmoothing1D
    // TODO: same parameter order as in Python?
    public Smoothing1D(int dim, int size)
    {
        this.dim = dim;
        this.size = size;
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

        // allocate memory for result
        ScalarArray<?> target = source.newInstance(source.size());
        
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
  
        double radius = (this.size - 1) * 0.5;
        int n0 = (int) Math.floor(radius);
        int n1 = (int) Math.ceil(radius);
        
        // iterate over positions in target array
        for (int[] pos : target.positions())
        {
            // copy position of current pixel
            System.arraycopy(pos, 0, srcPos, 0, nd);

            // clear accumulator value
            double acc = 0.0;
            
            // iterate over the length of the line
            for (int i = -n0; i < 0; i++)
            {
                srcPos[dim] = Math.max(pos[dim] + i, 0);
                acc+= source.getValue(srcPos);
            }
            for (int i = 0; i <= n1; i++)
            {
                srcPos[dim] = Math.min(pos[dim] + i, posMax);
                acc+= source.getValue(srcPos);
            }
            
            // compute local finite difference value
            target.setValue(pos, acc / this.size);
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
}
