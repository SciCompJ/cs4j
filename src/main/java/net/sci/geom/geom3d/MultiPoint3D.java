/**
 * 
 */
package net.sci.geom.geom3d;

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
public class MultiPoint3D implements Geometry3D
{
    // ===================================================================
    // Static factories
    
    /** Empty constructor. */
    public static final MultiPoint3D create()
    {
        return new MultiPoint3D(10);
    }
    
    /**
     * Empty constructor that specifies the initial capacity of the underlying
     * buffer.
     */
    public static final MultiPoint3D create(int initialCapacity)
    {
        return new MultiPoint3D(initialCapacity);
    }
    
    /** Creates a new MultiPoint3D from a collection of points. */
    public static final MultiPoint3D create(Collection<Point3D> points)
    {
        return new MultiPoint3D(10);
    }

    
    // ===================================================================
    // Class variables

    /**
     * The inner array of points.
     */
    ArrayList<Point3D> points;

    
    // ===================================================================
    // Constructors

    /** Empty constructor. */
    private MultiPoint3D(int initialCapacity)
    {
        this.points = new ArrayList<Point3D>(initialCapacity);
    }

    /** Constructor from a collection of points. */
    private MultiPoint3D(Collection<Point3D> points)
    {
        this.points = new ArrayList<Point3D>(points.size());
        this.points.addAll(points);
    }


    // ===================================================================
    // New methods
    
    public Point3D centroid()
    {
        double sx = 0;
        double sy = 0;
        double sz = 0;
        int n = 0;
        for (Point3D p : points)
        {
            sx += p.x;
            sy += p.y;
            sz += p.z;
            n++;
        }
        return new Point3D(sx / n, sy / n, sz / n);
    }
    
    public PrincipalAxes3D principalAxes()
    {
        Point3D center = centroid();
        double xc = center.x;
        double yc = center.y;
        double zc = center.z;
        
        double Ixx = 0, Iyy = 0, Izz = 0;
        double Ixy = 0, Ixz = 0, Iyz = 0;
        for (Point3D p : points)
        {
            // recenter point coords
            double x = p.x - xc;
            double y = p.y - yc;
            double z = p.z - zc;
            
            // compute coeffs of inertia matrix
            Ixx += x * x;
            Iyy += y * y;
            Izz += z * z;
            Ixy += x * y;
            Ixz += x * z;
            Iyz += y * z;
        }
        
        // normalize
        int n = points.size();
        Ixx /= n;
        Iyy /= n;
        Izz /= n;
        Ixy /= n;
        Ixz /= n;
        Iyz /= n;

        // Perform singular-Value Decomposition
        Matrix matrix = createMatrix(Ixx, Iyy, Izz, Ixy, Ixz, Iyz);
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        // Extract singular values
        Matrix values = svd.getS();
        double[] scalings = new double[3];
        scalings[0] = values.get(0, 0);
        scalings[1] = values.get(1, 1);
        scalings[2] = values.get(2, 2);
        
        // retrieve rotation matrix
        double[][] rotMat = svd.getU().getArray();

        // concatenate
        return new PrincipalAxes3D(center, rotMat, scalings);
    }
    
    private Matrix createMatrix(double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz)
    {
        // create the matrix
        Matrix matrix = new Matrix(3, 3);
        matrix.set(0, 0, Ixx);
        matrix.set(1, 1, Iyy);
        matrix.set(2, 2, Izz);
        matrix.set(0, 1, Ixy); matrix.set(1, 0, Ixy);
        matrix.set(0, 2, Ixz); matrix.set(2, 0, Ixz);
        matrix.set(1, 2, Iyz); matrix.set(2, 1, Iyz);
        return matrix;
    }

    
    // ===================================================================
    // Point management methods
    
    public void addPoint(Point3D p)
    {
        this.points.add(p);
    }
    
    public int pointCount()
    {
        return this.points.size();
    }
    

    // ===================================================================
    // Methods implementing the Geometry3D interface
    
    /**
     * Return true by definition.
     */
    @Override
    public boolean isBounded()
    {
        return true;
    }

    @Override
    public boolean contains(Point3D q, double eps)
    {
        for (Point3D p : this.points)
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
    public double distance(double x, double y, double z)
    {
        double minDist = Double.POSITIVE_INFINITY;
        for (Point3D p : this.points)
        {
            minDist = Math.min(minDist, p.distance(x, y, z));
        }
        return minDist;
    }

    @Override
    public Bounds3D bounds()
    {
        return Bounds3D.of(points);
    }

    @Override
    public MultiPoint3D duplicate()
    {
       return new MultiPoint3D(points);
    }
}
