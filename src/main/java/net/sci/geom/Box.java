/**
 * 
 */
package net.sci.geom;

/**
 * Used to defined the bounding box of geometries.
 * 
 * @author dlegland
 *
 */
public interface Box
{
    /**
     * @param d the dimension index
     * @return Returns the lowest coordinate of the geometry in the given dimension.
     */
    double getMin(int d);

    /**
     * @param d the dimension index
     * @return Returns the highest coordinate of the geometry in the given dimension.
     */
    double getMax(int d);
}
