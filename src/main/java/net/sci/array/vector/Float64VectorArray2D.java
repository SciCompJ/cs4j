/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array2D;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArray2D extends VectorArray2D<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArray2D create(int size0, int size1, int sizeV)
	{
		return new BufferedFloat64VectorArray2D(size0, size1, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArray2D(int size0, int size1)
	{
		super(size0, size1);
	}
	
	// =============================================================
	// New methods

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
	public Float64Array2D channel(int channel)
	{
	    return new ChannelView(channel);
	}
	
	public Iterable<Float64Array2D> channels()
	{
	    return new Iterable<Float64Array2D>()
	    {
	        @Override
	        public java.util.Iterator<Float64Array2D> iterator()
	        {
	            return new ChannelIterator();
	        }
	    };
	}

	public java.util.Iterator<Float64Array2D> channelIterator()
	{
	    return new ChannelIterator();
	}
	
	public abstract double[] getValues(int x, int y);
	
	public abstract void setValues(int x, int y, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int c);
	
	public abstract void setValue(int x, int y, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
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
	public abstract Float64VectorArray2D duplicate();

	private class ChannelView extends Float64Array2D
	{
	    int channel;
	    
        protected ChannelView(int channel)
        {
            super(Float64VectorArray2D.this.size0, Float64VectorArray2D.this.size1);
            int nChannels = Float64VectorArray2D.this.channelNumber();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public net.sci.array.scalar.Float64Array.Iterator iterator()
        {
            return new Iterator();
        }

        @Override
        public double getValue(int x, int y)
        {
            return Float64VectorArray2D.this.getValue(x, y, channel);
        }

        @Override
        public void setValue(int x, int y, double value)
        {
            Float64VectorArray2D.this.setValue(x, y, channel, value);
        }
        
        class Iterator implements net.sci.array.scalar.Float64Array.Iterator
        {
            int indX = -1;
            int indY = 0;

            @Override
            public Float64 next()
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
                return Float64VectorArray2D.this.getValue(indX, indY, channel);
            }

            @Override
            public void setValue(double value)
            {
                Float64VectorArray2D.this.setValue(indX, indY, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 || indY < size1 - 1;
            }     
        }
	}
	
	private class ChannelIterator implements java.util.Iterator<Float64Array2D> 
	{
	    int channel = 0;

        @Override
        public boolean hasNext()
        {
            return channel < channelNumber();
        }

        @Override
        public Float64Array2D next()
        {
            return new ChannelView(channel++);
        }
	    
	}
}
