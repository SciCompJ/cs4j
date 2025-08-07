/**
 * 
 */
package net.sci.geom.polygon2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Bounds2D;
import net.sci.geom.geom2d.Domain2D;
import net.sci.geom.geom2d.FeretDiameters;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.FeretDiameters.AngleDiameterPair;

/**
 * An oriented Box in 2 dimensions.
 * 
 * @author dlegland
 */
public class OrientedBox2D implements Polygon2D
{
    /**
     * Computes the object-oriented bounding box of a set of points. The methods
     * identifies the orientation of the box that minimizes its width.
     * 
     * @param points
     *            a list of points (not necessarily ordered)
     * @return the oriented bounding box of this set of points.
     */
    public static final OrientedBox2D orientedBoundingBox(List<Point2D> points)
    {
        // Compute convex hull to reduce complexity
        Polygon2D convexHull = Polygons2D.convexHull(points);
        
        // compute convex hull centroid
        Point2D center = convexHull.centroid();
        double cx = center.x();
        double cy = center.y();
        
        List<Point2D> vertices = convexHull.vertexPositions();
        AngleDiameterPair minFeret = FeretDiameters.minFeretDiameter(vertices);
        
        // recenter the convex hull
        ArrayList<Point2D> centeredHull = new ArrayList<Point2D>(vertices.size());
        for (Point2D p : vertices)
        {
            centeredHull.add(p.translate(-cx, -cy));
        }
        
        // orientation of the main axis
        // pre-compute trigonometric functions
        double cot = Math.cos(minFeret.angle);
        double sit = Math.sin(minFeret.angle);

        // compute elongation in direction of rectangle length and width
        double xmin = Double.MAX_VALUE;
        double ymin = Double.MAX_VALUE;
        double xmax = Double.MIN_VALUE;
        double ymax = Double.MIN_VALUE;
        for (Point2D p : centeredHull)
        {
            // coordinates of current point
            double x = p.x(); 
            double y = p.y();
            
            // compute rotated coordinates
            double x2 = x * cot + y * sit; 
            double y2 = - x * sit + y * cot;
            
            // update bounding box
            xmin = Math.min(xmin, x2);
            ymin = Math.min(ymin, y2);
            xmax = Math.max(xmax, x2);
            ymax = Math.max(ymax, y2);
        }
        
        // position of the center with respect to the centroid computed before
        double dl = (xmax + xmin) / 2;
        double dw = (ymax + ymin) / 2;

        // change coordinates from rectangle to user-space
        double dx  = dl * cot - dw * sit;
        double dy  = dl * sit + dw * cot;

        // coordinates of oriented box center
        cx += dx;
        cy += dy;

        // size of the rectangle
        double length = ymax - ymin;
        double width  = xmax - xmin;
        
        // store angle in degrees, between 0 and 180
        double angle = (Math.toDegrees(minFeret.angle) + 270) % 180;

        // Store results in a new instance of OrientedBox2D
        return new OrientedBox2D(cx, cy, length, width, angle);
    }
    

    // ===================================================================
    // Class variables
    
    /** X-coordinate of the box center. */
    protected final double xc;

    /** Y-coordinate of the box center. */
    protected final double yc;

    /** Length of first side. Must be positive. */
    protected final double size1;
    
    /** Length of second side. Must be positive. Usually smaller than first side. */
    protected final double size2;

    /**
     * Orientation of major semi-axis, in degrees, counter-clockwise. Usually
     * restrained to the range between -90 and +90.
     */
    protected final double theta;
    
    // buffer the coordinates of boundary vertices
    private LinearRing2D boundary;
    
    
    // ==================================================
    // Constructors
    
    /**
     * Default constructor for OrientedBox2D.
     * 
     * @param center
     *            the center of the box
     * @param length
     *            the box length
     * @param width
     *            the box width
     * @param orientInDegrees
     *            the orientation of the box, in degrees counter-clockwise
     */
    public OrientedBox2D(Point2D center, double length, double width, double orientInDegrees)
    {
        this(center.x(), center.y(), length, width, orientInDegrees);
    }
    
