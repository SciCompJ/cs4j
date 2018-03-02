/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.polygon.LineString2D;
import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom2d.polygon.Polyline2D;
import net.sci.geom.geom2d.transform.AffineTransform2D;

/**
 * A continuous curve embedded in the 2D plane.
 * 
 * @author dlegland
 *
 */
public interface Curve2D extends CurveShape2D
{
    /**
     * Converts this curve into a new Polyline2D with the specified number of
     * vertices. Depending on whether the curve is open or closed, the result
     * will be either an instance of LineString2D or of LinearRing2D.
     * 
     * Implementing classes may provide more efficient implementation.
     * 
     * @param nVertices
     *            the number of vertices of the created polyline
     * @return a new instance of Polyline2D
     */
    public default Polyline2D asPolyline(int nVertices)
    {
        double t0 = getT0();
        double t1 = getT1();
        double dt = (t1 - t0) / (isClosed() ? nVertices : nVertices + 1);
        
        ArrayList<Point2D> vertices = new ArrayList<>(nVertices);
        for (int i = 0; i < nVertices; i++)
        {
            vertices.add(getPoint(t0 + i * dt));
        }
        
        if (isClosed())
            return new LinearRing2D(vertices);
        else
            return new LineString2D(vertices);
    }

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
