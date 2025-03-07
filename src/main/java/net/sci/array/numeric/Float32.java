/**
 * 
 */
package net.sci.array.numeric;

/**
 * A floating-point value coded with 32 bits, using a <code>float</code> for
 * internal representation.
 * 
 * @author dlegland
 *
 */
public class Float32 implements Scalar<Float32>
{
    // =============================================================
    // Public constants
    
    /**
     * The Float32 instance equal to the smallest value that can be represented
     * with this type, corresponding to minus infinity.
     */
    public static final Float32 MIN_VALUE = new Float32(Float.NEGATIVE_INFINITY);
    
    /**
     * The Float32 instance equal to the largest value that can be represented
     * with this type, corresponding to plus infinity.
     */
    public static final Float32 MAX_VALUE = new Float32(Float.POSITIVE_INFINITY);
    
    public final static Float32 ONE = new Float32(1.0f);
    
    public final static Float32 ZERO = new Float32(0.0f);
    
    
    // =============================================================
    // Class member
    
    float value;

    
    // =============================================================
    // Constructors

    /**
     * Creates new Float with default value 0.
     */
    public Float32()
    {
        value = 0;
    }

    /**
     * Creates new Float with specified value.
     * 
     * @param value
     *            the value stored within this Float32
     */
    public Float32(float value)
    {
        this.value = value;
    }
    

    // =============================================================
    // Class methods

    /**
     * Returns the content of this Float32 as a float value.
     * 
     * @return this value as a float
     */
    public float floatValue()
    {
        return value;
    }

    
    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public Float32 typeMin()
    {
        return new Float32(Float.NEGATIVE_INFINITY);
    }

    @Override
    public Float32 typeMax()
    {
        return new Float32(Float.POSITIVE_INFINITY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.type.Scalar#getValue()
     */
    @Override
    public double value()
    {
        return value;
    }

    @Override
    public Float32 fromValue(double v)
    {
        return new Float32((float) v);
    }
    

    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Float32 one()
    {
        return ONE;
    }

    @Override
    public Float32 zero()
    {
        return ZERO;
    }

    @Override
    public Float32 plus(Float32 other)
    {
        return new Float32(this.value + other.value);
    }

    @Override
    public Float32 minus(Float32 other)
    {
        return new Float32(this.value - other.value);
    }

    @Override
    public Float32 opposite()
    {
        return new Float32(-this.value);
    }

    @Override
    public Float32 times(double k)
    {
        return new Float32((float) (this.value * k));
    }

    @Override
    public Float32 divideBy(double k)
    {
        return new Float32((float) (this.value / k));
    }    
    

    // =============================================================
    // Implementation of the Comparable interface

    @Override
    public int compareTo(Float32 other)
    {
        return Float.compare(this.value, other.value);
    }
    
    
    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;

        // Check class internal values, using pattern matching
        if (that instanceof Float32 thatFloat)
        {
            return this.value == thatFloat.value;
        }
        return false;
    }

    public int hashCode()
    {
        return java.lang.Float.hashCode(this.value);
    }

    @Override
    public String toString()
    {
        return String.format("Float32(%f)", this.value);
    }
}
