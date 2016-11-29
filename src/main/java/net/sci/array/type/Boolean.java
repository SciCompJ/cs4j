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
	// =============================================================
	// Constants

	public static final Boolean FALSE = new Boolean(false);

	public static final Boolean TRUE = new Boolean(true);


	// =============================================================
	// state of the boolean variable

	boolean state;
	
	
	// =============================================================
	// Constructor

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

	
	// =============================================================
	// Methods

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


	// =============================================================
	// Override Object methods
	
	public boolean equals(Object that)
	{
		// check for self-comparison
		if (this == that)
			return true;

		// check for class
		if (!(that instanceof Boolean))
			return false;

		// cast to native object is now safe
		Boolean thatBoolean = (Boolean) that;

	    // now a proper field-by-field evaluation can be made
	    return this.state == thatBoolean.state;
	}
	
	public int hashCode()
	{
		return java.lang.Boolean.hashCode(this.state);
	}
}
