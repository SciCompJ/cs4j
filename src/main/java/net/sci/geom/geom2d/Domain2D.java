/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.geom2d.curve.Boundary2D;

/**
 * A planar domain, such as a polygon, or the interior of a disc.
 * 
 * The boundary of a planar domain is an instance of Boundary2D, that inherits
 * the CurveShape2D interface.
 *
 * @see net.sci.geom.geom2d.polygon.PolygonalDomain2D
 * @see net.sci.geom.geom2d.curve.Boundary2D
 * 
 * @author dlegland
 */
public interface Domain2D extends Geometry2D
{
    /**
     * Returns the curve that bounds this domain.
     * 
     * @return the boundary curve of this domain
     */
    public Boundary2D boundary();
    
    /**
     * Checks if the point is contained within this domain.
     * 
     * The behavior for points located on the boundary is undefined.
     * 
     * @param point
     *            the point to test
     * @return true is the point is located within the domain.
     */
    public boolean contains(Point2D point);
    
    /**
     * Checks if the point specified by the two coordinates is contained within
     * this domain.
     * 
     * The behavior for points located on the boundary is undefined.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return true is the point is located within the domain.
     */
    public boolean contains(double x, double y);
}
