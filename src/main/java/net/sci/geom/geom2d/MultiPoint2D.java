/**
 * 
 */
package net.sci.geom.geom2d;

import java.util.ArrayList;
import java.util.Collection;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * The geometry obtained by the union of several points.
 * 
 * @author dlegland
 *
 */
public class MultiPoint2D implements Geometry2D
{
    // ===================================================================
    // Static factories
    
    /** Empty constructor. */
    public static final MultiPoint2D create()
    {
        return new MultiPoint2D(10);
    }
    
    /**
     * Empty constructor that specifies the initial capacity of the underlying
     * buffer.
     */
    public static final MultiPoint2D create(int initialCapacity)
    {
        return new MultiPoint2D(initialCapacity);
    }
    
    /** Creates a new MultiPoint2D from a collection of points. */
    public static final MultiPoint2D create(Collection<Point2D> points)
    {
        return new MultiPoint2D(10);
    }

    
    // ===================================================================
    // Class variables

    /**
     * The inner array of points.
     */
    ArrayList<Point2D> points;

    
    // ===================================================================
    // Constructors

    /** Empty constructor. */
    private MultiPoint2D(int initialCapacity)
    {
        this.points = new ArrayList<Point2D>(initialCapacity);
    }

    /** Constructor from a collection of points. */
    private MultiPoint2D(Collection<Point2D> points)
    {
        this.points = new ArrayList<Point2D>(points.size());
        this.points.addAll(points);
    }


    // ===================================================================
    // New methods
    
    public Point2D centroid()
    {
        double sx = 0;
        double sy = 0;
        int n = 0;
        for (Point2D p : points)
        {
            sx += p.x;
            sy += p.y;
            n++;
        }
        return new Point2D(sx / n, sy / n);
    }
    
    public PrincipalAxes2D principalAxes()
    {
        // compute points centroid
        Point2D center = centroid();
        double xc = center.x;
        double yc = center.y;
        
        // compute inertia matrix
        double Ixx = 0, Ixy = 0, Iyy = 0;
        for (Point2D p : points)
        {
            double x = p.x - xc;
            double y = p.y - yc;
            Ixx += x * x;
            Ixy += x * y;
            Iyy += y * y;
        }
        
        // normalize
        int n = points.size();
        Ixx /= n;
        Ixy /= n;
        Iyy /= n;

        // Perform singular-Value Decomposition
        SingularValueDecomposition svd = computeSVD(Ixx, Ixy, Iyy);

        // Extract singular values
        Matrix values = svd.getS();
        double[] scalings = new double[2];
        scalings[0] = values.get(0, 0);
        scalings[1] = values.get(1, 1);
        
        // retrieve rotation matrix
        double[][] rotMat = svd.getU().getArray();

        // concatenate
        return new PrincipalAxes2D(center, rotMat, scalings);
    }
    
    private SingularValueDecomposition computeSVD(double Ixx, double Ixy, double Iyy)
    {
        // create the matrix
        Matrix matrix = new Matrix(2, 2);
        matrix.set(0, 0, Ixx);
        matrix.set(0, 1, Ixy);
        matrix.set(1, 0, Ixy);
        matrix.set(1, 1, Iyy);

        // Extract singular values
        return new SingularValueDecomposition(matrix);
    }

    
    // ===================================================================
    // Methods
    
    public void addPoint(Point2D p)
    {
        this.points.add(p);
    }
    
    public int pointCount()
    {
        return this.points.size();
    }
    

    // ===================================================================
    // Methods implementing the Geometry2D interface
    
    /**
     * Return true by definition.
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public boolean contains(Point2D q, double eps)
    {
        for (Point2D p : this.points)
        {
            if (p.contains(q, eps))
            {
               return true;
            }
        }
        return false;
    }

    /**
     * Returns the distance to the closest point in the set, or +infinity if the
     * point does not contain any point.
     */
    @Override
    public double distance(double x, double y)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Point2D p : this.points)
        {
            minDist = Math.min(minDist, p.distance(x, y));
        }
        return minDist;
    }

    @Override
    public Bounds2D bounds()
    {
        if (this.points.isEmpty())
        {
            return new Bounds2D();
        }
        
        // init bounds
        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;
        
        // compute bounds by iterating over points
        for (Point2D p : this.points)
        {
            xmin = Math.min(p.x, xmin);
            ymin = Math.min(p.y, ymin);
            xmax = Math.max(p.x, xmax);
            ymax = Math.max(p.y, ymax);
        }

        return new Bounds2D(xmin, xmax, ymin, ymax);
    }

    @Override
    public MultiPoint2D duplicate()
    {
        MultiPoint2D pts = new MultiPoint2D(points.size());
        pts.points.addAll(points);
        return pts;
    }
}
