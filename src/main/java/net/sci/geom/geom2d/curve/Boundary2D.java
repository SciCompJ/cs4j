/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.Collection;

import net.sci.geom.geom2d.CurveShape2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A specialization of the CurveShape2D interface that represents the boundary
 * of a domain.
 * 
 * An instance of Boundary2D is composed of one or several instances of
 * Contour2D, that extends the Curve2D interface.
 * 
 * @author dlegland
 *
 */
public interface Boundary2D extends CurveShape2D
{
    /**
     * Returns the signed distance of the point to this boundary.
     * 
     * Let <em>dist</em> be the distance of the point to the curve.
     * The signed distance is defined by:
     * <ul>
     * <li> -dist if the point is inside the region defined by the boundary </li>
     * <li> +dist if the point is outside the region. </li>
     * </ul>
     * 
     * @see net.sci.geom.geom2d.distance(Point2D)
     * 
     * @param point a point in the plane
     * @return the signed distance of the point to the boundary
     */
    public double signedDistance(Point2D point);
    
    /**
     * Returns the signed distance of the point to this boundary.
     * 
     * Let <em>dist</em> be the distance of the point to the curve.
     * The signed distance is defined by:
     * <ul>
     * <li> -dist if the point is inside the region defined by the boundary </li>
     * <li> +dist if the point is outside the region. </li>
     * </ul>
     * 
     * @see net.sci.geom.geom2d.distance(Point2D)
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @return the signed distance of the point to the boundary
     */
    public double signedDistance(double x, double y);

    /**
	 * Checks if the specified point is contained within the domain bounded by
	 * this boundary.
	 * 
	 * @param point
	 *            the point to test
	 * @return true is the point is within the domain corresponding to this
	 *         boundary.
	 */
    public boolean isInside(Point2D point);

    /**
	 * Checks if the specified point is contained within the domain bounded by
	 * this boundary.
	 * 
	 * @param x
	 *            the x-coordinate of the point to test
	 * @param y
	 *            the y-coordinate of the point to test
	 * @return true is the point is within the domain corresponding to this
	 *         boundary.
	 */
    public boolean isInside(double x, double y);
    
    @Override
    public Collection<? extends Contour2D> curves();
}
