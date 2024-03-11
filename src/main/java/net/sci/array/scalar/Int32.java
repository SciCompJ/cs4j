/**
 * 
 */
package net.sci.array.scalar;


/**
 * Represents a signed 32-bits integer, simply coded with an int.
 * 
 * @author dlegland
 *
 */
public class Int32 extends Int<Int32>
{
    // =============================================================
    // Public constants
    
    /**
     * The Int32 equal to the smallest value that can be represented with this
     * type, corresponding to -2^31.
     */
    public static final Int32 MIN_VALUE = new Int32(Integer.MIN_VALUE);
    
    /**
     * The Int32 equal to the largest value that can be represented with this
     * type, corresponding to 2^31-1.
     */
    public static final Int32 MAX_VALUE = new Int32(Integer.MAX_VALUE);
    
    /**
     * The Int32 value that corresponds to one.
     */
    public final static Int32 ONE = new Int32(1);

    /**
     * The Int32 value that corresponds to zero.
     */
    public final static Int32 ZERO = new Int32(0);
    

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
        return (int) (value + 0.5);
    }
    

    // =============================================================
    // Class members

    int value;
    

    // =============================================================
    // Constructor

    /**
     * Creates a new instance of Int32 using the specified value.
     * 
     * @param value
     *            the value stored within this Int32
     */
    public Int32(int value)
    {
        this.value = value;
    }
    

    // =============================================================
    // Class methods

    @Override
    public int getInt()
    {
        return value;
    }

    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public Int32 typeMin()
    {
        return MIN_VALUE;
    }

    @Override
    public Int32 typeMax()
    {
        return MAX_VALUE;
    }

    @Override
    public double getValue()
    {
        return value;
    }

    @Override
    public Int32 fromValue(double v)
    {
        return new Int32((int) (v + 0.5));
    }
    

    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Int32 one()
    {
        return ONE;
    }

    @Override
    public Int32 zero()
    {
        return ZERO;
    }

    @Override
    public Int32 plus(Int32 other)
    {
        return new Int32(this.value + other.value);
    }

    @Override
    public Int32 minus(Int32 other)
    {
        return new Int32(this.value - other.value);
    }

    @Override
    public Int32 opposite()
    {
        return new Int32(-this.value);
    }

    @Override
    public Int32 times(double k)
    {
        return new Int32((int) (this.value * k));
    }

    @Override
    public Int32 divideBy(double k)
    {
        return new Int32((int) (this.value / k));
    }
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // Check class internal values, using pattern matching
        if (that instanceof Int32 thatInt)
        {
            return this.value == thatInt.value;
        }
        return false;
    }

    public int hashCode()
    {
        return java.lang.Integer.hashCode(this.value);
    }

    @Override
    public String toString()
    {
        return String.format("Int32(%d)", this.value);
    }
}
