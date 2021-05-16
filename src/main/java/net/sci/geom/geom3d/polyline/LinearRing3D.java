/**
 * 
 */
package net.sci.geom.geom3d.polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sci.geom.geom3d.AffineTransform3D;
import net.sci.geom.geom3d.LineSegment3D;
import net.sci.geom.geom3d.Point3D;

/**
 * <p>
 * A LinearRing3D is a polyline whose last point is connected to the first one.
 * </p>
 * @author dlegland
 */
public class LinearRing3D implements Polyline3D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point3D> vertices;
    
    // ===================================================================
    // Constructors

    public LinearRing3D() 
    {
        this.vertices = new ArrayList<Point3D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices the number of vertices in this polyline
     */
    public LinearRing3D(int nVertices)
    {
        this.vertices = new ArrayList<Point3D>(nVertices);
    }
    
    public LinearRing3D(Point3D... vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.length);
        for (Point3D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public LinearRing3D(Collection<? extends Point3D> vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public LinearRing3D(double[] xcoords, double[] ycoords, double[] zcoords)
    {
        this.vertices = new ArrayList<Point3D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point3D(xcoords[i], ycoords[i], zcoords[i]));
        }
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
    public int closestVertexIndex(Point3D point)
    {
        double minDist = Double.POSITIVE_INFINITY;
        int index = -1;
        
        for (int i = 0; i < vertices.size(); i++)
        {
            double dist = vertices.get(i).distance(point);
            if (dist < minDist)
            {
                index = i;
                minDist = dist;
            }
        }
        
        return index;
    }

 
    // ===================================================================
    // Management of vertices
    
    /**
     * Returns the inner collection of vertices.
     */
    public ArrayList<Point3D> vertices()
    {
        return vertices;
    }
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexCount()
    {
        return vertices.size();
    }

    public Iterator<LineSegment3D> edgeIterator()
    {
    	return new EdgeIterator();
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
    public LinearRing3D transform(AffineTransform3D trans)
    {
        int n = this.vertexCount();
        ArrayList<Point3D> newVertices = new ArrayList<Point3D>(n);
        for (int i = 0; i < n; i++)
        {
            newVertices.add(this.vertices.get(i).transform(trans));
        }
        
        LinearRing3D res = new LinearRing3D(0);
        res.vertices = newVertices;
        return res;
    }


    // ===================================================================
    // Methods implementing the Polyline2D interface
    
    /**
     * Returns a new linear ring with same vertices but in reverse order. The
     * first vertex remains the same.
     */
    @Override
    public Polyline3D reverse()
    {
        // create a new collection of vertices in reverse order, keeping first
        // vertex unchanged.
        int n = this.vertexCount();
        ArrayList<Point3D> newVertices = new ArrayList<Point3D>(n);
        newVertices.add(this.vertices.get(0));
        for (int i = 1; i < n; i++)
        {
            newVertices.add(this.vertices.get(n-i));
        }
        
        LinearRing3D reverse = new LinearRing3D(0);
        reverse.vertices = newVertices;
        return reverse;
    }

    // ===================================================================
    // Methods implementing the Curve3D interface
    
    @Override
    public Point3D getPoint(double t)
    {
        t = Math.min(Math.max(t, 0), 1);
        int n = vertices.size();

        // index of vertex before point
        int ind0 = (int) Math.floor(t + Double.MIN_VALUE);
        double tl = t - ind0;

        if (ind0 == n)
            ind0 = 0;
        Point3D p0 = vertices.get(ind0);

//        // check if equal to a vertex
//        if (Math.abs(t - ind0) < Shape2D.ACCURACY)
//            return p0;

        // index of vertex after point
        int ind1 = ind0 + 1;
        if (ind1 == n)
            ind1 = 0;
        Point3D p1 = vertices.get(ind1);

        // position on line;
        double x0 = p0.getX();
        double y0 = p0.getY();
        double z0 = p0.getZ();
        double dx = p1.getX() - x0;
        double dy = p1.getY() - y0;
        double dz = p1.getZ() - z0;

        return new Point3D(x0 + tl * dx, y0 + tl *dy, z0 + tl *dz);
    }

    @Override
    public double getT0()
    {
        return 0;
    }

    @Override
    public double getT1()
    {
        return vertices.size();
    }

    @Override
    public boolean isClosed()
    {
        return true;
    }
    
    
    // ===================================================================
    // Edge iterator implementation
    
    class EdgeIterator implements Iterator<LineSegment3D>
    {
    	/**
    	 * Index of the first vertex of current edge
    	 */
    	int index = -1;

    	@Override
		public boolean hasNext()
		{
			return index < vertices.size() - 1;
		}

		@Override
		public LineSegment3D next()
		{
			index++;
			int index2 = (index + 1) % vertices.size();
			return new LineSegment3D(vertices.get(index), vertices.get(index2));
		}
    }
}
