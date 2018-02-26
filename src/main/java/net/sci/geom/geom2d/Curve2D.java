/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * A continuous curve embedded in the 2D plane.
 * 
 * @author dlegland
 *
 */
public interface Curve2D extends CurveShape2D
{
    public abstract Point2D getPoint(double t);

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
    public default Collection<? extends Curve2D> curves() 
    {
        ArrayList<Curve2D> res = new ArrayList<Curve2D>(1);
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
    public Curve2D transform(AffineTransform2D trans);
}
