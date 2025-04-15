/**
 * 
 */
package net.sci.array.numeric;

import static java.lang.Float.floatToRawIntBits;

import net.sci.util.HashCodeBuilder;

/**
 * A vector containing 32-bits floating point values (floats).
 *
 * @see Float64Vector
 * 
 * @author dlegland
 *
 */
public class Float32Vector implements Vector<Float32Vector, Float32>
{
    // =============================================================
    // Class variables

    final float[] data;
    

    // =============================================================
    // Constructors

    /**
     * Creates a new vector with the specified number of components.
     * 
     * @param nComps the number of components of this vector.
     */
    public Float32Vector(int nComps)
    {
        this.data = new float[nComps];
    }

    /**
     * Creates a new vector based on the specified array of float values.
     * 
     * This implementation keeps reference to the specified array. To avoid
     * modifying the initial array, either use a copy of the initial array, or
     * use the {@code duplicate} method of the new vector instance.
     * 
     * @param array
     *            the array containing vector components.
     */
    public Float32Vector(float[] array)
    {
        this.data = array;
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
    
    /**
     * Changes the specified component of the float vector. No bound checking is
     * performed.
     * 
     * @param c
     *            the index of the component
     * @param value
     *            the new value of the specified component
     */
    public void setFloat(int c, float value)
    {
        this.data[c] = value;
    }
    
    /**
     * Creates a new Float32Vector with same size and containing the same
     * values, but using a different inner array of float value.
     * 
     * @return a copy of this vector.
     */
    public Float32Vector duplicate()
    {
        Float32Vector res = new Float32Vector(this.data.length);
        System.arraycopy(this.data, 0, res.data, 0, this.data.length);
        return res;
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
        return getValues(new double[this.data.length]);
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
    public void setValue(int i, double value)
    {
        this.data[i] = (float) value;
    }

    @Override
    public Float32 get(int c)
    {
        return new Float32(this.data[c]);
    }
    
    @Override
    public void set(int i, Float32 value)
    {
        this.data[i] = value.floatValue();
    }
    
    
    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Float32Vector one()
    {
        float[] vals = new float[this.data.length];
        vals[0] = 1;
        return new Float32Vector(vals);
    }

    @Override
    public Float32Vector zero()
    {
        float[] vals = new float[this.data.length];
        return new Float32Vector(vals);
    }

    @Override
    public Float32Vector plus(Float32Vector other)
    {
        float[] vals = new float[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] + other.data[i];
        return new Float32Vector(vals);
    }

    @Override
    public Float32Vector minus(Float32Vector other)
    {
        float[] vals = new float[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] - other.data[i];
        return new Float32Vector(vals);
    }

    @Override
    public Float32Vector opposite()
    {
        Float32Vector res = new Float32Vector(data.length);
        for (int i = 0; i < data.length; i++)
        {
            res.data[i] = -this.data[i];
        }
        return res;
    }
    
    @Override
    public Float32Vector times(double k)
    {
        float[] vals = new float[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = (float) (this.data[i] * k);
        return new Float32Vector(vals);
    }

    @Override
    public Float32Vector divideBy(double k)
    {
        float[] vals = new float[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = (float) (this.data[i] / k);
        return new Float32Vector(vals);
    }    
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object obj)
    {
        // check for self-comparison
        if (this == obj) return true;
        
        // check for class
        if (obj instanceof Float32Vector that)
        {
            // now a proper field-by-field evaluation can be made
            if (this.data.length != that.data.length) return false;
            for (int i = 0; i < this.data.length; i++)
            {
                if (floatToRawIntBits(this.data[i]) != floatToRawIntBits(that.data[i])) return false;
            }
            return true;
        }
        return false;
    }
    
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(this.data)
                .build();
    }
}
