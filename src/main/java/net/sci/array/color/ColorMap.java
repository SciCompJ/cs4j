/**
 * 
 */
package net.sci.array.color;

/**
 * General definition of a color map, that can contain an arbitrary number of
 * colors.
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
     * Return the number of colors stored within this color map
     * 
     * @return the number of colors stored within this color map
     */
    public int size();
}
