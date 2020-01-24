/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public abstract class VectorArray3D<V extends Vector<?>> extends Array3D<V> implements VectorArray<V>
{
    // =============================================================
    // Static methods

    public final static <T extends Vector<?>> VectorArray3D<T> wrap(VectorArray<T> array)
    {
        if (array instanceof VectorArray3D)
        {
            return (VectorArray3D<T>) array;
        }
        return new Wrapper<T>(array);
    }

	
    // =============================================================
	// Constructors

	protected VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	
	
	// =============================================================
	// New methods for VectorArray3D

    /**
     * Returns a view over the specified slice.
     * 
     * @param sliceIndex
     *            the index of the slice
     * @return a view on the specific slice, as a 2D array
     */
    public abstract VectorArray2D<V> slice(int sliceIndex);

    /**
     * Iterates over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract Iterable<? extends VectorArray2D<V>> slices();

    /**
     * Creates an iterator over the slices
     * 
     * @return an iterator over 2D slices
     */
    public abstract java.util.Iterator<? extends VectorArray2D<V>> sliceIterator();


    // =============================================================
    // New getter / setter methods

	public abstract double[] getValues(int x, int y, int z);
	
    /**
     * Returns the values at a given location in the specified pre-allocated
     * array.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param values
     *            the pre-allocated array for storing values
     * @return a reference to the pre-allocated array
     */
    public abstract double[] getValues(int x, int y, int z, double[] values);

    public abstract void setValues(int x, int y, int z, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param z
	 *            the z-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
	public abstract void setValue(int x, int y, int z, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
    public abstract ScalarArray3D<?> channel(int channel);

    /**
     * ITerates over the channels
     * @return
     */
    public abstract Iterable<? extends ScalarArray3D<?>> channels();

    public abstract java.util.Iterator<? extends ScalarArray3D<?>> channelIterator();

    public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1], pos[2]);
	}
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], pos[2], values);
    }

	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], pos[2], values);
	}
	
    @Override
    public double getValue(int[] pos, int channel)
    {
        return getValue(pos[0], pos[1], pos[2], channel);
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        setValue(pos[0], pos[1], pos[2], channel, channel);
    }


	// =============================================================
	// Specialization of Array3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
    @Override
    public VectorArray3D<V> duplicate()
    {
        VectorArray<V> tmp = this.newInstance(this.size0, this.size1, this.size2);
        if (!(tmp instanceof VectorArray3D))
        {
            throw new RuntimeException("Can not create VectorArray3D instance from " + this.getClass().getName() + " class.");
        }
        
        VectorArray3D<V> result = (VectorArray3D <V>) tmp;
        
        double[] buf = new double[this.channelNumber()];
        
        // iterate over positions
        for (int z = 0; z < this.size(2); z++)
        {
            for (int y = 0; y < this.size(1); y++)
            {
                for (int x = 0; x < this.size(0); x++)
                {
                    result.setValues(x, y, z, this.getValues(x, y, z, buf));
                }
            }
        }

        return result;
    }
    
	
    // =============================================================
    // Inner Wrapper class

    private static class Wrapper<T extends Vector<?>> extends VectorArray3D<T>
    {
        // --------------------------------------------------------
        // class variable

        private VectorArray<T> array;
        
        
        // --------------------------------------------------------
        // Constructor

        protected Wrapper(VectorArray<T> array)
        {
            super(0, 0, 0);
            if (array.dimensionality() < 3)
            {
                throw new IllegalArgumentException("Requires an array with at least three dimensions");
            }
            this.array = array;
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
        }

        
        // --------------------------------------------------------
        // Implementation of VectorArray3D interface

        @Override
        public double[] getValues(int x, int y, int z)
        {
            return array.getValues(new int[] {x, y, z});
        }

        @Override
        public double[] getValues(int x, int y, int z, double[] values)
        {
            return getValues(new int[] {x, y, z}, values);
        }

        @Override
        public void setValues(int x, int y, int z, double[] values)
        {
            setValues(new int[] {x, y, z}, values);
        }

        @Override
        public double getValue(int x, int y, int z, int c)
        {
            return getValues(new int[] {x, y, z})[c];
        }

        @Override
        public void setValue(int x, int y, int z, int c, double value)
        {
            int[] pos = new int[] {x, y, z};
            double[] values = array.getValues(pos);
            values[c] = value;
            array.setValues(pos, values);
        }
        
        // --------------------------------------------------------
        // Implementation of Array3D interface

        @Override
        public VectorArray2D<T> slice(int sliceIndex)
        {
            return new SliceView(sliceIndex);
        }

        @Override
        public Iterable<? extends VectorArray2D<T>> slices()
        {
            return new Iterable<VectorArray2D<T>>()
            {
                @Override
                public java.util.Iterator<VectorArray2D<T>> iterator()
                {
                    return new SliceIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<? extends VectorArray2D<T>> sliceIterator()
        {
            return new SliceIterator();
        }

        @Override
        public T get(int... pos)
        {
            // return value from specified position
            return this.array.get(pos);
        }

        @Override
        public void set(T value, int... pos)
        {
            // set value at specified position
            this.array.set(value, pos);
        }

        
        // --------------------------------------------------------
        // Implementation of VectorArray interface

        @Override
        public int channelNumber()
        {
            return array.channelNumber();
        }

        @Override
        public ScalarArray3D<?> channel(int channel)
        {
            return ScalarArray3D.wrap(array.channel(channel));
        }

        @Override
        public Iterable<? extends ScalarArray3D<?>> channels()
        {
            return new Iterable<ScalarArray3D<?>>()
            {
                @Override
                public java.util.Iterator<ScalarArray3D<?>> iterator()
                {
                    return new ChannelIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<ScalarArray3D<?>> channelIterator()
        {
            return new ChannelIterator();
        }

        
        // --------------------------------------------------------
        // Implementation of Array interface

        @Override
        public VectorArray<T> newInstance(int... dims)
        {
            return this.array.newInstance(dims);
        }

        @Override
        public Array.Factory<T> getFactory()
        {
            return this.array.getFactory();
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


        // --------------------------------------------------------
        // Inner Array2D iterator implementation

        private class SliceIterator implements java.util.Iterator<VectorArray2D<T>> 
        {
            int sliceIndex = -1;

            @Override
            public boolean hasNext()
            {
                return sliceIndex < array.size(2) - 1;
            }

            @Override
            public VectorArray2D<T> next()
            {
                sliceIndex++;
                return new SliceView(sliceIndex);
            }
        }

        
        // --------------------------------------------------------
        // View on a specific slice of the Vector array

        private class SliceView extends VectorArray2D<T>
        {
            int sliceIndex;
            
            protected SliceView(int slice)
            {
                super(Wrapper.this.size0, Wrapper.this.size1);
                if (slice < 0 || slice >= Wrapper.this.size2)
                {
                    throw new IllegalArgumentException(String.format(
                            "Slice index %d must be comprised between 0 and %d", slice, Wrapper.this.size2));
                }
                this.sliceIndex = slice;
            }


            // --------------------------------------------------------
            // Implements VectorArray2D

            @Override
            public double getValue(int x, int y, int c)
            {
                return Wrapper.this.getValue(x, y, c, this.sliceIndex);
            }

            @Override
            public void setValue(int x, int y, int c, double value)
            {
                Wrapper.this.setValue(x, y, c, this.sliceIndex, value);
            }

            @Override
            public double[] getValues(int x, int y)
            {
                return Wrapper.this.getValues(x, y, this.sliceIndex);
            }

            @Override
            public double[] getValues(int x, int y, double[] values)
            {
                return Wrapper.this.getValues(x, y, sliceIndex, values);
            }

            @Override
            public void setValues(int x, int y, double values[])
            {
                Wrapper.this.setValues(x, y, this.sliceIndex, values);            
            }


            // --------------------------------------------------------
            // Implements VectorArray

            @Override
            public int channelNumber()
            {
                return Wrapper.this.channelNumber();
            }

            @Override
            public ScalarArray2D<?> channel(int channel)
            {
                return ScalarArray3D.wrap(array.channel(channel)).slice(sliceIndex);
            }

            @Override
            public Iterable<? extends ScalarArray2D<?>> channels()
            {
                return new Iterable<ScalarArray2D<?>>()
                {
                    @Override
                    public java.util.Iterator<ScalarArray2D<?>> iterator()
                    {
                        return new SliceChannelIterator();
                    }
                };
            }

            @Override
            public java.util.Iterator<? extends ScalarArray2D<?>> channelIterator()
            {
                return new SliceChannelIterator();
            }
 
            
            // --------------------------------------------------------
            // Implements Array2D

            @Override
            public T get(int... pos)
            {
                return Wrapper.this.get(pos[0], pos[1], this.sliceIndex);
            }

            @Override
            public void set(T value, int... pos)
            {
                Wrapper.this.set(value, pos[0], pos[1], this.sliceIndex);            
            }


            // --------------------------------------------------------
            // Implements Array

            @Override
            public VectorArray<T> newInstance(int... dims)
            {
                return Wrapper.this.array.newInstance(dims);
            }

            @Override
            public Class<T> dataType()
            {
                return Wrapper.this.array.dataType();
            }

            @Override
            public VectorArray2D<T> duplicate()
            {
                // create a new array, and ensure type is 2D
                VectorArray2D<T> result = VectorArray2D.wrap(Wrapper.this.array.newInstance(this.size0, this.size1));
                
                // Fill new array with input slice
                for (int y = 0; y < Wrapper.this.size1; y++)
                {
                    for (int x = 0; x < Wrapper.this.size0; x++)
                    {
                        result.setValues(x, y, Wrapper.this.getValues(x, y, sliceIndex));
                    }
                }
                                
                return result;
            }

            @Override
            public VectorArray.Factory<T> getFactory()
            {
                return Wrapper.this.getFactory();
            }

            // --------------------------------------------------------
            // Inner Array2D iterator implementation

            /**
             * Iterator over the channels of the current slice.
             */
            private class SliceChannelIterator implements java.util.Iterator<ScalarArray2D<?>> 
            {
                int channel = -1;

                @Override
                public boolean hasNext()
                {
                    return channel < array.channelNumber() - 1;
                }

                @Override
                public ScalarArray2D<?> next()
                {
                    channel++;
                    return Wrapper.this.slice(sliceIndex).channel(channel);
                }
            }

            @Override
            public net.sci.array.vector.VectorArray.Iterator<T> iterator()
            {
                return new Iterator();
            }

            class Iterator implements VectorArray.Iterator<T>
            {
                int indX = -1;
                int indY = 0;
                
                public Iterator() 
                {
                }
                
                @Override
                public T next()
                {
                    forward();
                    return get();
                }

                @Override
                public void forward()
                {
                    indX++;
                    if (indX >= size0)
                    {
                        indX = 0;
                        indY++;
                    }
                }

                @Override
                public boolean hasNext()
                {
                    return indX < size0 - 1 || indY < size1 - 1;
                }

                @Override
                public double getValue(int c)
                {
                    return Wrapper.this.getValue(indX, indY, sliceIndex, c);
                }

                @Override
                public void setValue(int c, double value)
                {
                    Wrapper.this.setValue(indX, indY, sliceIndex, c, value);
                }

                @Override
                public T get()
                {
                    return Wrapper.this.get(indX, indY, sliceIndex);
                }

                @Override
                public void set(T value)
                {
                    Wrapper.this.set(value, indX, indY, sliceIndex);
                }

            }
       }
 
        // --------------------------------------------------------
        // Inner channel iterator implementation

        private class ChannelIterator implements java.util.Iterator<ScalarArray3D<?>> 
        {
            int channel = -1;

            @Override
            public boolean hasNext()
            {
                return channel < array.channelNumber() - 1;
            }

            @Override
            public ScalarArray3D<?> next()
            {
                channel++;
                return ScalarArray3D.wrap(array.channel(channel));
            }
        }
    }
}
