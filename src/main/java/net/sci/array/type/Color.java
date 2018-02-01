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
    // Constants

    public static final Color WHITE = new RGB8(255, 255, 255);
    public static final Color BLACK = new RGB8(0, 0, 0);
    public static final Color RED = new RGB8(255, 0, 0);
    public static final Color GREEN = new RGB8(0, 255, 0);
    public static final Color BLUE = new RGB8(0, 0, 255);
    public static final Color CYAN = new RGB8(0, 255, 255);
    public static final Color MAGENTA = new RGB8(255, 0, 255);
    public static final Color YELLOW = new RGB8(255, 255, 0);
    public static final Color GRAY = new RGB8(127, 127, 127);
    public static final Color DARK_GRAY = new RGB8(63, 63, 63);
    public static final Color LIGHT_GRAY = new RGB8(191, 191, 191);

    
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
