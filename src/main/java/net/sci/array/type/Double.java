/**
 * 
 */
package net.sci.array.type;

import net.sci.array.data.DoubleArray;

/**
 * @author dlegland
 *
 */
public class Double extends Scalar
{
	double value;
	
	/**
	 * Creates new double with default value 0.
	 */
	public Double()
	{
		value = 0;
	}
	
	/**
	 * Creates new double with specified value.
	 */
	public Double(double value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.type.Scalar#getValue()
	 */
	@Override
	public double getValue()
	{
		return value;
	}

	public DoubleArray createArray(int[] dims)
	{
		return DoubleArray.create(dims);
	}

}
