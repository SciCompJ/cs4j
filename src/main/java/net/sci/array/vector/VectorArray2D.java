/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Float32Array2D;
import net.sci.array.scalar.Float32Array3D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray2D<V extends Vector<?>> extends Array2D<V> implements VectorArray<V>
{
    // =============================================================
    // Static methods

    public final static <T extends Vector<?>> VectorArray2D<T> wrap(VectorArray<T> array)
    {
        if (array instanceof VectorArray2D)
        {
            return (VectorArray2D<T>) array;
        }
        return new Wrapper<T>(array);
    }

    /**
     * Creates a new instance of VectorArray from a scalar array with three dimensions.
     * 
     * @param array
     *            an instance of scalar array
     * @return a new instance of vector array, with the one dimension less than
     *         original array
     */
    public static VectorArray2D<?> fromStack(ScalarArray3D<?> array)
    {
        // size and dimension of input array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
    
        // create output array
        VectorArray2D<? extends Vector<?>> result = Float64VectorArray2D.create(sizeX, sizeY, sizeZ);
        int[] pos = new int[3];
        for (int c = 0; c < sizeZ; c++)
        {
            pos[2] = c;
            for (int y = 0; y < sizeY; y++)
            {
                pos[1] = y;
                for (int x = 0; x < sizeX; x++)
                {
                    pos[0] = x;
                    result.setValue(x, y, c, array.getValue(pos));
                }
            }
        }
        
        return result;
    }
    
    /**
     * Converts a vector array to a higher-dimensional array, by considering the
     * channels as a new dimension.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     *
     * @param array
     *            a vector array with two dimensions
     * @return a scalar array with three dimensions
     */
    public static ScalarArray3D<?> convertToStack(VectorArray2D<?> array)
    {
        // size and dimension of input array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int nChannels = array.channelCount();
        
        // create output array
        Float32Array3D result = Float32Array3D.create(sizeX, sizeY, nChannels);
        int[] pos = new int[2];
        for (int c = 0; c < nChannels; c++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                pos[1] = y;
                for (int x = 0; x < sizeX; x++)
                {
                    pos[0] = x;
                    result.setValue(x, y, c, array.get(pos).getValue(c));
                }
            }
        }
        
        return result;
    }
    
    
	// =============================================================
	// Constructors

	protected VectorArray2D(int size0, int size1)
	{
		super(size0, size1);
	}
	
	
    // =============================================================
    // New methods

   /**
     * Computes the norm of each element of the given vector array.
     * 
     * Current implementation returns the result in a new instance of
     * Float32Array.
     * 
     * @return a scalar array with the same size at the input array
     */
    public ScalarArray2D<?> norm()
    {
        // allocate memory for result
        Float32Array2D result = Float32Array2D.create(size(0), size(1));
        
        // create array iterators
        VectorArray.Iterator<? extends Vector<?>> iter1 = iterator();
        Float32Array.Iterator iter2 = result.iterator();
        
        // iterate over both arrays in parallel
        double[] values = new double[channelCount()]; 
        while (iter1.hasNext() && iter2.hasNext())
        {
            // get current vector
            iter1.forward();
            iter1.getValues(values);
            
            // compute norm of current vector
            double norm = 0;
            for (double d : values)
            {
                norm += d * d;
            }
            norm = Math.sqrt(norm);
            
            // allocate result
            iter2.forward();
            iter2.setValue(norm);
        }
        
        return result;
    }
    

    // =============================================================
	// New abstract methods

    public abstract double[] getValues(int x, int y);
    
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract double[] getValues(int x, int y, double[] values);
    
	public abstract void setValues(int x, int y, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int c);
	
	public abstract void setValue(int x, int y, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface

	public abstract ScalarArray2D<?> channel(int channel);

    /**
     * Iterates over the channels
     * @return 
     */
    public abstract Iterable<? extends ScalarArray2D<?>> channels();

    public abstract java.util.Iterator<? extends ScalarArray2D<?>> channelIterator();

	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1]);
	}
	
	@Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], values);
    }

    public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], values);
	}

    @Override
    public double getValue(int[] pos, int channel)
    {
        return getValue(pos[0], pos[1], channel);
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        setValue(pos[0], pos[1], channel, value);
    }


	// =============================================================
	// Specialization of Array interface

    /* (non-Javadoc)
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public VectorArray2D<V> duplicate()
    {
        VectorArray<V> tmp = this.newInstance(this.size0, this.size1);
        if (!(tmp instanceof VectorArray2D))
        {
            throw new RuntimeException("Can not create VectorArray2D instance from " + this.getClass().getName() + " class.");
        }
        
        VectorArray2D<V> result = (VectorArray2D <V>) tmp;
        
        double[] buf = new double[this.channelCount()];

        // iterate over positions
        for (int y = 0; y < this.size(1); y++)
        {
            for (int x = 0; x < this.size(0); x++)
            {
                result.setValues(x, y, this.getValues(x, y, buf));
            }
        }

        return result;
    }


    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Vector<?>> extends VectorArray2D<T>
    {
        private VectorArray<T> array;
        
        protected Wrapper(VectorArray<T> array)
        {
            super(0, 0);
            if (array.dimensionality() < 2)
            {
                throw new IllegalArgumentException("Requires an array with at least two dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
        }

        @Override
        public void set(int x, int y, T value)
        {
            // set value at specified position
            this.array.set(new int[] {x, y}, value);
        }

        @Override
        public VectorArray<T> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public Array.Factory<T> factory()
        {
            return this.array.factory();
        }

        @Override
        public T get(int... pos)
        {
            // return value from specified position
            return this.array.get(pos);
        }

        @Override
        public void set(int[] pos, T value)
        {
            // set value at specified position
            this.array.set(pos, value);
        }

        @Override
        public Class<T> dataType()
        {
            return array.dataType();
        }

        @Override
        public VectorArray.Iterator<T> iterator()
        {
            return array.iterator();
        }

        @Override
        public int channelCount()
        {
            return array.channelCount();
        }

        @Override
        public ScalarArray2D<?> channel(int channel)
        {
            return ScalarArray2D.wrap(array.channel(channel));
        }

        @Override
        public Iterable<? extends ScalarArray2D<?>> channels()
        {
            return new Iterable<ScalarArray2D<?>>()
                    {
                        @Override
                        public java.util.Iterator<ScalarArray2D<?>> iterator()
                        {
                            return new ChannelIterator();
                        }
                    };
        }

        @Override
        public java.util.Iterator<ScalarArray2D<?>> channelIterator()
        {
            return new ChannelIterator();
        }

        @Override
        public double[] getValues(int x, int y)
        {
            return array.getValues(new int[] {x, y});
        }

        @Override
        public double[] getValues(int x, int y, double[] values)
        {
            return getValues(new int[] {x, y}, values);
        }

        @Override
        public void setValues(int x, int y, double[] values)
        {
            setValues(new int[] {x, y}, values);
        }

        @Override
        public double getValue(int x, int y, int c)
        {
            return getValues(new int[] {x, y})[c];
        }

        @Override
        public void setValue(int x, int y, int c, double value)
        {
            int[] pos = new int[] {x, y};
            double[] values = array.getValues(pos);
            values[c] = value;
            array.setValues(pos, values);
        }
        
        private class ChannelIterator implements java.util.Iterator<ScalarArray2D<?>> 
        {
            int channel = -1;

            @Override
            public boolean hasNext()
            {
                return channel < array.channelCount();
            }

            @Override
            public ScalarArray2D<?> next()
            {
                channel++;
                return ScalarArray2D.wrap(array.channel(channel));
            }
        }

    }
}
