/**
 * 
 */
package net.sci.array.type;

import net.sci.array.data.FloatArray;

/**
 * @author dlegland
 *
 */
public class Float extends Scalar
{
	float value;
	
	/**
	 * Creates new Float with default value 0.
	 */
	public Float()
	{
		value = 0;
	}
	
	/**
	 * Creates new Float with specified value.
	 */
	public Float(float value)
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

	public FloatArray createArray(int[] dims)
	{
		return FloatArray.create(dims);
	}

}
