/**
 * 
 */
package net.sci.array.color;

/**
 * A factory for color maps, using the number of colors as parameter
 * 
 * @author dlegland
 */
public interface ColorMapFactory
{
    /**
     * Creates a color map with the specified number of colors.
     * 
     * @param nColors
     *            the number of colors of the color map.
     * @return the new color map.
     */
    public ColorMap createColorMap(int nColors);
}
