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
     * The minimum integer value that can be stored in a Int16 instance,
     * corresponding to -2^15.
     */
    public final static int MIN_INT = Short.MIN_VALUE;
    
    /**
     * The maximum integer value that can be stored in a Int16 instance,
     * corresponding to 2^15-1.
     */
    public final static int MAX_INT = Short.MAX_VALUE;

    /**
     * The Int16 equal to the smallest value that can be represented with this
     * type, corresponding to -2^15.
     */
    public static final Int16 MIN_VALUE = new Int16(MIN_INT);
    
    /**
     * The UInt8 equal to the largest value that can be represented with this
     * type, corresponding to 2^15-1.
     */
    public static final Int16 MAX_VALUE = new Int16(MAX_INT);
    
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
        return (int) Math.min(Math.max(value + 0.5, MIN_INT), MAX_INT);
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
        return (int) Math.min(Math.max(value, MIN_INT), MAX_INT);
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
    public Int16 fromInt(int value)
    {
        return new Int16(clamp(value));
    }


    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public Int16 typeMin()
    {
        return MIN_VALUE;
    }

    @Override
    public Int16 typeMax()
    {
        return MAX_VALUE;
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
    public Int16 opposite()
    {
        return new Int16(-this.value);
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
