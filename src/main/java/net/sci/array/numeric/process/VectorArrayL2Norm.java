/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.Vector;
import net.sci.array.numeric.VectorArray;

/**
 * Computes the classical L2 norm (or Euclidean norm) of each vector element of
 * a VectorArray, and returns the result as a scalar array the same size as the
 * input array.
 */
public class VectorArrayL2Norm extends AlgoStub implements ArrayOperator
{
    /**
     * The factory used to create output array.
     */
    protected ScalarArray.Factory<?> factory = Float32Array.defaultFactory;
    
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
        ScalarArray<?> result = factory.create(array.size());

        // iterate over both arrays in parallel
        double[] buffer = new double[array.channelCount()];
        for(int[] pos : result.positions())
        {
            result.setValue(pos, Vector.norm(array.getValues(pos, buffer)));
        }

        return result;
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

    @Override
    public <T> ScalarArray<?> process(Array<T> array)
    {
        if (array instanceof VectorArray<?,?> vectArray)
        {
            return processVector(vectArray);
        }
        
        throw new RuntimeException("Requires a VectorArray as input");
    }
}
