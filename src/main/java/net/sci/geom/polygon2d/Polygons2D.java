/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.polygon2d.process.GiftWrappingConvexHull2D;

/**
 * A set of static methods operating on polygons.
 * 
 * @author dlegland
 *
 */
public class Polygons2D
{
	/**
     * Computes the convex hull of a set of points and return the result as a
     * single Polygon2D.
     * 
     * Uses Jarvis algorithm, also known as "Gift wrap" algorithm.
     * 
     * 
     * @param points
     *            a set of points in the 2D space
     * @return the convex hull of the points, as a Polygon2D
     * 
     * @see net.sci.geom.polygon2d.process.GiftWrappingConvexHull2D 
     */
	public static final Polygon2D convexHull(Collection<? extends Point2D> points)
	{
		GiftWrappingConvexHull2D algo = new GiftWrappingConvexHull2D();
		return algo.process(points);
	}
	
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private Polygons2D()
	{
	}
}