    /**
     * Default constructor for OrientedBox2D, that specifies center as two
     * coordinates.
     * 
     * @param xc
     *            the x-coordinate of the box center
     * @param yc
     *            the y-coordinate of the box center
     * @param length
     *            the box length
     * @param width
     *            the box width
     * @param orientInDegrees
     *            the orientation of the box, in degrees counter-clockwise
     */
    public OrientedBox2D(double xc, double yc, double length, double width, double orientInDegrees)
    {
        this.xc = xc;
        this.yc = yc;
        this.size1 = length;
        this.size2 = width;
        this.theta = orientInDegrees;
        
        computeBoundary();
    }
    
    private void computeBoundary()
    {
        // create the "local to global" affine transform
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(this.theta));
        AffineTransform2D tra = AffineTransform2D.createTranslation(xc, yc);
        AffineTransform2D transfo = tra.compose(rot);
        
        // create vertex array
        ArrayList<Point2D> vertices = new ArrayList<>(4);
        vertices.add(new Point2D(-size1/2, -size2/2).transform(transfo));
        vertices.add(new Point2D( size1/2, -size2/2).transform(transfo));
        vertices.add(new Point2D( size1/2,  size2/2).transform(transfo));
        vertices.add(new Point2D(-size1/2,  size2/2).transform(transfo));
        
        // create boundary
        this.boundary = LinearRing2D.create(vertices);
    }
    
    
    // ===================================================================
    // Accessors
    
    public Point2D center()
    {
        return new Point2D(this.xc, this.yc);
    }
    
    public double size1()
    {
        return this.size1;
    }
    
    public double size2()
    {
        return this.size2;
    }
    
    /**
     * @return the orientation of the box, in degrees.
     */
    public double orientation()
    {
        return this.theta;
    }
    

    // ===================================================================
    // Implementation of Polygon2D interface
    
    @Override
    public double signedArea()
    {
        return size1 * size2;
    }

    @Override
    public int vertexCount()
    {
        return 4;
    }

    @Override
    public List<Point2D> vertexPositions()
    {
        // create the "local to global" affine transform
        AffineTransform2D rot = AffineTransform2D.createRotation(Math.toRadians(this.theta));
        AffineTransform2D tra = AffineTransform2D.createTranslation(xc, yc);
        AffineTransform2D transfo = tra.compose(rot);
        
        // create vertex array
        ArrayList<Point2D> vertices = new ArrayList<>(4);
        vertices.add(new Point2D(-size1/2, -size2/2).transform(transfo));
        vertices.add(new Point2D( size1/2, -size2/2).transform(transfo));
        vertices.add(new Point2D( size1/2,  size2/2).transform(transfo));
        vertices.add(new Point2D(-size1/2,  size2/2).transform(transfo));
        
        // return vertices
        return this.boundary.vertexPositions();
    }

    @Override
    public void addVertex(Point2D vertexPosition)
    {
        throw new RuntimeException("Can not add vertex to this geometry");
    }
    
    @Override
    public Iterable<LinearRing2D> rings()
    {
        return Collections.singleton(this.boundary);
    }

    
    // ===================================================================
    // Implementation of the PolygonalDomain2D interface
    
    @Override
    public Polygon2D complement()
    {
        throw new RuntimeException("Unimplemented operation");
    }

    @Override
    public LinearRing2D boundary()
    {
        return this.boundary;
    }

    
    // ===================================================================
    // Implementation of the Geometry interface
    
    @Override
    public double distance(double x, double y)
    {
        double dist = this.boundary.signedDistance(x, y);
        return dist > 0 ? dist : 0;
    }

    @Override
    public boolean contains(Point2D point)
    {
        return this.boundary.signedDistance(point) <= 0;
    }

    @Override
    public boolean contains(double x, double y)
    {
        return this.boundary.signedDistance(x, y) <= 0;
    }

    @Override
    public boolean contains(Point2D point, double eps)
    {
        return this.boundary.signedDistance(point) <= eps;
    }

    @Override
    public Bounds2D bounds()
    {
        return this.boundary.bounds();
    }

    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public Domain2D duplicate()
    {
        return new OrientedBox2D(this.xc, this.yc, this.size1, this.size2, this.theta);
    }
}
