/**
 * 
 */
package net.sci.array.color;

import net.sci.array.scalar.UInt8;
import net.sci.array.vector.IntVector;

/**
 * A color that is represented by a triplet of red, green and blue components,
 * each of them being coded as UInt8.
 * 
 * Immutable class.
 * 
 * @author dlegland
 *
 */
public class RGB8 extends IntVector<UInt8> implements Color
{
    // =============================================================
    // Constants

    public static final RGB8 WHITE = new RGB8(255, 255, 255);
    public static final RGB8 BLACK = new RGB8(0, 0, 0);
    public static final RGB8 RED = new RGB8(255, 0, 0);
    public static final RGB8 GREEN = new RGB8(0, 255, 0);
    public static final RGB8 BLUE = new RGB8(0, 0, 255);
    public static final RGB8 CYAN = new RGB8(0, 255, 255);
    public static final RGB8 MAGENTA = new RGB8(255, 0, 255);
    public static final RGB8 YELLOW = new RGB8(255, 255, 0);
    public static final RGB8 GRAY = new RGB8(127, 127, 127);
    public static final RGB8 DARK_GRAY = new RGB8(63, 63, 63);
    public static final RGB8 LIGHT_GRAY = new RGB8(191, 191, 191);

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
	 * Creates a new RGB8 from grayscale value
	 * 
	 * @param value
	 *            a double value corresponding to grayscale value, between 0 and 255
	 * @return the corresponding RGB8 instance
	 * @see #getValue()
	 */
	public final static RGB8 fromValue(double value)
	{
		int val = clamp255(value);
		return new RGB8(val << 16 | val << 8 | val);
	}
	
    public static final RGB8 fromIntCode(int intCode)
    {
        return new RGB8(intCode);
    }

    
    public final static RGB8 fromUInt8(UInt8 value)
	{
	    int val = clamp255(value.getInt());
	    return new RGB8(val << 16 | val << 8 | val);
	}
	
    /**
     * Converts an array of 3 RGB values into an int code
     * 
     * @param rgbValues
     *            the three values of red, green and blue components
     * @return the corresponding intCode
     */
    public static final int intCode(int[] rgb)
    {
        int r = clamp255(rgb[0]);
        int g = clamp255(rgb[1]);
        int b = clamp255(rgb[2]);
        return b << 16 | g << 8 | r;   
    }
	
    /**
     * Converts the int code representing a RGBvalue into the three components
     * as integers.
     * 
     * @param intCode
     *            the int code of the RGB color
     * @return the three red, green and blue components
     */
    public static final int[] rgbValues(int intCode)
    {
        int[] rgb = new int[3];
        rgb[0] = intCode & 0x00FF;
        rgb[1] = (intCode >> 8) & 0x00FF;
        rgb[2] = (intCode >> 16) & 0x00FF;
        return rgb;
    }

    /**
     * Converts the int code representing a RGBvalue into the three components
     * as integers.
     * 
     * @param intCode
     *            the int code of the RGB color
     * @param rgbValues
     *            the pre-allocated array of int
     * @return the three red, green and blue components
     */
    public static final int[] rgbValues(int intCode, int[] rgb)
    {
        rgb[0] = intCode & 0x00FF;
        rgb[1] = (intCode >> 8) & 0x00FF;
        rgb[2] = (intCode >> 16) & 0x00FF;
        return rgb;
    }

    
    // =============================================================
	// Class variables
	
	final int intCode;
	
	
	// =============================================================
	// Constructors
	
	/**
	 * Creates a new color by specifying the integer code representing this
	 * color.
	 * 
	 * @param intCode
	 *            the integer code of the RGB8 value
	 */
	public RGB8(int intCode)
	{
		this.intCode = intCode;
	}
	
	/**
	 * Creates a new color by specifying the int value of each component.
	 * 
	 * @param red
	 *            the value of the red component, between 0 and 255
	 * @param green
	 *            the value of the green component, between 0 and 255
	 * @param blue
	 *            the value of the blue component, between 0 and 255
	 */
	public RGB8(int red, int green, int blue)
	{
		int r = clamp255(red);
		int g = clamp255(green);
		int b = clamp255(blue);
		this.intCode = b << 16 | g << 8 | r;   
	}	
	
    /**
     * Creates a new color by specifying the int value of each component.
     * 
     * @param red
     *            the value of the red component, between 0 and 255
     * @param green
     *            the value of the green component, between 0 and 255
     * @param blue
     *            the value of the blue component, between 0 and 255
     */
    public RGB8(int[] rgb)
    {
        int r = clamp255(rgb[0]);
        int g = clamp255(rgb[1]);
        int b = clamp255(rgb[2]);
        this.intCode = b << 16 | g << 8 | r;   
    }   
    
