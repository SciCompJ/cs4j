/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.type.RGB8;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public abstract class RGB8Array2D extends VectorArray2D<RGB8> implements RGB8Array
{
	// =============================================================
	// Static methods

//	public static final RGB8Array2D create(int size0, int size1)
//	{
//		return new BufferedUInt8Array2D(size0, size1);
//	}
	

	// =============================================================
	// Constructor

	protected RGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// New methods

	
	// =============================================================
	// Specialization of Array2D interface

	@Override
	public double getValue(int x, int y)
	{
		return get(x, y).getValue();
	}

	@Override
	public void setValue(int x, int y, double value)
	{
		set(x, y, RGB8.fromValue(value));
	}

	@Override
	public double[] getValues(int x, int y)
	{
		return get(x, y).getValues();
	}

	@Override
	public void setValues(int x, int y, double[] values)
	{
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		set(x, y, new RGB8(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB8Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
	 */
	@Override
	public abstract net.sci.array.data.color.RGB8Array.Iterator iterator();

}
