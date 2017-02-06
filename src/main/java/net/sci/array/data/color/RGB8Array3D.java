/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.vector.VectorArray3D;
import net.sci.array.type.RGB8;
import net.sci.array.type.UInt8;

/**
 * @author dlegland
 *
 */
public abstract class RGB8Array3D extends VectorArray3D<RGB8> implements RGB8Array
{
	// =============================================================
	// Static methods

	public static final RGB8Array3D create(int size0, int size1, int size2)
	{
		return new Int32EncodedRGB8Array3D(size0, size1, size2);
	}
	

	// =============================================================
	// Constructor

	protected RGB8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size1);
	}


	// =============================================================
	// New methods

	
	// =============================================================
	// Specialization of Array3D interface

	@Override
	public double getValue(int x, int y, int z)
	{
		return get(x, y, z).getValue();
	}

	@Override
	public void setValue(int x, int y, int z, double value)
	{
		set(x, y, z, RGB8.fromValue(value));
	}

	@Override
	public double[] getValues(int x, int y, int z)
	{
		return get(x, y, z).getValues();
	}

    /* (non-Javadoc)
     * @see net.sci.array.data.vector.VectorArray3D#getValues(int, int, int, double[])
     */
    @Override
    public double[] getValues(int x, int y, int z, double[] values)
    {
        return get(x, y, z).getValues(values);
    }

    @Override
	public void setValues(int x, int y, int z, double[] values)
	{
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		set(x, y, z, new RGB8(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB8Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
	 */
	@Override
	public abstract net.sci.array.data.color.RGB8Array.Iterator iterator();

}
