/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Point2D;

/**
 * A polygonal region whose boundary is a single linear ring.
 * 
 * @author dlegland
 * 
 * @see LinearRing2D
 */
public class DefaultPolygon2D implements Polygon2D
{
    // ===================================================================
    // Class variables
    
    /**
     * The inner ordered list of vertices. The last point is connected to the
     * first one.
     */
    protected ArrayList<Point2D> vertices;
    protected DefaultLinearRing2D boundary;
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Empty constructor: no vertex.
     */
    public DefaultPolygon2D()
    {
        vertices = new ArrayList<Point2D>();
        this.boundary = new DefaultLinearRing2D();
    }
    
    public DefaultPolygon2D(Collection<? extends Point2D> points)
    {
        this.vertices = new ArrayList<Point2D>(points.size());
        this.vertices.addAll(points);
        this.boundary = new DefaultLinearRing2D(points);
    }

    /**
     * Constructor from an array of points
     * 
     * @param vertices
     *            the vertices stored in an array of Point2D
     */
    public DefaultPolygon2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
            this.vertices.add(vertex);
        this.boundary = new DefaultLinearRing2D(vertices);
    }
    
    /**
     * Constructor from two arrays, one for each coordinate.
     * 
     * @param xcoords
     *            the x coordinate of each vertex
     * @param ycoords
     *            the y coordinate of each vertex
     */
    public DefaultPolygon2D(double[] xcoords, double[] ycoords)
    {
        this.vertices = new ArrayList<Point2D>(xcoords.length);
        for (int i = 0; i < xcoords.length; i++)
            this.vertices.add(new Point2D(xcoords[i], ycoords[i]));
        this.boundary = new DefaultLinearRing2D(vertices);
    }
    
    /**
	 * Ensures the polygon has enough memory for storing the required number of
	 * vertices.
	 * 
	 * @param nVertices
	 *            the estimated number of vertices the polygon will contain.
	 */
    public DefaultPolygon2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
        this.boundary = new DefaultLinearRing2D(nVertices);
    }
    
    
    // ===================================================================
    // Methods implementing the PolygonalDomain2D interface
    
    @Override
    public Iterable<LinearRing2D> rings()
    {
        return List.of(this.boundary);
    }

    @Override
    public DefaultPolygon2D complement()
    {
        // create a new collection of vertices in reverse order, keeping first vertex unchanged.
        int n = this.boundary.vertexCount();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        newVertices.add(this.boundary.vertexPosition(0));
        for (int i = 1; i < n; i++)
        {
            newVertices.add(this.boundary.vertexPosition(n-i));
        }
        
        // create a new SimplePolygon2D with this new set of vertices
        return new DefaultPolygon2D(newVertices);
    }

    /**
     * Computes the signed area of this polygon. 
     * 
     * Algorithm is taken from the following page:
     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed area
     * is positive if polygon is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polygon is self-intersecting.
     * 
     * @return the signed area of the polygon.
     */
    public double signedArea() 
    {
        return boundary.signedArea();
    }
    

    // ===================================================================
    // Implementation of the Polygonal2D interface
    
    /**
     * Returns the vertex positions of this polygon. The result is a pointer to the inner
     * collection of vertex positions.
     * 
     * @return a reference to the inner vertex positions array
     */
    public List<Point2D> vertexPositions() 
    {
        return this.boundary.vertexPositions();
    }
        
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexCount()
    {
        return this.boundary.vertexPositions().size();
    }

    @Override
    public Iterable<? extends Vertex> vertices()
    {
        return this.boundary.vertices();
    }

    @Override
    public Vertex vertex(int index)
    {
        return this.boundary.vertex(index);
    }

    public void addVertex(Point2D vertexPosition)
    {
        this.boundary.addVertex(vertexPosition);
    }
    
    public void removeVertex(int vertexIndex)
    {
        this.boundary.removeVertex(vertexIndex);
    }
    
    public Point2D vertexPosition(int vertexIndex)
    {
        return this.boundary.vertexPosition(vertexIndex);
    }
    
    /**
     * Computes the index of the closest vertex to the input query point.
     * 
     * @param point
     *            the query point
     * @return the index of the closest vertex to the query point
     */
    public int closestVertexIndex(Point2D point)
    {
        return this.boundary.closestVertexIndex(point);
    }
    
    
    // ===================================================================
    // Implementation of the Region2D interface
    
    @Override
    public LinearRing2D boundary()
    {
        return this.boundary;
    }
    
    /**
     * Returns true if the specified point is inside the polygon. 
     * No specific test is made for points on the boundary.
     */
    @Override
    public boolean contains(Point2D point)
    {
        return contains(point.x(), point.y());
    }
    
    /**
     * Returns true if the point specified by the given coordinates is inside
     * the polygon, without checking of it belongs to the boundary or not.
     * 
     * @param x
     *            the x-coordinate of the point to test
     * @param y
     *            the y-coordinate of the point to test
     * @return true if the point is located within the polygon
     * 
     * @see #signedArea()
     * @see #contains(Point2D, double)
     */
    public boolean contains(double x, double y)
    {
        return this.boundary.isInside(x, y);
    }

    
    // ===================================================================
    // Implementation of the Geometry2D interface
    
    /**
     * Returns true if the specified point is inside the polygon, or located on
     * its boundary.
     */
    @Override
    public boolean contains(Point2D point, double eps)
    {
        if (this.contains(point.x(), point.y()))
            return true;
        if (this.boundaryContains(point, eps))
            return true;
        
        return false;
    }

    private boolean boundaryContains(Point2D point, double eps)
    {
        return this.boundary.contains(point, eps);
    }
    
    /**
     * Returns the distance to the boundary of this polygon, or zero if the
     * point is inside the polygon.
     */
    @Override
    public double distance(double x, double y)
    {
        // if point is inside of the polygon returns 0
        if (this.contains(x, y))
            return 0;
        
        // computes distance to boundary
        LinearRing2D boundary = LinearRing2D.create(this.vertices);
        return boundary.distance(x, y);
    }
    

    // ===================================================================
    // Implementation of the Geometry interface
    
    /**
     * Returns true if the signed area of this polygon is greater than zero.
     */
    public boolean isBounded()
    {
        return this.signedArea() >= 0;
    }

    /**
     * Returns the bounding box of this polygon.
     * 
     * @see net.sci.geom.geom2d.Geometry2D#bounds()
     */
    @Override
    public Bounds2D bounds()
    {
        return boundary.bounds();
    }
    
    @Override
    public Polygon2D duplicate()
    {
        return new DefaultPolygon2D(this.boundary.vertexPositions());
    }
}
