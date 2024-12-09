/**
 * 
 */
package net.sci.geom;

import net.sci.array.Dimensional;

/**
 * Interface for a geometric transform, that can transform a ND-point into
 * another ND-point.
 * 
 * Implementations of the <code>Transform</code> interface should implement a
 * <code>transform</code> method with the following signature:
 * 
 * <code>Point p2 = transfo.transform(p);</code>.
 * 
 * As the class of point to transform depends on dimension, this method is not
 * declared within the interface.
 * 
 * This interface considers that the dimensionality of input and output spaces
 * are the same, i.e. it does not manages projection transforms.
 * 
 * @see net.sci.geom.geom2d.Transform2D
 * @see net.sci.geom.geom3d.Transform3D
 */
public interface Transform extends Dimensional
{
    // abstract Point transform(Point p);
}
