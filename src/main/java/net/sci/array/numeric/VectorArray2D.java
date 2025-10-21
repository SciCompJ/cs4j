/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.Array2D;

/**
 * Base implementation of <code>VectorArray</code> interface for 2D arrays.
 * 
 * @see VectorArray
 * 
 * @param <V>
 *            the type of the vector contained within the array
 * @param <S>
 *            the type of the elements contained by the vector
 * @author dlegland
 */
public abstract class VectorArray2D<V extends Vector<V, S>, S extends Scalar<S>> extends Array2D<V> implements VectorArray<V,S>
{
    // =============================================================
    // Static methods

    public final static <V extends Vector<V, S>, S extends Scalar<S>> VectorArray2D<V, S> wrap(VectorArray<V,S> array)
    {
        if (array instanceof VectorArray2D)
        {
            return (VectorArray2D<V, S>) array;
        }
        return new Wrapper<V, S>(array);
    }

    /**
     * Creates a new instance of VectorArray from a scalar array with three dimensions.
     * 
     * @param array
     *            an instance of scalar array
     * @return a new instance of vector array, with the one dimension less than
     *         original array
     */
    public static VectorArray2D<?,?> fromStack(ScalarArray3D<?> array)
    {
        // size and dimension of input array
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
    
        // create output array
        VectorArray2D<?,?> result = Float64VectorArray2D.create(sizeX, sizeY, sizeZ);
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
    public static ScalarArray3D<?> convertToStack(VectorArray2D<?,?> array)
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
        
        // iterate over both arrays in parallel
        double[] values = new double[channelCount()];
        for (int[] pos : result.positions())
        {
            this.getValues(pos, values);
            
            // compute norm of current vector
            double norm = 0;
            for (double d : values)
            {
                norm += d * d;
            }
            norm = Math.sqrt(norm);
            
            // allocate result
            result.setValue(pos, norm);
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
    public VectorArray2D<V,S> duplicate()
    {
        VectorArray2D<V,S> result = VectorArray2D.wrap(newInstance(this.size0, this.size1));
        
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

    private static class Wrapper<V extends Vector<V,S>, S extends Scalar<S>> extends VectorArray2D<V,S> implements Array.View<V>
    {
        private VectorArray<V,S> array;
        
        protected Wrapper(VectorArray<V,S> array)
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
        public V get(int x, int y)
        {
            return this.array.get(new int[] {x, y});
        }
        
        @Override
        public void set(int x, int y, V value)
        {
            // set value at specified position
            this.array.set(new int[] {x, y}, value);
        }

        @Override
        public V get(int[] pos)
        {
            // return value from specified position
            return this.array.get(pos);
        }

        @Override
        public void set(int[] pos, V value)
        {
            // set value at specified position
            this.array.set(pos, value);
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
        
        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
        }
        
        
        @Override
        public VectorArray<V,S> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public VectorArray.Factory<V,S> factory()
        {
            return this.array.factory();
        }

        @Override
        public Class<V> elementClass()
        {
            return array.elementClass();
        }

        @Override
        public VectorArray.Iterator<V,S> iterator()
        {
            return array.iterator();
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
