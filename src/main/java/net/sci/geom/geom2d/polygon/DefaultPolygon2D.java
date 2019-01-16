/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.LineSegment2D;
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
    
    
    // ===================================================================
    // Constructors
    
    /**
     * Empty constructor: no vertex.
     */
    public DefaultPolygon2D()
    {
        vertices = new ArrayList<Point2D>();
    }
    
    public DefaultPolygon2D(Collection<? extends Point2D> points)
    {
        this.vertices = new ArrayList<Point2D>(points.size());
        this.vertices.addAll(points);
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
    }
    
    /**
     * Ensures the polygon has enough memory for storing the required number of
     * vertices.
     */
    private DefaultPolygon2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    
    // ===================================================================
    // Methods implementing the PolygonalDomain2D interface
    
    @Override
    public Iterable<LinearRing2D> rings()
    {
        ArrayList<LinearRing2D> rings = new ArrayList<LinearRing2D>(1); 
        rings.add(new LinearRing2D(this.vertices));
        return rings;
    }

    @Override
    public DefaultPolygon2D complement()
    {
        // create a new collection of vertices in reverse order, keeping first vertex unchanged.
        int n = this.vertexNumber();
        ArrayList<Point2D> newVertices = new ArrayList<Point2D>(n);
        newVertices.add(this.vertices.get(0));
        for (int i = 1; i < n; i++)
        {
            newVertices.add(this.vertices.get(n-i));
        }
        
        // create a new SimplePolygon2D with this new set of vertices
        DefaultPolygon2D reverse = new DefaultPolygon2D(0);
        reverse.vertices = newVertices;
        return reverse;
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
    public double signedArea() {
        double area = 0;
        
        // number of vertices
        int n = this.vertices.size();
    
        // initialize with the last vertex
        Point2D prev = this.vertices.get(n-1);
        
        // iterate on edges
        for (Point2D point : vertices)
        {
            area += prev.getX() * point.getY() - prev.getY() * point.getX();
            prev = point;
        }
        
        return area /= 2;
    }

    // ===================================================================
    // Implementation of the Polygon2D interface
    
    /**
     * Returns the vertex positions of this polygon. The result is a pointer to the inner
     * collection of vertex positions.
     * 
     * @return a reference to the inner vertex positions array
     */
    public Iterable<Point2D> vertexPositions() 
    {
        return vertices;
    }
        
    /**
     * @return the number of vertices in this polygon.
     */
    public int vertexNumber()
    {
        return this.vertices.size();
    }

 
    // ===================================================================
    // Implementation of the Region2D interface
    
    @Override
    public LinearRing2D boundary()
    {
        return new LinearRing2D(this.vertices);
    }
    
    /**
     * Returns true if the specified point is inside the polygon. 
     * No specific test is made for points on the boundary.
     */
    @Override
    public boolean contains(Point2D point)
    {
        return contains(point.getX(), point.getY());
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
        double area = 0;
    
        // the winding number counter
        int winding = 0;
    
        // initialize with the last vertex
        Point2D previous = this.vertices.get(vertices.size() - 1);
        double xprev = previous.getX();
        double yprev = previous.getY();
    
        // iterate on vertices, keeping coordinates of previous vertex in memory
        for (Point2D current : vertices)
        {
            // coordinates of current vertex
            double xcurr = current.getX();
            double ycurr = current.getY();
    
            // update area computation
            area += xprev * ycurr - yprev * xcurr;
            
            
            if (yprev <= y)
            {
                // detect upward crossing
                if (ycurr > y) 
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) > 0)
                        winding++;
            }
            else
            {
                // detect downward crossing
                if (ycurr <= y)
                    if (isLeft(xprev, yprev, xcurr, ycurr, x, y) < 0)
                        winding--;
            }
            
            // for next iteration
            xprev = xcurr;
            yprev = ycurr;
            previous = current;
        }
    
        if (area > 0) 
        {
            return winding == 1;
        }
        else 
        {
            return winding == 0;
        }
    }

    /**
     * Tests if the point p3 is Left|On|Right of the infinite line formed by p1 and p2.
     * 
     * Input:  three points P0, P1, and P2
     * Return: >0 for P2 left of the line through P0 and P1
     *         =0 for P2 on the line
     *         <0 for P2 right of the line
     *         
     * See: the January 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
     */
    private final static int isLeft(double x1, double y1, double x2, double y2, double x3, double y3)
    {
        return (int) Math.signum((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
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
        if (this.contains(point.getX(), point.getY()))
            return true;
        if (this.boundaryContains(point, eps))
            return true;
        
        return false;
    }

    private boolean boundaryContains(Point2D point, double eps)
    {
        // Extract the vertex of the collection
        Point2D previous = vertices.get(vertices.size() - 1);
        
        // iterate over pairs of adjacent vertices
        for (Point2D current : this.vertices)
        {
            LineSegment2D edge = new LineSegment2D(previous, current);
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
    public double distance(double x, double y)
    {
        // if point is inside of the polygon returns 0
        if (this.contains(x, y))
            return 0;
        
        // computes distance to boundary
        LinearRing2D boundary = new LinearRing2D(this.vertices);
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
     * @see net.sci.geom.geom2d.Geometry2D#boundingBox()
     */
    @Override
    public Box2D boundingBox()
    {
        // initialize with extreme values
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute min/max for each coordinate
        for (Point2D vertex : this.vertices)
        {
            double x = vertex.getX();
            double y = vertex.getY();
            xmin = Math.min(xmin, x);
            xmax = Math.max(xmax, x);
            ymin = Math.min(ymin, y);
            ymax = Math.max(ymax, y);
        }
        
        // return new Bounding Box
        return new Box2D(xmin, xmax, ymin, ymax);
    }

}
