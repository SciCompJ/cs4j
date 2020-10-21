/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64ArrayND;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArrayND extends VectorArrayND<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArrayND create(int[] sizes, int sizeV)
	{
		return new BufferedFloat64VectorArrayND(sizes, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArrayND(int[] sizes)
	{
		super(sizes);
	}
	
	public Iterable<Float64ArrayND> channels()
    {
        return new Iterable<Float64ArrayND>()
        {
            @Override
            public java.util.Iterator<Float64ArrayND> iterator()
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
    public Float64ArrayND channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public java.util.Iterator<Float64ArrayND> channelIterator()
    {
        return new ChannelIterator();
    }


    private class ChannelView extends Float64ArrayND
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float64VectorArrayND.this.sizes);
            int nChannels = Float64VectorArrayND.this.channelNumber();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public Float64 get(int... pos)
        {
            return Float64VectorArrayND.this.get(pos).get(channel);
        }

        @Override
        public void set(int[] pos, Float64 value)
        {
            Float64Vector vect = Float64VectorArrayND.this.get(pos);
            vect.data[channel] = value.getValue();
            Float64VectorArrayND.this.set(pos, vect);
        }

        @Override
        public double getValue(int... pos)
        {
            return Float64VectorArrayND.this.get(pos).getValue(channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            Float64Vector vect = Float64VectorArrayND.this.get(pos);
            vect.data[channel] = value;
            Float64VectorArrayND.this.set(pos, vect);
        }

        @Override
        public net.sci.array.scalar.Float64Array.Iterator iterator()
        {
            return new Iterator();
        }
        
        class Iterator implements net.sci.array.scalar.Float64Array.Iterator
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
            public Float64 next()
            {
                forward();
                return get();
            }
            
            @Override
            public double getValue()
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                return Float64VectorArrayND.this.get(pos).getValue(channel);
            }

            @Override
            public void setValue(double value)
            {
                int[] res = new int[nd];
                System.arraycopy(this.pos, 0, res, 0, nd);
                Float64Vector vect = Float64VectorArrayND.this.get(pos);
                vect.data[channel] = value;
                Float64VectorArrayND.this.set(pos, vect);
            }
        }

    }
    
    private class ChannelIterator implements java.util.Iterator<Float64ArrayND> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelNumber();
        }

        @Override
        public Float64ArrayND next()
        {
            return new ChannelView(channel++);
        }
        
    }
}
