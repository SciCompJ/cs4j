/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
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
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		UInt8Array2D result = UInt8Array2D.create(size0, size1);
		
		RGB8Array.Iterator rgb8Iter = iterator();
		UInt8Array.Iterator uint8Iter = result.iterator();
		while(rgb8Iter.hasNext() && uint8Iter.hasNext())
		{
			uint8Iter.setNextInt(rgb8Iter.next().getInt());
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
	public abstract RGB8Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#iterator()
	 */
	@Override
	public abstract RGB8Array.Iterator iterator();

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
        public byte getByte(int x, int y)
        {
            return (byte) RGB8Array2D.this.getSample(x, y, channel);
        }

        @Override
        public void setByte(int x, int y, byte byteValue)
        {
            RGB8Array2D.this.setSample(x, y, channel, byteValue & 0x00FF);
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
                return indX < size0 - 1 && indY < size1 - 1;
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
            return channel < 3;
        }

        @Override
        public UInt8Array2D next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
}
