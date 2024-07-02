/**
 * 
 */
package net.sci.array.numeric;

/**
 * Represents an unsigned 16-bits integer, coded with a short.
 * 
 * @author dlegland
 *
 */
public class UInt16 extends Int<UInt16>
{
    // =============================================================
    // Constants

    /**
     * The minimum integer value that can be stored in a UInt16 instance,
     * corresponding to 0.
     */
    public final static int MIN_INT = 0;

    /**
     * The maximum integer value that can be stored in a UInt16 instance,
     * corresponding to 2^16-1.
     */
    public final static int MAX_INT = 0x0FFFF;

    /**
     * The UInt16 equal to the smallest value that can be represented with this
     * type, corresponding to zero.
     */
    public static final UInt16 MIN_VALUE = new UInt16(MIN_INT);
    
    /**
     * The UInt8 equal to the largest value that can be represented with this
     * type, corresponding to 255.
     */
    public static final UInt16 MAX_VALUE = new UInt16(MAX_INT);
    
    /**
     * The UInt16 value that corresponds to one.
     */
    public final static UInt16 ONE = new UInt16(1);

    /**
     * The UInt16 value that corresponds to zero.
     */
    public final static UInt16 ZERO = new UInt16(0);


    // =============================================================
    // Static methods
    
    /**
     * Computes the integer value between MIN_INT and MAX_INT closest to the
     * specified double value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between MIN_INT and MAX_INT
     */
    public final static int convert(double value)
    {
        return (int) Math.min(Math.max(value + 0.5, MIN_INT), MAX_INT);
    }

    /**
     * Computes the integer value between 0 and MAX_VALUE closest to the
     * specified double value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between 0 and MAX_VALUE
     */
    public final static int clamp(int value)
    {
        return (int) Math.min(Math.max(0, value), MAX_INT);
    }
    

    // =============================================================
    // Class members
    
    short value;
    

    // =============================================================
    // Constructor
    
    /**
     * Creates a new instance of UInt16 using the specified value.
     * 
     * @param value
     *            the value stored within this UInt16
     */
    public UInt16(int value)
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
        return value & 0x00FFFF;
    }
    
    @Override
    public UInt16 fromInt(int value)
    {
        return new UInt16(clamp(value));
    }


    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public UInt16 typeMin()
    {
        return MIN_VALUE;
    }

    @Override
    public UInt16 typeMax()
    {
        return MAX_VALUE;
    }

    @Override
    public double getValue()
    {
        return value & 0x00FFFF;
    }

    @Override
    public UInt16 fromValue(double v)
    {
        return new UInt16(convert(v));
    }


    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public UInt16 one()
    {
        return ONE;
    }

    @Override
    public UInt16 zero()
    {
        return ZERO;
    }

    @Override
    public UInt16 plus(UInt16 other)
    {
        return new UInt16((this.value & 0x00FFFF) + (other.value & 0x00FFFF));
    }

    @Override
    public UInt16 minus(UInt16 other)
    {
        return new UInt16((this.value & 0x00FFFF) - (other.value & 0x00FFFF));
    }
    
    /**
     * Always returns the value zero, as the negative of a positive integer
     * results in a negative value, that is clamped by the range of the UInt16
     * type.
     * 
     * @return the value UInt16.ZERO
     */
    @Override
    public UInt16 opposite()
    {
        return ZERO;
    }

    @Override
    public UInt16 times(double k)
    {
        return new UInt16((int) ((this.value & 0x00FFFF) * k));
    }

    @Override
    public UInt16 divideBy(double k)
    {
        return new UInt16((int) ((this.value & 0x00FFFF) / k));
    }    
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // Check class internal values, using pattern matching
        if (that instanceof UInt16 thatInt)
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
        return String.format("UInt16(%d)", this.value & 0x00FFFF);
    }    
}
