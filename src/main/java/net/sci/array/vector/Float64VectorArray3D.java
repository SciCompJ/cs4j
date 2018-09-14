/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array3D;

/**
 * @author dlegland
 *
 */
public abstract class Float64VectorArray3D extends VectorArray3D<Float64Vector> implements Float64VectorArray
{
	// =============================================================
	// Static methods

	public static final Float64VectorArray3D create(int size0, int size1, int size2, int sizeV)
	{
		return new BufferedFloat64VectorArray3D(size0, size1, size2, sizeV);
	}
	
	// =============================================================
	// Constructors

	protected Float64VectorArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
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
    public Float64Array3D channel(int channel)
    {
        return new ChannelView(channel);
    }
    
    public java.util.Iterator<Float64Array3D> channelIterator()
    {
        return new ChannelIterator();
    }

	public abstract double[] getValues(int x, int y, int z);
	
	public abstract void setValues(int x, int y, int z, double[] values);
	
	/**
	 * Returns the scalar value for the specified position and the specified
	 * component.
	 * 
	 * @param x
	 *            the x-position of the vector
	 * @param y
	 *            the y-position of the vector
	 * @param z
	 *            the z-position of the vector
	 * @param c
	 *            the component to investigate
	 * @return the value of the given component at the given position
	 */
	public abstract double getValue(int x, int y, int z, int c);
	
	public abstract void setValue(int x, int y, int z, int c, double value);


	// =============================================================
	// Specialization of VectorArray interface
	
	public double[] getValues(int[] pos)
	{
		return getValues(pos[0], pos[1], pos[2]);
	}
	
    @Override
    public double[] getValues(int[] pos, double[] values)
    {
        return getValues(pos[0], pos[1], pos[2], values);
    }

	public void setValues(int[] pos, double[] values)
	{
		setValues(pos[0], pos[1], pos[2], values);
	}
	

	// =============================================================
	// Specialization of Array interface


	/* (non-Javadoc)
	 * @see net.sci.array.data.VectorArray#duplicate()
	 */
	@Override
	public abstract Float64VectorArray3D duplicate();


    private class ChannelView extends Float64Array3D
    {
        int channel;
        
        protected ChannelView(int channel)
        {
            super(Float64VectorArray3D.this.size0, Float64VectorArray3D.this.size1, Float64VectorArray3D.this.size2);
            int nChannels = Float64VectorArray3D.this.getVectorLength();
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
        public double getValue(int x, int y, int z)
        {
            return Float64VectorArray3D.this.getValue(x, y, z, channel);
        }

        @Override
        public void setValue(int x, int y, int z, double value)
        {
            Float64VectorArray3D.this.setValue(x, y, z, channel, value);
        }
        
        class Iterator implements net.sci.array.scalar.Float64Array.Iterator
        {
            int indX = -1;
            int indY = 0;
            int indZ = 0;

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
                    if (indY >= size1)
                    {
                        indY = 0;
                        indZ++;
                    }
                }
            }

            @Override
            public double getValue()
            {
                return Float64VectorArray3D.this.getValue(indX, indY, indZ, channel);
            }

            @Override
            public void setValue(double value)
            {
                Float64VectorArray3D.this.setValue(indX, indY, indZ, channel, value);
            }

            @Override
            public boolean hasNext()
            {
                return indX < size0 - 1 && indY < size1 - 1 && indZ < size2 - 1;
            }     
        }
    }
    
    private class ChannelIterator implements java.util.Iterator<Float64Array3D> 
    {
        int channel = -1;

        @Override
        public boolean hasNext()
        {
            return channel < getVectorLength();
        }

        @Override
        public Float64Array3D next()
        {
            channel++;
            return new ChannelView(channel);
        }
        
    }
}
