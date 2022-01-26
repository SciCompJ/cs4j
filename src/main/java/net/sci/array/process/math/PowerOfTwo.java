/**
 * 
 */
package net.sci.array.process.math;

import net.sci.algo.AlgoStub;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Sample operator to demonstrate the use of ScalarArrayOperator interface.
 * 
 * Computes the square of each element of an array of scalars.
 * 
 * @author dlegland
 *
 */
public class PowerOfTwo extends AlgoStub implements ScalarArrayOperator
{
	/**
	 * Empty constructor.
	 */
	public PowerOfTwo()
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
	    for (int[] pos : output.positions())
	    {
	        output.setValue(pos, Math.pow(input.getValue(pos), 2));
	    }
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<? extends Scalar> output = array.newInstance(array.size());
        processScalar(array, output);
        return output;
    }
}
