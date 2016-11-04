/**
 * 
 */
package net.sci.array.data.color;

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
	// New methods

	

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
