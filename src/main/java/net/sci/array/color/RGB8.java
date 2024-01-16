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
public class RGB8 extends IntVector<RGB8,UInt8> implements Color
{
    // =============================================================
    // Constants

    /** The white color. */
    public static final RGB8 WHITE = new RGB8(255, 255, 255);
    /** The black color. */
    public static final RGB8 BLACK = new RGB8(0, 0, 0);
    /** The red color. */
    public static final RGB8 RED = new RGB8(255, 0, 0);
    /** The green color. */
    public static final RGB8 GREEN = new RGB8(0, 255, 0);
    /** The blue color. */
    public static final RGB8 BLUE = new RGB8(0, 0, 255);
    /** The cyan color. */
    public static final RGB8 CYAN = new RGB8(0, 255, 255);
    /** The magenta color. */
    public static final RGB8 MAGENTA = new RGB8(255, 0, 255);
    /** The yellow color. */
    public static final RGB8 YELLOW = new RGB8(255, 255, 0);
    /** The gray color (all sample values equal to 127). */
    public static final RGB8 GRAY = new RGB8(127, 127, 127);
    /** The light gray color (all sample values equal to 63). */
    public static final RGB8 DARK_GRAY = new RGB8(63, 63, 63);
    /** The dark gray color (all sample values equal to 191). */
    public static final RGB8 LIGHT_GRAY = new RGB8(191, 191, 191);
    

    // =============================================================
    // Static methods

    /**
     * Creates a new RGB8 from grayscale value
     * 
     * @param value
     *            a double value corresponding to grayscale value, between 0 and
     *            255
     * @return the corresponding RGB8 instance
     * @see #getValue()
     */
    public final static RGB8 fromValue(double value)
    {
        int val = UInt8.convert(value);
        return new RGB8(val << 16 | val << 8 | val);
    }

    /**
     * Converts the specified color into an instance of RGB8, or returns the
     * color instance if it is already a RGB8 instance.
     * 
     * @param color
     *            the color to convert
     * @return the RGB8 color corresponding to the input color
     */
    public static final RGB8 fromColor(Color color)
    {
        if (color instanceof RGB8) return (RGB8) color;
        int r = UInt8.convert(color.red() * 255 + 0.5);
        int g = UInt8.convert(color.green() * 255 + 0.5);
        int b = UInt8.convert(color.blue() * 255 + 0.5);
        int intCode = b << 16 | g << 8 | r;
        return new RGB8(intCode);
    }

    public static final RGB8 fromIntCode(int intCode)
    {
        return new RGB8(intCode);
    }

    public final static RGB8 fromUInt8(UInt8 value)
    {
        int val = UInt8.clamp(value.getInt());
        return new RGB8(val << 16 | val << 8 | val);
    }

    /**
     * @param intCode
     *            an RGB8 value encoded into an integer.
     * @return the maximum component of the rgb value, as an integer between 0
     *         and 255.
     */
    public static final int maxSampleFromIntCode(int intCode)
    {
        int r = intCode & 0x00FF;
        int g = (intCode >> 8) & 0x00FF;
        int b = (intCode >> 16) & 0x00FF;
        return Math.max(Math.max(r, g), b);
    }
    
    /**
     * Computes the RGB8 color resulting from the merge of two colors.
     * 
     * @param rgb1
     *            the first color
     * @param rgb2
     *            the second color
     * @param ratio
     *            a floating-point value between 0 and 1 denoting the relative
     *            fraction of the two colors: if ratio is 0, the result equals
     *            rgb1; if ratio is 1, the result equals rgb2.
     * @return the merged color
     */
    public static final RGB8 interp(RGB8 rgb1, RGB8 rgb2, double ratio)
    {
        double op1 = 1.0 - ratio;
        double op2 = ratio;
        int r = (int) (rgb1.intRed() * op1 + rgb2.intRed() * op2);
        int g = (int) (rgb1.intGreen() * op1 + rgb2.intGreen() * op2);
        int b = (int) (rgb1.intBlue() * op1 + rgb2.intBlue() * op2);
        return new RGB8(r, g, b);
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
        int r = rgb[0] & 0x00FF;
        int g = rgb[1] & 0x00FF;
        int b = rgb[2] & 0x00FF;
        return b << 16 | g << 8 | r;   
    }
    
