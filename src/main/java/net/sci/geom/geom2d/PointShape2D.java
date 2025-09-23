/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.Collection;

/**
 * A geometry in the 2D plane composed of a single or multiple individual
 * isolated point(s).
 * 
 * @see Point2D
 * @see MultiPoint2D
 */
public interface PointShape2D extends Geometry2D
{
    // ===================================================================
    // New methods
    
    public int pointCount();
    
    /**
     * Returns the points that compose this point shape.
     * 
     * @return the collection of points that forms this point shape.
     */
    public Collection<? extends Point2D> points();

    
    // ===================================================================
    // Specialization of the Geometry interface

    /**
     * Returns true, as a point or a point set is bounded by definition.
     */
    public default boolean isBounded()
    {
        return true;
    }

    @Override
    public PointShape2D duplicate();
}
