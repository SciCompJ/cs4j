/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array3D;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArray3D extends VectorArray3D<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArray3D create(int size0, int size1, int size2, int sizeV)
	{
		return new BufferedFloat64VectorArray3D(size0, size1, size2, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}

	
    // =============================================================
    // Specialization of VectorArray3D interface

    @Override
    public Float64VectorArray2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    @Override
    public Iterable<? extends Float64VectorArray2D> slices()
    {
        return new Iterable<Float64VectorArray2D>()
        {
            @Override
            public java.util.Iterator<Float64VectorArray2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    
    @Override
    public Float64Vector get(int x, int y, int z)
    {
        return new Float64Vector(getValues(x, y, z));
    }

    @Override
    public void set(int x, int y, int z, Float64Vector vect)
    {
        setValues(x, y, z, vect.getValues());
    }
    
    @Override
    public java.util.Iterator<? extends Float64VectorArray2D> sliceIterator()
    {
        return new SliceIterator();
    }


    // =============================================================
    // Specialization of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public Float64Array3D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<Float64Array3D> channels()
    {
        return new Iterable<Float64Array3D>()
                {
                    @Override
                    public java.util.Iterator<Float64Array3D> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }

    public java.util.Iterator<Float64Array3D> channelIterator()
    {
        return new ChannelIterator();
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
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
	public abstract void setValue(int x, int y, int z, int c, double value);


	// =============================================================
	// Specialization of Array interface


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract Float64VectorArray3D duplicate();

    
    // =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends Float64VectorArray2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(Float64VectorArray3D.this.size0, Float64VectorArray3D.this.size1);
            if (slice < 0 || slice >= Float64VectorArray3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, Float64VectorArray3D.this.size2));
            }
            this.sliceIndex = slice;
        }
    

        @Override
        public int channelCount()
        {
            return Float64VectorArray3D.this.channelCount();
        }

        @Override
        public double[] getValues(int x, int y)
        {
            return Float64VectorArray3D.this.getValues(x, y, sliceIndex);
        }


        @Override
        public void set(int x, int y, Float64Vector value)
        {
            Float64VectorArray3D.this.set(x, y, sliceIndex, value);
        }
        
        @Override
        public double[] getValues(int x, int y, double[] values)
        {
            return Float64VectorArray3D.this.getValues(x, y, sliceIndex, values);
        }
     
        @Override
        public void setValues(int x, int y, double[] values)
        {
            Float64VectorArray3D.this.setValues(x, y, sliceIndex, values);
        }

        @Override
        public double getValue(int x, int y, int c)
        {
            return Float64VectorArray3D.this.getValue(x, y, sliceIndex, c);
        }

        @Override
        public void setValue(int x, int y, int c, double value)
        {
            Float64VectorArray3D.this.setValue(x, y, sliceIndex, c, value);
        }

        @Override
        public Float64Vector get(int[] pos)
        {
            return Float64VectorArray3D.this.get(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void set(int[] pos, Float64Vector value)
        {
            Float64VectorArray3D.this.set(pos[0], pos[1], sliceIndex, value);
        }

        @Override
        public Float64VectorArray2D duplicate()
        {
            // allocate
            Float64VectorArray2D res = Float64VectorArray2D.create(size0, size1, channelCount());
            
            // fill values
            double[] buffer = new double[channelCount()];
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.setValues(x, y, Float64VectorArray3D.this.getValues(x, y, sliceIndex, buffer));
                }
            }
            
            // return
            return res;
        }

        @Override
        public Float64VectorArray.Iterator iterator()
        {
            return new Iterator();
        }
    

        class Iterator implements Float64VectorArray.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public Float64Vector next()
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
            public Float64Vector get()
            {
                return Float64VectorArray3D.this.get(indX, indY, sliceIndex);
            }

            @Override
            public void set(Float64Vector value)
            {
                Float64VectorArray3D.this.set(indX, indY, sliceIndex, value);
            }

            @Override
            public double getValue(int c)
            {
                return Float64VectorArray3D.this.getValue(indX, indY, sliceIndex, c);
            }

            @Override
            public void setValue(int c, double value)
            {
                Float64VectorArray3D.this.setValue(indX, indY, sliceIndex, c, value);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<Float64VectorArray2D> 
    {
        int sliceIndex = 0;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < Float64VectorArray3D.this.size2;
        }
    
        @Override
        public Float64VectorArray2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }


    // =============================================================
    // Inner classes for VectorArray

    private class ChannelView extends Float64Array3D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float64VectorArray3D.this.size0, Float64VectorArray3D.this.size1, Float64VectorArray3D.this.size2);
            int nChannels = Float64VectorArray3D.this.channelCount();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public double getValue(int x, int y, int z)
        {
            return Float64VectorArray3D.this.getValue(x, y, z, channel);
        }

        @Override
        public void setValue(int x, int y, int z, double value)
        {
            Float64VectorArray3D.this.setValue(x, y, z, channel, value);
        }

        @Override
        public void set(int x, int y, int z, Float64 value)
        {
            Float64VectorArray3D.this.setValue(x, y, z, channel, value.getValue());
        }


        @Override
        public net.sci.array.scalar.Float64Array.Iterator iterator()
        {
            return new Iterator();
        }

        @Override
        public double getValue(int[] pos)
        {
            return Float64VectorArray3D.this.getValue(pos[0], pos[1], pos[2], channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float64VectorArray3D.this.setValue(pos[0], pos[1], pos[2], channel, value);
        }
        
        class Iterator implements net.sci.array.scalar.Float64Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            int indZ = 0;

            @Override
            public Float64 next()
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
            public double getValue()
            {
                return Float64VectorArray3D.this.getValue(indX, indY, indZ, channel);
            }

            @Override
            public void setValue(double value)
            {
                Float64VectorArray3D.this.setValue(indX, indY, indZ, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1 || indZ < size2 - 1;
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<Float64Array3D> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelCount();
        }

        @Override
        public Float64Array3D next()
        {
            return new ChannelView(channel++);
        }
        
    }
}
