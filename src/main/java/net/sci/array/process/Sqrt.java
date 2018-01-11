/**
 * 
 */
package net.sci.array.process;

import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

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
		// create array iterators
		ScalarArray.Iterator<?> sourceIter = input.iterator();
		ScalarArray.Iterator<?> targetIter = output.iterator();
		
		// iterate over both arrays in parallel 
		while (sourceIter.hasNext() && targetIter.hasNext())
		{
			// process the next value
			targetIter.setNextValue(Math.sqrt(sourceIter.nextValue()));
		}
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        // TODO: choose the class of the output array
        ScalarArray<?> output = array.newInstance(array.getSize());
        processScalar(array, output);
        return output;
    }
}
