/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32Array;

/**
 * Multi-dimensional implementation of arrays of vector that contains 32-bits
 * floating point values.
 * 
 * @author dlegland
 *
 */
public abstract class Float32VectorArrayND extends VectorArrayND<Float32Vector, Float32> implements Float32VectorArray
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
    // Implementation of VectorArray interface

    public Iterable<Float32Array> channels()
    {
        return new Iterable<Float32Array>()
        {
            @Override
            public java.util.Iterator<Float32Array> iterator()
            {
                return new ChannelIterator();
            }
        };
    }
    
    public java.util.Iterator<Float32Array> channelIterator()
    {
        return new ChannelIterator();
    }

    private class ChannelIterator implements java.util.Iterator<Float32Array> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelCount();
        }

        @Override
        public Float32Array next()
        {
            return new ChannelView(Float32VectorArrayND.this, channel++);
        }
    }
}
