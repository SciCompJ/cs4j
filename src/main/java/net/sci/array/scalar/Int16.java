/**
 * 
 */
package net.sci.array.scalar;


/**
 * Represents a signed 16-bits integer, coded with a short.
 * 
 * @author dlegland
 *
 */
public class Int16 extends Int<Int16>
{
    // =============================================================
    // Constants
    
    /**
     * The maximum value that can be stored in a Int16 instance, corresponding
     * to 2^15-1.
     */
    public final static int MAX_VALUE = Short.MAX_VALUE;

    /**
     * The minimum value that can be stored in a Int16 instance, corresponding
     * to -2^15.
     */
    public final static int MIN_VALUE = Short.MIN_VALUE;
    
    
    // =============================================================
    // Static methods

    /**
     * Computes the integer value between MIN_VALUE and MAX_VALUE closest to the
     * specified double value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between MIN_VALUE and MAX_VALUE
     */
    public final static int convert(double value)
    {
        return (int) Math.min(Math.max(value + 0.5, MIN_VALUE), MAX_VALUE);
    }

    /**
     * Computes the integer value between MIN_VALUE and MAX_VALUE closest to the
     * specified double value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between MIN_VALUE and MAX_VALUE
     */
    public final static int clamp(int value)
    {
        return (int) Math.min(Math.max(value, MIN_VALUE), MAX_VALUE);
    }
    
	
    // =============================================================
    // Class members

    short value;
    

    // =============================================================
    // Constructor

    /**
     * Creates a new instance of Int16 using the specified value.
     * 
     * @param value
     *            the value stored within this Int16
     */
    public Int16(int value)
    {
        this.value = (short) clamp(value);
    }
    

    // =============================================================
    // Class methods

    public short getShort()
    {
        return value;
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

    @Override
    public Int16 fromValue(double v)
    {
        return new Int16(convert(v));
    }
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;

        // check for class
        if (!(that instanceof Int16)) return false;

        // cast to native object is now safe
        Int16 thatInt = (Int16) that;

        // now a proper field-by-field evaluation can be made
        return this.value == thatInt.value;
    }

    public int hashCode()
    {
        return java.lang.Short.hashCode(this.value);
    }

    @Override
    public String toString()
    {
        return String.format("Int16(%d)", this.value);
    }
}
