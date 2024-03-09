/**
 * 
 */
package net.sci.array.scalar;

/**
 * A floating-point value coded with 64 bits, using a <code>double</code> for
 * internal representation.
 * 
 * @author dlegland
 *
 */
public class Float64 extends Scalar<Float64>
{
    // =============================================================
    // Public constants
    
    /**
     * The Float64 instance equal to the smallest value that can be represented
     * with this type, corresponding to minus infinity.
     */
    public static final Float64 MIN_VALUE = new Float64(Double.NEGATIVE_INFINITY);
    
    /**
     * The Float64 instance equal to the largest value that can be represented
     * with this type, corresponding to plus infinity.
     */
    public static final Float64 MAX_VALUE = new Float64(Double.POSITIVE_INFINITY);
    
    public final static Float64 ONE = new Float64(1.0);
    
    public final static Float64 ZERO = new Float64(0.0);
    
    
    // =============================================================
    // Class member

    double value;
	

    // =============================================================
    // Constructors

    /**
     * Creates new double with default value 0.
     */
    public Float64()
    {
        value = 0;
    }

    /**
     * Creates new double with specified value.
     * 
     * @param value
     *            the value stored within this Float64
     */
    public Float64(double value)
    {
        this.value = value;
    }
    

    // =============================================================
    // Implementation of the Scalar interface
    
    @Override
    public Float64 typeMin()
    {
        return new Float64(Double.NEGATIVE_INFINITY);
    }

    @Override
    public Float64 typeMax()
    {
        return new Float64(Double.POSITIVE_INFINITY);
    }

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
    public Float64 fromValue(double v)
    {
        return new Float64(v);
    }
    

    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Float64 one()
    {
        return ONE;
    }

    @Override
    public Float64 zero()
    {
        return ZERO;
    }

    @Override
    public Float64 plus(Float64 other)
    {
        return new Float64(this.value + other.value);
    }

    @Override
    public Float64 minus(Float64 other)
    {
        return new Float64(this.value - other.value);
    }

    @Override
    public Float64 times(double k)
    {
        return new Float64(this.value * k);
    }

    @Override
    public Float64 divideBy(double k)
    {
        return new Float64(this.value / k);
    }    
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // Check class internal values, using pattern matching
        if (that instanceof Float64 thatFloat)
        {
            return this.value == thatFloat.value;
        }
        return false;
    }

    public int hashCode()
    {
        return java.lang.Double.hashCode(this.value);
    }

    @Override
    public String toString()
    {
        return String.format("Float64(%f)", this.value);
    }    
}
