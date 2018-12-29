/**
 * 
 */
package net.sci.array.process.math;

import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Sample operator to demonstrate the use of ScalarArrayOperator interface.
 * 
 * Computes the square root of each element in an array of scalars.
 * 
 * @author dlegland
 *
 */
public class Sqrt implements ScalarArrayOperator
{
    ScalarArray.Factory<? extends Scalar> factory = Float32Array.factory;

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
	public void processScalar(ScalarArray<? extends Scalar> input,
			ScalarArray<? extends Scalar> output)
	{
	    // iterate over positions
	    for (int[] pos : input.positions())
	    {
	        output.setValue(pos, Math.sqrt(input.getValue(pos)));
	    }
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<?> output = factory.create(array.getSize());
        processScalar(array, output);
        return output;
    }
}
