/**
 * 
 */
package net.sci.geom;

import net.sci.array.Dimensional;

/**
 * A point in a N-dimensional space.
 *
 * @author dlegland
 *
 */
public interface Point extends Dimensional
{
    /**
     * Returns the given coordinate of this multidimensional point
     * 
     * @param dim
     *            dimension index of the coordinate (between 0 and nd-1)
     * @return the coordinate for the given dimension
     */
    public double get(int dim);
}
