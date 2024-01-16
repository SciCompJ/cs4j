/**
 * 
 */
package net.sci.array.vector;

import static java.lang.Float.floatToRawIntBits;

import net.sci.array.scalar.Float32;

/**
 * A vector containing 32-bits floating point values (floats).
 *
 * @see Float64Vector
 * 
 * @author dlegland
 *
 */
public class Float32Vector extends Vector<Float32Vector, Float32>
{
    // =============================================================
    // Class variables

    float[] data;
    

    // =============================================================
    // Constructors

    public Float32Vector(int nChannels)
    {
        this.data = new float[nChannels];
    }

    public Float32Vector(float[] array)
    {
        this.data = new float[array.length];
        System.arraycopy(array, 0, this.data, 0, array.length);
    }

    public Float32Vector(double[] array)
    {
        this.data = new float[array.length];
        for (int c = 0; c < array.length; c++)
        {
            this.data[c] = (float) array[c];
        }
    }
    

    // =============================================================
    // New methods

    /**
     * @return a defensive copy of the inner float data.
     */
    public float[] getFloats()
    {
        float[] res = new float[this.data.length];
        System.arraycopy(this.data, 0, res, 0, this.data.length);
        return res;
    }

    /**
     * Returns the specified component of the float vector. No bound checking is
     * performed.
     * 
     * @param c
     *            the index of the component
     * @return the specified component of the vector.
     */
    public float getFloat(int c)
    {
        return this.data[c];
    }
    

    // =============================================================
    // Implementation of Vector interface

    @Override
    public int size()
    {
    	return this.data.length;
    }

    /**
     * Returns a defensive copy of the inner array.
     */
    @Override
    public double[] getValues()
    {
        double[] res = new double[this.data.length];
        for(int c = 0; c < this.data.length; c++)
        {
            res[c] = this.data[c];
        }
        return res;
    }

    /**
     * Fill in the specified array.
     */
    @Override
    public double[] getValues(double[] values)
    {
        for(int c = 0; c < this.data.length; c++)
        {
            values[c] = this.data[c];
        }
        return values;
    }

    /**
     * Returns the value at the specified position.
     */
    @Override
    public double getValue(int c)
    {
        return this.data[c];
    }

    @Override
    public Float32 get(int c)
    {
        return new Float32(this.data[c]);
    }
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object that)
    {
        // check for self-comparison
        if (this == that) return true;
        
        // check for class
        if (that instanceof Float32Vector thatVector)
        {
            // now a proper field-by-field evaluation can be made
            if (this.data.length != thatVector.data.length) return false;
            for (int i = 0; i < this.data.length; i++)
            {
                if (floatToRawIntBits(this.data[i]) != floatToRawIntBits(thatVector.data[i])) return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode()
    {
        int code = 23;
        for (float f : this.data)
        {
            code = hash(code, floatToRawIntBits(f));
        }
        return code;
    }

    /** longs. */
    private static int hash(int aSeed, int anInt)
    {
        return firstTerm(aSeed) + (int) (anInt ^ (anInt >>> 16));
    }

    // PRIVATE
    private static final int ODD_PRIME_NUMBER = 37;

    private static int firstTerm(int aSeed)
    {
        return ODD_PRIME_NUMBER * aSeed;
    }
}
