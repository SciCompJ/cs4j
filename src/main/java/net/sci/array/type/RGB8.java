/**
 * 
 */
package net.sci.array.type;

/**
 * A color that is represented by a triplet of red, green and blue components,
 * each of them being coded as UInt8.
 * 
 * @author dlegland
 *
 */
public class RGB8 extends Vector<UInt8>
{
	// =============================================================
	// Static methods
	
	private final static int clamp255(int value)
	{
		return Math.min(Math.max(value, 0), 255);
	}
	
	private final static int clamp255(double value)
	{
		return (int) Math.min(Math.max(value, 0), 255);
	}
	
	/**
	 * Creates a new RGB8 from luma value
	 * 
	 * @param value
	 *            a double value corresponding to luma value between 0 and 255.
	 * @return the corresponding RGB8 instance
	 * @see getValue()
	 */
	public final static RGB8 fromValue(double value)
	{
		int val = clamp255(value);
		return new RGB8(val << 16 | val << 8 | val);
	}
	
	// =============================================================
	// Class variables
	
	int intCode;
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Creates a new color by specifying the integer code representing this color.
	 */
	public RGB8(int intCode)
	{
		this.intCode = intCode;
	}
	
	/**
	 * Creates a new color by specifying the int value of each component.
	 */
	public RGB8(int red, int green, int blue)
	{
		int r = clamp255(red);
		int g = clamp255(green);
		int b = clamp255(blue);
		this.intCode = b << 16 | g << 8 | r;   
	}	
	
	/**
	 * Creates a new color by specifying the double value of each component.
	 */
	public RGB8(double red, double green, double blue)
	{
		int r = clamp255(red);
		int g = clamp255(green);
		int b = clamp255(blue);
		this.intCode = b << 16 | g << 8 | r;   
	}

	// =============================================================
	// General methods
	
	/**
	 * Returns the int-based representation of this RGB8 element.
	 * 
	 * @return an int-based representation of this RGB8 color.
	 */
	public int getIntCode()
	{
		return this.intCode;
	}
	
	/**
	 * @return a double value corresponding to the luma of this color.
	 */
	public double getValue()
	{
		int r = this.intCode & 0x00FF;
		int g = (this.intCode >> 8) & 0x00FF;
		int b = (this.intCode >> 16) & 0x00FF;
		return .2989 * r  + .5870 * g + .1140 * b;
	}
	
	public int[] getSamples()
	{
		int[] rgb = new int[3];
		rgb[0] = this.intCode & 0x00FF;
		rgb[1] = (this.intCode >> 8) & 0x00FF;
		rgb[2] = (this.intCode >> 16) & 0x00FF;
		return rgb;
	}
	
	public int getSample(int channel)
	{
		switch (channel)
		{
		case 0: return this.intCode & 0x00FF;
		case 1: return (this.intCode >> 8) & 0x00FF;
		case 2: return (this.intCode >> 16) & 0x00FF;
		}
		throw new IllegalArgumentException("Channel number must be comprised between 0 and 2");
	}

	/* (non-Javadoc)
	 * @see net.sci.array.type.Vector#getValues()
	 */
	@Override
	public double[] getValues()
	{
		double[] values = new double[3];
		int c = 0;
		for (int v : getSamples())
		{
			values[c++] = v;
		}
		return values;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.type.Vector#getValue(int)
	 */
	@Override
	public double getValue(int c)
	{
		return getSample(c);
	}

}