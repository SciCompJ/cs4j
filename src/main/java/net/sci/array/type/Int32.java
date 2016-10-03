/**
 * 
 */
package net.sci.array.type;


/**
 * Array value stored as 32 bits integer.
 * 
 * @author dlegland
 *
 */
public class Int32 extends Int
{
	int value;
	
	/**
	 * 
	 */
	public Int32(int value)
	{
		this.value =  value;
	}
	
	@Override
	public int getInt()
	{
		return value;
	}
	
	@Override
	public double getValue()
	{
		return value;
	}

}
