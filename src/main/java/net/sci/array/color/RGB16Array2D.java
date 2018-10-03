/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16Array;
import net.sci.array.scalar.UInt16Array2D;
import net.sci.array.vector.IntVectorArray2D;

/**
 * @author dlegland
 *
 */
public abstract class RGB16Array2D extends IntVectorArray2D<RGB16> implements RGB16Array
{
	// =============================================================
	// Static methods

	public static final RGB16Array2D create(int size0, int size1)
	{
		return new BufferedPackedShortRGB16Array2D(size0, size1);
	}
	

	// =============================================================
	// Constructor

	protected RGB16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt16Array2D convertToUInt16()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		UInt16Array2D result = UInt16Array2D.create(size0, size1);
		
		// TODO: use position iterator
		RGB16Array.Iterator rgb16Iter = iterator();
		UInt16Array.Iterator uint16Iter = result.iterator();
		while(rgb16Iter.hasNext() && uint16Iter.hasNext())
		{
			uint16Iter.setNextInt(rgb16Iter.next().getInt());
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
        set(x, y, new RGB16(values));
    }

    
	// =============================================================
	// Specialization of Array2D interface


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
		int r = UInt16.clamp(values[0]);
		int g = UInt16.clamp(values[1]);
		int b = UInt16.clamp(values[2]);
		set(x, y, new RGB16(r, g, b));
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
    public UInt16Array2D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<UInt16Array2D> channels()
    {
        return new Iterable<UInt16Array2D>()
                {
                    @Override
                    public java.util.Iterator<UInt16Array2D> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }

    public java.util.Iterator<UInt16Array2D> channelIterator()
    {
        return new ChannelIterator();
    }


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB16Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#iterator()
	 */
	@Override
	public abstract RGB16Array.Iterator iterator();

    private class ChannelView extends UInt16Array2D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB16Array2D.this.size0, RGB16Array2D.this.size1);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public short getShort(int x, int y)
        {
            return (short) RGB16Array2D.this.getSample(x, y, channel);
        }

        @Override
        public void setShort(int x, int y, short shortValue)
        {
            RGB16Array2D.this.setSample(x, y, channel, shortValue & 0x00FFFF);
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
                }
            }

            @Override
            public double getValue()
            {
                return RGB16Array2D.this.getValue(indX, indY, channel);
            }

            @Override
            public void setValue(double value)
            {
                RGB16Array2D.this.setValue(indX, indY, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 && indY < size1 - 1;
            }

            @Override
            public int getInt()
            {
                return RGB16Array2D.this.getSample(indX, indY, channel);
            }

            @Override
            public void setInt(int intValue)
            {
                RGB16Array2D.this.setSample(indX, indY, channel, intValue);
            }

            @Override
            public short getShort()
            {
                return (short) RGB16Array2D.this.getSample(indX, indY, channel);
            }

            @Override
            public void setShort(short shortValue)
            {
                RGB16Array2D.this.setSample(indX, indY, channel, shortValue & 0x00FFFF);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt16Array2D> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 3;
        }

        @Override
        public UInt16Array2D next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }

}
