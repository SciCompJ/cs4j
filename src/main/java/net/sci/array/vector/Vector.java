/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Scalar;

/**
 * A vector of numeric values.
 * 
 * @author dlegland
 *
 */
public abstract class Vector<T extends Scalar>
{
    /**
     * Returns the set of values that constitutes this vector.
     * 
     * @return the set of values that constitutes this vector.
     */
    public abstract double[] getValues();
    
    /**
	 * Returns the set of values that constitutes this vector.
	 * 
	 * @param values
	 *            an array used to store the result
	 * @return the set of values that constitutes this vector.
	 */
    public abstract double[] getValues(double[] values);
    
	/**
	 * Returns the value at the specified index as a double.
	 * 
	 * @param i the index of element
	 * @return the value at the specified index.
	 */
	public abstract double getValue(int i);
	
	/**
	 * Returns the value at the specified index.
	 * 
	 * @param i the index of element
	 * @return the value at the specified index.
	 */
	public abstract T get(int i);
	
	/**
	 * Returns the number of component of this vector.
	 * 
	 * @return the number of components of this vector. 
	 */
	public abstract int size();
}