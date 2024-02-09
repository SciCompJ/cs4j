/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32Array2D;

/**
 * Specialization of the interface VectorArray for 2D arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public abstract class Float32VectorArray2D extends VectorArray2D<Float32Vector, Float32> implements Float32VectorArray
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
    // Declaration of new methods
    
    /**
     * Returns the float value at the specified position and for the specified
     * channel.
     * 
     * @param x
     *            the x-coordinate of the element
     * @param y
     *            the y-coordinate of the element
     * @param channel
     *            the channel index
     * @return the channel value at the (x,y) position
     */
    public abstract float getFloat(int x, int y, int channel);

    /**
     * Changes the float value at the specified position and for the specified
     * channel.
     * 
     * @param x
     *            the x-coordinate of the element
     * @param y
     *            the y-coordinate of the element
     * @param channel
     *            the channel index
     * @param f the new value of the specified channel at the (x,y) position
     */
    public abstract void setFloat(int x, int y, int channel, float f);
    

    // =============================================================
    // Implementation of VectorArray interface

    @Override
    public float getFloat(int[] pos, int channel)
    {
        return getFloat(pos[0], pos[1], channel);
    }

    @Override
    public void setFloat(int[] pos, int channel, float value)
    {
        setFloat(pos[0], pos[1], channel, value);
    }
    
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


    @Override
    public Float32Vector get(int x, int y)
    {
        return new Float32Vector(getValues(x, y));
    }

    @Override
    public void set(int x, int y, Float32Vector vect)
    {
        setValues(x, y, vect.getValues());
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
            int nChannels = Float32VectorArray2D.this.channelCount();
            if (channel < 0 || channel >= nChannels)
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, nChannels));
            }
            this.channel = channel;
        }

        @Override
        public float getFloat(int x, int y)
        {
            return Float32VectorArray2D.this.getFloat(x, y, channel);
        }
        
        @Override
        public void setFloat(int x, int y, float f)
        {
            Float32VectorArray2D.this.setFloat(x, y, channel, f);
        }
        
        @Override
        public void setValue(int x, int y, double value)
        {
            Float32VectorArray2D.this.setValue(x, y, channel, value);
        }
        
        @Override
        public void set(int x, int y, Float32 value)
        {
            Float32VectorArray2D.this.setFloat(x, y, channel, value.getFloat());
        }

        @Override
        public double getValue(int[] pos)
        {
            return Float32VectorArray2D.this.getValue(pos[0], pos[1], channel);
        }

        @Override
        public net.sci.array.scalar.Float32Array.Iterator iterator()
        {
            return new Iterator();
        }

        @Override
        public void setValue(int[] pos, double value)
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
            public float getFloat()
            {
                return Float32VectorArray2D.this.getFloat(indX, indY, channel);
            }

            @Override
            public void setFloat(float value)
            {
                Float32VectorArray2D.this.setFloat(indX, indY, channel, value);
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
            return channel < channelCount();
        }

        @Override
        public Float32Array2D next()
        {
            return new ChannelView(channel++);
        }
    }
}
