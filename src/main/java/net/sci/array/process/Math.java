/**
 * 
 */
package net.sci.array.process;

import java.util.function.BiFunction;

import net.sci.array.Arrays;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;

/**
 * Collection of static methods for math operations on scalar arrays.
 * 
 * @author dlegland
 *
 */
public class Math
{

	/**
	 * Private constructor to prevent instantiation.
	 */
	private Math()
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
		    result.setValue(array.getValue(pos) + value, pos);
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
            result.setValue(array.getValue(pos) - value, pos);
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
            result.setValue(array.getValue(pos) * value, pos);
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
            result.setValue(array.getValue(pos) / value, pos);
        }
        
		// returns the created array
		return result;
	}

    public static final ScalarArray<? extends Scalar> add(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(array1.getValue(pos) + array2.getValue(pos), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> subtract(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(array1.getValue(pos) - array2.getValue(pos), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> multiply(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(array1.getValue(pos) * array2.getValue(pos), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> divide(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(array1.getValue(pos) / array2.getValue(pos), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> modulo(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(array1.getValue(pos) % array2.getValue(pos), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> min(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
              output.setValue(java.lang.Math.min(array1.getValue(pos), array2.getValue(pos)), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> max(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
            output.setValue(java.lang.Math.max(array1.getValue(pos), array2.getValue(pos)), pos);
        }
        
        return output;
    }
    
    public static final ScalarArray<? extends Scalar> apply(
            ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output, BiFunction<Double,Double,Double> fun)
    {
        checkArrays(array1, array2, output);
        
        for (int[] pos : array1.positions())
        {
            output.setValue(fun.apply(array1.getValue(pos), array2.getValue(pos)), pos);
        }
        
        return output;
    }
    
    private static final void checkArrays(ScalarArray<? extends Scalar> array1,
            ScalarArray<? extends Scalar> array2,
            ScalarArray<? extends Scalar> output)
    {
        if (!Arrays.isSameDimensionality(array1, array2) || !Arrays.isSameDimensionality(array1, output))
        {
            throw new IllegalArgumentException("Arrays must have same dimension");
        }
        if (!Arrays.isSameSize(array1, array2) || !Arrays.isSameSize(array1, output))
        {
            throw new IllegalArgumentException("Arrays must have same size");
        }
    }
}
