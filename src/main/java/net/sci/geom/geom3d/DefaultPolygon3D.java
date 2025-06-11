/**
 * 
 */
package net.sci.geom.geom3d;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polygon2D;
import net.sci.geom.geom3d.polyline.LinearRing3D;

/**
 * A 3D polygon whose boundary is a single (3D) linear ring.
 * 
 * @author dlegland
 * 
 * @see LinearRing3D
 */
public class DefaultPolygon3D implements Polygon3D
{
    // ===================================================================
    // Class variables
    
    /**
     * The inner ordered list of vertices. The last point is connected to the
     * first one.
     */
    protected ArrayList<Point3D> vertices;
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Empty constructor: no vertex.
     */
    public DefaultPolygon3D()
    {
        vertices = new ArrayList<Point3D>();
    }
    
    public DefaultPolygon3D(Collection<? extends Point3D> points)
    {
        this.vertices = new ArrayList<Point3D>(points.size());
        this.vertices.addAll(points);
    }

    /**
     * Constructor from an array of points
     * 
     * @param vertices
     *            the vertices stored in an array of Point3D
     */
    public DefaultPolygon3D(Point3D... vertices)
    {
        this.vertices = new ArrayList<Point3D>(vertices.length);
        for (Point3D vertex : vertices)
            this.vertices.add(vertex);
    }
    
    /**
     * Constructor from three arrays, one for each coordinate. All arrays must
     * have the same length.
     * 
     * @param xcoords
     *            the x-coordinate of each vertex
     * @param ycoords
     *            the y-coordinate of each vertex
     * @param zcoords
     *            the z-coordinate of each vertex
     */
    public DefaultPolygon3D(double[] xcoords, double[] ycoords, double[] zcoords)
    {
        this.vertices = new ArrayList<Point3D>(xcoords.length);
        for (int i = 0; i < xcoords.length; i++)
        {
            this.vertices.add(new Point3D(xcoords[i], ycoords[i], zcoords[i]));
        }
    }
    
    /**
	 * Ensures the polygon has enough memory for storing the required number of
	 * vertices.
	 * 
	 * @param nVertices
	 *            the estimated number of vertices the polygon will contain.
	 */
    public DefaultPolygon3D(int nVertices)
    {
        this.vertices = new ArrayList<Point3D>(nVertices);
    }
    
    
    // ===================================================================
    // Methods implementing the PolygonalDomain2D interface
    
    /**
     * Returns an Iterable over the linear rings that constitute this polygon
     * boundary.
     * 
     * @return an Iterable over the linear rings that constitute this polygon
     *         boundary.
     */
    public Iterable<LinearRing3D> rings()
    {
        ArrayList<LinearRing3D> rings = new ArrayList<LinearRing3D>(1); 
        rings.add(LinearRing3D.create(this.vertices));
        return rings;
    }


//    /**
//     * Computes the signed area of this polygon. 
//     * 
//     * Algorithm is taken from the following page:
//     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
//     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
//     * is positive if polygon is oriented counter-clockwise, and negative
//     * otherwise. Result is wrong if polygon is self-intersecting.
//     * 
//     * @return the signed area of the polygon.
//     */
//    public double signedArea() {
//        double area = 0;
//        
//        // number of vertices
//        int n = this.vertices.size();
//    
//        // initialize with the last vertex
//        Point3D prev = this.vertices.get(n-1);
//        
//        // iterate on edges
//        for (Point3D point : vertices)
//        {
//            area += prev.x() * point.y() - prev.y() * point.x();
//            prev = point;
//        }
//        
//        return area /= 2;
//    }

    // ===================================================================
    // Implementation of the Polygon2D interface
    
    /**
     * Returns the vertex positions of this polygon. The result is a pointer to the inner
     * collection of vertex positions.
     * 
     * @return a reference to the inner vertex positions array
     */
    public Collection<Point3D> vertexPositions() 
    {
        return vertices;
    }
        
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexCount()
    {
        return this.vertices.size();
    }

    public void addVertex(Point3D vertexPosition)
    {
        this.vertices.add(vertexPosition);
    }
    
    public void removeVertex(int vertexIndex)
    {
        this.vertices.remove(vertexIndex);
    }
    