	/**
	 * Creates a new color by specifying the double value of each component.
	 * 
	 * @param red
	 *            the value of the red component, between 0 and 255
	 * @param green
	 *            the value of the green component, between 0 and 255
	 * @param blue
	 *            the value of the blue component, between 0 and 255
	 */
	public RGB8(double red, double green, double blue)
	{
		int r = clamp255(red);
		int g = clamp255(green);
		int b = clamp255(blue);
		this.intCode = b << 16 | g << 8 | r;   
	}

	/**
     * Conversion constructor from another color, that can use another
     * representation format.
     * 
     * @param color
     *            the other color
     */
	public RGB8(Color color)
	{
        int r = (int) Math.floor(color.red() * 255 + .5);
        int g = (int) Math.floor(color.green() * 255 + .5);
        int b = (int) Math.floor(color.blue() * 255 + .5);
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
     * Converts this RGB8 value into an instance of UInt8.
     * 
     * @return the UInt8 instance corresponding to the maximum channel value.
     */
    public UInt8 toUInt8()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        return new UInt8((byte) Math.max(Math.max(r, g), b));
    }

    /**
	 * Converts this RGB8 value into an integer value representing the maximum
	 * channel value, coded between 0 and 255.
	 * 
	 * @return an integer value corresponding to the maximum channel value.
	 */
	public int getInt()
	{
		int r = this.intCode & 0x00FF;
		int g = (this.intCode >> 8) & 0x00FF;
		int b = (this.intCode >> 16) & 0x00FF;
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
		int r = this.intCode & 0x00FF;
		int g = (this.intCode >> 8) & 0x00FF;
		int b = (this.intCode >> 16) & 0x00FF;
		return Math.max(Math.max(r, g), b);
	}

	
    // =============================================================
    // Extraction of color components
    
    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double red()
    {
        return (this.intCode & 0x00FF) / 255.0;
            
    }
    
    /**
     * @return the green component of this color, between 0 and 1.
     */
    public double green()
    {
        return ((this.intCode >> 8) & 0x00FF) / 255.0;
            
    }
    
    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double blue()
    {
        return ((this.intCode >> 16) & 0x00FF) / 255.0;
            
    }
    
    /**
     * Returns the hue component of this color, coded between 0 and 1.
     * 
     * @return the hue value of this color, between 0 and 1. 
     * 
     * @see <a href= "http://www.rapidtables.com/convert/color/rgb-to-hsv.htm">
     *      http://www.rapidtables.com/convert/color/rgb-to-hsv.htm</a>
     */
    public double hue()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        
        // max components
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        double delta = cmax - cmin;
        
        // case of gray colors. Maybe return NaN ?
        if (delta < 0.0001) 
        {
            return 0;
        }
        
        // switch depending on dominant channel
        // Compute hue between 0 and 6
        double hue = 0;
        if (r >= g && r >= b)
        {
            // between yellow & magenta
            hue = (g - b) / delta;
            if (hue < 0)
                hue += 6;
        }
        else if (g >= r && g >= b)
        {
            // between cyan & yellow
            hue = 2 + (b - r) / delta;
        }
        else if (b >= r && b >= r)
        {
            // between magenta & cyan
            hue = 4 + (r - g) / delta;
        }
        
        return hue / 6;
    }

    /**
     * Returns the saturation component of this color, coded between 0 and 1.
     * 
     * @return the saturation of this color, between 0 and 1. 
     * 
     * @see <a href= "http://www.rapidtables.com/convert/color/rgb-to-hsv.htm">
     *      http://www.rapidtables.com/convert/color/rgb-to-hsv.htm</a>
     */
    public double saturation()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        
        if (cmax == 0)
        {
            return 0;
        }
        return (cmax - cmin) / cmax;
    }
    
    /**
     * @return the luma / luminance of this color, between 0 and 1. 
     */
    public double luminance()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        return (0.2989 * r  + 0.5870 * g + 0.1140 * b) / 255.0;
    }
    

    // =============================================================
    // Implementation of vector type
    
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
        return getValues(values);
    }

    /**
     * Returns the three red, green and blue as floating point values in the
     * pre-allocated array.
     */
    public double[] getValues(double[] values)
    {
        values[0] = this.intCode & 0x00FF;
        values[1] = (this.intCode >> 8) & 0x00FF;
        values[2] = (this.intCode >> 16) & 0x00FF;
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
	public UInt8 get(int c)
	{
		return new UInt8(getSample(c));
	}

	@Override
	public int size()
	{
		return 3;
	}

	// =============================================================
    // Override Object methods
	
	@Override
	public String toString()
	{
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
	    return String.format("RGB8(%d,%d,%d)", r, g, b);
	}
    
}
