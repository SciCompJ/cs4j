/**
 * 
 */
package net.sci.image;

import net.sci.array.type.Color;

/**
 * Color map definition for intensity or label images.
 * 
 * @author dlegland
 *
 */
public interface ColorMap
{
    /**
     * Returns the color from the specified index as an instance of RGB8.
     * 
     * @param i
     *            the index of the color, 0-based
     * @return the color corresponding to the index.
     */
    public Color getColor(int i);
    
    /**
     * @return the number of colors stored in this color map
     */
    public int size();
}
