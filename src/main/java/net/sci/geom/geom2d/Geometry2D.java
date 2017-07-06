/**
 * 
 */
package net.sci.geom.geom2d;

import net.sci.geom.Geometry;

/**
 * A shape embedded into a 2-dimensional space.
 * 
 * @author dlegland
 *
 */
public interface Geometry2D extends Geometry
{
    
    /**
	 * Checks if the geometry contains the given point, with a given precision.
	 * 
	 * @param point
	 *            the point to test
	 * @param eps
	 *            the tolerance to use for distance comparison
	 * @return true if the point is inside this geometry, with respect to the
	 *         given tolerance
	 */
    public boolean contains(Point2D point, double eps);
    
    /**
     * @param point
     *            a point in the same space
     * @return the Euclidean distance between this geometry and the specified point
     */
    public double distance(Point2D point);
    
    /**
     * @return a dimensionality value equals to 2.
     */
    @Override
    public default int dimensionality()
    {
        return 2;
    }

    /**
     * @return the bounds of this geometry.
     */
    public Box2D boundingBox();
}
