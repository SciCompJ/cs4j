/**
 * 
 */
package net.sci.geom;

import net.sci.array.Dimensional;

/**
 * Used to defined the bounding box of geometries.
 * 
 * @author dlegland
 *
 */
public interface Bounds extends Dimensional
{
    /**
     * Computes the size of the bounds in the specified dimension. May be
     * infinite, or NaN.
     * 
     * @param d
     *            the dimension index
     * @return the size of the bounds in the specified dimension
     */
    double size(int d);
    
    /**
     * Checks if the bounds are finite.
     *
     * @return true if all the bounding limits have finite values.
     */
    public boolean isFinite();
    
    /**
     * Returns the smallest bounds limit in the specified dimension.
     * 
     * @param d
     *            the dimension index
     * @return Returns the smallest coordinate of the geometry in the given
     *         dimension.
     */
    double minCoord(int d);

    /**
     * Returns the largest bounds limit in the specified dimension.
     * 
     * @param d
     *            the dimension index
     * @return Returns the largest coordinate of the geometry in the given
     *         dimension.
     */
    double maxCoord(int d);
}
