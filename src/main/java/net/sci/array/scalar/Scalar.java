/**
 * 
 */
package net.sci.array.scalar;

/**
 * Top-level definition of scalar value. Can represent any double or integer value. 
 * 
 * @author dlegland
 *
 */
public abstract class Scalar
{
	/**
	 * Returns the value of this scalar element as a double.
	 * @return the value as a double
	 */
	public abstract double getValue();
}
