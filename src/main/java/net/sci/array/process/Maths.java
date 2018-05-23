/**
 * 
 */
package net.sci.array.process;

import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Collection of static methods for math operations on scalar arrays.
 * 
 * @author dlegland
 *
 */
public class Maths
{

	/**
	 * Private constructor to prevent instantiation.
	 */
	public Maths()
	{
	}

	/**
	 * Adds the specified value to each element of the input array, and returns
	 * the result in a new array.
	 * 
	 * @param array
	 *            the input array
	 * @param value
	 *            the value to add
	 * @return a new array
	 */
	public static final ScalarArray<? extends Scalar> add(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
		// create iterators
		ScalarArray.Iterator<? extends Scalar> iter1 = array.iterator(); 
		ScalarArray.Iterator<? extends Scalar> iter2 = result.iterator();
		
		// iterate over elements of each array
		while(iter1.hasNext())
		{
			double val = iter1.nextValue();
			iter2.forward();
			iter2.setValue(val + value);
		}
		
		// returns the created array
		return result;
	}
	
	/**
	 * Subtracts the specified value to each element of the input array, and returns
	 * the result in a new array.
	 * 
	 * @param array
	 *            the input array
	 * @param value
	 *            the value to subtract
	 * @return a new array
	 */
	public static final ScalarArray<? extends Scalar> subtract(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
		// create iterators
		ScalarArray.Iterator<? extends Scalar> iter1 = array.iterator(); 
		ScalarArray.Iterator<? extends Scalar> iter2 = result.iterator();
		
		// iterate over elements of each array
		while(iter1.hasNext())
		{
			double val = iter1.nextValue();
			iter2.forward();
			iter2.setValue(val - value);
		}
		
		// returns the created array
		return result;
	}
	
	/**
	 * Multiplies the specified value to each element of the input array, and returns
	 * the result in a new array.
	 * 
	 * @param array
	 *            the input array
	 * @param value
	 *            the value to multiply by
	 * @return a new array
	 */
	public static final ScalarArray<? extends Scalar> multiply(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
		// create iterators
		ScalarArray.Iterator<? extends Scalar> iter1 = array.iterator(); 
		ScalarArray.Iterator<? extends Scalar> iter2 = result.iterator();
		
		// iterate over elements of each array
		while(iter1.hasNext())
		{
			double val = iter1.nextValue();
			iter2.forward();
			iter2.setValue(val * value);
		}
		
		// returns the created array
		return result;
	}
	
	/**
	 * Divides the specified value to each element of the input array, and returns
	 * the result in a new array.
	 * 
	 * @param array
	 *            the input array
	 * @param value
	 *            the value to divide by
	 * @return a new array
	 */
	public static final ScalarArray<? extends Scalar> divide(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
		// create iterators
		ScalarArray.Iterator<? extends Scalar> iter1 = array.iterator(); 
		ScalarArray.Iterator<? extends Scalar> iter2 = result.iterator();
		
		// iterate over elements of each array
		while(iter1.hasNext())
		{
			double val = iter1.nextValue();
			iter2.forward();
			iter2.setValue(val / value);
		}
		
		// returns the created array
		return result;
	}
}
