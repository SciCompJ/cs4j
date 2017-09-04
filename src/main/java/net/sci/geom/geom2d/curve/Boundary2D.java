/**
 * 
 */
package net.sci.geom.geom2d.curve;

import java.util.Collection;

import net.sci.geom.geom2d.CurveShape2D;
import net.sci.geom.geom2d.Point2D;

/**
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
    
    @Override
    public Collection<? extends Contour2D> curves();
}
