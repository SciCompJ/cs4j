/**
 * 
 */
package net.sci.array.color;

import net.sci.array.process.type.ConvertToUInt8;
import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.array.vector.IntVectorArray3D;

/**
 * Base class for implementation of 3D arrays containing colors represented as RGB8.
 * 
 * @author dlegland
 *
 */
public abstract class RGB8Array3D extends IntVectorArray3D<RGB8> implements RGB8Array
{
	// =============================================================
	// Static methods

	public static final RGB8Array3D create(int size0, int size1, int size2)
	{
		return new Int32EncodedRGB8Array3D(size0, size1, size2);
	}
	
    public static final RGB8Array3D wrap(RGB8Array array)
    {
        if (array instanceof RGB8Array3D)
        {
            return (RGB8Array3D) array;
        }
        return new Wrapper(array);
    }
    

	// =============================================================
	// Constructor

	protected RGB8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
	}


    // =============================================================
    // Implementation of new methods

    /**
     * Returns the largest value within the samples of the RGB8 element at the
     * specified position.
     * 
     * The aim of this method is to facilitate the conversion of RGB8 arrays
     * into grayscale (UInt8) arrays.
     * 
     * @see RGB8.maxSample()
     * 
     * @param pos
     *            the position within array
     * @return largest value within the samples, as an integer.
     */
    public int getMaxSample(int x, int y, int z)
    {
        return get(x, y, z).maxSample();
    }
    
    /**
     * Returns the intcode of the RGB8 value at specified position.
     * 
     * @see #setIntCode(int[], int)
     * 
     * @param pos
     *            the position within array
     * @return the intcode representing the RGB value
     */
    public int getIntCode(int x, int y, int z)
    {
        return get(x, y, z).intCode();
    }
    
    
	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt8Array3D convertToUInt8()
	{
	    return UInt8Array3D.wrap(new ConvertToUInt8().processRGB8(this));
	}
	
    @Override
    public int getMaxSample(int[] pos)
    {
        return getMaxSample(pos[0], pos[1], pos[2]);
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        return getIntCode(pos[0], pos[1], pos[2]);
    }
    

    // =============================================================
    // Specialization of IntVectorArray3D interface

    @Override
    public RGB8Array2D slice(int sliceIndex)
    {
        return new SliceView(sliceIndex);
    }

    @Override
    public Iterable<? extends RGB8Array2D> slices()
    {
        return new Iterable<RGB8Array2D>()
        {
            @Override
            public java.util.Iterator<RGB8Array2D> iterator()
            {
                return new SliceIterator();
            }
        };
    }

    @Override
    public java.util.Iterator<? extends RGB8Array2D> sliceIterator()
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
        set(x, y, z, new RGB8(values));
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
    public UInt8Array3D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<UInt8Array3D> channels()
    {
        return new Iterable<UInt8Array3D>()
        {
            @Override
            public java.util.Iterator<UInt8Array3D> iterator()
            {
                return new ChannelIterator();
            }
        };
    }

    public java.util.Iterator<UInt8Array3D> channelIterator()
    {
        return new ChannelIterator();
    }


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
		int r = UInt8.convert(values[0]);
		int g = UInt8.convert(values[1]);
		int b = UInt8.convert(values[2]);
		set(x, y, z, new RGB8(r, g, b));
	}


    // =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#duplicate()
	 */
	@Override
	public RGB8Array3D duplicate()
	{
        RGB8Array3D res = RGB8Array3D.create(size0, size1, size2);
        res.fill(pos -> this.get(pos));
        return res;
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.Array3D#set(int, int, int, java.lang.Object)
     */
    @Override
    public void set(int[] pos, RGB8 rgb)
    {
        set(pos[0], pos[1], pos[2], rgb);
    }
    

	// =============================================================
    // Inner classes for Array3D
    
    private class SliceView extends RGB8Array2D
    {
        int sliceIndex;
        
        protected SliceView(int slice)
        {
            super(RGB8Array3D.this.size0, RGB8Array3D.this.size1);
            if (slice < 0 || slice >= RGB8Array3D.this.size2)
            {
                throw new IllegalArgumentException(String.format(
                        "Slice index %d must be comprised between 0 and %d", slice, RGB8Array3D.this.size2));
            }
            this.sliceIndex = slice;
        }
    
        
        @Override
        public net.sci.array.color.RGB8Array.Iterator iterator()
        {
            return new Iterator();
        }
    
        @Override
        public RGB8Array2D duplicate()
        {
            // allocate
            RGB8Array2D res = RGB8Array2D.create(size0, size1);
            
            // fill values
            for (int y = 0; y < size1; y++)
            {
                for (int x = 0; x < size0; x++)
                {
                    res.set(x, y, RGB8Array3D.this.get(x, y, sliceIndex));
                }
            }
            
            // return
            return res;
        }

        @Override
        public int getSample(int x, int y, int c)
        {
            return RGB8Array3D.this.get(x, y, sliceIndex).getSample(c);
        }

        @Override
        public void setSample(int x, int y, int c, int intValue)
        {
            RGB8Array3D.this.setSample(x, y, sliceIndex, c, intValue);
        }


        @Override
        public void set(int x, int y, RGB8 value)
        {
            RGB8Array3D.this.set(x, y, sliceIndex, value);
        }

        @Override
        public RGB8 get(int... pos)
        {
            return RGB8Array3D.this.get(pos[0], pos[1], sliceIndex);
        }

        @Override
        public void set(int[] pos, RGB8 value)
        {
            RGB8Array3D.this.set(pos[0], pos[1], sliceIndex, value);
        }

        class Iterator implements RGB8Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            
            public Iterator() 
            {
            }
            
            @Override
            public RGB8 next()
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
            public RGB8 get()
            {
                return RGB8Array3D.this.get(indX, indY, sliceIndex);
            }

            @Override
            public void set(RGB8 value)
            {
                RGB8Array3D.this.set(indX, indY, sliceIndex, value);
            }
        }
    }

    private class SliceIterator implements java.util.Iterator<RGB8Array2D> 
    {
        int sliceIndex = 0;
    
        @Override
        public boolean hasNext()
        {
            return sliceIndex < RGB8Array3D.this.size2;
        }
    
        @Override
        public RGB8Array2D next()
        {
            return new SliceView(sliceIndex++);
        }
    }

    // =============================================================
    // Inner classes for VectorArray3D
    
    private class ChannelView extends UInt8Array3D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB8Array3D.this.size0, RGB8Array3D.this.size1, RGB8Array3D.this.size2);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public void setByte(int x, int y, int z, byte byteValue)
        {
            RGB8Array3D.this.setSample(x, y, z, channel, byteValue & 0x00FF);
        }

        @Override
        public byte getByte(int... pos)
        {
            return (byte) RGB8Array3D.this.getSample(pos[0], pos[1], pos[2], channel);
        }

        @Override
        public void setByte(int[] pos, byte byteValue)
        {
            RGB8Array3D.this.setSample(pos[0], pos[1], pos[2], channel, byteValue);
        }

        @Override
        public net.sci.array.scalar.UInt8Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.scalar.UInt8Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            int indZ = 0;

            @Override
            public UInt8 next()
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
                return RGB8Array3D.this.getValue(indX, indY, indZ, channel);
            }

            @Override
            public void setValue(double value)
            {
                RGB8Array3D.this.setValue(indX, indY, indZ, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1 || indZ < size2 - 1;
            }

            @Override
            public int getInt()
            {
                return RGB8Array3D.this.getSample(indX, indY, indZ, channel);
            }

            @Override
            public void setInt(int intValue)
            {
                RGB8Array3D.this.setSample(indX, indY, indZ, channel, intValue);
            }

            @Override
            public byte getByte()
            {
                return (byte) RGB8Array3D.this.getSample(indX, indY, indZ, channel);
            }

            @Override
            public void setByte(byte byteValue)
            {
                RGB8Array3D.this.setSample(indX, indY, indZ, channel, byteValue & 0x00FF);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt8Array3D> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt8Array3D next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
    
    /**
     * Wraps a RGB8 array into a RGB8Array3D, with three dimensions.
     */
    private static class Wrapper extends RGB8Array3D
    {
        RGB8Array array;

        public Wrapper(RGB8Array array)
        {
            super(0, 0, 0);
            if (array.dimensionality() != 3)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 3.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.size2 = array.size(2);
            this.array = array;
        }
        
        @Override
        public int getSample(int x, int y, int z, int c)
        {
            return array.getSample(new int[] {x, y, z}, c);
        }


        @Override
        public void setSample(int x, int y, int z, int c, int intValue)
        {
            array.setSample(new int[] {x, y, z}, c, intValue);
        }


        @Override
        public RGB8 get(int... pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int x, int y, int z, RGB8 value)
        {
            array.set(new int[] {x, y, z}, value);
        }

        /**
         * Simply returns an iterator on the original array.
         */
        @Override
        public net.sci.array.color.RGB8Array.Iterator iterator()
        {
            return this.array.iterator();
        }
    }
}
