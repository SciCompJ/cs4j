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
	private Maths()
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
     * @return the result of the element-wise addition
	 */
	public static final ScalarArray<? extends Scalar> add(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
		// iterate over elements of each array
		for (int[] pos : result.positions())
		{
		    result.setValue(pos, array.getValue(pos) + value);
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
     * @return the result of the element-wise subtraction
	 */
	public static final ScalarArray<? extends Scalar> subtract(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, array.getValue(pos) - value);
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
     * @return the result of the element-wise multiplication
	 */
	public static final ScalarArray<? extends Scalar> multiply(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, array.getValue(pos) * value);
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
	 * @return the result of the element-wise division
	 */
	public static final ScalarArray<? extends Scalar> divide(ScalarArray<? extends Scalar> array, double value)
	{
		// create array for result
		ScalarArray<? extends Scalar> result = array.duplicate();
		
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, array.getValue(pos) / value);
        }
        
		// returns the created array
		return result;
	}
}
