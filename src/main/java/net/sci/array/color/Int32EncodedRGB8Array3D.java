/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.Int32Array3D;
import net.sci.array.scalar.UInt8;

/**
 * Implementation of 3D array of RGB8, by keeping value in a buffer if Int32.
 * 
 * The integer values are encoded and decoded on the fly. The number of elements
 * of the underlying integer array is the same as the number of elements of the
 * RGB8 array.
 * 
 * @author dlegland
 *
 */
public class Int32EncodedRGB8Array3D extends RGB8Array3D
{
	// =============================================================
	// Class variables

	Int32Array3D buffer;
	
	// =============================================================
	// Constructor

	public Int32EncodedRGB8Array3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.buffer = Int32Array3D.create(size0, size1, size2);
	}

	public Int32EncodedRGB8Array3D(Int32Array3D buffer)
	{
		super(buffer.getSize(0), buffer.getSize(1), buffer.getSize(2));
		this.buffer = buffer;
	}


    // =============================================================
    // Implementation of the IntVectorArray3D interface

    @Override
    public int getSample(int x, int y, int z, int c)
    {
        return get(x, y, z).getSample(c);
    }

    @Override
    public void setSample(int x, int y, int z, int c, int intValue)
    {
        int intCode = this.buffer.getInt(x, y, z);
        int r = intCode & 0x00FF;
        int g = intCode & 0x00FF00;
        int b = intCode & 0x00FF0000;
        intValue = UInt8.clamp(intValue);
        
        switch (c)
        {
        case 0: r = intValue; break;
        case 1: g = intValue << 8; break;
        case 2: b = intValue << 16; break;
        default: throw new IllegalArgumentException("Channel number must be comprised between 0 and 2");
        }
        intCode = r | g | b;
        this.buffer.setInt(x, y, z, intCode);
    }


    // =============================================================
	// Implementation of the VectorArray3D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#getValues(int, int, int)
	 */
	@Override
	public double[] getValues(int x, int y, int z)
	{
	 	return new RGB8(this.buffer.getInt(x, y, z)).getValues();
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.vector.VectorArray3D#setValues(int, int, int, double[])
	 */
	@Override
	public void setValues(int x, int y, int z, double[] values)
	{
		int r = UInt8.clamp(values[0]);
		int g = UInt8.clamp(values[1]);
		int b = UInt8.clamp(values[2]);
		int intCode = b << 16 | g << 8 | r;
		this.buffer.setInt(x, y, z, intCode);
	}

	
	// =============================================================
	// Implementation of the Array2D interface

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public RGB8 get(int x, int y, int z)
	{
		return new RGB8(this.buffer.getInt(x, y, z));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int x, int y, int z, RGB8 rgb)
	{
		this.buffer.setInt(x, y, z, rgb.getIntCode());
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8Array3D duplicate()
	{
		Int32Array3D newBuffer = this.buffer.duplicate();
		return new Int32EncodedRGB8Array3D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.color.RGB8Array#iterator()
	 */
	@Override
	public RGB8Array.Iterator iterator()
	{
      return new Int32ArrayRGB8Iterator(this.buffer);
	}
}
