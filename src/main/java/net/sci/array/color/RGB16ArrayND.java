/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt16;
import net.sci.array.scalar.UInt16ArrayND;
import net.sci.array.vector.VectorArrayND;

/**
 * @author dlegland
 *
 */
public abstract class RGB16ArrayND extends VectorArrayND<RGB16> implements RGB16Array
{
	// =============================================================
	// Static methods

	public static final RGB16ArrayND create(int... dims)
	{
		return new BufferedPackedShortRGB16ArrayND(dims);
	}
	

	// =============================================================
	// Constructor

	protected RGB16ArrayND(int... sizes)
	{
		super(sizes);
	}


	// =============================================================
	// Implementation of the RGB16Array interface



    // =============================================================
    // Implementation of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public UInt16ArrayND channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<UInt16ArrayND> channels()
    {
        return new Iterable<UInt16ArrayND>()
                {
                    @Override
                    public java.util.Iterator<UInt16ArrayND> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }

    @Override
    public double getValue(int[] pos, int channel)
    {
        return getSamples(pos)[channel];
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        int[] samples = getSamples(pos);
        samples[channel] = UInt16.clamp(value);
        setSamples(pos, samples);
    }

    public java.util.Iterator<UInt16ArrayND> channelIterator()
    {
        return new ChannelIterator();
    }


	// =============================================================
	// Inner class implementations

    private class ChannelView extends UInt16ArrayND
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB16ArrayND.this.sizes);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public short getShort(int... pos)
        {
            return (short) RGB16ArrayND.this.get(pos).getSample(channel);
        }

        @Override
        public void setShort(int[] pos, short value)
        {
            int[] samples = RGB16ArrayND.this.getSamples(pos);
            samples[channel] = value & 0x00FFFF;
            RGB16ArrayND.this.setSamples(pos, samples);
        }

        @Override
        public net.sci.array.scalar.UInt16Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.scalar.UInt16Array.Iterator
        {
            int[] pos;
            int nd;
            
            public Iterator()
            {
                this.nd = sizes.length;
                this.pos = new int[this.nd];
                for (int d = 0; d < this.nd - 1; d++)
                {
                    this.pos[d] = sizes[d] - 1;
                }
                this.pos[this.nd - 2] = -1;
            }

            @Override
            public UInt16 next()
            {
                forward();
                return get();
            }

            @Override
            public void forward()
            {
                incrementDim(0);
            }
            
            private void incrementDim(int d)
            {
                this.pos[d]++;
                if (this.pos[d] == sizes[d] && d < nd - 1)
                {
                    this.pos[d] = 0;
                    incrementDim(d + 1);
                }
            }

            @Override
            public boolean hasNext()
            {
                for (int d = 0; d < nd; d++)
                {
                    if (this.pos[d] < sizes[d] - 1)
                        return true;
                }
                return false;
            }

            @Override
            public short getShort()
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                return (short) RGB16ArrayND.this.get(pos).getSample(channel);
            }

            @Override
            public void setShort(short byteValue)
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                int[] samples = RGB16ArrayND.this.get(pos).getSamples();
                samples[channel] = byteValue & 0x00FFFF;
                RGB16ArrayND.this.setSamples(pos, samples);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt16ArrayND> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt16ArrayND next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
}