    public static final int intCode(int r, int g, int b)
    {
        return (b & 0x00FF) << 16 | (g & 0x00FF) << 8 | (r & 0x00FF);   
    }
    
    /**
     * Converts the int code representing a RGB value into the three components
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
     * Converts the int code representing a RGB value into the three components
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

    private final int intCode;
    

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
        int r = UInt8.clamp(red);
        int g = UInt8.clamp(green);
        int b = UInt8.clamp(blue);
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
        int r = UInt8.clamp(rgb[0]);
        int g = UInt8.clamp(rgb[1]);
        int b = UInt8.clamp(rgb[2]);
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
        int r = UInt8.convert(red);
        int g = UInt8.convert(green);
        int b = UInt8.convert(blue);
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
        int r = UInt8.convert(color.red() * 255 + 0.5);
        int g = UInt8.convert(color.green() * 255 + 0.5);
        int b = UInt8.convert(color.blue() * 255 + 0.5);
        this.intCode = b << 16 | g << 8 | r;
    }
    

    // =============================================================
    // General methods

    /**
     * Returns the int-based representation of this RGB8 element.
     * 
     * @return an int-based representation of this RGB8 color.
     */
    public int intCode()
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
     * @return an integer value corresponding to the maximum value within channels.
     */
    public int maxSample()
    {
        return maxSampleFromIntCode(this.intCode);
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
     * @return the red component of this color, as an integer between 0 and 255.
     */
    public double intRed()
    {
        return this.intCode & 0x00FF;
            
    }
    
    /**
     * @return the green component of this color, between 0 and 1.
     */
    public double green()
    {
        return ((this.intCode >> 8) & 0x00FF) / 255.0;
            
    }
    
    /**
     * @return the green component of this color, as an integer between 0 and 255.
     */
    public double intGreen()
    {
        return (this.intCode >> 8) & 0x00FF;
            
    }
    
    /**
     * @return the blue component of this color, between 0 and 1.
     */
    public double blue()
    {
        return ((this.intCode >> 16) & 0x00FF) / 255.0;
    }
    
    /**
     * @return the blue component of this color, as an integer between 0 and 255.
     */
    public double intBlue()
    {
        return (this.intCode >> 16) & 0x00FF;
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
        
        return cmax > 0 ? (cmax - cmin) / cmax : 0.0;
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
     * @return the reference to the array of integer component values
     */
    public int[] getSamples()
    {
        return getSamples(new int[3]);
    }
    
    /**
     * Returns the red, green and blue values into the pre-allocated array.
     * 
     * @param rgb
     *            a preallocated array with three elements
     * @return the reference to the array of integer component values
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
        return switch (channel)
        {
            case 0 -> this.intCode & 0x00FF;
            case 1 -> (this.intCode >> 8) & 0x00FF;
            case 2 -> (this.intCode >> 16) & 0x00FF;
            default -> throw new IllegalArgumentException("Channel number must be comprised between 0 and 2");
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.type.Vector#getValues()
     */
    @Override
    public double[] getValues()
    {
        return getValues(new double[3]);
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
    // Implementation of the Numeric interface
    
    public RGB8 one()
    {
        return new RGB8(1,0,0);
    }

    @Override
    public RGB8 zero()
    {
        return new RGB8(0,0,0);
    }

    @Override
    public RGB8 plus(RGB8 other)
    {
        int r = this.getSample(0) + other.getSample(0);
        int g = this.getSample(1) + other.getSample(1);
        int b = this.getSample(2) + other.getSample(2);
        return new RGB8(r, g, b);
    }

    @Override
    public RGB8 minus(RGB8 other)
    {
        int r = this.getSample(0) - other.getSample(0);
        int g = this.getSample(1) - other.getSample(1);
        int b = this.getSample(2) - other.getSample(2);
        return new RGB8(r, g, b);
    }

    @Override
    public RGB8 times(double k)
    {
        int r = (int) (this.getSample(0) * k);
        int g = (int) (this.getSample(1) * k);
        int b = (int) (this.getSample(2) * k);
        return new RGB8(r, g, b);
    }

    @Override
    public RGB8 divideBy(double k)
    {
        int r = (int) (this.getSample(0) / k);
        int g = (int) (this.getSample(1) / k);
        int b = (int) (this.getSample(2) / k);
        return new RGB8(r, g, b);
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

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (obj instanceof RGB8 that)
        { return this.intCode == that.intCode; }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(intCode);
    }
}
