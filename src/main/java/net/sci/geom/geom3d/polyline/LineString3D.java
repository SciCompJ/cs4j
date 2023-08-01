/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom2d.polygon.LineString2D;
import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LineString3D is an open polyline whose last point is NOT connected to the
 * first one.
 * </p>
 * 
 * @see LinearRing3D
 * @see net.sci.geom.geom2d.polygon.LineString2D
 * 
 * @author dlegland
 */
public interface LineString3D extends Polyline3D
{
    // ===================================================================
    // Constructors

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public static LineString3D create(int nVertices)
    {
        return new DefaultLineString3D(nVertices);
    }
    
    public static LineString3D create(Point3D... vertices)
    {
        return new DefaultLineString3D(vertices);
    }
    
    public static LineString3D create(Collection<? extends Point3D> vertices)
    {
        return new DefaultLineString3D(vertices);
    }

    
 
    // ===================================================================
    // New methods
    
    public default LineString3D resampleBySpacing(double spacing)
    {
        // compute vertex number of resulting curve
        double length = this.length();
        int nv = (int) Math.round(length / spacing);
        
        // adjust step length to avoid last edge to have different size
        double spacing2 = length / nv;
        
        // create new vertices
        LineString3D res = LineString3D.create(nv);
        for (int i = 0; i < nv - 1; i++)
        {
            double pos = Math.min(i * spacing2, length);
            res.addVertex(getPointAtLength(pos));
        }
        
        // add a vertex corresponding to last vertex of initial polyline
        res.addVertex(this.vertexPosition(this.vertexCount() - 1));
        
        return res;
    }
        
    public default Point3D getPointAtLength(double pos)
    {
        double cumSum = 0;
        Iterator<Point3D> vertexIter = vertexPositions().iterator();
        Point3D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point3D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            if (cumSum >= pos)
            {
                double pos0 = pos - cumSum + dist;
                double t1 = pos0 / dist;
                return Point3D.interpolate(prev, vertex, t1);
            }
            prev = vertex;
        }
        return prev;
    }
    

    // ===================================================================
    
    /**
     * Computes the index of the closest vertex to the input query point.
     * 
     * @param point
     *            the query point
     * @return the index of the closest vertex to the query point
     */
    public default int closestVertexIndex(Point3D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        
        for (int i = 0; i < vertexCount(); i++)
        {
            double dist = vertexPosition(i).distance(point);
            if (dist < minDist)
            {
                index = i;
                minDist = dist;
            }
        }
        
        return index;
    }
       

    // ===================================================================
    // Methods implementing the Polyline3D interface
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex of the new line string is the last vertex of this line
     * string.
     */
    @Override
    public default LineString3D reverse()
    {
        int n = this.vertexCount();
        ArrayList<Point3D> newVertices = new ArrayList<Point3D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertexPosition(n-1-i));
        }
        
        return LineString3D.create(newVertices);
    }

    @Override
    public default LineString2D projectXY()
    {
        LineString2D res = LineString2D.create(vertexCount());
        for(Point3D pos : vertexPositions())
        {
            res.addVertex(pos.projectXY());
        }
        return res;
    }

    
    // ===================================================================
    // Methods implementing the Curve2D interface
    
    public default double length()
    {
        double cumSum = 0.0;
        Iterator<Point3D> vertexIter = vertexPositions().iterator();
        Point3D prev = vertexIter.next();
        while(vertexIter.hasNext())
        {
            Point3D vertex = vertexIter.next();
            double dist = vertex.distance(prev);
            cumSum += dist;
            prev = vertex;
        }
        
        return cumSum;
    }

    @Override
    public default Point3D getPoint(double t)
    {
        // format position to stay between limits
        double t0 = this.getT0();
        double t1 = this.getT1();
        t = Math.max(Math.min(t, t1), t0);

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;
        Point3D p0 = vertexPosition(ind0);

        // check if equal to last vertex
        if (t == t1)
            return p0;

        // index of vertex after point
        int ind1 = ind0+1;
        Point3D p1 = vertexPosition(ind1);

        // interpolate on current line;
        return Point3D.interpolate(p0, p1, tl);
    }

    @Override
    public default double getT0()
    {
        return 0;
    }

    @Override
    public default double getT1()
    {
        return vertexCount();
    }
    
    @Override
    public default boolean isClosed()
    {
        return false;
    }

    // ===================================================================
    // Methods implementing the Geometry3D interface
    
    /**
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line string
     */
    public default LineString3D transform(AffineTransform3D trans)
    {
        int nv = this.vertexCount();
        LineString3D res = LineString3D.create(nv);
        for (int i = 0; i < nv; i++)
        {
            res.addVertex(this.vertexPosition(i).transform(trans));
        }
        
        return res;
    }
    
    @Override
    public LineString3D duplicate();
}
