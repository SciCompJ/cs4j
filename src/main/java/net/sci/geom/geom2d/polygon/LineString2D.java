/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Point2D;

/**
 * <p>
 * A LineString2D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * <p>
 * This interface declares methods for line strings, and provides default
 * implementations for some high-level operations that are independent of the
 * representation of vertices: smoothing, resampling....
 * </p>
 * 
 * @see DefaultLineString2D
 * @see LinearRing2D
 * 
 * @author dlegland
 */
public interface LineString2D extends Polyline2D
{
    // ===================================================================
    // Static methods
    
    public static LineString2D create(Collection<? extends Point2D> vertices)
    {
        return new DefaultLineString2D(vertices);
    }
    
    /**
     * Creates a new line string by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public static LineString2D create(int nVertices)
    {
        return new DefaultLineString2D(nVertices);
    }
    
    public static LineString2D create(Point2D... vertices)
    {
        return new DefaultLineString2D(vertices);
    }

    /**
     * Interpolates a new line string between two line strings, assuming the vertices
     * are in correspondence.
     * 
     * @param ring0
     *            the first line string, at position t=0
     * @param ring1
     *            the second line string, at position t=1
     * @param t
     *            the position of the line string to interpolate, between 0 and 1
     * @return the interpolated line string
     */
    public static LineString2D interpolate(LineString2D ring0, LineString2D ring1, double t)
    {
        // check number of vertices
        int nv = ring0.vertexCount();
        if (ring1.vertexCount() != nv)
        {
            throw new RuntimeException("Both rings must have same number of vertices");
        }
        
        // check interpolation interval
        if (t < 0 || t > 1)
        {
            throw new RuntimeException("Interpolation value must be comprised between 0 and 1.");
        }
        double t0 = t;
        double t1 = 1 - t0;
        
        // allocate memory for result
        LineString2D res = LineString2D.create(nv);
        
        // iterate over vertices
        for (int iv = 0; iv < nv; iv++)
        {
            Point2D p1 = ring0.vertexPosition(iv);
            Point2D p2 = ring1.vertexPosition(iv);
            
            double x = p1.x() * t1 + p2.x() * t0;
            double y = p1.y() * t1 + p2.y() * t0;
            res.addVertex(new Point2D(x, y));
        }

        return res;
    }
        
    
    // ===================================================================
    // Methods implementing the Polyline2D interface
    

    public default LineString2D smooth(int smoothingSize)
    {
        // compute the number of elements before and after central vertex
        // (ensuring M1+M2 = smoothingSize)
        int M1 = (int) Math.floor((smoothingSize - 1) / 2);
        int M2 = (int) Math.ceil((smoothingSize - 1) / 2);
        
        int nv = this.vertexCount();
        LineString2D res = LineString2D.create(nv);
        
        for (int i = 0; i < nv; i++)
        {
            double x = 0;
            double y = 0;
            for (int i2 = i - M1; i2 <= i + M2; i2++)
            {
                // clamp index between 0 and vertex number
                int i2c = Math.min(Math.max(i2, 0), nv - 1);
                Point2D v = vertexPosition(i2c);
                x += v.x();
                y += v.y();
            }
            x /= smoothingSize;
            y /= smoothingSize;
            
            // add new vertex
            res.addVertex(new Point2D(x, y));
        }

        return res;
    }
    
    public default LineString2D resampleBySpacing(double spacing)
    {
        // compute vertex number of resulting curve
        double length = this.length();
        int nv = (int) Math.round(length / spacing);
        
        // adjust step length to avoid last edge to have different size
        double spacing2 = length / nv;
        
        // create new vertices
        LineString2D res = LineString2D.create(nv);
        for (int i = 0; i < nv - 1; i++)
        {
            double pos = Math.min(i * spacing2, length);
            res.addVertex(getPointAtLength(pos));
        }
        
        // add a vertex corresponding to last vertex of initial polyline
        res.addVertex(this.vertexPosition(this.vertexCount() - 1));
        
        return res;
    }
    
    public default LineString2D mergeMultipleVertices(double minDist)
    {
        // Allocate memory for new vertex array
        int nv = vertexCount();
        DefaultLineString2D res = new DefaultLineString2D(nv);
        
        // start with the position of the last vertex
        Point2D lastPosition = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        
        for (Point2D pos : this.vertexPositions())
        {
            double dist = pos.distance(lastPosition);
            if (dist > minDist)
            {
                res.addVertex(pos);
                lastPosition = pos;
            }
        }
        
        return res;
    }
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex of the new line string is the last vertex of this line
     * string.
     */
    @Override
    public default LineString2D reverse()
    {
        int n = this.vertexCount();
        LineString2D reverse = LineString2D.create(n);
        for (int i = 0; i < n; i++)
        {
            reverse.addVertex(this.vertexPosition(n-1-i));
        }

        return reverse;
    }

    public default Point2D getPointAtLength(double pos)
    {
        double cumSum = 0;
        Iterator<Point2D> vertexIter = vertexPositions().iterator();
        Point2D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point2D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            if (cumSum >= pos)
            {
                double pos0 = pos - cumSum + dist;
                double t1 = pos0 / dist;
                double t0 = 1 - t1;
                
                double x = prev.x() * t0 + vertex.x() * t1;
                double y = prev.y() * t0 + vertex.y() * t1;
                return new Point2D(x, y);
            }
            prev = vertex;
        }
        return prev;
    }


    // ===================================================================
    // Management of edges
    

    // ===================================================================
    // Methods implementing the Curve2D interface
    
    @Override
    public default double getT0()
    {
        return 0;
    }

    @Override
    public default double getT1()
    {
        return vertexCount() - 1;
    }
    @Override
    public default boolean isClosed()
    {
        return false;
    }
    

    // ===================================================================
    // Implementation of Geometry methods
    
    /**
	 * Transforms this geometry with the specified affine transform.
	 * 
	 * @param trans
	 *            an affine transform
	 * @return the transformed line string
	 */
	public default LineString2D transform(AffineTransform2D trans)
	{
	    int n = this.vertexCount();
	    LineString2D res = LineString2D.create(n);
	    
	    for (Point2D pos : this.vertexPositions())
	    {
	        res.addVertex(pos.transform(trans));
	    }
	    return res;
	}

	@Override
    public LineString2D duplicate();
}
