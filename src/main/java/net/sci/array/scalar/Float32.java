/**
 * 
 */
package net.sci.array.scalar;

/**
 * A floating-point value coded with 32 bits, using a <code>float</code> for
 * internal representation.
 * 
 * @author dlegland
 *
 */
public class Float32 extends Scalar<Float32>
{
    // =============================================================
    // Public constants
    
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
     * @return this value as a float
     */
    public float getFloat()
    {
        return value;
    }

    // =============================================================
    // Implementation of the Scalar interface
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.type.Scalar#getValue()
     */
    @Override
    public double getValue()
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
