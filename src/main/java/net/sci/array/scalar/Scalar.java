/**
 * 
 */
package net.sci.array.scalar;

/**
 * Top-level definition of scalar value. Can represent any double or integer
 * value.
 * 
 * @param <S>
 *            the type of Scalar.
 *
 * @author dlegland
 */
public abstract class Scalar<S extends Scalar<S>>
{
	/**
	 * Returns the value of this scalar element as a double.
	 * @return the value as a double
	 */
	public abstract double getValue();
	
	/**
     * Creates a new Scalar from the specified double value.
     * 
     * @param v
     *            the value
     * @return the Scalar corresponding to the input value.
     */
	public abstract S fromValue(double v);
}
