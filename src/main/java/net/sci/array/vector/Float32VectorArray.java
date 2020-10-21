/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains 32-bits floating point values.
 * 
 * @author dlegland
 *
 */
public interface Float32VectorArray extends VectorArray<Float32Vector>
{
    // =============================================================
    // Static variables

    public static final Array.Factory<Float32Vector> factory = new Array.Factory<Float32Vector>()
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
    public default Float32Vector get(int... pos)
    {
        return new Float32Vector(getValues(pos, new double[channelNumber()]));
    }

    @Override
    public default void set(int[] pos, Float32Vector vect)
    {
        setValues(pos, vect.getValues());
    }

	@Override
	public default Float32VectorArray newInstance(int... dims)
	{
		return Float32VectorArray.create(dims, this.channelNumber());
	}

	@Override
	public default Array.Factory<Float32Vector> getFactory()
	{
		return factory;
	}

	@Override
	public default Float32VectorArray duplicate()
	{
		// create output array
		Float32VectorArray result = Float32VectorArray.create(this.size(), this.channelNumber());

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
	public default Class<Float32Vector> dataType()
	{
		return Float32Vector.class;
	}

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<Float32Vector>
	{
	}
}
