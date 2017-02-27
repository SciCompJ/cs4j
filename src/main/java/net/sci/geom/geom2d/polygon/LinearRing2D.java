/**
 * 
 */
package net.sci.geom.geom2d.polygon;

import java.util.ArrayList;
import java.util.Collection;

import net.sci.geom.geom2d.Box2D;
import net.sci.geom.geom2d.Curve2D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.line.LineSegment2D;

/**
 * <p>
 * A LinearRing2D is a line string whose last point is connected to the first one.
 * This is typically the boundary of a (Simple)Polygon2D.
 * </p>
 * <p>
 * The name 'LinearRing2D' was used for 2 reasons:
 * <ul><li>it is short</li> <li>it is consistent with the JTS name</li></ul>
 * </p>
 * @author dlegland
 */
public class LinearRing2D implements Curve2D
{
    // ===================================================================
    // Class variables
    
    private ArrayList<Point2D> vertices;
    
    // ===================================================================
    // Contructors

    public LinearRing2D() 
    {
        this.vertices = new ArrayList<Point2D>();
    }

    /**
     * Creates a new linear curve by allocating enough memory for the specified
     * number of vertices.
     * 
     * @param nVertices
     */
    public LinearRing2D(int nVertices)
    {
        this.vertices = new ArrayList<Point2D>(nVertices);
    }
    
    public LinearRing2D(Point2D... vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.length);
        for (Point2D vertex : vertices)
        {
            this.vertices.add(vertex);
        }
    }
    
    public LinearRing2D(Collection<? extends Point2D> vertices)
    {
        this.vertices = new ArrayList<Point2D>(vertices.size());
        this.vertices.addAll(vertices);
    }
    
    public LinearRing2D(double[] xcoords, double[] ycoords)
    {
        this.vertices = new ArrayList<Point2D>(xcoords.length);
        int n = xcoords.length;
        this.vertices.ensureCapacity(n);
        for (int i = 0; i < n; i++)
        {
            vertices.add(new Point2D(xcoords[i], ycoords[i]));
        }
    }
    
    // ===================================================================
    // Methods specific to ClosedPolyline2D

    /**
     * Computes the signed area of the linear ring. Algorithm is taken from page:
     * <a href="http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/">
     * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/</a>. Signed are
     * is positive if polyline is oriented counter-clockwise, and negative
     * otherwise. Result is wrong if polyline is self-intersecting.
     * 
     * @return the signed area of the polyline.
     */
    public double signedArea() {
        // start from edge joining last and first vertices
        Point2D prev = this.vertices.get(this.vertices.size() - 1);

        // Iterate over all couples of adjacent vertices
        double area = 0;
        for (Point2D point : this.vertices) {
            // add area of elementary parallelogram
            area += prev.getX() * point.getY() - prev.getY() * point.getX();
            prev = point;
        }
        
        // divides by 2 to consider only elementary triangles
        return area /= 2;
    }

 
    // ===================================================================
    // Management of vertices
    
    
    /**
     * Returns the inner collection of vertices.
     */
    public ArrayList<Point2D> vertices()
    {
        return vertices;
    }
    
    /**
     * Returns the number of vertices.
     * 
     * @return the number of vertices
     */
    public int vertexNumber()
    {
        return vertices.size();
    }

    /**
     * Computes the index of the closest vertex to the input point.
     */
    public int closestVertexIndex(Point2D point)
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
    
    
    // TODO: create Edge iterator
    
    // ===================================================================
    // Implementation of the Geometry2D interface 

    @Override
    public boolean contains(Point2D point, double eps)
    {
        // Extract the last point of the collection
        Point2D previous = vertices.get(vertices.size() - 1);
        
        // Iterate on couple of vertices, starting from couple (last,first)
        for (Point2D current : vertices)
        {
            LineSegment2D edge = new LineSegment2D(previous, current);
            
            if (edge.contains(point, eps))
            {
                return true;
            }
            
            previous = current;
        }
        
        return false;
    }

    // Iterate over edges to find the minimal distance
    @Override
    public double distance(Point2D point)
    {
        // Extract the last point of the collection
        Point2D previous = vertices.get(vertices.size() - 1);
        double minDist = Double.POSITIVE_INFINITY;
        
        // Iterate on couple of vertices, starting from couple (last,first)
        for (Point2D current : vertices)
        {
            LineSegment2D edge = new LineSegment2D(previous, current);
            
            double dist = edge.distance(point);
            if (dist < minDist)
            {
                minDist = dist;
            }
            
            previous = current;
        }
        
        return minDist;
    }
    
    // ===================================================================
    // Implementation of the Geometry interface 

    /**
     * Returns true, as a linear ring is bounded by definition.
     */
    public boolean isBounded()
    {
        return true;
    }

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
