/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array;

/**
 * Multi-dimensional implementation of arrays of vector that contains double
 * values.
 * 
 * @author dlegland
 *
 */
public abstract class Float64VectorArrayND extends VectorArrayND<Float64Vector,Float64> implements Float64VectorArray
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
    
    
    // =============================================================
    // Implementation of Float64VectorArray methods

	public Iterable<Float64Array> channels()
    {
        return new Iterable<Float64Array>()
        {
            @Override
            public java.util.Iterator<Float64Array> iterator()
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
    public Float64Array channel(int channel)
    {
        return new ChannelView(this, channel);
    }
    
    public java.util.Iterator<Float64Array> channelIterator()
    {
        return new ChannelIterator();
    }

    private class ChannelIterator implements java.util.Iterator<Float64Array> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelCount();
        }

        @Override
        public Float64Array next()
        {
            return new ChannelView(Float64VectorArrayND.this, channel++);
        }
    }
}
