/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8ArrayND;
import net.sci.array.numeric.VectorArrayND;

/**
 * @author dlegland
 *
 */
public abstract class RGB8ArrayND extends VectorArrayND<RGB8,UInt8> implements RGB8Array
{
	// =============================================================
	// Static methods

	public static final RGB8ArrayND create(int...sizes)
	{
		return new Int32EncodedRGB8ArrayND(sizes);
	}
	

	// =============================================================
	// Constructor

	protected RGB8ArrayND(int... sizes)
	{
		super(sizes);
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
    public UInt8ArrayND channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<UInt8ArrayND> channels()
    {
        return new Iterable<UInt8ArrayND>()
                {
                    @Override
                    public java.util.Iterator<UInt8ArrayND> iterator()
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
        samples[channel] = UInt8.convert(value);
        setSamples(pos, samples);
    }

    public java.util.Iterator<UInt8ArrayND> channelIterator()
    {
        return new ChannelIterator();
    }


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB8ArrayND duplicate();
	
    private class ChannelView extends UInt8ArrayND
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(RGB8ArrayND.this.sizes);
            int nChannels = 3;
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public byte getByte(int[] pos)
        {
            return (byte) RGB8ArrayND.this.get(pos).getSample(channel);
        }

        @Override
        public void setByte(int[] pos, byte value)
        {
            int[] samples = RGB8ArrayND.this.getSamples(pos);
            samples[channel] = value & 0x00FF;
            RGB8ArrayND.this.setSamples(pos, samples);
        }

        @Override
        public net.sci.array.numeric.UInt8Array.Iterator iterator()
        {
            return new Iterator();
        }

        class Iterator implements net.sci.array.numeric.UInt8Array.Iterator
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
            public UInt8 next()
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
            public byte getByte()
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                return (byte) RGB8ArrayND.this.get(pos).getSample(channel);
            }

            @Override
            public void setByte(byte byteValue)
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                int[] samples = RGB8ArrayND.this.get(pos).getSamples();
                samples[channel] = byteValue & 0x00FF;
                RGB8ArrayND.this.setSamples(pos, samples);
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<UInt8ArrayND> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < 2;
        }

        @Override
        public UInt8ArrayND next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
}
