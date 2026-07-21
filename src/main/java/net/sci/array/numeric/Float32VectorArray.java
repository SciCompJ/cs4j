/**
 * 
 */
package net.sci.array.numeric;

import java.util.Collection;
import java.util.List;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.PositionIterator;
import net.sci.array.impl.ArrayWrapperStub;
import net.sci.array.numeric.impl.BufferedFloat32VectorArrayND;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public interface Float32VectorArray extends VectorArray<Float32Vector, Float32>
{
    // =============================================================
    // Static variables

    /**
     * The default factory for creating multi-dimensional arrays containing
     * vectors of {@code Float32} values.
     */
    public static final Factory defaultFactory = new Factory()
    {
        @Override
        public Float32VectorArray create(int[] dims, Float32Vector value)
        {
            Float32VectorArray array = Float32VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }

        @Override
        public Float32VectorArray create(int[] dims, int nComponents)
        {
            return Float32VectorArray.create(dims, nComponents);
        }
    };
    

    // =============================================================
    // Static methods

    /**
     * Creates a new multi-dimensianal array containing vectors of Float32 values.
     * 
     * @param dims
     *            the size of the array
     * @param sizeV
     *            the size of the vectors
     * @return a new instance of Float32VectorArray
     */
    public static Float32VectorArray create(int[] dims, int sizeV)
    {
        return switch (dims.length)
        {
            case 2 -> Float32VectorArray2D.create(dims[0], dims[1], sizeV);
            case 3 -> Float32VectorArray3D.create(dims[0], dims[1], dims[2], sizeV);
            default -> new BufferedFloat32VectorArrayND(dims, sizeV);
        };
    }
    
    /**
     * Encapsulates the specified array into a new Float32VectorArray, by
     * creating a Wrapper if necessary. If the original array is already an
     * instance of Float32VectorArray, it is returned.
     * 
     * @param array
     *            the original array
     * @return a Float32VectorArray view of the original array
     */
    @SuppressWarnings("unchecked")
    public static Float32VectorArray wrap(Array<?> array)
    {
        if (array instanceof Float32VectorArray)
        {
            return (Float32VectorArray) array;
        }
        if (Float32Vector.class.isAssignableFrom(array.elementClass()))
        {
            return new Wrapper((Array<Float32Vector>) array);
        }
        throw new IllegalArgumentException("Can not wrap an array with class " + array.getClass() + " and type " + array.elementClass());
    }
    
    
    // =============================================================
    // New methods

    /**
     * Retrieves the vector element value for the specified position and channel
     * index.
     * 
     * @param pos
     *            the array of coordinate indices of the position
     * @param channel
     *            the channel index
     * @return the vector element value
     */
    public float getFloat(int[] pos, int channel);
    
    /**
     * Updates the vector element value for the specified position and channel
     * index.
     * 
     * @param pos
     *            the array of coordinate indices of the position
     * @param channel
     *            the channel index
     * @param value
     *            the new value
     */
    public void setFloat(int[] pos, int channel, float value);
    
    
    // =============================================================
    // Specialization of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public default Float32Array channel(int channel)
    {
        return new ChannelView(this, channel);
    }

    public Iterable<? extends Float32Array> channels();

    public java.util.Iterator<? extends Float32Array> channelIterator();
    

    // =============================================================
    // Specialization of Array interface

    /**
     * Override default behavior of Array interface to return a Float32Vector
     * element.
     * 
     * @return a Float32Vector instance with as many elements as the number of
     *         channels.
     */
    @Override
    public default Float32Vector sampleElement()
    {
        return new Float32Vector(channelCount());
    }
    
    @Override
    public default Float32Vector get(int[] pos)
    {
        return new Float32Vector(getValues(pos, new double[channelCount()]));
    }

    @Override
    public default void set(int[] pos, Float32Vector vect)
    {
        setValues(pos, vect.getValues());
    }

    @Override
    public default Float32VectorArray newInstance(int... dims)
    {
        return Float32VectorArray.create(dims, this.channelCount());
    }

    @Override
    public default Factory factory()
    {
        return defaultFactory;
    }

    @Override
    public default Float32VectorArray duplicate()
    {
        // create output array
        Float32VectorArray result = Float32VectorArray.create(this.size(), this.channelCount());

        for (int[] pos : result.positions())
        {
            result.setValues(pos, this.getValues(pos));
        }
        
        // return output
        return result;
    }

    @Override
    public default Class<Float32Vector> elementClass()
    {
        return Float32Vector.class;
    }

    /**
     * Creates a default iterator over the <code>Float32Vector</code> elements
     * stored within this array. Default iterator is based on position iterator.
     * 
     * @return an iterator of the elements stored in the array.
     */
	public default Iterator iterator()
    {
        return new Iterator()
        {
            PositionIterator iter = PositionIterator.of(Float32VectorArray.this);

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
            public Float32Vector next()
            {
                iter.forward();
                return Float32VectorArray.this.get(iter.get());
            }

            @Override
            public double getValue(int c)
            {
                return Float32VectorArray.this.getValue(iter.get(), c);
            }
            
            @Override
            public void setValue(int c, double value)
            {
                Float32VectorArray.this.setValue(iter.get(), c, value);
            }
            
            @Override
            public Float32Vector get()
            {
                return Float32VectorArray.this.get(iter.get());
            }
            
            @Override
            public void set(Float32Vector value)
            {
                Float32VectorArray.this.set(iter.get(), value);
            }
        };
    }
	
	
    // =============================================================
    // Inner interface

    /**
     * An interface for iterating over the <code>Float32Vector</code> elements
     * stored within this array.
     */
    public interface Iterator extends VectorArray.Iterator<Float32Vector, Float32>
    {
    }
    

    // =============================================================
    // Specialization of the Factory interface

    /**
     * Specialization of the ArrayFactory for generating instances of
     * Float32VectorArray.
     */
    public interface Factory extends VectorArray.Factory<Float32Vector, Float32>
    {
        /**
         * Creates a new Float32VectorArray with the specified dimensions, filled with
         * the specified initial value.
         * 
         * @param dims
         *            the dimensions of the array to be created
         * @param value
         *            an instance of the initial integer value
         * @return a new instance of Float32VectorArray
         */
        public Float32VectorArray create(int[] dims, Float32Vector value);
        
        @Override
        public Float32VectorArray create(int[] dims, int nComponents);
        
    }

    /**
     * The default factory for generation of Float32VectorArray instances.
     */
    public static class DefaultFactory extends AlgoStub implements VectorArray.Factory<Float32Vector, Float32>
    {
        @Override
        public Float32VectorArray create(int[] dims, Float32Vector value)
        {
            Float32VectorArray array = Float32VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }

        @Override
        public VectorArray<Float32Vector, Float32> create(int[] dims, int nComponents)
        {
            return Float32VectorArray.create(dims, nComponents);
        }
        
        // private constructor
        private DefaultFactory(){}
    };
    
    /**
     * Wraps an array containing <code>Float32Vector</code> instances into an
     * explicit instance of <code>Float32VectorArray</code>.
     * 
     * Assumes all vectors have the same size and that the array is not empty.
     */
    static class Wrapper extends ArrayWrapperStub<Float32Vector> implements Float32VectorArray
    {
        Array<Float32Vector> array;
        int channelCount;
        
        /**
         * Creates a new {@code Float32VectorArray} wrapper from the specified array.
         * 
         * @param array
         *            the array to wrap.
         */
        public Wrapper(Array<Float32Vector> array)
        {
            super(array);
            this.array = array;
            this.channelCount = array.iterator().next().size();
        }

        @Override
        public int channelCount()
        {
            return channelCount;
        }

        @Override
        public float getFloat(int[] pos, int channel)
        {
            return array.get(pos).getFloat(channel);
        }

        @Override
        public void setFloat(int[] pos, int channel, float value)
        {
            float[] values = array.get(pos).getFloats();
            values[channel] = value;
            array.set(pos, new Float32Vector(values));
        }

        @Override
        public Iterable<Float32Array> channels()
        {
            return new Iterable<Float32Array>()
            {
                @Override
                public java.util.Iterator<Float32Array> iterator()
                {
                    return channelIterator();
                }
            };
        }

        @Override
        public java.util.Iterator<Float32Array> channelIterator()
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
            array.set(pos, new Float32Vector(values));
        }

        @Override
        public double getValue(int[] pos, int channel)
        {
            return array.get(pos).getValue(channel);
        }

        @Override
        public void setValue(int[] pos, int channel, double value)
        {
            float[] values = array.get(pos).getFloats();
            values[channel] = (float) value;
            array.set(pos, new Float32Vector(values));
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
                return new ChannelView(Float32VectorArray.Wrapper.this, channel++);
            }
        }
    }
    
    
    /**
     * Utility class that implements a view on a channel of a
     * <code>Float32VectorArray</code> array as a an instance of
     * <code>Float32Array</code>.
     * 
     * @see Float32VectorArray#channel(int)
     * @see Float32VectorArray#channelIterator()
     */
    static class ChannelView implements Float32Array, Array.View<Float32>
    {
        Float32VectorArray array;
        int channel;
        
        /**
         * Creates a view on the specified channel of the specified array.
         * 
         * @param array
         *            the array to retrieve the values from
         * @param channel
         *            the channel index within the array
         */
        public ChannelView(Float32VectorArray array, int channel)
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
        public float getFloat(int[] pos)
        {
            return array.getFloat(pos, channel);
        }

        @Override
        public void setFloat(int[] pos, float value)
        {
            array.setFloat(pos, channel, value);
        }

        @Override
        public double getValue(int[] pos)
        {
            return array.getValue(pos, channel);
        }

        @Override
        public void setValue(int[] pos, double value)
        {
            array.setFloat(pos, channel, (float) value);
        }

        @Override
        public Collection<Array<?>> parentArrays()
        {
            return List.of(array);
        }

        @Override
        public Float32 get(int[] pos)
        {
            return array.get(pos).get(channel);
        }

        @Override
        public void set(int[] pos, Float32 value)
        {
            array.setFloat(pos, channel, value.floatValue());
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
