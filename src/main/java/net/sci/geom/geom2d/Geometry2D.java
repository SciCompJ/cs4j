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
     * Checks if the shape contains the given point, with a given precision.
     */
    public boolean contains(Point2D point, double eps);
    
    /**
     * @param point
     *            a point in the same space
     * @return the Euclidean distance between this shape and the specified point
     */
    public double distance(Point2D point);
    
    /**
     * Returns dimensionality equals to 2.
     */
    @Override
    public default int dimensionality()
    {
        return 2;
    }

    public Box2D boundingBox();
}
