/**
 * 
 */
package net.sci.array.numeric;

import static java.lang.Double.doubleToLongBits;

import net.sci.util.HashCodeBuilder;

/**
 * A vector containing 64-bits floating point values (doubles).
 *
 * @see Float32Vector
 * 
 * @author dlegland
 *
 */
public class Float64Vector implements Vector<Float64Vector, Float64>
{
    // =============================================================
    // Class variables

    double[] data;

    
    // =============================================================
    // Constructors

    public Float64Vector(int nChannels)
    {
        this.data = new double[nChannels];
    }

    public Float64Vector(double[] array)
    {
        this.data = new double[array.length];
        System.arraycopy(array, 0, this.data, 0, array.length);
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
        System.arraycopy(this.data, 0, res, 0, this.data.length);
        return res;
    }

    /**
     * Fill in the specified array.
     */
    @Override
    public double[] getValues(double[] values)
    {
        for (int c = 0; c < this.data.length; c++)
        {
            values[c] = this.data[c];
        }
        return values;
    }

    /**
     * Returns the value at the specified position.
     */
    @Override
    public double getValue(int i)
    {
        return this.data[i];
    }

    @Override
    public Float64 get(int i)
    {
        return new Float64(this.data[i]);
    }
    
    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public Float64Vector one()
    {
        double[] vals = new double[this.data.length];
        vals[0] = 1;
        return new Float64Vector(vals);
    }

    @Override
    public Float64Vector zero()
    {
        double[] vals = new double[this.data.length];
        return new Float64Vector(vals);
    }

    @Override
    public Float64Vector plus(Float64Vector other)
    {
        double[] vals = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] + other.data[i];
        return new Float64Vector(vals);
    }
    
    @Override
    public Float64Vector minus(Float64Vector other)
    {
        double[] vals = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] - other.data[i];
        return new Float64Vector(vals);
    }

    @Override
    public Float64Vector opposite()
    {
        Float64Vector res = new Float64Vector(data.length);
        for (int i = 0; i < data.length; i++)
        {
            res.data[i] = -this.data[i];
        }
        return res;
    }
    
    @Override
    public Float64Vector times(double k)
    {
        double[] vals = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] * k;
        return new Float64Vector(vals);
    }

    @Override
    public Float64Vector divideBy(double k)
    {
        double[] vals = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++)
            vals[i] = this.data[i] / k;
        return new Float64Vector(vals);
    }    
    

    // =============================================================
    // Override Object methods

    public boolean equals(Object obj)
    {
        // check for self-comparison
        if (this == obj) return true;
        
        // check for class
        if (obj instanceof Float64Vector that)
        {
            // now a proper field-by-field evaluation can be made
            if (this.data.length != that.data.length) return false;
            for (int i = 0; i < this.data.length; i++)
            {
                if (doubleToLongBits(this.data[i]) != doubleToLongBits(that.data[i])) return false;
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
