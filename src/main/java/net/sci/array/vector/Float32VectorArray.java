/**
 * 
 */
package net.sci.array.vector;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.scalar.Float32;
import net.sci.array.scalar.Float32Array;

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

    public static final Array.Factory<Float32Vector> factory = new DefaultFactory()
    {
        @Override
        public Float32VectorArray create(int[] dims, Float32Vector value)
        {
            Float32VectorArray array = Float32VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }
    };
    

    // =============================================================
    // Static methods

    public static Float32VectorArray create(int[] dims, int sizeV)
    {
        switch (dims.length)
        {
            case 2:
                return Float32VectorArray2D.create(dims[0], dims[1], sizeV);
            case 3:
                return Float32VectorArray3D.create(dims[0], dims[1], dims[2], sizeV);
            default:
                return Float32VectorArrayND.create(dims, sizeV);
        }
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
    public Float32Array channel(int channel);

    public Iterable<? extends Float32Array> channels();

    public java.util.Iterator<? extends Float32Array> channelIterator();
    

    // =============================================================
    // Specialization of Array interface

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
    public default Array.Factory<Float32Vector> factory()
    {
        return factory;
    }

    @Override
    public default Float32VectorArray duplicate()
    {
        // create output array
        Float32VectorArray result = Float32VectorArray.create(this.size(), this.channelCount());

        // initialize iterators
        Array.PositionIterator iter1 = this.positionIterator();
        Array.PositionIterator iter2 = result.positionIterator();

        // copy values into output array
        while (iter1.hasNext())
        {
            result.setValues(iter2.next(), this.getValues(iter1.next()));
        }

        // return output
        return result;
    }

    @Override
    public default Class<Float32Vector> dataType()
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
    public interface Factory extends VectorArray.Factory<Float32Vector>
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
    }

    /**
     * The default factory for generation of Float32VectorArray instances.
     */
    public static class DefaultFactory extends AlgoStub implements Factory
    {
        @Override
        public Float32VectorArray create(int[] dims, Float32Vector value)
        {
            Float32VectorArray array = Float32VectorArray.create(dims, value.size());
            array.fill(value);
            return array;
        }
    };
}
