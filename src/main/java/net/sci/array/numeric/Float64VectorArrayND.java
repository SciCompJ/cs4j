/**
 * 
 */
package net.sci.array.numeric;

import net.sci.array.numeric.impl.BufferedFloat64VectorArrayND;

/**
 * Multi-dimensional implementation of arrays of vector that contains double
 * values.
 * 
 * @author dlegland
 *
 */
@Deprecated
public abstract class Float64VectorArrayND extends VectorArrayND<Float64Vector,Float64> implements Float64VectorArray
{
    // =============================================================
    // Static methods
    
    @Deprecated
    /**
     * foo
     * @param sizes
     *            sd
     * @param sizeV
     *            dd
     * @return d
     */
    public static final Float64VectorArray create(int[] sizes, int sizeV)
    {
        return new BufferedFloat64VectorArrayND(sizes, sizeV);
    }
    
    
    // =============================================================
    // Constructors
    
    @Deprecated
    protected Float64VectorArrayND(int[] sizes)
    {
        super(sizes);
    }
    
    
    // =============================================================
    // Implementation of Float64VectorArray methods

    @Deprecated
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
    @Deprecated
    public Float64Array channel(int channel)
    {
        return new ChannelView(this, channel);
    }
    
    @Deprecated
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
