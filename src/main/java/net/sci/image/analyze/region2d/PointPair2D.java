/**
 * 
 */
package net.sci.image.analyze.region2d;

import net.sci.geom.geom2d.Point2D;

/**
 * A pair of points in the plane, useful for representing result of Max Feret
 * Diameter computation or similar problems.
 * 
 * Simply contains the reference to each extremity.
 * 
 * @author dlegland
 *
 */
public class PointPair2D
{	
	public final Point2D p1;
	public final Point2D p2;

	public PointPair2D(Point2D p1, Point2D p2)
	{
		this.p1 = p1;
		this.p2 = p2;
	}

	public double diameter()
	{
		double dx = p2.x() - p1.x();
		double dy = p2.y() - p1.y();
		return Math.hypot(dx,  dy);
	}

	public double angle()
	{
		double dx = p2.x() - p1.x();
		double dy = p2.y() - p1.y();
		return Math.atan2(dy, dx);
	}

}
