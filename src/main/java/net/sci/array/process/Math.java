/**
 * 
 */
package net.sci.array.process;

import java.util.function.BiFunction;

import net.sci.array.Arrays;
import net.sci.array.process.math.MathBinaryOperator;
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
    // =============================================================
    // Operations involving an array and a scalar value

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
	public static final ScalarArray<? extends Scalar<?>> add(ScalarArray<? extends Scalar<?>> array, double value)
	{
	    return add(array, value, array.newInstance(array.size()));
	}
	
    /**
     * Adds the specified value to each element of the input array, and returns
     * the result in a new array.
     * 
     * @param array
     *            the input array
     * @param value
     *            the value to add
     * @param result
     *            the output array
     * @return the result of the element-wise addition
     */
    public static final ScalarArray<? extends Scalar<?>> add(
            ScalarArray<? extends Scalar<?>> array, double value,
            ScalarArray<? extends Scalar<?>> result)
    {
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
    public static final ScalarArray<? extends Scalar<?>> subtract(
            ScalarArray<? extends Scalar<?>> array, double value)
    {
        return subtract(array, value, array.newInstance(array.size()));
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
    public static final ScalarArray<? extends Scalar<?>> subtract(
            ScalarArray<? extends Scalar<?>> array, double value,
            ScalarArray<? extends Scalar<?>> result)
	{
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
	public static final ScalarArray<? extends Scalar<?>> multiply(ScalarArray<? extends Scalar<?>> array, double value)
	{
		return multiply(array, value, array.newInstance(array.size()));
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
    public static final ScalarArray<? extends Scalar<?>> multiply(ScalarArray<? extends Scalar<?>> array, double value, ScalarArray<? extends Scalar<?>> result)
    {
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
    public static final ScalarArray<? extends Scalar<?>> divide(ScalarArray<? extends Scalar<?>> array, double value)
    {
        return divide(array, value, array.newInstance(array.size()));
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
	public static final ScalarArray<? extends Scalar<?>> divide(ScalarArray<? extends Scalar<?>> array, double value, ScalarArray<? extends Scalar<?>> result)
	{
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, array.getValue(pos) / value);
        }
        
		// returns the created array
		return result;
	}

    /**
     * Adds the specified value to each element of the input array, and returns
     * the result in a new array.
     * 
     * @param array
     *            the input array
     * @param value
     *            the value to compare with
     * @return the result of the element-wise comparison
     */
    public static final ScalarArray<? extends Scalar<?>> min(ScalarArray<? extends Scalar<?>> array, double value)
    {
        return add(array, value, array.newInstance(array.size()));
    }
    
    /**
     * Keeps the minimum of each element compared with the given value.
     * 
     * @param array
     *            the input array
     * @param value
     *            the value to compare with
     * @param result
     *            the output array
     * @return the result of the element-wise comparison
     */
    public static final ScalarArray<? extends Scalar<?>> min(
            ScalarArray<? extends Scalar<?>> array, double value,
            ScalarArray<? extends Scalar<?>> result)
    {
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, java.lang.Math.min(array.getValue(pos), value));
        }
        
        // returns the created array
        return result;
    }
    
    /**
     * Keeps the maximum of each element compared with the given value.
     * 
     * @param array
     *            the input array
     * @param value
     *            the value to compare with
     * @return the result of the element-wise comparison
     */
    public static final ScalarArray<? extends Scalar<?>> max(ScalarArray<? extends Scalar<?>> array, double value)
    {
        return max(array, value, array.newInstance(array.size()));
    }
    
    /**
     * Keeps the maximum of each element compared with the given value.
     * 
     * @param array
     *            the input array
     * @param value
     *            the value to compare with
     * @param result
     *            the output array
     * @return the result of the element-wise comparison
     */
    public static final ScalarArray<? extends Scalar<?>> max(
            ScalarArray<? extends Scalar<?>> array, double value,
            ScalarArray<? extends Scalar<?>> result)
    {
        // iterate over elements of each array
        for (int[] pos : result.positions())
        {
            result.setValue(pos, java.lang.Math.min(array.getValue(pos), value));
        }
        
        // returns the created array
        return result;
    }
    
    
    // =============================================================
    // Operations involving two arrays

    public static final ScalarArray<? extends Scalar<?>> add(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator((a, b) -> a + b);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> subtract(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator((a, b) -> a - b);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> multiply(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator((a, b) -> a * b);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> divide(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator((a, b) -> a / b);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> modulo(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator((a, b) -> a % b);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> min(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator(java.lang.Math::min);
        op.process(array1, array2, output);
        return output;
    }
    
    public static final ScalarArray<? extends Scalar<?>> max(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator(java.lang.Math::max);
        op.process(array1, array2, output);
        return output;
    }
    
    /**
     * Applies a function to each pair of elements read from two arrays, and put
     * the result in a third array.
     * 
     * @param array1
     *            the first input array
     * @param array2
     *            the second input array
     * @param output
     *            the output array
     * @param fun
     *            the function to apply
     * @return the reference to the output array.
     */
    public static final ScalarArray<? extends Scalar<?>> apply(
            ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output, BiFunction<Double,Double,Double> fun)
    {
        checkArrays(array1, array2, output);
        MathBinaryOperator op = new MathBinaryOperator(fun);
        op.process(array1, array2, output);
        return output;
    }
    
    private static final void checkArrays(ScalarArray<? extends Scalar<?>> array1,
            ScalarArray<? extends Scalar<?>> array2,
            ScalarArray<? extends Scalar<?>> output)
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

    
    // =============================================================
    // Constructor

    /**
     * Private constructor to prevent instantiation.
     */
    private Math()
    {
    }
}
