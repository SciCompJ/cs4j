/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.Arrays;
import java.util.Collection;

/**
 * A continuous curve embedded in the 3D space.
 * 
 * @author dlegland
 *
 */
public interface Curve3D extends CurveShape3D
{
    public abstract Point3D getPoint(double t);

    public abstract double getT0();
    public abstract double getT1();
    
    /**
     * @return true if this curve is closed.
     */
    boolean isClosed();
    
    /**
     * Returns a collection of curves that contains only this curve.
     * 
     * @return a collection of curves containing this curve.
     */
    @Override
    public default Collection<? extends Curve3D> curves() 
    {
        return Arrays.asList(this);
    }
    
    /**
     * Returns the result of the given transformation applied to this curve.
     * 
     * @param trans
     *            the transformation to apply
     * @return the transformed curve
     */
    public Curve3D transform(AffineTransform3D trans);
    
    @Override
    public Curve3D duplicate();
}
