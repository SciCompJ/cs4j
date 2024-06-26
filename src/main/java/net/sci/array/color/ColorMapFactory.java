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
    public ColorMap createColorMap(int nColors);
}
