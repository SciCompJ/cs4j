/**
 * 
 */
package net.sci.array.data;

import net.sci.array.Array;
import net.sci.array.data.vector.Float32VectorArray2D;
import net.sci.array.data.vector.Float32VectorArray3D;
import net.sci.array.data.vector.Float32VectorArrayND;
import net.sci.array.type.Float32Vector;

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
	// Specialization of Array interface

	@Override
	public default Float32VectorArray newInstance(int... dims)
	{
		return Float32VectorArray.create(dims, this.getVectorLength());
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
		Float32VectorArray result = Float32VectorArray.create(this.getSize(), this.getVectorLength());

		// initialize iterators
		Float32VectorArray.Iterator iter1 = this.iterator();
		Float32VectorArray.Iterator iter2 = result.iterator();
		
		// copy values into output array
		while(iter1.hasNext())
		{
			iter2.setNext(iter1.next());
		}
		
		// return output
		return result;
	}

	@Override
	public default Class<Float32Vector> getDataType()
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
