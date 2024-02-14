/**
 * 
 */
package net.sci.array.vector;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains double values.
 * 
 * @author dlegland
 *
 */
public interface Float64VectorArray extends VectorArray<Float64Vector, Float64>
{
    // =============================================================
    // Static variables

    public static final Array.Factory<Float64Vector> factory = new DefaultFactory()
    {
        @Override
        public Float64VectorArray create(int[] dims, Float64Vector value)
        {
            Float64VectorArray array = Float64VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }
    };
    

    // =============================================================
    // Static methods

    public static Float64VectorArray create(int[] dims, int sizeV)
    {
        switch (dims.length)
        {
            case 2:
                return Float64VectorArray2D.create(dims[0], dims[1], sizeV);
            case 3:
                return Float64VectorArray3D.create(dims[0], dims[1], dims[2], sizeV);
            default:
                return Float64VectorArrayND.create(dims, sizeV);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static Float64VectorArray wrap(Array<?> array)
    {
        if (array instanceof Float64VectorArray)
        {
            return (Float64VectorArray) array;
        }
        if (Float64Vector.class.isAssignableFrom(array.dataType()))
        {
            return new Wrapper((Array<Float64Vector>) array);
        }
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.dataType());
    }
    

    // =============================================================
    // Specialization of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public default Float64Array channel(int channel)
    {
        return new ChannelView(this, channel);
    }

    public Iterable<? extends Float64Array> channels();

    public java.util.Iterator<? extends Float64Array> channelIterator();
    

    // =============================================================
    // Specialization of Array interface

    @Override
    public default Float64Vector get(int[] pos)
    {
        return new Float64Vector(getValues(pos, new double[channelCount()]));
    }

    @Override
    public default void set(int[] pos, Float64Vector vect)
    {
        setValues(pos, vect.getValues());
    }

    @Override
    public default Float64VectorArray newInstance(int... dims)
    {
        return Float64VectorArray.create(dims, this.channelCount());
    }

    @Override
    public default Array.Factory<Float64Vector> factory()
    {
        return factory;
    }

    @Override
    public default Float64VectorArray duplicate()
    {
        // create output array
        Float64VectorArray result = Float64VectorArray.create(this.size(), this.channelCount());

        // initialize iterators
        Array.PositionIterator iter1 = this.positionIterator();
        Array.PositionIterator iter2 = result.positionIterator();
        
        // copy values into output array
        while(iter1.hasNext())
        {
            result.setValues(iter2.next(), this.getValues(iter1.next()));
        }
        
        // return output
        return result;
	}

	@Override
	public default Class<Float64Vector> dataType()
	{
		return Float64Vector.class;
	}


    /**
     * Creates a default iterator over the <code>Float64Vector</code> elements
     * stored within this array.  
     * Default iterator is based on position iterator.
     * 
     * @return an iterator of the elements stored in the array.
     */
    public default Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = positionIterator();

            @Override
            public boolean hasNext()
            {
                return iter.hasNext();
            }

            @Override
            public void forward()
            {
                iter.forward();
            }

            @Override
            public Float64Vector next()
            {
                iter.forward();
                return Float64VectorArray.this.get(iter.get());
            }

            @Override
            public double getValue(int c)
            {
                return Float64VectorArray.this.getValue(iter.get(), c);
            }
            
            @Override
            public void setValue(int c, double value)
            {
                Float64VectorArray.this.setValue(iter.get(), c, value);
            }
            
            @Override
            public Float64Vector get()
            {
                return Float64VectorArray.this.get(iter.get());
            }
            
            @Override
            public void set(Float64Vector value)
            {
                Float64VectorArray.this.set(iter.get(), value);
            }
        };
    }
    

    // =============================================================
    // Inner interface

    /**
     * An interface for iterating over the <code>Float64Vector</code> elements
     * stored within this array.
     */
    public interface Iterator extends VectorArray.Iterator<Float64Vector, Float64>
    {
    }
    
    
    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of Float64VectorArray.
     */
    public interface Factory extends VectorArray.Factory<Float64Vector>
    {
        /**
         * Creates a new Float64VectorArray with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of Float64VectorArray
         */
        public Float64VectorArray create(int[] dims, Float64Vector value);
    }

    /**
     * The default factory for generation of Float64VectorArray instances.
     */
    public static class DefaultFactory extends AlgoStub implements Factory
    {
        @Override
        public Float64VectorArray create(int[] dims, Float64Vector value)
        {
            Float64VectorArray array = Float64VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }
    };
    
    /**
     * Wraps an array containing <code>Float64Vector</code> instances into an
     * explicit instance of <code>Float64VectorArray</code>.
     * 
     * Assumes all vectors have the same size and that the array is not empty.
     */
    static class Wrapper implements Float64VectorArray
    {
        Array<Float64Vector> array;
        int channelCount;
        
        public Wrapper(Array<Float64Vector> array)
        {
            this.array = array;
            this.channelCount = array.iterator().next().size();
        }

        @Override
        public int channelCount()
        {
            return channelCount;
        }

        @Override
        public Iterable<Float64Array> channels()
        {
            return new Iterable<Float64Array>()
            {
                @Override
                public java.util.Iterator<Float64Array> iterator()
                {
                    return channelIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<Float64Array> channelIterator()
        {
            return new ChannelIterator();
        }

        @Override
        public double[] getValues(int[] pos)
        {
            return array.get(pos).getValues();
        }

        @Override
        public double[] getValues(int[] pos, double[] values)
        {
            return array.get(pos).getValues(values);
        }

        @Override
        public void setValues(int[] pos, double[] values)
        {
            array.set(pos, new Float64Vector(values));
        }

        @Override
        public double getValue(int[] pos, int channel)
        {
            return array.get(pos).getValue(channel);
        }

        @Override
        public void setValue(int[] pos, int channel, double value)
        {
            double[] values = array.get(pos).getValues();
            values[channel] = (float) value;
            array.set(pos, new Float64Vector(values));
        }

        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
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
                return new ChannelView(Float64VectorArray.Wrapper.this, channel++);
            }
        }
    }

    /**
     * Utility class that implements a view on a channel of a
     * <code>Float64VectorArray</code> array as a an instance of
     * <code>Float64Array</code>.
     *
     * @see Float64VectorArray#channel(int)
     * @see Float64VectorArray#channelIterator()
     */
    static class ChannelView implements Float64Array
    {
        Float64VectorArray array;
        int channel;
        
        public ChannelView(Float64VectorArray array, int channel)
        {
            if (channel < 0 || channel >= array.channelCount())
            {
                throw new IllegalArgumentException(String.format(
                        "Channel index %d must be comprised between 0 and %d", channel, array.channelCount()));
            }
            
            this.array = array;
            this.channel = channel;
        }

        @Override
        public Float64 get(int[] pos)
        {
            return array.get(pos).get(channel);
        }

        @Override
        public void set(int[] pos, Float64 value)
        {
            array.setValue(pos, channel, value.getValue());
        }

        @Override
        public double getValue(int[] pos)
        {
            return array.getValue(pos, channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.setValue(pos, channel, value);
        }

        @Override
        public int[] size()
        {
            return array.size();
        }

        @Override
        public int size(int dim)
        {
            return array.size(dim);
        }

        @Override
        public int dimensionality()
        {
            return array.dimensionality();
        }
    }
}
