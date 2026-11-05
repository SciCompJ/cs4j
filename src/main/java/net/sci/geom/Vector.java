/**
 * 
 */
package net.sci.geom;

import net.sci.array.Dimensional;

/**
 * Abstract definition of a vector in a N-dimensional space.
 */
public interface Vector extends Dimensional
{
    /**
     * Returns the given coordinate of this multi-dimensional vector.
     * 
     * @param dim
     *            dimension index of the coordinate (between 0 and nd-1)
     * @return the coordinate for the given dimension
     */
    public double coord(int dim);
}
