/**
 * 
 */
package net.sci.geom.geom2d;

/**
 * @author dlegland
 *
 */
public interface Region2D extends Geometry2D
{
    /**
     * Checks if the point is contained within this region.
     * 
     * The behavior for points located on the boundary is undefined.
     * 
     * @param point
     *            the point to test
     * @return true is the point is located within the region.
     */
    public boolean contains(Point2D point);
}
