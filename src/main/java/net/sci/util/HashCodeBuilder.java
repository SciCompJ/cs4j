/**
 * 
 */
package net.sci.util;

/**
 * An utility to facilitate the computation of hash codes. Largely inspired by
 * Apache commons HashCodeBuilder, but implemented here to avoid dependency.
 * 
 * 
 */
public class HashCodeBuilder
{
    /**
     * The current hashcode value.
     */
    private int total;
    
    /**
     * The multiplicative constant. Must be an odd value, preferentially a prime.
     */
    private final int mult;
    
    /**
     * Creates a new hash code builder initialized with default values for seed
     * and multiplier.
     */
    public HashCodeBuilder()
    {
        this(17, 37);
    }
    
    /**
     * Creates a new hash code builder by specifying the values for initial hash
     * code value and for multiplier. Both must be odd values.
     * 
     * @param initialValue
     *            the initial value for hash code
     * @param multiplicativeConstant
     *            the value used to multiply the hash code value at the
     *            beginning of each append operation.
     */
    public HashCodeBuilder(int initialValue, int multiplicativeConstant)
    {
        this.total = initialValue;
        this.mult = multiplicativeConstant;
    }
    
    /**
     * Returns the current hashcode value
     * 
     * @return the current hashcode value
     */
    public int build()
    {
        return total;
    }
    
    /**
     * Updates the current hash code value with the specified boolean value.
     * 
     * In practice, adds {@code 1} when {@code value} is {@code true}, and
     * {@code 0} otherwise.
     * 
     * @param value
     *            a boolean value
     * @return this builder instance
     */
    public HashCodeBuilder append(final boolean value)
    {
        total = total * mult + (value ? 1 : 0);
        return this;
    }
            
    /**
     * Updates the current hash code value with the specified array of boolean
     * values.
     * 
     * @param array
     *            an array of boolean values
     * @return this builder instance
     */
    public HashCodeBuilder append(final boolean[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final boolean v : array) 
            {
                append(v);
            }
        }
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified {@code byte} value.
     * 
     * @param value
     *            a byte value
     * @return this builder instance
     */
    public HashCodeBuilder append(final byte value)
    {
        total = total * mult + value;
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified array of byte
     * values.
     * 
     * @param array
     *            an array of byte values
     * @return this builder instance
     */
    public HashCodeBuilder append(final byte[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final byte v : array) 
            {
                append(v);
            }
        }
        return this;
    }
            
    /**
     * Updates the current hash code value with the specified {@code short} value.
     * 
     * @param value
     *            a short value
     * @return this builder instance
     */
    public HashCodeBuilder append(final short value)
    {
        total = total * mult + value;
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified array of short
     * values.
     * 
     * @param array
     *            an array of short values
     * @return this builder instance
     */
    public HashCodeBuilder append(final short[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final short v : array) 
            {
                append(v);
            }
        }
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified {@code integer} value.
     * 
     * @param value
     *            an integer value
     * @return this builder instance
     */
    public HashCodeBuilder append(final int value)
    {
        total = total * mult + value;
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified array of integer
     * values.
     * 
     * @param array
     *            an array of integer values
     * @return this builder instance
     */
    public HashCodeBuilder append(final int[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final int v : array) 
            {
                append(v);
            }
        }
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified {@code long} value.
     * 
     * @param value
     *            a long value
     * @return this builder instance
     */
    public HashCodeBuilder append(final long value)
    {
        total = total * mult + (int) (value ^ value >> 32);
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified array of long
     * values.
     * 
     * @param array
     *            an array of long values
     * @return this builder instance
     */
    public HashCodeBuilder append(final long[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final long v : array) 
            {
                append(v);
            }
        }
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified {@code float} value.
     * 
     * @param value
     *            a float value
     * @return this builder instance
     */
    public HashCodeBuilder append(final float value)
    {
        total = total * mult + Float.floatToIntBits(value);
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified array of float
     * values.
     * 
     * @param array
     *            an array of float values
     * @return this builder instance
     */
    public HashCodeBuilder append(final float[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final float v : array) 
            {
                append(v);
            }
        }
        return this;
    }
    
    /**
     * Updates the current hash code value with the specified {@code double} value.
     * 
     * @param value
     *            a double value
     * @return this builder instance
     */
    public HashCodeBuilder append(final double value)
    {
        return append(Double.doubleToLongBits(value));
    }
    
    /**
     * Updates the current hash code value with the specified array of double
     * values.
     * 
     * @param array
     *            an array of double values
     * @return this builder instance
     */
    public HashCodeBuilder append(final double[] array)
    {
        if (array == null) 
        {
            total *= mult;
        }
        else 
        {
            for (final double v : array) 
            {
                append(v);
            }
        }
        return this;
    }
}
