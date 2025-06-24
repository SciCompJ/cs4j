/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.UInt8;

/**
 * Implementation of 2D array of RGB8, by keeping value in a buffer if Int32.
 * 
 * The integer values are encoded and decoded on the fly. The number of elements
 * of the underlying integer array is the same as the number of elements of the
 * RGB8 array.
 * 
 * @author dlegland
 *
 */
public class Int32EncodedRGB8Array2D extends RGB8Array2D
{
	// =============================================================
	// Class variables

	Int32Array2D buffer;
	
	// =============================================================
	// Constructor

	public Int32EncodedRGB8Array2D(int size0, int size1)
	{
		super(size0, size1);
		this.buffer = Int32Array2D.create(size0, size1);
	}

	public Int32EncodedRGB8Array2D(Int32Array2D buffer)
	{
		super(buffer.size(0), buffer.size(1));
		this.buffer = buffer;
	}

	
    // =============================================================
    // Override some methods of the RGB8Array2D class
	
    @Override
    public int getGrayValue(int x, int y)
    {
        int intCode = this.buffer.getInt(x, y);
        int r = intCode & 0x00FF;
        int g = (intCode >> 8) & 0x00FF;
        int b = (intCode >> 16) & 0x00FF;
        return RGB8.grayValue(r, g, b);
    }
    
    @Override
    public int getMaxSample(int x, int y)
    {
        int intCode = this.buffer.getInt(x, y);
        int r = intCode & 0x00FF;
        int g = (intCode >> 8) & 0x00FF;
        int b = (intCode >> 16) & 0x00FF;
        return Math.max(Math.max(r, g), b);
    }
    
    @Override
    public int getIntCode(int x, int y)
    {
        return this.buffer.getInt(x, y);
    }
    
    
    // =============================================================
    // Implementation of the RGB8Array interface

    @Override
    public int getIntCode(int[] pos)
    {
        return this.buffer.getInt(pos[0], pos[1]);
    }
    
    @Override
    public void setIntCode(int[] pos, int intCode)
    {
        this.buffer.setInt(pos[0], pos[1], intCode);
    }
    

    // =============================================================
    // Implementation of the IntVectorArray2D interface

    @Override
    public int getSample(int x, int y, int c)
    {
        int intCode = this.buffer.getInt(x, y);
        switch (c)
        {
        case 0: return intCode & 0x00FF;
        case 1: return (intCode >> 8) & 0x00FF;
        case 2: return (intCode >> 16) & 0x00FF;
        }
        throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + c);
    }

    @Override
    public void setSample(int x, int y, int c, int intValue)
    {
        int intCode = this.buffer.getInt(x, y);
        int r = intCode & 0x00FF;
        int g = intCode & 0x00FF00;
        int b = intCode & 0x00FF0000;
        intValue = UInt8.clamp(intValue);
        
        switch (c)
        {
        case 0: r = intValue; break;
        case 1: g = intValue << 8; break;
        case 2: b = intValue << 16; break;
        default: throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + c);
        }
        
        intCode = r | g | b;
        this.buffer.setInt(x, y, intCode);
    }

    @Override
    public int[] getSamples(int x, int y)
    {
        return RGB8.rgbValues(this.buffer.getInt(x, y));
    }

    @Override
    public int[] getSamples(int x, int y, int[] values)
    {
        return RGB8.rgbValues(this.buffer.getInt(x, y), values);
    }

    @Override
    public void setSamples(int x, int y, int[] rgb)
    {
        this.buffer.setInt(x, y, RGB8.intCode(rgb));
    }


	// =============================================================
	// Implementation of the Array2D interface

    @Override
    public RGB8 get(int x, int y)
    {
        return new RGB8(this.buffer.getInt(x, y));
    }

    /* (non-Javadoc)
     * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
     */
    @Override
    public void set(int x, int y, RGB8 rgb)
    {
        this.buffer.setInt(x, y, rgb.intCode());
    }

    /* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#get(int, int)
	 */
	@Override
	public RGB8 get(int[] pos)
	{
		return new RGB8(this.buffer.getInt(pos));
	}

	/* (non-Javadoc)
	 * @see net.sci.array.data.Array2D#set(int, int, java.lang.Object)
	 */
	@Override
	public void set(int[] pos, RGB8 rgb)
	{
		this.buffer.setInt(pos[0], pos[1], rgb.intCode());
	}


	// =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8Array2D duplicate()
	{
		Int32Array2D newBuffer = this.buffer.duplicate();
		return new Int32EncodedRGB8Array2D(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#iterator()
	 */
	@Override
	public RGB8Array.Iterator iterator()
	{
		return new Int32ArrayRGB8Iterator(this.buffer);
	}
}
