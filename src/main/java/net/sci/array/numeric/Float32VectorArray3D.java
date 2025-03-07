/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.numeric.impl.BufferedFloat32VectorArray3D;

/**
 * Specialization of the interface VectorArray for 3D arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public abstract class Float32VectorArray3D extends VectorArray3D<Float32Vector, Float32> implements Float32VectorArray
{
	// =============================================================
	// Static methods

	public static final Float32VectorArray3D create(int size0, int size1, int size2, int sizeV)
	{
		return new BufferedFloat32VectorArray3D(size0, size1, size2, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float32VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}
	
	
    // =============================================================
    // Declaration of new methods

    /**
     * Returns the float value at the specified position and for the specified
     * channel.
     * 
     * @param x
     *            the x-coordinate of the element
     * @param y
     *            the y-coordinate of the element
     * @param z
     *            the z-coordinate of the element
     * @param channel
     *            the channel index
     * @return the channel value at the (x,y,z) position
     */
    public abstract float getFloat(int x, int y, int z, int channel);

    /**
     * Changes the float value at the specified position and for the specified
     * channel.
     * 
     * @param x
     *            the x-coordinate of the element
     * @param y
     *            the y-coordinate of the element
     * @param z
     *            the z-coordinate of the element
     * @param channel
     *            the channel index
     * @param f the new value of the specified channel at the (x,y,z) position
     */
    public abstract void setFloat(int x, int y, int z, int channel, float f);
    
    
    // =============================================================
    // Implementation of VectorArray interface

    @Override
    public float getFloat(int[] pos, int channel)
    {
        return getFloat(pos[0], pos[1], pos[2], channel);
    }

    @Override
    public void setFloat(int[] pos, int channel, float value)
    {
        setFloat(pos[0], pos[1], pos[2], channel, value);
    }
    
    
    // =============================================================
    // Specialization of VectorArray3D interface

    @Override
    public Float32VectorArray2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    @Override
    public Iterable<? extends Float32VectorArray2D> slices()
    {
        return new Iterable<Float32VectorArray2D>()
        {
            @Override
            public java.util.Iterator<Float32VectorArray2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    @Override
    public java.util.Iterator<? extends Float32VectorArray2D> sliceIterator()
    {
        return new SliceIterator();
    }

	public abstract double[] getValues(int x, int y, int z);
	
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
	 *            the component index
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
    /**
     * Changes the scalar value for the specified position and the specified
     * component.
     * 
     * @param x
     *            the x-position of the vector
     * @param y
     *            the y-position of the vector
     * @param z
     *            the z-position of the vector
     * @param c
     *            the component index
     * @param value
     *            the new value at the specified position and component index
     */
	public abstract void setValue(int x, int y, int z, int c, double value);

	
    // =============================================================
    // Implementation of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public Float32Array3D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<Float32Array3D> channels()
    {
        return new Iterable<Float32Array3D>()
        {
            @Override
            public java.util.Iterator<Float32Array3D> iterator()
            {
                return new ChannelIterator();
            }
        };
    }

    @Override
    public Float32Vector get(int x, int y, int z)
    {
        return new Float32Vector(getValues(x, y, z));
    }

    @Override
    public void set(int x, int y, int z, Float32Vector vect)
    {
        setValues(x, y, z, vect.getValues());
    }

    public java.util.Iterator<Float32Array3D> channelIterator()
    {
        return new ChannelIterator();
    }

    
	// =============================================================
	// Specialization of Array interface

    /* (non-Javadoc)
     * @see net.sci.array.data.VectorArray#duplicate()
     */
    @Override
    public abstract Float32VectorArray3D duplicate();

    
    // =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends Float32VectorArray2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Float32VectorArray3D.this.size0, Float32VectorArray3D.this.size1);
            if (slice < 0 || slice >= Float32VectorArray3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Float32VectorArray3D.this.size2));
            }
            this.sliceIndex = slice;
        }
    

        @Override
        public float getFloat(int x, int y, int channel)
        {
            return Float32VectorArray3D.this.getFloat(x, y, sliceIndex, channel);
        }


        @Override
        public void setFloat(int x, int y, int channel, float value)
        {
            Float32VectorArray3D.this.setValue(x, y, sliceIndex, channel, value);
        }


        @Override
        public int channelCount()
        {
            return Float32VectorArray3D.this.channelCount();
        }

        @Override
        public double[] getValues(int x, int y)
        {
            return Float32VectorArray3D.this.getValues(x, y, sliceIndex);
        }

        @Override
        public double[] getValues(int x, int y, double[] values)
        {
            return Float32VectorArray3D.this.getValues(x, y, sliceIndex, values);
        }
     
        @Override
        public void setValues(int x, int y, double[] values)
        {
            Float32VectorArray3D.this.setValues(x, y, sliceIndex, values);
        }

        @Override
        public double getValue(int x, int y, int c)
        {
            return Float32VectorArray3D.this.getValue(x, y, sliceIndex, c);
        }

        @Override
        public void setValue(int x, int y, int c, double value)
        {
            Float32VectorArray3D.this.setValue(x, y, sliceIndex, c, value);
        }

        @Override
        public Float32Vector get(int[] pos)
        {
            return Float32VectorArray3D.this.get(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void set(int[] pos, Float32Vector value)
        {
            Float32VectorArray3D.this.set(pos[0], pos[1], sliceIndex, value);
        }

        @Override
        public Float32VectorArray2D duplicate()
        {
            // allocate
            Float32VectorArray2D res = Float32VectorArray2D.create(size0, size1, channelCount());
            
            // fill values
            double[] buffer = new double[channelCount()];
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setValues(x, y, Float32VectorArray3D.this.getValues(x, y, sliceIndex, buffer));
                }
            }
            
            // return
            return res;
        }

        @Override
        public Float32VectorArray.Iterator iterator()
        {
            return new Iterator();
        }
    

        class Iterator implements Float32VectorArray.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Float32Vector next()
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
            public Float32Vector get()
            {
                return Float32VectorArray3D.this.get(indX, indY, sliceIndex);
            }

            @Override
            public void set(Float32Vector value)
            {
                Float32VectorArray3D.this.set(indX, indY, sliceIndex, value);
            }

            @Override
            public double getValue(int c)
            {
                return Float32VectorArray3D.this.getValue(indX, indY, sliceIndex, c);
            }

            @Override
            public void setValue(int c, double value)
            {
                Float32VectorArray3D.this.setValue(indX, indY, sliceIndex, c, value);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Float32VectorArray2D> 
    {
        int sliceIndex = 0;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < Float32VectorArray3D.this.size2;
        }
    
        @Override
        public Float32VectorArray2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }


    // =============================================================
    // Inner classes for VectorArray

    private class ChannelView extends Float32Array3D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float32VectorArray3D.this.size0, Float32VectorArray3D.this.size1, Float32VectorArray3D.this.size2);
            int nChannels = Float32VectorArray3D.this.channelCount();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public float getFloat(int x, int y, int z)
        {
            return Float32VectorArray3D.this.getFloat(x, y, z, channel);
        }

        @Override
        public void setFloat(int x, int y, int z, float f)
        {
            Float32VectorArray3D.this.setFloat(x, y, z, channel, f);
        }

       @Override
        public void setValue(int x, int y, int z, double value)
        {
            Float32VectorArray3D.this.setValue(x, y, z, channel, value);
        }

        @Override
        public double getValue(int[] pos)
        {
            return Float32VectorArray3D.this.getValue(pos[0], pos[1], pos[2], channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float32VectorArray3D.this.setValue(pos[0], pos[1], pos[2], channel, value);
        }
        
        @Override
        public void set(int x, int y, int z, Float32 f)
        {
            Float32VectorArray3D.this.setFloat(x, y, z, channel, f.floatValue());
        }

        @Override
        public net.sci.array.numeric.Float32Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.numeric.Float32Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            int indZ = 0;

            @Override
            public Float32 next()
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
                    if (indY >= size1)
                    {
                        indY = 0;
                        indZ++;
                    }
                }
            }

            @Override
            public float getFloat()
            {
                return Float32VectorArray3D.this.getFloat(indX, indY, indZ, channel);
            }

            @Override
            public void setFloat(float value)
            {
                Float32VectorArray3D.this.setFloat(indX, indY, indZ, channel, value);
            }

            @Override
            public double getValue()
            {
                return Float32VectorArray3D.this.getValue(indX, indY, indZ, channel);
            }

            @Override
            public void setValue(double value)
            {
                Float32VectorArray3D.this.setValue(indX, indY, indZ, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1 || indZ < size2 - 1;
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<Float32Array3D> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelCount();
        }

        @Override
        public Float32Array3D next()
        {
            return new ChannelView(channel++);
        }
    }
}
