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
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;

        // check for class
        if (!(that instanceof Float32)) return false;

        // cast to native object is now safe
        Float32 thatFloat = (Float32) that;

        // now a proper field-by-field evaluation can be made
        return this.value == thatFloat.value;
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
