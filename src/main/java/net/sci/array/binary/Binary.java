/**
 * 
 */
package net.sci.array.binary;

import net.sci.array.numeric.Int;

/**
 * A binary type that encapsulates a boolean.
 * 
 * @author dlegland
 *
 */
public class Binary implements Int<Binary>
{
    // =============================================================
    // Constants

    /** The binary constant corresponding to a FALSE state. */
    public static final Binary FALSE = new Binary(false);

    /** The binary constant corresponding to a TRUE state. */
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
     * Constructor from an int, setting to a TRUE state if the value is greater
     * than 0.
     * 
     * @param value
     *            the value used to defined the state of this boolean
     */
    public Binary(int value)
    {
        this.state = value > 0;
    }

    /**
     * Constructor from a double, setting to a TRUE state if the value is
     * greater than 0.
     * 
     * @param value
     *            the value used to defined the state of this boolean
     */
    public Binary(double value)
    {
        this.state = value > 0;
    }
    

    // =============================================================
    // Methods

    public boolean getBoolean()
    {
        return state;
    }

    @Override
    public int intValue()
    {
        return state ? 1 : 0;
    }
    
    @Override
    public Binary fromInt(int value)
    {
        return value > 0.0 ? TRUE : FALSE;
    }


    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public Binary typeMin()
    {
        return FALSE;
    }

    @Override
    public Binary typeMax()
    {
        return TRUE;
    }

    @Override
    public double value()
    {
        return state ? 1 : 0;
    }

    @Override
    public Binary fromValue(double v)
    {
        return v > 0.0 ? TRUE : FALSE;
    }
    
    
    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Binary one()
    {
        return TRUE;
    }

    @Override
    public Binary zero()
    {
        return FALSE;
    }

    @Override
    public Binary plus(Binary other)
    {
        return new Binary((this.state ? 1 : 0) + (other.state ? 1 : 0) > 0);
    }

    @Override
    public Binary minus(Binary other)
    {
        return new Binary((this.state ? 1 : 0) - (other.state ? 1 : 0) > 0);
    }

    /**
     * Always returns the value FALSE.
     * 
     * @return the value Binary.FALSE.
     */
    @Override
    public Binary opposite()
    {
        return FALSE;
    }

    @Override
    public Binary times(double k)
    {
        return new Binary((this.state ? 1 : 0) * k > 0);
    }

    @Override
    public Binary divideBy(double k)
    {
        return new Binary((this.state ? 1 : 0) / k > 0);
    }
    
    
    // =============================================================
    // Implementation of the Comparable interface

    @Override
    public int compareTo(Binary other)
    {
        return Boolean.compare(this.state, other.state);
    }
    
    
    // =============================================================
    // Override Object methods

    @Override
    public String toString()
    {
        return state ? "TRUE" : "FALSE";
    }

    @Override
    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // Check class internal values, using pattern matching
        if (that instanceof Binary thatBinary)
        {
            return this.state == thatBinary.state;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return java.lang.Boolean.hashCode(this.state);
    }
}
