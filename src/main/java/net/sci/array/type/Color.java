/**
 * 
 */
package net.sci.array.type;

/**
 * Abstract representation of a color.
 * 
 * The typical implementation is based on a (red, green, blue) triplet, but
 * other representations may be envisioned.
 * 
 * All the components are normalized between 0 and 1.
 * 
 * @author dlegland
 */
public interface Color
{    
    // =============================================================
    // methods

    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double red();
    
    /**
     * @return the green component of this color, between 0 and 1.
     */
    public double green();
    
    /**
     * @return the red component of this color, between 0 and 1.
     */
    public double blue();
    
    /**
     * @see http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
     * 
     * @return the hue value of this color, between 0 and 1. 
     */
    public double hue();

    /**
     * @see http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
     * 
     * @return the saturation of this color, between 0 and 1. 
     */
    public double saturation();
    
    /**
     * @return the luma / luminance of this color, between 0 and 1. 
     */
    public double luminance();
}
