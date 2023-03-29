/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.Int32Array;
import net.sci.array.scalar.UInt8;

/**
 * Implementation of multidimensional array of RGB8, by keeping value in a buffer if Int32.
 * 
 * The integer values are encoded and decoded on the fly. The number of elements
 * of the underlying integer array is the same as the number of elements of the
 * RGB8 array.
 * 
 * @author dlegland
 *
 */
public class Int32EncodedRGB8ArrayND extends RGB8ArrayND
{
	// =============================================================
	// Class variables

	Int32Array buffer;
	
	// =============================================================
	// Constructor

	public Int32EncodedRGB8ArrayND(int... sizes)
	{
		super(sizes);
		this.buffer = Int32Array.create(sizes);
	}

	public Int32EncodedRGB8ArrayND(Int32Array buffer)
	{
		super(buffer.size());
		this.buffer = buffer;
	}

    
    // =============================================================
    // Implementation of the RGB8Array interface

    @Override
    public int getMaxSample(int[] pos)
    {
        int intCode = this.buffer.getInt(pos);
        int r = intCode & 0x00FF;
        int g = (intCode >> 8) & 0x00FF;
        int b = (intCode >> 16) & 0x00FF;
        return Math.max(Math.max(r, g), b);
    }
    
    @Override
    public int getIntCode(int[] pos)
    {
        return this.buffer.getInt(pos);
    }
    
    @Override
    public void setIntCode(int[] pos, int intCode)
    {
        this.buffer.setInt(pos, intCode);
    }
        
	
    // =============================================================
    // Implementation of the IntVectorArray interface

    @Override
    public int getSample(int[] pos, int channel)
    {
        int intCode = this.buffer.getInt(pos);
        switch (channel)
        {
        case 0: return intCode & 0x00FF;
        case 1: return (intCode >> 8) & 0x00FF;
        case 2: return (intCode >> 16) & 0x00FF;
        }
        throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + channel);
    }

    @Override
    public void setSample(int[] pos, int channel, int intValue)
    {
        int intCode = this.buffer.getInt(pos);
        int r = intCode & 0x00FF;
        int g = intCode & 0x00FF00;
        int b = intCode & 0x00FF0000;
        intValue = UInt8.clamp(intValue);
        
        switch (channel)
        {
        case 0: r = intValue; break;
        case 1: g = intValue << 8; break;
        case 2: b = intValue << 16; break;
        default: throw new IllegalArgumentException("Channel number must be comprised between 0 and 2, not " + channel);
        }
        
        intCode = r | g | b;
        this.buffer.setInt(pos, intCode);
    }

    @Override
    public int[] getSamples(int[] pos)
    {
        return RGB8.rgbValues(this.buffer.getInt(pos));
    }

    @Override
    public int[] getSamples(int[] pos, int[] rgb)
    {
        return RGB8.rgbValues(this.buffer.getInt(pos), rgb);
    }

    @Override
    public void setSamples(int[] pos, int[] rgb)
    {
        this.buffer.setInt(pos, RGB8.intCode(rgb));
    }


    // =============================================================
    // Specialization of the VectorArray interface

    @Override
    public double getValue(int[] pos, int channel)
    {
        return RGB8.rgbValues(this.buffer.getInt(pos))[channel];
    }

    @Override
    public void setValue(int[] pos, int channel, double value)
    {
        int[] samples = getSamples(pos);
        samples[channel] = UInt8.convert(value);
        this.buffer.setInt(pos, RGB8.intCode(samples));
    }


    // =============================================================
	// Implementation of the Array interface

	@Override
	public RGB8 get(int[] pos)
	{
		return new RGB8(this.buffer.getInt(pos));
	}

	@Override
	public void set(int[] pos, RGB8 rgb)
	{
		this.buffer.setInt(pos, rgb.intCode());
	}

	@Override
	public RGB8Array newInstance(int... dims)
	{
		return RGB8Array.create(dims);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array2D#duplicate()
	 */
	@Override
	public RGB8ArrayND duplicate()
	{
		Int32Array newBuffer = this.buffer.duplicate();
		return new Int32EncodedRGB8ArrayND(newBuffer);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.color.RGB8Array#iterator()
	 */
	@Override
	public RGB8Array.Iterator iterator()
	{
	    return new Int32ArrayRGB8Iterator(this.buffer);
	}
}
