/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.IntVector;
import net.sci.array.numeric.UInt16;

/**
 * A color that is represented by a triplet of red, green and blue components,
 * each of them being coded as UInt16.
 * 
 * @author dlegland
 *
 */
public class RGB16 implements IntVector<RGB16,UInt16>, Color
{
    // =============================================================
    // Static constants

    private static final double MAX_UINT16_FLOAT = 0X00FFFF;
    
    /** The RGB16 value corresponding to the black color. */
    public static final RGB16 BLACK = new RGB16(0, 0, 0);

    /** The RGB16 value corresponding to the white color. */
    public static final RGB16 WHITE = new RGB16(UInt16.MAX_INT, UInt16.MAX_INT, UInt16.MAX_INT);
    /** The RGB16 value corresponding to the red color. */
    public static final RGB16 RED = new RGB16(UInt16.MAX_INT, 0, 0);
    /** The RGB16 value corresponding to the green color. */
    public static final RGB16 GREEN = new RGB16(0, UInt16.MAX_INT, 0);
    /** The RGB16 value corresponding to the blue color. */
    public static final RGB16 BLUE = new RGB16(0, 0, UInt16.MAX_INT);
    /** The RGB16 value corresponding to the cyan color. */
    public static final RGB16 CYAN = new RGB16(0, UInt16.MAX_INT, UInt16.MAX_INT);
    /** The RGB16 value corresponding to the magenta color. */
    public static final RGB16 MAGENTA = new RGB16(UInt16.MAX_INT, 0, UInt16.MAX_INT);
    /** The RGB16 value corresponding to the yellow color. */
    public static final RGB16 YELLOW = new RGB16(UInt16.MAX_INT, UInt16.MAX_INT, 0);
    /**
     * The RGB16 value corresponding to the gray color (all sample values equal
     * to UInt16.MAX_INT/2).
     */
    public static final RGB16 GRAY = new RGB16(UInt16.MAX_INT / 2, UInt16.MAX_INT / 2, UInt16.MAX_INT / 2);
    /**
     * The RGB16 value corresponding to the light gray color (all sample values
     * equal to UInt16.MAX_INT/4).
     */
    public static final RGB16 DARK_GRAY = new RGB16(UInt16.MAX_INT / 4, UInt16.MAX_INT / 4, UInt16.MAX_INT / 4);
    /**
     * The RGB16 value corresponding to the dark gray color (all sample values
     * equal to UInt16.MAX_INT*3/4).
     */
    public static final RGB16 LIGHT_GRAY = new RGB16(UInt16.MAX_INT * 3 / 4, UInt16.MAX_INT * 3 / 4, UInt16.MAX_INT * 3 / 4);    
    
    // =============================================================
    // Static methods

    /**
     * Converts an array of 3 RGB values into an int code
     * 
     * @param rgbValues
     *            the three values of red, green and blue components
     * @return the corresponding intCode
     */
    public static final long longCode(int[] rgb)
    {
        long r = UInt16.clamp(rgb[0]);
        long g = UInt16.clamp(rgb[1]);
        long b = UInt16.clamp(rgb[2]);
        return b << 32 | g << 16 | r;   
    }
    
    /**
     * Converts the int code representing a RGBvalue into the three components
     * as integers.
     * 
     * @param intCode
     *            the int code of the RGB color
     * @return the three red, green and blue components
     */
    public static final int[] rgbValues(long longCode)
    {
        int[] rgb = new int[3];
        rgb[0] = (int) (longCode & 0x00FFFF);
        rgb[1] = (int) ((longCode >> 16) & 0x00FFFF);
        rgb[2] = (int) ((longCode >> 32) & 0x00FFFF);
        return rgb;
    }

    /**
     * Converts the int code representing a RGB value into the three components
     * as integers.
     * 
     * @param intCode
     *            the int code of the RGB color
     * @param rgbValues
     *            the pre-allocated array of int
     * @return the three red, green and blue components
     */
    public static final int[] rgbValues(long longCode, int[] rgb)
    {
        rgb[0] = (int) (longCode & 0x00FFFF);
        rgb[1] = (int) ((longCode >> 16) & 0x00FFFF);
        rgb[2] = (int) ((longCode >> 32) & 0x00FFFF);
        return rgb;
    }

