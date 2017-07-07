/**
 * 
 */
package net.sci.array.data.color;

import net.sci.array.data.UInt8Array;
import net.sci.array.data.scalar2d.UInt8Array2D;
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

	public static final RGB8Array2D create(int size0, int size1)
	{
		return new Int32EncodedRGB8Array2D(size0, size1);
	}
	

	// =============================================================
	// Constructor

	protected RGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt8Array2D convertToUInt8()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		UInt8Array2D result = UInt8Array2D.create(size0, size1);
		
		RGB8Array.Iterator rgb8Iter = iterator();
		UInt8Array.Iterator uint8Iter = result.iterator();
		while(rgb8Iter.hasNext() && uint8Iter.hasNext())
		{
			uint8Iter.setNextInt(rgb8Iter.next().getInt());
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
		set(x, y, RGB8.fromValue(value));
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
	public abstract RGB8Array.Iterator iterator();

}
