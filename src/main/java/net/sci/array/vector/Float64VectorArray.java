/**
 * 
 */
package net.sci.array.vector;

import net.sci.array.Array;
import net.sci.array.scalar.Float64Array;

/**
 * Specialization of the interface VectorArray for arrays of vectors that
 * contains double values.
 * 
 * @author dlegland
 *
 */
public interface Float64VectorArray extends VectorArray<Float64Vector>
{
    // =============================================================
    // Static variables

    public static final Array.Factory<Float64Vector> factory = new Array.Factory<Float64Vector>()
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

	
    // =============================================================
    // Specialization of VectorArray interface

    /**
     * Returns a view on the channel specified by the given index.
     * 
     * @param channel
     *            index of the channel to view
     * @return a view on the channel
     */
    public Float64Array channel(int channel);

    public Iterable<? extends Float64Array> channels();

    public java.util.Iterator<? extends Float64Array> channelIterator();


    // =============================================================
	// Specialization of Array interface

	@Override
	public default Float64VectorArray newInstance(int... dims)
	{
		return Float64VectorArray.create(dims, this.getVectorLength());
	}

	@Override
	public default Array.Factory<Float64Vector> getFactory()
	{
		return factory;
	}

	@Override
	public default Float64VectorArray duplicate()
	{
		// create output array
		Float64VectorArray result = Float64VectorArray.create(this.getSize(), this.getVectorLength());

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
	public default Class<Float64Vector> getDataType()
	{
		return Float64Vector.class;
	}

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<Float64Vector>
	{
	}
}