    /**
     * Creates a new RGB16 from grayscale value
     * 
     * @param value
     *            a double value corresponding to grayscale value, between 0 and
     *            2^16-1.
     * @return the corresponding RGB16 instance
     * @see #getValue()
     */
    public final static RGB16 fromValue(double value)
    {
        int val = UInt16.convert(value);
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
     *            the integer code of the RGB16 value
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
        long r = UInt16.clamp(red);
        long g = UInt16.clamp(green);
        long b = UInt16.clamp(blue);
        this.longCode = b << 32 | g << 16 | r;
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
    public RGB16(int[] rgb)
    {
        long r = UInt16.clamp(rgb[0]);
        long g = UInt16.clamp(rgb[1]);
        long b = UInt16.clamp(rgb[2]);
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
        long r = UInt16.convert(red);
        long g = UInt16.convert(green);
        long b = UInt16.convert(blue);
        this.longCode = b << 32 | g << 16 | r;
    }
    
    
    // =============================================================
    // General methods

    /**
     * Returns the long-based representation of this RGB16 element.
     * 
     * @return a long-based representation of this RGB16 color.
     */
    public long longCode()
    {
        return this.longCode;
    }

    /**
     * Converts this RGB16 value into an instance of UInt16.
     * 
     * @return the RGB16 instance corresponding to the maximum channel value.
     */
    public UInt16 toUInt16()
    {
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) ((this.longCode >> 8) & 0x00FFFF);
        int b = (int) ((this.longCode >> 16) & 0x00FFFF);
        return new UInt16(Math.max(Math.max(r, g), b));
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
    
    
    // =============================================================
    // Extraction of color components
    
    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double red()
    {
        return (this.longCode & 0x00FFFF) / MAX_UINT16_FLOAT;
            
    }
    
    /**
     * @return the green component of this color, between 0 and 1.
     */
    public double green()
    {
        return ((this.longCode >> 16) & 0x00FFFF) / MAX_UINT16_FLOAT;
            
    }
    
    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double blue()
    {
        return ((this.longCode >> 32) & 0x00FFFF) / MAX_UINT16_FLOAT;
            
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
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) ((this.longCode >> 16) & 0x00FFFF);
        int b = (int) ((this.longCode >> 32) & 0x00FFFF);
        
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
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) ((this.longCode >> 16) & 0x00FFFF);
        int b = (int) ((this.longCode >> 32) & 0x00FFFF);
        
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
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) ((this.longCode >> 16) & 0x00FFFF);
        int b = (int) ((this.longCode >> 32) & 0x00FFFF);
        return (0.2989 * r  + 0.5870 * g + 0.1140 * b) / MAX_UINT16_FLOAT;
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
        rgb[0] = (int) (this.longCode & 0x00FFFF);
        rgb[1] = (int) ((this.longCode >> 16) & 0x00FFFF);
        rgb[2] = (int) ((this.longCode >> 32) & 0x00FFFF);
        return rgb;
    }
    
    public int getSample(int channel)
    {
        return switch (channel)
        {
            case 0 -> (int) (this.longCode & 0x00FFFF);
            case 1 -> (int) ((this.longCode >> 16) & 0x00FFFF);
            case 2 -> (int) ((this.longCode >> 32) & 0x00FFFF);
            default -> throw new IllegalArgumentException("Channel index must be comprised between 0 and 2");
        };
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

    /*
     * (non-Javadoc)
     * 
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
    // =============================================================
    // Implementation of the Numeric interface
    
    @Override
    public RGB16 one()
    {
        return new RGB16(1,0,0);
    }

    @Override
    public RGB16 zero()
    {
        return new RGB16(0,0,0);
    }

    @Override
    public RGB16 plus(RGB16 other)
    {
        long r = Math.min(this.getSample(0) + other.getSample(0), UInt16.MAX_INT);
        long g = Math.min(this.getSample(1) + other.getSample(1), UInt16.MAX_INT);
        long b = Math.min(this.getSample(2) + other.getSample(2), UInt16.MAX_INT);
        return new RGB16(b << 32 | g << 16 | r);
    }

    @Override
    public RGB16 minus(RGB16 other)
    {
        long r = Math.min(this.getSample(0) - other.getSample(0), 0);
        long g = Math.min(this.getSample(1) - other.getSample(1), 0);
        long b = Math.min(this.getSample(2) - other.getSample(2), 0);
        return new RGB16(b << 32 | g << 16 | r);
    }

    /**
     * Always returns the color BLACK, that corresponds to a triplet of negative
     * values clamped between 0 and <code>UInt16.MAX_INT</code>.
     * 
     * @return the color RGB8.BLACK
     */
    @Override
    public RGB16 opposite()
    {
        return RGB16.BLACK;
    }
    
    @Override
    public RGB16 times(double k)
    {
        int r = (int) (this.getSample(0) * k);
        int g = (int) (this.getSample(1) * k);
        int b = (int) (this.getSample(2) * k);
        return new RGB16(r, g, b);
    }

    @Override
    public RGB16 divideBy(double k)
    {
        int r = (int) (this.getSample(0) / k);
        int g = (int) (this.getSample(1) / k);
        int b = (int) (this.getSample(2) / k);
        return new RGB16(r, g, b);
    }    
    
    
    // =============================================================
    // Override Object methods
    
    @Override
    public String toString()
    {
        int r = (int) (this.longCode & 0x00FFFF);
        int g = (int) (this.longCode >> 16) & 0x00FFFF;
        int b = (int) (this.longCode >> 32) & 0x00FFFF;
        return String.format("RGB16(%d,%d,%d)", r, g, b);
    }
    
    // =============================================================
    // Override Object methods

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof RGB16 that)
        {
            return this.longCode == that.longCode; 
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Long.hashCode(longCode);
    }
}
