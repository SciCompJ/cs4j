/**
 * 
 */
package net.sci.array.type;

/**
 * @author dlegland
 *
 */
public class DoubleVector extends Vector
{
	double[] data;
	
	public DoubleVector(double[] array)
	{
		this.data = new double[array.length];
		System.arraycopy(array, 0, this.data, 0, array.length);
	}
	
	/**
	 * Returns a defensive copy of the inner array.
	 */
	@Override
	public double[] getValues()
	{
		double[] res = new double[this.data.length];
		System.arraycopy(this.data, 0, res, 0, this.data.length);
		return res;
	}

	/**
	 * Returns the value at the specified position.
	 */
	@Override
	public double getValue(int i)
	{
		return this.data[i];
	}

}
