/**
 * 
 */
package net.sci.array.type;

/**
 * A vector a numeric values.
 * 
 * @author dlegland
 *
 */
public abstract class Vector
{
	/**
	 * Returns the set of values that constitutes this vector.
	 * 
	 * @return the set of values that constitutes this vector.
	 */
	public abstract double[] getValues();
	
	/**
	 * 
	 * @param i the index of element
	 * @return the valueat the specified index.
	 */
	public abstract double getValue(int i);
}
