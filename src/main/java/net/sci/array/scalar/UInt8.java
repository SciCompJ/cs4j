/**
 * 
 */
package net.sci.array.scalar;

/**
 * Represents an unsigned 8-bits integer, coded with a byte.
 * 
 * @author dlegland
 *
 */
public class UInt8 extends Int<UInt8>
{
    // =============================================================
    // Public constants
    
    /**
     * The minimum value that can be stored in a UInt8 instance, equal to 0.
     */
    public static final int MIN_VALUE = 0;
    
    /**
     * The maximum value that can be stored in a UInt8 instance, equal to 255.
     */
    public static final int MAX_VALUE = 255;
    
    /**
     * The UInt8 value that corresponds to one.
     */
    public final static UInt8 ONE = new UInt8(1);

    /**
     * The UInt8 value that corresponds to zero.
     */
    public final static UInt8 ZERO = new UInt8(0);
    
    
    // =============================================================
    // Static methods
    
    /**
     * Computes the integer value between 0 and 255 closest to the specified
     * value.
     * 
     * @param value
     *            a double value
     * @return the closest corresponding integer between 0 and 255
     */
    public final static int convert(double value)
    {
        return (int) Math.min(Math.max(0, value + 0.5), 255);
    }

    /**
     * Forces the input integer value to stay within the [0;255] interval.
     * 
     * @param value
     *            an integer value
     * @return the closest integer value between 0 and 255.
     */
    public final static int clamp(int value)
    {
        return Math.min(Math.max(0, value), 255);
    }
    

    // =============================================================
    // Class variables

    byte value;
    

    // =============================================================
    // Constructors
    
    /**
     * Creates a new instance of UInt8 using the specified value.
     * 
     * @param value
     *            the value stored within this UInt8
     */
    public UInt8(int value)
    {
        this.value = (byte) clamp(value);
    }
    
    /**
     * Creates a new instance of UInt8 using the specified byte value.
     * 
     * @param value
     *            the byte value stored within this UInt8
     */
    public UInt8(byte value)
    {
        this.value = value;
    }
    

    // =============================================================
    // Methods
    
    public byte getByte()
    {
        return value;
    }

    @Override
    public int getInt()
    {
        return value & 0x00FF;
    }

    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public UInt8 typeMin()
    {
        return new UInt8(MIN_VALUE);
    }

    @Override
    public UInt8 typeMax()
    {
        return new UInt8(MAX_VALUE);
    }

    @Override
    public double getValue()
    {
        return value & 0x00FF;
    }

    @Override
    public UInt8 fromValue(double v)
    {
        return new UInt8(convert(v));
    }
    
    
    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public UInt8 one()
    {
        return ONE;
    }

    @Override
    public UInt8 zero()
    {
        return ZERO;
    }

    @Override
    public UInt8 plus(UInt8 other)
    {
        return new UInt8((this.value & 0x00FF) + (other.value & 0x00FF));
    }

    @Override
    public UInt8 minus(UInt8 other)
    {
        return new UInt8((this.value & 0x00FF) - (other.value & 0x00FF));
    }

    @Override
    public UInt8 times(double k)
    {
        return new UInt8((int) ((this.value & 0x00FF) * k));
    }

    @Override
    public UInt8 divideBy(double k)
    {
        return new UInt8((int) ((this.value & 0x00FF) / k));
    }
    
    
    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;

        // Check class internal values, using pattern matching
        if (that instanceof UInt8 thatInt)
        {
            return this.value == thatInt.value;
        }
        return false;
    }

    public int hashCode()
    {
        return java.lang.Byte.hashCode(this.value);
    }

    @Override
    public String toString()
    {
        return String.format("UInt8(%d)", this.value & 0x00FF);
    }    
}
