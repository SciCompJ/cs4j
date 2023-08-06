/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.polygon.Polyline2D;

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
        double t0 = t0();
        double t1 = t1();
        double dt = (t1 - t0) / (isClosed() ? nVertices : nVertices - 1);
        
        ArrayList<Point2D> vertices = new ArrayList<>(nVertices);
        
        for (int i = 0; i < nVertices; i++)
        {
            vertices.add(point(t0 + i * dt));
        }
        
        return Polyline2D.create(vertices, this.isClosed());
    }

    /**
     * Computes the position of the point located at the specified value of
     * <code>t</code>.
     * 
     * @param t
     *            the parameterization value of the point
     * @return the position of the point
     */
    public abstract Point2D point(double t);

    /**
     * Returns the lower bound of the parameterization range.
     * 
     * @return the lower bound of the parameterization range.
     */
    public abstract double t0();
    
    /**
     * Returns the upper bound of the parameterization range.
     * 
     * @return the upper bound of the parameterization range.
     */
    public abstract double t1();

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
    
    @Override
    public Curve2D duplicate();
}
