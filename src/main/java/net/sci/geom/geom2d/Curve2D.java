/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A continuous curve embedded in the 2D plane.
 * 
 * @author dlegland
 *
 */
public interface Curve2D extends CurveShape2D
{
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
}
