/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.VectorArray;
import net.sci.array.type.RGB8;

/**
 * An array that contains colors that can be represented as instances of RGB8 type.
 * 
 * @author dlegland
 *
 */
public interface RGB8Array extends VectorArray<RGB8>
{
	// =============================================================
	// Static methods

	public static RGB8Array create(int[] dims)
	{
		switch (dims.length)
		{
//		case 2:
//			return DoubleArray2D.create(dims[0], dims[1]);
//		case 3:
//			return UInt8Array3D.create(dims[0], dims[1], dims[2]);
		default:
			//TODO: implement the rest
			throw new RuntimeException("Can not create such image");
//			return UInt8ArrayND.create(dims);
		}
	}

	// =============================================================
	// Specialization of VectorArray interface

	/**
	 * Always returns 3, as this is the number of components of the RGB8 type.
	 * 
	 * @see net.sci.array.data.VectorArray#getVectorLength()
	 */
	@Override
	public default int getVectorLength()
	{
		return 3;
	}


	// =============================================================
	// Specialization of Array interface

	@Override
	public default RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	@Override
	public RGB8Array duplicate();

	public Iterator iterator();

	
	// =============================================================
	// Inner interface

	public interface Iterator extends VectorArray.Iterator<RGB8>
	{
	}

}
