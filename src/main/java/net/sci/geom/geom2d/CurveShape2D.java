/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.Collection;

import net.sci.geom.Curve;

/**
 * A geometry that is composed of one or more continuous curves.
 * 
 * @author dlegland
 *
 */
public interface CurveShape2D extends Curve, Geometry2D
{
    /**
     * @return the collection of continuous curves that forms this curve shape.
     */
    public Collection<? extends Curve2D> curves();
}
