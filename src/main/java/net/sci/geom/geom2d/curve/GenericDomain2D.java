/**
 * 
 */
package net.sci.geom.geom2d.curve;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Domain2D;
import net.sci.geom.geom2d.Point2D;

/**
 * @author dlegland
 *
 */
public class GenericDomain2D implements Domain2D
{
	Boundary2D boundary;
	
	/**
	 * 
	 */
	public GenericDomain2D(Boundary2D boundary)
	{
		this.boundary = boundary;
	}

	@Override
	public boolean contains(Point2D point, double eps)
	{
		return this.boundary.signedDistance(point) <= eps;
	}

	@Override
	public double distance(double x, double y)
	{
		return Math.max(this.boundary.signedDistance(x, y), 0);
	}

	@Override
	public boolean isBounded()
	{
		// TODO should also manage unbounded domains
		return this.boundary.isBounded();
	}

	/**
	 * Simply returns the boundary curve referenced by this domain.
	 * 
	 * @return the inner boundary curve
	 */
	@Override
	public Boundary2D boundary()
	{
		return this.boundary;
	}

	
    // ===================================================================
    // Methods implementing the Geometry2D interface
    
	@Override
	public boolean contains(Point2D point)
	{
		return this.boundary.isInside(point);
	}

	@Override
	public boolean contains(double x, double y)
	{
		return this.boundary.isInside(x, y);
	}

	@Override
	public Box2D boundingBox()
	{
		// TODO should also manage unbounded domains
		return boundary.boundingBox();
	}
}
