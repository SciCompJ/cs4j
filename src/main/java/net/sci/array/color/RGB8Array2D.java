/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.vector.IntVectorArray2D;

/**
 * @author dlegland
 *
 */
public abstract class RGB8Array2D extends IntVectorArray2D<RGB8> implements RGB8Array
{
	// =============================================================
	// Static methods

    public static final RGB8Array2D create(int size0, int size1)
    {
        return new Int32EncodedRGB8Array2D(size0, size1);
    }
    
    public static final RGB8Array2D wrap(RGB8Array array)
    {
        if (array instanceof RGB8Array2D)
        {
            return (RGB8Array2D) array;
        }
        return new Wrapper(array);
    }
    

	// =============================================================
	// Constructor

	protected RGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt8Array2D convertToUInt8()
	{
		int size0 = this.size(0);
		int size1 = this.size(1);
		UInt8Array2D result = UInt8Array2D.create(size0, size1);
		
		for (int[] pos : positions())
		{
		    result.setInt(pos, this.get(pos).getInt());
		}
		
		return result;
	}
	
    // =============================================================
    // Specialization of IntVectorArray2D interface

    @Override
    public int[] getSamples(int x, int y)
    {
        return get(x, y).getSamples();
    }

    @Override
    public int[] getSamples(int x, int y, int[] values)
    {
        return get(x, y).getSamples(values);
    }

    @Override
    public void setSamples(int x, int y, int[] values)
    {
        set(x, y, new RGB8(values));
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
    public UInt8Array2D channel(int channel)
    {
        return new ChannelView(channel);
    }

    public Iterable<UInt8Array2D> channels()
    {
        return new Iterable<UInt8Array2D>()
        {
            @Override
            public java.util.Iterator<UInt8Array2D> iterator()
            {
                return new ChannelIterator();
            }
        };
    }

    public java.util.Iterator<UInt8Array2D> channelIterator()
    {
        return new ChannelIterator();
    }

	@Override
	public double[] getValues(int x, int y)
	{
		return get(x, y).getValues();
	}

    @Override
    public double[] getValues(int x, int y, double[] values)
    {
        return get(x, y).getValues(values);
    }

    @Override
	public void setValues(int x, int y, double[] values)
	{
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		set(x, y, new RGB8(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#duplicate()
	 */
	@Override
	public RGB8Array2D duplicate()
	{
	    RGB8Array2D res = RGB8Array2D.create(size0, size1);
	    res.fill(pos -> this.get(pos));
	    return res;
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
     */
    @Override
    public void set(int[] pos, RGB8 rgb)
    {
        set(pos[0], pos[1], rgb);
    }
    

    // =============================================================
    // View implementations

	private class ChannelView extends UInt8Array2D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB8Array2D.this.size0, RGB8Array2D.this.size1);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public void setByte(int x, int y, byte byteValue)
        {
            RGB8Array2D.this.setSample(x, y, channel, byteValue & 0x00FF);
        }

        @Override
        public byte getByte(int... pos)
        {
            return (byte) RGB8Array2D.this.getSample(pos[0], pos[1], channel);
        }

        @Override
        public void setByte(int[] pos, byte byteValue)
        {
            RGB8Array2D.this.setSample(pos[0], pos[1], channel, byteValue & 0x00FF);
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
                }
            }

            @Override
            public double getValue()
            {
                return RGB8Array2D.this.getValue(indX, indY, channel);
            }

            @Override
            public void setValue(double value)
            {
                RGB8Array2D.this.setValue(indX, indY, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1;
            }

            @Override
            public int getInt()
            {
                return RGB8Array2D.this.getSample(indX, indY, channel);
            }

            @Override
            public void setInt(int intValue)
            {
                RGB8Array2D.this.setSample(indX, indY, channel, intValue);
            }

            @Override
            public byte getByte()
            {
                return (byte) RGB8Array2D.this.getSample(indX, indY, channel);
            }

            @Override
            public void setByte(byte byteValue)
            {
                RGB8Array2D.this.setSample(indX, indY, channel, byteValue & 0x00FF);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt8Array2D> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt8Array2D next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
    
    /**
     * Wraps a RGB8 array into a RGB8Array2D, with two dimensions.
     */
    private static class Wrapper extends RGB8Array2D
    {
        RGB8Array array;

        public Wrapper(RGB8Array array)
        {
            super(0, 0);
            if (array.dimensionality() != 2)
            {
                throw new IllegalArgumentException("Requires an array of dimensionality equal to 2.");
            }
            this.size0 = array.size(0);
            this.size1 = array.size(1);
            this.array = array;
        }
        
        @Override
        public int getSample(int x, int y, int c)
        {
            return array.getSample(new int[] {x, y}, c);
        }


        @Override
        public void setSample(int x, int y, int c, int intValue)
        {
            array.setSample(new int[] {x, y}, c, intValue);
        }


        @Override
        public RGB8 get(int... pos)
        {
            return array.get(pos);
        }

        @Override
        public void set(int x, int y, RGB8 value)
        {
            array.set(new int[] {x, y}, value);
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
