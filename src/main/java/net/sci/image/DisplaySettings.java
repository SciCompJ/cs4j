/**
 * 
 */
package net.sci.image;

import net.sci.array.color.Color;
import net.sci.array.color.ColorMap;
import net.sci.array.color.RGB8;

/**
 * Encapsulates information necessary for representing and interpreting
 * intensity values.
 * 
 * @author dlegland
 *
 */
public class DisplaySettings
{
    // =============================================================
    // Class fields

    /**
     * The min and max displayable values of scalar images. Default is [0, 255].
     * 
     * Can be used for color images as well. In that case, display range is applied
     * to each channel identically.
     */
    double[] displayRange = new double[]{0, 255};

    /**
     * The color map for representing grayscale/intensity images, or label images. 
     */
    ColorMap colorMap = null;
    
    /**
     * The background color used to represent label or binary images.
     */
    Color backgroundColor = RGB8.BLACK;
    

    // =============================================================
    // Constructor

    /**
     * Empty constructor.
     */
    public DisplaySettings()
    {
    }
    
    
    // =============================================================
    // Accessor and modifiers

    /**
     * Returns the range of values used for displaying this image.
     * 
     * @return an array containing the min and max values to display as black an
     *         white.
     */
    public double[] getDisplayRange()
    {
        return displayRange;
    }

    public void setDisplayRange(double[] displayRange)
    {
        this.displayRange = displayRange;
    }
    
    public ColorMap getColorMap()
    {
        return this.colorMap;
    }
    
    public void setColorMap(ColorMap map)
    {
        this.colorMap = map;
    }
    
    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    // =============================================================
    // Methods
    
    public DisplaySettings duplicate()
    {
        DisplaySettings result = new DisplaySettings();
        result.displayRange = new double[] {this.displayRange[0], this.displayRange[1]};
        result.colorMap = this.colorMap; 
        result.backgroundColor = this.backgroundColor;
        return result;
    }

}
