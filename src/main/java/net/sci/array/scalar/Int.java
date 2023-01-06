/**
 * 
 */
package net.sci.array.scalar;

/**
 * @author dlegland
 *
 */
public abstract class Int extends Scalar
{
    /**
     * @return the int value corresponding to this Int.
     */
	public abstract int getInt();
	
	@Override
	public abstract Int fromValue(double v);
}
