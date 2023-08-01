/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.Collection;

import net.sci.geom.geom2d.polygon.LinearRing2D;
import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LinearRing3D is a polyline whose last point is connected to the first one.
 * </p>
 * 
 * @see LineString3D
 * @see net.sci.geom.geom2d.polygon.LinearRing2D
 * 
 * @author dlegland
 */
public interface LinearRing3D extends Polyline3D
{
    // ===================================================================
    // Constructors

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public static LinearRing3D create(int nVertices)
    {
        return new DefaultLinearRing3D(nVertices);
    }
    
    public static LinearRing3D create(Point3D... vertices)
    {
        return new DefaultLinearRing3D(vertices);
    }
    
    public static LinearRing3D create(Collection<? extends Point3D> vertices)
    {
        return new DefaultLinearRing3D(vertices);
    }

    
    // ===================================================================
    // Methods specific to LinearRing3D

    /**
     * Computes the index of the closest vertex to the input point.
     * 
     * @param point
     *            the query point
     * @return the index of the closest vertex to the query point
     */
    public default int closestVertexIndex(Point3D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        
        for (int i = 0; i < vertexPositions().size(); i++)
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
     * Transforms this geometry with the specified affine transform.
     * 
     * @param trans
     *            an affine transform
     * @return the transformed line string
     */
    public default LinearRing3D transform(AffineTransform3D trans)
    {
        int nv = this.vertexCount();
        LinearRing3D res = LinearRing3D.create(nv);

        for (int i = 0; i < nv; i++)
        {
            res.addVertex(this.vertexPosition(i).transform(trans));
        }
        
        return res;
    }


    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex remains the same.
     */
    @Override
    public default Polyline3D reverse()
    {
        // create a new collection of vertices in reverse order, keeping first
        // vertex unchanged.
        int nv = this.vertexCount();
        LinearRing3D res = LinearRing3D.create(nv);

        res.addVertex(this.vertexPosition(0));
        for (int i = 1; i < nv; i++)
        {
            res.addVertex(this.vertexPosition(nv-i));
        }
        
        return res;
    }
    
    @Override
    public default LinearRing2D projectXY()
    {
        LinearRing2D res = LinearRing2D.create(vertexCount());
        for(Point3D pos : vertexPositions())
        {
            res.addVertex(pos.projectXY());
        }
        return res;
    }


    // ===================================================================
    // Methods implementing the Curve3D interface
    
    @Override
    public default Point3D getPoint(double t)
    {
        t = Math.min(Math.max(t, 0), 1);
        int n = vertexCount();

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;

        if (ind0 == n)
            ind0 = 0;
        Point3D p0 = vertexPosition(ind0);
        
        // index of vertex after point
        int ind1 = ind0 + 1;
        if (ind1 == n)
            ind1 = 0;
        Point3D p1 = vertexPosition(ind1);

        // interpolate on current line;
        return Point3D.interpolate(p0, p1, tl);
    }

    @Override
    public default double t0()
    {
        return 0;
    }

    @Override
    public default double t1()
    {
        return vertexPositions().size();
    }

    @Override
    public default boolean isClosed()
    {
        return true;
    }
    
    
    @Override
    public LinearRing3D duplicate();
}
