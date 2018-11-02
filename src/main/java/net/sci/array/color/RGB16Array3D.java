/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt16Array3D;
import net.sci.array.vector.IntVectorArray3D;

/**
 * @author dlegland
 *
 */
public abstract class RGB16Array3D extends IntVectorArray3D<RGB16> implements RGB16Array
{
	// =============================================================
	// Static methods

	public static final RGB16Array3D create(int size0, int size1, int size2)
	{
		return new BufferedPackedShortRGB16Array3D(size0, size1, size2);
	}
	

	// =============================================================
	// Constructor

	protected RGB16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


	// =============================================================
	// Implementation of the RGB16Array interface

	@Override
	public UInt16Array3D convertToUInt16()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		int size2 = this.getSize(2);
		UInt16Array3D result = UInt16Array3D.create(size0, size1, size2);
		
		RGB16Array.Iterator rgb16Iter = iterator();
		UInt16Array.Iterator uint16Iter = result.iterator();
		while(rgb16Iter.hasNext() && uint16Iter.hasNext())
		{
			uint16Iter.setNextInt(rgb16Iter.next().getInt());
		}
		
		return result;
	}

    // =============================================================
    // Specialization of IntVectorArray3D interface

    @Override
    public RGB16Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    @Override
    public Iterable<? extends RGB16Array2D> slices()
    {
        return new Iterable<RGB16Array2D>()
        {
            @Override
            public java.util.Iterator<RGB16Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    @Override
    public java.util.Iterator<? extends RGB16Array2D> sliceIterator()
    {
        return new SliceIterator();
    }

    @Override
    public int[] getSamples(int x, int y, int z)
    {
        return get(x, y, z).getSamples();
    }

    @Override
    public int[] getSamples(int x, int y, int z, int[] values)
    {
        return get(x, y, z).getSamples(values);
    }

    @Override
    public void setSamples(int x, int y, int z, int[] values)
    {
        set(x, y, z, new RGB16(values));
    }

    
	// =============================================================
	// Specialization of Array3D interface

	@Override
	public double[] getValues(int x, int y, int z)
	{
		return get(x, y, z).getValues();
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.vector.VectorArray3D#getValues(int, int, int, double[])
     */
    @Override
    public double[] getValues(int x, int y, int z, double[] values)
    {
        return get(x, y, z).getValues(values);
    }

    @Override
	public void setValues(int x, int y, int z, double[] values)
	{
		int r = UInt16.clamp(values[0]);
		int g = UInt16.clamp(values[1]);
		int b = UInt16.clamp(values[2]);
		set(x, y, z, new RGB16(r, g, b));
	}

    
    // =============================================================
    // Implementation of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public UInt16Array3D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<UInt16Array3D> channels()
    {
        return new Iterable<UInt16Array3D>()
                {
                    @Override
                    public java.util.Iterator<UInt16Array3D> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }

    public java.util.Iterator<UInt16Array3D> channelIterator()
    {
        return new ChannelIterator();
    }


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB16Array#duplicate()
	 */
	@Override
	public abstract RGB16Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB16Array#iterator()
	 */
	@Override
	public abstract net.sci.array.color.RGB16Array.Iterator iterator();

	
	// =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends RGB16Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(RGB16Array3D.this.size0, RGB16Array3D.this.size1);
            if (slice < 0 || slice >= RGB16Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, RGB16Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }
    

        
        @Override
        public net.sci.array.color.RGB16Array.Iterator iterator()
        {
            return new Iterator();
        }
    
        @Override
        public RGB16Array2D duplicate()
        {
            // allocate
            RGB16Array2D res = RGB16Array2D.create(size0, size1);
            
            // fill values
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.set(x, y, RGB16Array3D.this.get(x, y, sliceIndex));
                }
            }
            
            // return
            return res;
        }

        @Override
        public int getSample(int x, int y, int c)
        {
            return RGB16Array3D.this.get(x, y, sliceIndex).getSample(c);
        }

        @Override
        public void setSample(int x, int y, int c, int intValue)
        {
            RGB16Array3D.this.setSample(x, y, sliceIndex, c, intValue);
        }

        @Override
        public RGB16 get(int x, int y)
        {
            return RGB16Array3D.this.get(x, y, sliceIndex);
        }

        @Override
        public void set(int x, int y, RGB16 value)
        {
            RGB16Array3D.this.set(x, y, sliceIndex, value);
        }

        class Iterator implements RGB16Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public RGB16 next()
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
            public RGB16 get()
            {
                return RGB16Array3D.this.get(indX, indY, sliceIndex);
            }

            @Override
            public void set(RGB16 value)
            {
                RGB16Array3D.this.set(indX, indY, sliceIndex, value);
            }
        }
    }
    
    private class SliceIterator implements java.util.Iterator<RGB16Array2D> 
    {
        int sliceIndex = -1;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < RGB16Array3D.this.size2;
        }
    
        @Override
        public RGB16Array2D next()
        {
            sliceIndex++;
            return new SliceView(sliceIndex);
        }
    }


    // =============================================================
    // Inner classes for VectorArray

    private class ChannelView extends UInt16Array3D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB16Array3D.this.size0, RGB16Array3D.this.size1, RGB16Array3D.this.size2);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public short getShort(int x, int y, int z)
        {
            return (short) RGB16Array3D.this.getSample(x, y, z, channel);
        }

        @Override
        public void setShort(int x, int y, int z, short shortValue)
        {
            RGB16Array3D.this.setSample(x, y, z, channel, shortValue & 0x00FFFF);
        }

        @Override
        public net.sci.array.scalar.UInt16Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.scalar.UInt16Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            int indZ = 0;

            @Override
            public UInt16 next()
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
                return RGB16Array3D.this.getValue(indX, indY, indZ, channel);
            }

            @Override
            public void setValue(double value)
            {
                RGB16Array3D.this.setValue(indX, indY, indZ, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1 || indZ < size2 - 1;
            }

            @Override
            public int getInt()
            {
                return RGB16Array3D.this.getSample(indX, indY, indZ, channel);
            }

            @Override
            public void setInt(int intValue)
            {
                RGB16Array3D.this.setSample(indX, indY, indZ, channel, intValue);
            }

            @Override
            public short getShort()
            {
                return (short) RGB16Array3D.this.getSample(indX, indY, indZ, channel);
            }

            @Override
            public void setShort(short shortValue)
            {
                RGB16Array3D.this.setSample(indX, indY, indZ, channel, shortValue & 0x00FFFF);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt16Array3D> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt16Array3D next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }

}
