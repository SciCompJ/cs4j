/**
 * 
 */
package net.sci.array.scalar;

/**
 * Base class for Integer based types. Final classes should manage integer
 * values compatible with native java integer type.
 * 
 * @param <I>
 *            The type of Int
 * 
 * @author dlegland
 */
public abstract class Int<I extends Int<I>> extends Scalar<I>
{
    /**
     * Return the integer value corresponding to this Int instance.
     * 
     * @return the integer value corresponding to this Int instance.
     */
	public abstract int getInt();
	
	/**
     * Creates a new instance of type I from the specified integer value.
     * 
     * @param value
     *            the integer value
     * @return the instance of type I that corresponds to the value
     */
	public abstract I fromInt(int value);
	
    @Override
	public abstract I fromValue(double v);
}
