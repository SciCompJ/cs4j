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
