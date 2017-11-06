/**
 * 
 */
package net.sci.array.type;


/**
 * A binary type that encapsulates a boolean.
 * 
 * @author dlegland
 *
 */
public class Binary extends Int
{
	// =============================================================
	// Constants

	public static final Binary FALSE = new Binary(false);

	public static final Binary TRUE = new Binary(true);


	// =============================================================
	// state of the boolean variable

	/**
	 * The inner state of this binary variable.
	 */
	boolean state;
	
	
	// =============================================================
	// Constructor

	/**
	 * Default constructor.
	 * 
	 * @param state
	 *            the logical state of this Binary
	 */
	public Binary(boolean state)
	{
		this.state = state;
	}
	
	/**
	 * Constructor from an int, setting to a TRUE state if the value is
	 * different from 0.
	 * 
	 * @param value
	 *            the value used to defined the state of this boolean
	 */
	public Binary(int value)
	{
		this.state = value != 0;
	}

	
	// =============================================================
	// Methods

	public boolean getBoolean()
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
		if (!(that instanceof Binary))
			return false;

		// cast to native object is now safe
		Binary thatBinary = (Binary) that;

	    // now a proper field-by-field evaluation can be made
	    return this.state == thatBinary.state;
	}
	
	public int hashCode()
	{
		return java.lang.Boolean.hashCode(this.state);
	}
}
