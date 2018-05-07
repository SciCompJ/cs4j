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
     * Returns the red component of this color, coded between 0 and 1.
     * 
     * @return the red component of this color, between 0 and 1.
     */
    public double red();
    
    /**
     * Returns the green component of this color, coded between 0 and 1.
     * 
     * @return the green component of this color, between 0 and 1.
     */
    public double green();
    
    /**
     * Returns the blue component of this color, coded between 0 and 1.
     * 
     * @return the red component of this color, between 0 and 1.
     */
    public double blue();
    
    /**
     * Returns the hue component of this color, coded between 0 and 1.
     * 
     * @return the hue value of this color, between 0 and 1.
     * 
     * @see <a href= "http://www.rapidtables.com/convert/color/rgb-to-hsv.htm">
     *      http://www.rapidtables.com/convert/color/rgb-to-hsv.htm</a>
     */
    public double hue();

    /**
     * Returns the saturation component of this color, coded between 0 and 1.
     * 
     * @return the saturation of this color, between 0 and 1. 
     *  
     * @see <a href= "http://www.rapidtables.com/convert/color/rgb-to-hsv.htm">
     *      http://www.rapidtables.com/convert/color/rgb-to-hsv.htm</a>
     */
    public double saturation();
    
    /**
     * Returns the luma / luminance component of this color, coded between 0 and
     * 1.
     * 
     * @return the luma / luminance of this color, between 0 and 1.
     */
    public double luminance();
}
