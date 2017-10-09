/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.UInt16Array;
import net.sci.array.data.scalar3d.UInt16Array3D;
import net.sci.array.data.vector.VectorArray3D;
import net.sci.array.type.RGB16;
import net.sci.array.type.UInt16;

/**
 * @author dlegland
 *
 */
public abstract class RGB16Array3D extends VectorArray3D<RGB16> implements RGB16Array
{
	// =============================================================
	// Static methods

	public static final RGB16Array3D create(int size0, int size1, int size2)
	{
		return new BufferedPackedShortRGB16Array3D(size0, size1, size2);
	}
	

	// =============================================================
	// Constructor

	protected RGB16Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size1);
	}


	// =============================================================
	// Implementation of the RGB16Array interface

	@Override
	public UInt16Array3D convertToUInt16()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		int size2 = this.getSize(2);
		UInt16Array3D result = UInt16Array3D.create(size0, size1, size2);
		
		RGB16Array.Iterator rgb16Iter = iterator();
		UInt16Array.Iterator uint16Iter = result.iterator();
		while(rgb16Iter.hasNext() && uint16Iter.hasNext())
		{
			uint16Iter.setNextInt(rgb16Iter.next().getInt());
		}
		
		return result;
	}

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
		set(x, y, z, RGB16.fromValue(value));
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
		int r = UInt16.clamp(values[0]);
		int g = UInt16.clamp(values[1]);
		int b = UInt16.clamp(values[2]);
		set(x, y, z, new RGB16(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB16Array#duplicate()
	 */
	@Override
	public abstract RGB16Array3D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB16Array#iterator()
	 */
	@Override
	public abstract net.sci.array.data.color.RGB16Array.Iterator iterator();

}
