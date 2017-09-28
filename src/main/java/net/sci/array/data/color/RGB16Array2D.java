/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.UInt16Array;
import net.sci.array.data.scalar2d.UInt16Array2D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.type.RGB16;
import net.sci.array.type.UInt16;

/**
 * @author dlegland
 *
 */
public abstract class RGB16Array2D extends VectorArray2D<RGB16> implements RGB16Array
{
	// =============================================================
	// Static methods

	public static final RGB16Array2D create(int size0, int size1)
	{
		return new BufferedPackedShortRGB16Array2D(size0, size1);
	}
	

	// =============================================================
	// Constructor

	protected RGB16Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt16Array2D convertToUInt16()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		UInt16Array2D result = UInt16Array2D.create(size0, size1);
		
		RGB16Array.Iterator rgb16Iter = iterator();
		UInt16Array.Iterator uint16Iter = result.iterator();
		while(rgb16Iter.hasNext() && uint16Iter.hasNext())
		{
			uint16Iter.setNextInt(rgb16Iter.next().getInt());
		}
		
		return result;
	}
	
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
		set(x, y, RGB16.fromValue(value));
	}

	@Override
	public double[] getValues(int x, int y)
	{
		return get(x, y).getValues();
	}

    @Override
    public double[] getValues(int x, int y, double[] values)
    {
        return get(x, y).getValues(values);
    }

    @Override
	public void setValues(int x, int y, double[] values)
	{
		int r = UInt16.clamp(values[0]);
		int g = UInt16.clamp(values[1]);
		int b = UInt16.clamp(values[2]);
		set(x, y, new RGB16(r, g, b));
	}


	// =============================================================
	// Specialization of Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#duplicate()
	 */
	@Override
	public abstract RGB16Array2D duplicate();

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
	 */
	@Override
	public abstract RGB16Array.Iterator iterator();

}
