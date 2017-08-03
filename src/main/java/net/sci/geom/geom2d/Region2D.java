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
     * Returns the curve that bounds this region.
     * 
     * @return the boundary curve of this region
     */
    public CurveShape2D boundary();
    
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
    
    /**
     * Checks if the point specified by the two coordinates is contained within
     * this region.
     * 
     * The behavior for points located on the boundary is undefined.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return true is the point is located within the region.
     */
    public boolean contains(double x, double y);
}
