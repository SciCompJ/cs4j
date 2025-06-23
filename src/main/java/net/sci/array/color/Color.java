/**
 * 
 */
package net.sci.array.color;

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
     * Typical hue values:
     * <ul>
     * <li>0.000: red</li>
     * <li>0.167 (1/6): yellow</li>
     * <li>0.333 (1/3): green</li>
     * <li>0.500 (1/2): cyan</li>
     * <li>0.667 (2/3): blue</li>
     * <li>0.833 (5/6): magenta</li>
     * <li>1.000: red</li>
     * </ul>
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
