/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.Vector;
import net.sci.array.numeric.VectorArray;

/**
 * Computes the max norm (or Infinity norm) of each {@code Vector} element of an
 * Array of {@code Vector}, and returns the result as an array of {@code Scalar}
 * the same size as the input array.
 * 
 * The default behavior is to store the result into a new instance of
 * {@code Float64Array}, but the type of output array can be changed with the
 * {@code setFactory()} method.
 * 
 * @see Norm
 */
public class MaxNorm extends AlgoStub implements ArrayOperator
{
    /**
     * The factory used to create output array.
     */
    protected ScalarArray.Factory<?> factory = Float64Array.defaultFactory;
    
    /**
     * Default empty constructor.
     */
    public MaxNorm()
    {
    }
    
    /**
     * Sets up the factory used to create output arrays.
     * 
     * @param factory
     *            the factory to set
     * @throws IllegalArgumentException
     *             if the factory is {@code null}
     */
    public void setFactory(ScalarArray.Factory<?> factory)
    {
        if (factory == null)
        {
            throw new IllegalArgumentException("Can not specify a null factory.");
        }
        this.factory = factory;
    }

    /**
     * Computes the norm of the specified vector array.
     * 
     * @param array
     *            the input vector array
     * @return a new Scalar array containing the norm of each element of the
     *         vector array.
     */
    public ScalarArray<?> processVector(VectorArray<?,?> array)
    {
        // allocate memory for result
        ScalarArray<?> result = createOutputArray(array);
        return processVectorArray(array, result);
    }
    
    /**
     * Computes the norm of the specified vector array, using the specified
     * output array to store the result. It the output array is {@code null}, a
     * new array is allocated.
     * 
     * @param array
     *            the input vector array
     * @param output
     *            the output array
     * @return a reference to the the second input argument, or to a new array
     *         if the output array is null.
     */
    public ScalarArray<?> processVectorArray(VectorArray<?,?> array, ScalarArray<?> output)
    {
        // check output array
        if (output == null)
        {
            output = factory.create(array.size());
        }

        // iterate over both arrays in parallel
        double[] buffer = new double[array.channelCount()];
        for(int[] pos : output.positions())
        {
            output.setValue(pos, Vector.maxNorm(array.getValues(pos, buffer)));
        }

        return output;
    }
    
    /**
     * Creates a new array that can be used as output array when processing the
     * specified array as input array.
     * 
     * @param array
     *            the array to process
     * @return an array that can be used to store the result
     */
    public ScalarArray<?> createOutputArray(VectorArray<?,?> array)
    {
        return factory.create(array.size());
    }
    
    @Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        if (array instanceof VectorArray<?,?> vectArray)
        {
            return processVector(vectArray);
        }
        if (array.elementInstanceOf(Vector.class))
        {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            VectorArray<?,?> wrapped = VectorArray.wrap((Array<? extends Vector>) array);
            return processVector(wrapped);
        }
        
        throw new RuntimeException("Requires an Array containing Vector elements as input, not " + array.elementClass().getName());
    }
    
    public boolean canProcess(Array<?> array)
    {
        return array.elementInstanceOf(Vector.class);
    }
}
