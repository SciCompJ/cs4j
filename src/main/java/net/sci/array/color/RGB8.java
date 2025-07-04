/**
 * 
 */
package net.sci.array.color;

import net.sci.array.numeric.IntVector;
import net.sci.array.numeric.UInt8;

/**
 * A color that is represented by a triplet of red, green and blue components,
 * each of them being coded as UInt8.
 * 
 * Immutable class.
 * 
 * @see RGB16
 * 
 * @author dlegland
 */
public class RGB8 implements IntVector<RGB8,UInt8>, Color
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

    /**
     * Creates a new RGB8 value from an integer code.
     * 
     * @param intCode
     *            the integer code of the color
     * @return the corresponding color as an RGB8
     */
    public static final RGB8 fromIntCode(int intCode)
    {
        return new RGB8(intCode);
    }

    /**
     * Creates a new RGB8 value from a gray value between 0 and 255, using this
     * value for each component of the color.
     * 
     * @see #grayValue()
     * 
     * @param value
     *            the integer value of each component
     * @return the corresponding color as an RGB8
     */
    public final static RGB8 fromGrayValue(int value)
    {
        int val = UInt8.clamp(value);
        return new RGB8(val << 16 | val << 8 | val);
    }

    /**
     * Creates a new RGB8 value from an UInt8 value, using this value for each
     * component of the color.
     * 
     * @param value
     *            the integer value of each component
     * @return the corresponding color as an RGB8
     */
    public final static RGB8 fromUInt8(UInt8 value)
    {
        int val = value.intValue(); // no need to clamp
        return new RGB8(val << 16 | val << 8 | val);
    }

    /**
     * Computes the gray value of the color given by its three red, green and
     * blue components.
     * 
     * @param r
     *            the value of the red component, between 0 and 255.
     * @param g
     *            the value of the green component, between 0 and 255.
     * @param b
     *            the value of the blue component, between 0 and 255.
     * @return the corresponding gray value, between 0 and 255.
     */
    public final static int grayValue(int r, int g, int b)
    {
        return UInt8.convert(0.2989 * r + 0.5870 * g + 0.1140 * b);
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
     * @param rgb
     *            the three values of red, green and blue components
     * @return the corresponding intCode
     */
    public static final int intCode(int[] rgb)
    {
        return intCode(rgb[0], rgb[1], rgb[2]);
    }
    
    /**
     * Computes the integer code corresponding to the triplet of channel values
     * given as three integers between 0 and 255.
     * 
     * @param r
     *            the value of the red channel, between 0 and 255
     * @param g
     *            the value of the green channel, between 0 and 255
     * @param b
     *            the value of the blue channel, between 0 and 255
     * @return the corresponding integer code
     */
    public static final int intCode(int r, int g, int b)
    {
        return UInt8.clamp(r) << 16 | UInt8.clamp(g) << 8 | UInt8.clamp(b);
    }
    
    /**
     * Converts the int code representing a RGB value into the three components
     * as integers.
     * 
     * @param intCode
     *            the integer code of the RGB color
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

    /**
     * The integer value corresponding to the concatenation of the red, green
     * and blue components, each encoded with 8 bits.
     * 
     * {@code intCode = 0x00BBGGRR}
     */
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
     * @param rgb
     *            the array of component values
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
     * Converts this RGB8 into its corresponding gray value. The resulting gray
     * value ranges between 0 and 255.
     * 
     * @see #fromGrayValue(int)
     * 
     * @return the gray value corresponding to this color, between 0 and 255.
     */
    public int grayValue()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        return grayValue(r, g, b);
    }

    /**
     * Converts this RGB8 value into an instance of UInt8, by computing the
     * luminance of the color.
     * 
     * @return the UInt8 instance corresponding to this color.
     */
    public UInt8 toUInt8()
    {
        return new UInt8(grayValue());
    }
    
    /**
     * Returns the maximum value within channels.
     * 
     * @return an integer value corresponding to the maximum value within
     *         channels.
     */
    public int maxSample()
    {
        return maxSampleFromIntCode(this.intCode);
    }
    

    // =============================================================
    // Extraction of color components
    
    /**
     * Returns the red component of this color.
     * 
     * @return the red component of this color, between 0 and 1.
     */
    public double red()
    {
        return (this.intCode & 0x00FF) / 255.0;
            
    }
    
    /**
     * Returns the red component of this color as an integer value.
     * 
     * @return the red component of this color, as an integer between 0 and 255.
     */
    public int intRed()
    {
        return this.intCode & 0x00FF;
    }
    
    /**
     * Returns the green component of this color.
     * 
     * @return the green component of this color, between 0 and 1.
     */
    public double green()
    {
        return ((this.intCode >> 8) & 0x00FF) / 255.0;
    }
    
    /**
     * Returns the green component of this color as an integer value.
     * 
     * @return the green component of this color, as an integer between 0 and 255.
     */
    public int intGreen()
    {
        return (this.intCode >> 8) & 0x00FF;
    }
    
    /**
     * Returns the blue component of this color.
     * 
     * @return the blue component of this color, between 0 and 1.
     */
    public double blue()
    {
        return ((this.intCode >> 16) & 0x00FF) / 255.0;
    }
    
    /**
     * Returns the blue component of this color as an integer value.
     * 
     * @return the blue component of this color, as an integer between 0 and 255.
     */
    public int intBlue()
    {
        return (this.intCode >> 16) & 0x00FF;
    }

    public double hue()
    {
        int r = this.intCode & 0x00FF;
        int g = (this.intCode >> 8) & 0x00FF;
        int b = (this.intCode >> 16) & 0x00FF;
        
        // max components
        double cmax = Math.max(Math.max(r, g), b);
        double cmin = Math.min(Math.min(r, g), b);
        double delta = cmax - cmin;
        
        // case of gray colors.
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
     * Returns the luminance (or luma) of this color, obtained as a linear
     * combination of the three channel values. 
     * 
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
            default -> throw new IllegalArgumentException("Channel index must be comprised between 0 and 2");
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sci.array.numeric.Vector#getValues()
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
     * @see net.sci.array.numeric.Vector#getValue(int)
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
        int r = Math.min(this.getSample(0) + other.getSample(0), UInt8.MAX_INT);
        int g = Math.min(this.getSample(1) + other.getSample(1), UInt8.MAX_INT);
        int b = Math.min(this.getSample(2) + other.getSample(2), UInt8.MAX_INT);
        return RGB8.fromIntCode(b << 16 | g << 8 | r);
    }

    @Override
    public RGB8 minus(RGB8 other)
    {
        int r = Math.max(this.getSample(0) - other.getSample(0), 0);
        int g = Math.max(this.getSample(1) - other.getSample(1), 0);
        int b = Math.max(this.getSample(2) - other.getSample(2), 0);
        return RGB8.fromIntCode(b << 16 | g << 8 | r);
    }

    /**
     * Always returns the color BLACK, that corresponds to a triplet of negative
     * values clamped between 0 and 255.
     * 
     * @return the color RGB8.BLACK
     */
    @Override
    public RGB8 opposite()
    {
        return RGB8.BLACK;
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
        {
            return this.intCode == that.intCode;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return Integer.hashCode(intCode);
    }
}