    public Point3D vertexPosition(int vertexIndex)
    {
        return this.vertices.get(vertexIndex);
    }
    
    public Point3D closestPoint(Point3D point)
    {
        // project 3D onto the supporting plane
        Plane3D plane = supportingPlane();
        Polygon2D poly2d = embed(plane);
        
        // ensure projection is counter-clockwise
        if (poly2d.signedArea() < 0)
        {
            poly2d = poly2d.complement();
        }
        
        // project point onto the same plane
        Point2D point2d = plane.projection2d(point);
        
        // if projected point is outside polygon, project on the boundary
        if (!poly2d.contains(point2d))
        {
            point2d = poly2d.boundary().projection(point2d);
        }
        
        // convert back to a 3D point
        return plane.point(point2d.x(), point2d.y());
    }

    /**
     * Computes the index of the closest vertex to the input query point.
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
    // Implementation of the Region2D interface
    
    @Override
    public Plane3D supportingPlane()
    {
        if (vertices.size() < 3) throw new RuntimeException("Requires the polygon to have at least three vertices");
        Point3D p0 = vertices.get(0);
        Vector3D v1 = new Vector3D(p0, vertices.get(1));
        Vector3D v2 = new Vector3D(p0, vertices.get(2));
        
        // enforce second direction vector to be orthogonal to first one
        Vector3D normal = Vector3D.crossProduct(v1, v2).normalize();
        Vector3D v2b = Vector3D.crossProduct(normal, v1).normalize();
        v2b = v2b.times(Vector3D.dotProduct(v2b, v2));
        
        return new Plane3D(p0, v1, v2b);
    }

    
    // ===================================================================
    // Implementation of the Geometry2D interface
    
    /**
     * Returns true if the specified point is inside the polygon, or located on
     * its boundary.
     * 
     * @param point
     *            the point to test
     * @param eps
     *            the tolerance for computing distance
     * @return true is the point is contained within this polygon
     */
    @Override
    public boolean contains(Point3D point, double eps)
    {
        // first check if point is within supporting plane
        Plane3D plane = supportingPlane();
        if (!plane.contains(point, eps)) return false;
        
        // project 3D onto the supporting plane
        Polygon2D poly2d = embed(plane);
        
        // ensure projection is counter-clockwise
        if (poly2d.signedArea() < 0)
        {
            poly2d = poly2d.complement();
        }
        
        // project point onto the same plane, and test insideness
        Point2D point2d = plane.projection2d(point);
        if (poly2d.contains(point2d)) return true;

        // also test if point belong to boundary
        if (this.boundaryContains(point, eps))
            return true;
        
        return false;
    }

    private Polygon2D embed(Plane3D plane)
    {
        // project 3D onto the supporting plane
        ArrayList<Point2D> verts2d = new ArrayList<Point2D>(this.vertexCount());
        for (Point3D p : vertices)
        {
            verts2d.add(plane.projection2d(p));
        }
        return Polygon2D.create(verts2d);
    }
    
    private boolean boundaryContains(Point3D point, double eps)
    {
        // Extract the vertex of the collection
        Point3D previous = vertices.get(vertices.size() - 1);
        
        // iterate over pairs of adjacent vertices
        for (Point3D current : this.vertices)
        {
            LineSegment3D edge = new LineSegment3D(previous, current);
            // avoid problem of degenerated line segments
            if (edge.length() == 0)
                continue;
            if (edge.contains(point, eps))
                return true;
            
            previous = current;
        }
        
        return false;
    }
    
    /**
     * Returns the distance to the boundary of this polygon, or zero if the
     * point is inside the polygon.
     */
    @Override
    public double distance(double x, double y, double z)
    {
        return closestPoint(new Point3D(x, y, z)).distance(x, y, z);
    }
    

    // ===================================================================
    // Implementation of the Geometry interface
    
    /**
     * Returns always true.
     */
    public boolean isBounded()
    {
        return true;
    }

    /**
     * Returns the bounding box of this polygon.
     * 
     * @see net.sci.geom.geom2d.Geometry2D#bounds()
     */
    @Override
    public Bounds3D bounds()
    {
        return Bounds3D.of(vertices);
    }
    
    @Override
    public Polygon3D duplicate()
    {
        return new DefaultPolygon3D(this.vertices);
    }

}
