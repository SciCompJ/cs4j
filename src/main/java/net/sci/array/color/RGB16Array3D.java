/**
 * 
 */
package net.sci.array.color;

import java.util.Collection;
import java.util.List;

import net.sci.array.Array;
import net.sci.array.numeric.IntVectorArray3D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array3D;

/**
 * @author dlegland
 *
 */
public abstract class RGB16Array3D extends IntVectorArray3D<RGB16,UInt16> implements RGB16Array
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
    // Implementation of new methods

    /**
     * Returns the corresponding gray value of the RGB16 element at the specified
     * position.
     * 
     * The aim of this method is to facilitate the conversion of RGB16 arrays
     * into grayscale (UInt16) arrays.
     * 
     * @see RGB16.grayValue()
     * 
     * @param x
     *            the x-coordinate of the array element
     * @param y
     *            the y-coordinate of the array element
     * @param z
     *            the z-coordinate of the array element
     * @return largest value within the samples, as an integer.
     */
    public int getGrayValue(int x, int y, int z)
    {
        return get(x, y, z).grayValue();
    }
    

	// =============================================================
	// Implementation of the RGB16Array interface

	@Override
	public UInt16Array3D convertToUInt16()
	{
		int size0 = this.size(0);
		int size1 = this.size(1);
		int size2 = this.size(2);
		UInt16Array3D result = UInt16Array3D.create(size0, size1, size2);
		
        // iterate over pixels
        for (int z = 0; z < size2; z++)
        {
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    result.setInt(x, y, z, this.getGrayValue(x, y, z));
                }
            }
        }
		
		return result;
	}

    @Override
    public int getGrayValue(int[] pos)
    {
        return getGrayValue(pos[0], pos[1], pos[2]);
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
		int r = UInt16.convert(values[0]);
		int g = UInt16.convert(values[1]);
		int b = UInt16.convert(values[2]);
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
     * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
     */
    @Override
    public void set(int[] pos, RGB16 rgb)
    {
        set(pos[0], pos[1], pos[2], rgb);
    }

	
	// =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends RGB16Array2D implements Array.View<RGB16>
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
            return RGB16Array3D.this.get(new int[] {x, y, sliceIndex});
        }


        @Override
        public void set(int x, int y, RGB16 value)
        {
            RGB16Array3D.this.set(x, y, sliceIndex, value);
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(RGB16Array3D.this);
        }   
        
        @Override
        public RGB16 get(int[] pos)
        {
            return RGB16Array3D.this.get(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void set(int[] pos, RGB16 value)
        {
            RGB16Array3D.this.set(pos[0], pos[1], sliceIndex, value);
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
        int sliceIndex = 0;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < RGB16Array3D.this.size2;
        }
    
        @Override
        public RGB16Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }


    // =============================================================
    // Inner classes for VectorArray

    private class ChannelView extends UInt16Array3D implements Array.View<UInt16>
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
        public void setShort(int x, int y, int z, short s)
        {
            RGB16Array3D.this.setSample(x, y, z, channel, s & 0x00FFFF);
        }

        @Override
        public short getShort(int[] pos)
        {
            return (short) RGB16Array3D.this.getSample(pos[0], pos[1], pos[2], channel);
        }

        @Override
        public void setShort(int[] pos, short s)
        {
            RGB16Array3D.this.setSample(pos[0], pos[1], pos[2], channel, s & 0x00FFFF);
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(RGB16Array3D.this);
        }
        
        @Override
        public net.sci.array.numeric.UInt16Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.numeric.UInt16Array.Iterator
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
