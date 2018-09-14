/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class Float32VectorArrayND extends VectorArrayND<Float32Vector> implements Float32VectorArray
{
	// =============================================================
	// Static methods

	public static final Float32VectorArrayND create(int[] sizes, int sizeV)
	{
		return new BufferedFloat32VectorArrayND(sizes, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float32VectorArrayND(int[] sizes)
	{
		super(sizes);
	}
	
    // =============================================================
    // New methods

    public Iterable<Float32ArrayND> channels()
    {
        return new Iterable<Float32ArrayND>()
                {
                    @Override
                    public java.util.Iterator<Float32ArrayND> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }
    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public Float32ArrayND channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public java.util.Iterator<Float32ArrayND> channelIterator()
    {
        return new ChannelIterator();
    }


    private class ChannelView extends Float32ArrayND
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float32VectorArrayND.this.sizes);
            int nChannels = Float32VectorArrayND.this.getVectorLength();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public Float32 get(int[] pos)
        {
            return Float32VectorArrayND.this.get(pos).get(channel);
        }

        @Override
        public void set(int[] pos, Float32 value)
        {
            Float32Vector vect = Float32VectorArrayND.this.get(pos);
            vect.data[channel] = value.getFloat();
            Float32VectorArrayND.this.set(pos, vect);
        }

        @Override
        public double getValue(int[] pos)
        {
            return Float32VectorArrayND.this.get(pos).getValue(channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float32Vector vect = Float32VectorArrayND.this.get(pos);
            vect.data[channel] = (float) value;
            Float32VectorArrayND.this.set(pos, vect);
        }

        @Override
        public net.sci.array.scalar.Float32Array.Iterator iterator()
        {
            return new Iterator();
        }
        
        class Iterator implements net.sci.array.scalar.Float32Array.Iterator
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
            public Float32 next()
            {
                forward();
                return get();
            }
            
            @Override
            public double getValue()
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                return Float32VectorArrayND.this.get(pos).getValue(channel);
            }

            @Override
            public void setValue(double value)
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                Float32Vector vect = Float32VectorArrayND.this.get(pos);
                vect.data[channel] = (float) value;
                Float32VectorArrayND.this.set(pos, vect);
            }
        }

    }
    
    private class ChannelIterator implements java.util.Iterator<Float32ArrayND> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < getVectorLength();
        }

        @Override
        public Float32ArrayND next()
        {
            channel++;
            return new ChannelView(channel);
        }
    }
}
