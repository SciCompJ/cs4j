/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array3D;
import net.sci.array.vector.IntVectorArray3D;

/**
 * @author dlegland
 *
 */
public abstract class RGB8Array3D extends IntVectorArray3D<RGB8> implements RGB8Array
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
		super(size0, size1, size2);
	}


	// =============================================================
	// Implementation of the RGB8Array interface

	@Override
	public UInt8Array3D convertToUInt8()
	{
		int size0 = this.getSize(0);
		int size1 = this.getSize(1);
		int size2 = this.getSize(2);
		UInt8Array3D result = UInt8Array3D.create(size0, size1, size2);
		
		RGB8Array.Iterator rgb8Iter = iterator();
		UInt8Array.Iterator uint8Iter = result.iterator();
		while(rgb8Iter.hasNext() && uint8Iter.hasNext())
		{
			uint8Iter.setNextInt(rgb8Iter.next().getInt());
		}
		
		return result;
	}

    // =============================================================
    // Specialization of IntVectorArray3D interface

    @Override
    public int[] getSamples(int x, int y, int z)
    {
        return get(x, y, z).getSamples();
    }

    @Override
    public int[] getSamples(int x, int y, int z, int[] values)
    {
        return get(x, y, z).getSamples(values);
    }

    @Override
    public void setSamples(int x, int y, int z, int[] values)
    {
        set(x, y, z, new RGB8(values));
    }

	// =============================================================
	// Specialization of VectorArray3D interface

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
	public abstract net.sci.array.color.RGB8Array.Iterator iterator();

}
