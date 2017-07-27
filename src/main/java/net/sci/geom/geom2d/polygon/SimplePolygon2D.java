/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

/**
 * A polygonal region whose boundary is a single linear ring.
 * 
 * @author dlegland
 * 
 * @see LinearRing2D
 */
public class SimplePolygon2D implements Polygon2D
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
    public SimplePolygon2D()
    {
        vertices = new ArrayList<Point2D>();
    }
    
    public SimplePolygon2D(Collection<? extends Point2D> points)
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
    public SimplePolygon2D(Point2D... vertices)
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
    public SimplePolygon2D(double[] xcoords, double[] ycoords)
    {
        vertices = new ArrayList<Point2D>(xcoords.length);
        for (int i = 0; i < xcoords.length; i++)
            vertices.add(new Point2D(xcoords[i], ycoords[i]));
    }
    
    /**
     * Ensures the polygon has enough memory for storing the required number of
     * vertices.
     */
    public SimplePolygon2D(int nVertices)
    {
        vertices = new ArrayList<Point2D>(nVertices);
    }
    
    // ===================================================================
    // Specific methods
    
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
     * Returns the vertices of this polygon. The result is a pointer to the inner
     * collection of vertices.
     */
    public Collection<Point2D> vertices() 
    {
        // TODO: do we need to protect vertices, or is polygon mutable?
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
    
    /**
     * Returns true if the specified point is inside the polygon. 
     * No specific test is made for points on the boundary.
     */
    @Override
    public boolean contains(Point2D point)
    {
        return isInside(point);
    }

    /**
     * Returns true if the specified point is inside the polygon. 
     * No specific test is made for points on the boundary.
     */
    @Override
    public boolean contains(double x, double y)
    {
        return isInside(new Point2D(x, y));
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
        if (this.isInside(point))
            return true;
        if (this.boundaryContains(point, eps))
            return true;
        
        return false;
    }

    /**
     * Returns true if the specified point is inside the polygon, without
     * checking of it belongs to the boundary or not.
     */
    private boolean isInside(Point2D point)
    {
        // TODO: use only one iteration for both computations
        double area = this.signedArea();
        int winding = this.windingNumber(point);
        if (area > 0) 
        {
            return winding == 1;
        }
        else 
        {
            return winding == 0;
        }
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
    
    private int windingNumber(Point2D point)
    {
        int wn = 0; // the winding number counter
        
        // Extract the last point of the collection
        Point2D previous = vertices.get(vertices.size() - 1);
        double y1 = previous.getY();
        double y2;
        
        // keep y-coordinate of test point
        double y = point.getY();
        
        // Iterate on couple of vertices, starting from couple (last,first)
        for (Point2D current : vertices)
        {
            // second vertex of current edge
            y2 = current.getY();
            
            if (y1 <= y)
            {
                if (y2 > y) // an upward crossing
                    if (isLeft(previous, current, point) > 0)
                        wn++;
            }
            else
            {
                if (y2 <= y) // a downward crossing
                    if (isLeft(previous, current, point) < 0)
                        wn--;
            }
            
            // for next iteration
            y1 = y2;
            previous = current;
        }
        
        return wn;
    }
    
    /**
     * Tests if a point is Left|On|Right of an infinite line.
     * Input:  three points P0, P1, and P2
     * Return: >0 for P2 left of the line through P0 and P1
     *         =0 for P2 on the line
     *         <0 for P2 right of the line
     * See: the January 2001 Algorithm "Area of 2D and 3D Triangles and Polygons"
     */
    private final static int isLeft(Point2D p1, Point2D p2, Point2D pt)
    {
        double x = p1.getX();
        double y = p1.getY();
        return (int) Math.signum((p2.getX() - x) * (pt.getY() - y) - (pt.getX() - x) * (p2.getY() - y));
    }
    
    /**
     * Returns the distance to the boundary of this polygon, or zero if the
     * point is inside the polygon.
     */
    @Override
    public double distance(Point2D point)
    {
        // computes distance to boundary
        LinearRing2D boundary = new LinearRing2D(this.vertices);
        double dist = boundary.distance(point);
        
        // choose sign depending on if the point is inside or outside
        if (this.isInside(point))
            return 0;
        else
            return dist;
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
