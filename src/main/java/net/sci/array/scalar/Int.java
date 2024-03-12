/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Int<I extends Int<I>> extends Scalar<I>
{
    /**
     * @return the int value corresponding to this Int.
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
