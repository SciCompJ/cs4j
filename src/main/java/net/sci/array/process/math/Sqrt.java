/**
 * 
 */
package net.sci.array.process.math;

import net.sci.algo.AlgoStub;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.ScalarArray;

/**
 * Sample operator to demonstrate the use of ScalarArrayOperator interface.
 * 
 * Computes the square root of each element in an array of scalars.
 * 
 * @author dlegland
 *
 */
public class Sqrt extends AlgoStub implements ScalarArrayOperator
{
    ScalarArray.Factory<?> factory = Float32Array.defaultFactory;

	/**
	 * Empty constructor
	 */
	public Sqrt()
	{
	}

    /**
     * Processes an input array and populates the output array.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     */
    public void processScalar(ScalarArray<?> input, ScalarArray<?> output)
	{
	    // iterate over positions
	    for (int[] pos : output.positions())
	    {
	        output.setValue(pos, Math.sqrt(input.getValue(pos)));
	    }
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> output = factory.create(array.size());
        processScalar(array, output);
        return output;
    }
}
