/**
 * 
 */
package net.sci.array.process;

import net.sci.array.data.ScalarArray;
import net.sci.array.type.Scalar;

/**
 * Sample operator to demonstrate the use of ScalarArrayOperator interface.
 * 
 * Computes the square root of each scalar in the array and puts the result in
 * target array.
 * 
 * @author dlegland
 *
 */
public class Sqrt implements ScalarArrayOperator
{

	/**
	 * 
	 */
	public Sqrt()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.array.process.ScalarArrayOperator#processScalar(net.sci.array.data.ScalarArray, net.sci.array.data.ScalarArray)
	 */
	@Override
	public void processScalar(ScalarArray<? extends Scalar> input,
			ScalarArray<? extends Scalar> output)
	{
		// create array iterators
		ScalarArray.Iterator<?> sourceIter = input.iterator();
		ScalarArray.Iterator<?> targetIter = output.iterator();
		
		// iterate over both arrays in parallel 
		while (sourceIter.hasNext() && targetIter.hasNext())
		{
			sourceIter.forward();
			targetIter.forward();
			
			// compute new walue
			targetIter.setValue(Math.sqrt(sourceIter.getValue()));
		}
	}

}
