/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;

/**
 * Specialization of Array for 1D arrays of Float64 values.
 * 
 * @author dlegland
 *
 */
public abstract class Float64Array1D extends ScalarArray1D<Float64> implements Float64Array
{
	// =============================================================
	// Static methods

	public static final Float64Array1D create(int size0)
	{
	    return wrap(Float64Array.create(size0));
	}
	
    /**
     * Creates a new Float64Array1D from an array of doubles.
     * 
     * @param array
     *            the array of doubles containing the values.
     * @return a new instance of Float64Array1D initialized with the values of
     *         <code>array</code>
     */
    public static final Float64Array1D fromDoubleArray(double[] array)
    {
        int size0 = array.length;
        Float64Array1D res = Float64Array1D.create(size0);
        for (int x = 0; x < size0; x++)
        {
            res.setValue(x, array[x]);
        }
        return res;
    }
    
    /**
     * Encapsulates the specified instance of Float64Array into a new
     * Float64Array1D, by creating a Wrapper if necessary. If the original array
     * is already an instance of Float64Array1D, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float64Array1D view of the original array
     */
    public static Float64Array1D wrap(Float64Array array)
    {
        if (array instanceof Float64Array1D)
        { 
            return (Float64Array1D) array; 
        }
        return new Wrapper(array);
    }
    
    
	// =============================================================
	// Constructor

	/**
	 * Initialize the protected size variables. 
	 * 
	 * @param size0
	 *            the size of the array along the first dimension
	 * @param size1
	 *            the size of the array along the second dimension
	 */
	protected Float64Array1D(int size0)
	{
		super(size0);
	}

    public abstract double getValue(int x);
    
    public abstract void setValue(int x, double value);
    
    
	// =============================================================
	// Specialization of Array interface
	
    @Override
    public Float64Array1D duplicate()
    {
        // create output array
        Float64Array1D res = Float64Array1D.wrap(Float64Array.create(this.size0));
        for (int x = 0; x < size0; x++)
        {
            res.setValue(x, getValue(x));
        }
        return res;
    }

    @Override
    public Float64 get(int x)
    {
        return new Float64(getValue(x));
    }
    
    @Override
    public void set(int x, Float64 value)
    {
        setValue(x, value.value);
    }
    
    
    // =============================================================
    // Implementation of inner classes
    
    /**
     * Wraps a Float64 array with two dimensions into a Float64Array1D.
     */
    private static class Wrapper extends Float64Array1D implements Array.View<Float64>
    {
        Float64Array array;

        public Wrapper(Float64Array array)
        {
            super(0);
            this.size0 = array.size(0);
            this.array = array;
        }

        @Override
        public double getValue(int x)
        {
            return this.array.getValue(new int[] {x});
        }

        @Override
        public void setValue(int x, double value)
        {
            this.array.setValue(new int[] {x}, value);
        }

        @Override
        public double getValue(int[] pos)
        {
            return this.array.getValue(pos);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            this.array.setValue(pos, value);
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
        }
        
        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.numeric.Float64Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
