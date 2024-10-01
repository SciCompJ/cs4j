/**
 * 
 */
package net.sci.array.numeric.process;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray;

/**
 * Sample operator to demonstrate the use of ScalarArrayOperator interface.
 * 
 * Computes the square of each element of an array of scalars.
 * 
 * @author dlegland
 * @see ApplyFunction
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
    public void processScalar(ScalarArray<?> input, ScalarArray<?> output)
	{
	    for (int[] pos : output.positions())
	    {
	        output.setValue(pos, Math.pow(input.getValue(pos), 2));
	    }
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> output = array.newInstance(array.size());
        processScalar(array, output);
        return output;
    }
}
