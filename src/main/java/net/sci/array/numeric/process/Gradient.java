/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Arrays;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.VectorArray;

/**
 * Computes the gradient vector for each element of a scalar array, by computing
 * finite differences along each dimension.
 * 
 * {@snippet lang="java":
 * UInt8Array2D array = UInt8Array2D.create(20, 20);
 * array.fillInts((x,y) -> Math.hypot(x-10, y-10) < 6 ? 100 : 0);
 * Gradient op = new Gradient();
 * // optional: setup factory
 * op.setFactory(Float32VectorArray.defaultFactory);
 * VectorArray<?,?> grad = op.process(array);
 * // compute norm of gradient
 * ScalarArray<?> norm = new VectorArrayL2Norm().process(grad);
 * new Image(norm).show(); 
 * }
 * 
 * @see FiniteDifferences
 * 
 * @author dlegland
 */
public class Gradient extends AlgoStub implements ArrayOperator
{
    /**
     * Default empty constructor.
     */
    public Gradient()
    {
    }
    
    /**
     * The factory used to create output array.
     */
    protected VectorArray.Factory<?,?> factory = Float32VectorArray.defaultFactory;

    /**
     * Computes gradient of the elements in the source array, given as a
     * ScalarArray, and stores the result in the second input argument, given as
     * a VectorArray. Returns the reference to the target array.
     * 
     * @param source
     *            the input scalar array
     * @param target
     *            the output vector array
     * @return the reference to the target array
     */
    public VectorArray<?,?> processScalar(ScalarArray<?> source, VectorArray<?,?> target)
    {
        // check dimensionality of inputs
        checkArrayDimensions(source, target);

        int nd = source.dimensionality();
        for (int d = 0; d < nd; d++)
        {
            FiniteDifferences diffOp = new FiniteDifferences(d);
            diffOp.processScalar(source, target.channel(d));
        }
        
        return target;
    }
    
    /**
     * Changes the factory used to create output arrays. Default is
     * {@code Float32VectorArray.defaultFactory}.
     * 
     * @param factory
     *            the factory to set
     */
    public void setFactory(VectorArray.Factory<?,?> factory)
    {
        this.factory = factory;
    }

    private void checkArrayDimensions(ScalarArray<?> source, VectorArray<?,?> target)
    {
        if (target.channelCount() != source.dimensionality())
        {
            throw new IllegalArgumentException("Target array must have at least " + source.dimensionality() + " channels");
        }
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Source and Target arrays must have same dimensions");
        }
    }

    /**
     * Computes gradient of the specified input scalar array.
     * 
     * @param array
     *            the input scalar array
     * @return the gradient of the array, as an instance of {@code VectorArray}
     */
    public VectorArray<?,?> processScalar(ScalarArray<?> array)
    {
        return processScalar(array, factory.create(array.size(), array.dimensionality()));
    }
    
    /* (non-Javadoc)
     * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
     */
    @Override
    public <T> VectorArray<?,?> process(Array<T> array)
    {
        // check validity of input
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires an instance of ScalarArray as input");
        }
        
        // allocate memory for result
        ScalarArray<?> source = (ScalarArray<?>) array;
        VectorArray<?,?> target = factory.create(source.size(), source.dimensionality());
        
        processScalar(source, target);
        
        return target;
    }
}
