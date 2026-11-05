/**
 * 
 */
package net.sci.geom;

/**
 * A point in a N-dimensional space.
 *
 * @author dlegland
 *
 */
public interface Point extends Geometry
{
    /**
     * Returns the given coordinate of this multi-dimensional point.
     * 
     * @param dim
     *            dimension index of the coordinate (between 0 and nd-1)
     * @return the coordinate for the given dimension
     */
    public double coord(int dim);
}
