/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom3d.transform.AffineTransform3D;

/**
 * A continuous curve embedded in the 3D space.
 * 
 * @author dlegland
 *
 */
public interface Curve3D extends CurveShape3D
{
    public abstract Point3D point(double t);

    public abstract double getT0();
    public abstract double getT1();
    
    /**
     * @return true if this curve is closed.
     */
    boolean isClosed();
    
    /**
     * Returns a collection of curves that contains only this curve.
     * 
     * @returns a collection of curve containing this curve.
     */
    @Override
    public default Collection<? extends Curve3D> curves() 
    {
        ArrayList<Curve3D> res = new ArrayList<Curve3D>(1);
        res.add(this);
        return res;
    }
    
    /**
     * Returns the result of the given transformation applied to this curve.
     * 
     * @param trans
     *            the transformation to apply
     * @return the transformed curve
     */
    public Curve3D transform(AffineTransform3D trans);
}
