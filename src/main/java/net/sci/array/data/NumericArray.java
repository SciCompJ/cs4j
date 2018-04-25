/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;

/**
 * @author dlegland
 *
 */
public interface NumericArray<N> extends Array<N>
{
    /**
     * Adds the specified value to each element of the array, and returns the
     * new array.
     * 
     * @param value
     *            the value to add
     * @return the new array containing the result
     */
    public NumericArray<N> plus(N value);

    /**
     * Subtracts  the specified value from each element of the array, and returns the
     * new array.
     * 
     * @param value
     *            the value to subtract
     * @return the new array containing the result
     */
    public NumericArray<N> minus(N value);
    
    /**
     * Multiplies the specified value with each element of the array, and returns the
     * new array.
     * 
     * @param value
     *            the value to multiply with
     * @return the new array containing the result
     */
    public NumericArray<N> times(N value);
    
    /**
     * Divides each element of the array by the specified value, and returns the
     * new array.
     * 
     * @param value
     *            the value to divide by
     * @return the new array containing the result
     */
    public NumericArray<N> divideBy(N value);
}
