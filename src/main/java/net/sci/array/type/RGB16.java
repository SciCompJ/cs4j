/**
 * 
 */
package net.sci.array.type;

/**
 * A color that is represented by a triplet of red, green and blue components,
 * each of them being coded as UInt16.
 * 
 * @author dlegland
 *
 */
public class RGB16 extends IntVector<UInt16>
{
	// =============================================================
	// Static methods
	
	private final static int clampUInt16(int value)
	{
		return Math.min(Math.max(value, 0), 0x00FFFF);
	}
	
	private final static int clampUInt16(double value)
	{
		return (int) Math.min(Math.max(value, 0), 0x00FFFF);
	}
	
	/**
	 * Creates a new RGB8 from grayscale value
	 * 
	 * @param value
	 *            a double value corresponding to grayscale value, between 0 and 2^16-1.
	 * @return the corresponding RGB16 instance
	 * @see #getValue()
	 */
	public final static RGB16 fromValue(double value)
	{
		int val = clampUInt16(value);
		return new RGB16(val, val, val);
	}
	
	// =============================================================
	// Class variables
	
	long longCode;
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Creates a new color by specifying the integer code representing this
	 * color.
	 * 
	 * @param intCode
	 *            the integer code of the RGB8 value
	 */
	public RGB16(long intCode)
	{
		this.longCode = intCode;
	}
	
	/**
	 * Creates a new color by specifying the int value of each component.
	 * 
	 * @param red
	 *            the value of the red component, between 0 and 2^16-1
	 * @param green
	 *            the value of the green component, between 0 and 2^16-1
	 * @param blue
	 *            the value of the blue component, between 0 and 2^16-1
	 */
	public RGB16(int red, int green, int blue)
	{
		long r = clampUInt16(red);
		long g = clampUInt16(green);
		long b = clampUInt16(blue);
		this.longCode = b << 32 | g << 16 | r;   
	}	
	
	/**
	 * Creates a new color by specifying the double value of each component.
	 * 
	 * @param red
	 *            the value of the red component, between 0 and 2^16-1
	 * @param green
	 *            the value of the green component, between 0 and 2^16-1
	 * @param blue
	 *            the value of the blue component, between 0 and 2^16-1
	 */
	public RGB16(double red, double green, double blue)
	{
		long r = clampUInt16(red);
		long g = clampUInt16(green);
		long b = clampUInt16(blue);
		this.longCode = b << 32 | g << 16 | r;   
	}

	
//    // =============================================================
//    // Methods specific to RGB16
//	
//    /**
//     * @see http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
//     * 
//     * @return the hue value of this color, between 0 and 1. 
//     */
//	public double getHue()
//	{
//	    int r = this.longCode & 0x00FF;
//	    int g = (this.longCode >> 8) & 0x00FF;
//	    int b = (this.longCode >> 16) & 0x00FF;
//	    
//	    // max components
//        double cmax = Math.max(Math.max(r, g), b);
//        double cmin = Math.min(Math.min(r, g), b);
//        double delta = cmax - cmin;
//        
//        // case of gray colors. Maybe return NaN ?
//        if (delta < .0001) 
//        {
//            return 0;
//        }
//        
//        // switch depending on dominant channel
//        // Compute hue between 0 and 6
//        double hue = 0;
//        if (r >= g && r >= b)
//        {
//            // between yellow & magenta
//            hue = (g - b) / delta;
//            if (hue < 0)
//                hue += 6;
//        }
//        else if (g >= r && g >= b)
//        {
//            // between cyan & yellow
//            hue = 2 + (b - r) / delta;
//        }
//        else if (b >= r && b >= r)
//        {
//            // between magenta & cyan
//            hue = 4 + (r - g) / delta;
//        }
//        
//	    return hue / 6;
//	}
//
//	/**
//	 * @see http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
//	 * 
//	 * @return the saturation of this color, between 0 and 1. 
//	 */
//    public double getSaturation()
//    {
//        int r = this.longCode & 0x00FF;
//        int g = (this.longCode >> 8) & 0x00FF;
//        int b = (this.longCode >> 16) & 0x00FF;
//        
//        double cmax = Math.max(Math.max(r, g), b);
//        double cmin = Math.min(Math.min(r, g), b);
//        
//        if (cmax == 0)
//        {
//            return 0;
//        }
//        return (cmax - cmin) / cmax;
//    }
//    
//    /**
//     * @return the luma / luminance of this color, between 0 and 1. 
//     */
//	public double getLuminance()
//	{
//	    int r = this.longCode & 0x00FF;
//        int g = (this.longCode >> 8) & 0x00FF;
//        int b = (this.longCode >> 16) & 0x00FF;
//        return (.2989 * r  + .5870 * g + .1140 * b) / 255.0;
//	}
    

	// =============================================================
	// General methods
	
	/**
	 * Returns the long-based representation of this RGB16 element.
	 * 
	 * @return a long-based representation of this RGB16 color.
	 */
	public long getLongCode()
	{
		return this.longCode;
	}
	
	/**
	 * Converts this RGB8 value into an integer value representing the maximum
	 * channel value, coded between 0 and 2^16-1.
	 * 
	 * @return an integer value corresponding to the maximum channel value.
	 */
	public int getInt()
	{
		int r = (int) (this.longCode & 0x00FFFF);
		int g = (int) ((this.longCode >> 8) & 0x00FFFF);
		int b = (int) ((this.longCode >> 16) & 0x00FFFF);
		return Math.max(Math.max(r, g), b);
	}

	/**
	 * Converts this RGB8 value into a floating-point value value representing
	 * the maximum channel value.
	 * 
	 * @return a double value corresponding to the maximum channel value.
	 */
	public double getValue()
	{
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) ((this.longCode >> 16) & 0x00FFFF);
        int b = (int) ((this.longCode >> 32) & 0x00FFFF);
		return Math.max(Math.max(r, g), b);
	}
	
    /**
     * Returns the red, green and blue values into an integer array.
     * 
     * @return the reference to the RGB array
     */
    public int[] getSamples()
    {
        int[] rgb = new int[3];
        return getSamples(rgb);
    }
    
    /**
     * Returns the red, green and blue values into the pre-allocated array.
     * 
     * @param rgb
     *            a preallocated array with three elements
     * @return the reference to the RGB array
     */
    public int[] getSamples(int[] rgb)
    {
        rgb[0] = (int) (this.longCode & 0x00FFFF);
        rgb[1] = (int) ((this.longCode >> 16) & 0x00FFFF);
        rgb[2] = (int) ((this.longCode >> 32) & 0x00FFFF);
        return rgb;
    }
    
	public int getSample(int channel)
	{
		switch (channel)
		{
		case 0: return (int) (this.longCode & 0x00FFFF);
		case 1: return (int) ((this.longCode >> 16) & 0x00FFFF);
		case 2: return (int) ((this.longCode >> 32) & 0x00FFFF);
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
        return getValues(values);
    }

    /**
     * Returns the three red, green and blue as floating point values in the
     * pre-allocated array.
     */
    public double[] getValues(double[] values)
    {
        values[0] = (this.longCode & 0x00FFFF);
        values[1] = ((this.longCode >> 16) & 0x00FFFF);
        values[2] = ((this.longCode >> 32) & 0x00FFFF);
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

	@Override
	public UInt16 get(int c)
	{
		return new UInt16(getSample(c));
	}

	@Override
	public int size()
	{
		return 3;
	}
	
}