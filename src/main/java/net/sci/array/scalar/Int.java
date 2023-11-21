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
	
	@Override
	public abstract I fromValue(double v);
}
