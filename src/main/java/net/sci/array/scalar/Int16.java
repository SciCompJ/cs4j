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
    
    /**
     * The INT16 value that corresponds to one.
     */
    public final static Int16 ONE = new Int16(1);

    /**
     * The INT16 value that corresponds to zero.
     */
    public final static Int16 ZERO = new Int16(0);


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
    // Implementation of the Numeric interface
    
    @Override
    public Int16 one()
    {
        return ONE;
    }

    @Override
    public Int16 zero()
    {
        return ZERO;
    }

    @Override
    public Int16 plus(Int16 other)
    {
        return new Int16(this.value + other.value);
    }

    @Override
    public Int16 minus(Int16 other)
    {
        return new Int16(this.value - other.value);
    }

    @Override
    public Int16 times(double k)
    {
        return new Int16(Int16.convert(this.value * k));
    }

    @Override
    public Int16 divideBy(double k)
    {
        return new Int16(Int16.convert(this.value / k));
    }
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // Check class internal values, using pattern matching
        if (that instanceof Int16 thatInt)
        {
            return this.value == thatInt.value;
        }
        return false;
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
