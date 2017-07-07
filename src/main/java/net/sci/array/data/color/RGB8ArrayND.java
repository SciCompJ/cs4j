/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.UInt8Array;
import net.sci.array.data.vector.VectorArrayND;
import net.sci.array.type.RGB8;

/**
 * @author dlegland
 *
 */
public abstract class RGB8ArrayND extends VectorArrayND<RGB8> implements RGB8Array
{
	// =============================================================
	// Static methods

	public static final RGB8ArrayND create(int...sizes)
	{
		return new Int32EncodedRGB8ArrayND(sizes);
	}
	

	// =============================================================
	// Constructor

	protected RGB8ArrayND(int... sizes)
	{
		super(sizes);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt8Array convertToUInt8()
	{
		int[] sizes = this.getSize();
		UInt8Array result = UInt8Array.create(sizes);
		
		RGB8Array.Iterator rgb8Iter = iterator();
		UInt8Array.Iterator uint8Iter = result.iterator();
		while(rgb8Iter.hasNext() && uint8Iter.hasNext())
		{
			uint8Iter.setNextInt(rgb8Iter.next().getInt());
		}
		
		return result;
	}

	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB8ArrayND duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
	 */
	@Override
	public abstract RGB8Array.Iterator iterator();

}
