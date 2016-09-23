/**
 * 
 */
package net.sci.array.type;


/**
 * @author dlegland
 *
 */
public class Boolean extends Int
{
	boolean state;
	
	/**
	 * Default constructor.
	 */
	public Boolean(boolean state)
	{
		this.state = state;
	}
	
	/**
	 * Constructor from an int, setting to a TRUE state if the value is different from 0. 
	 */
	public Boolean(int value)
	{
		this.state = value != 0;
	}
	
	public boolean getState()
	{
		return state;
	}

	@Override
	public int getInt()
	{
		return state ? 1 : 0;
	}
	
	@Override
	public double getValue()
	{
		return state ? 1 : 0;
	}

}
