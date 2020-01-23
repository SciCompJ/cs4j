/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32Array2D;

/**
 * @author dlegland
 *
 */
public abstract class Float32VectorArray2D extends VectorArray2D<Float32Vector> implements Float32VectorArray
{
	// =============================================================
	// Static methods

	public static final Float32VectorArray2D create(int size0, int size1, int sizeV)
	{
		return new BufferedFloat32VectorArray2D(size0, size1, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float32VectorArray2D(int size0, int size1)
	{
		super(size0, size1);
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
    public Float32Array2D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public Iterable<Float32Array2D> channels()
    {
        return new Iterable<Float32Array2D>()
                {
                    @Override
                    public java.util.Iterator<Float32Array2D> iterator()
                    {
                        return new ChannelIterator();
                    }
                };
    }

    public java.util.Iterator<Float32Array2D> channelIterator()
    {
        return new ChannelIterator();
    }

	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1]);
	}
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], values);
    }

	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], values);
	}
	

	// =============================================================
	// Specialization of Array interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract Float32VectorArray2D duplicate();

    private class ChannelView extends Float32Array2D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float32VectorArray2D.this.size0, Float32VectorArray2D.this.size1);
            int nChannels = Float32VectorArray2D.this.channelNumber();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public net.sci.array.scalar.Float32Array.Iterator iterator()
        {
            return new Iterator();
        }

        @Override
        public double getValue(int... pos)
        {
            return Float32VectorArray2D.this.getValue(pos[0], pos[1], channel);
        }

        @Override
        public void setValue(double value, int... pos)
        {
            Float32VectorArray2D.this.setValue(pos[0], pos[1], channel, value);
        }
        
        class Iterator implements net.sci.array.scalar.Float32Array.Iterator
        {
            int indX = -1;
            int indY = 0;

            @Override
            public Float32 next()
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
                return Float32VectorArray2D.this.getValue(indX, indY, channel);
            }

            @Override
            public void setValue(double value)
            {
                Float32VectorArray2D.this.setValue(indX, indY, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1;
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<Float32Array2D> 
    {
        int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelNumber();
        }

        @Override
        public Float32Array2D next()
        {
            return new ChannelView(channel++);
        }
        
    }
	
}
