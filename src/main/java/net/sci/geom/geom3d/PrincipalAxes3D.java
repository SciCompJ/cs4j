/**
 * 
 */
package net.sci.geom.geom3d;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Stores the results of principal axes computation in 3D.
 * 
 * @see net.sci.geom.geom3d.Ellipsoid3D
 * 
 * @author dlegland
 *
 */
public class PrincipalAxes3D
{
    // ===================================================================
    // Static factories
    
    /**
     * Creates a new PrincipalAxis2D instance from a series of points. In
     * practice, two iterations are required: the first one for computing the
     * center, the second one for computing second-order inertia coefficients.
     * 
     * @param points
     *            the points to iterate on
     * @return the equivalent PrincipalAxes2D
     */
    public static final PrincipalAxes3D fromPoints(Iterable<Point3D> points)
    {
        // compute centroid
        double xc = 0, yc = 0, zc = 0;
        int n = 0;
        for (Point3D p : points)
        {
            xc += p.x();
            yc += p.y();
            zc += p.z();
            n++;
        }
        
        // normalize by point count
        xc /= n;
        yc /= n;
        zc /= n;
        
        // compute second-order coefficients
        double Ixx = 0, Iyy = 0, Izz = 0;
        double Ixy = 0, Ixz = 0, Iyz = 0;
        for (Point3D p : points)
        {
            double x = p.x() - xc;
            double y = p.y() - yc;
            double z = p.z() - zc;
            Ixx += x * x;
            Iyy += y * y;
            Izz += z * z;
            Ixy += x * y;
            Ixz += x * z;
            Iyz += y * z;
        }
        
        // normalize by point count
        Ixx /= n;
        Iyy /= n;
        Izz /= n;
        Ixy /= n;
        Ixz /= n;
        Iyz /= n;
        
        // convert inertia coefficients to PrincipalAxes
        Point3D center = new Point3D(xc, yc, zc);
        return PrincipalAxes3D.fromInertiaCoefficients(center, Ixx, Iyy, Izz, Ixy, Ixz, Iyz);
    }
    
    /**
     * Creates a new centered PrincipalAxis3D instance from a center and the
     * unique coefficients of the inertia matrix. The diagonal coefficients of
     * the inertia matrix are provided first.
     * 
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Izz
     *            the second-order inertia coefficient along the z axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @param Ixz
     *            the second-order inertia coefficient along the (xz) diagonal
     *            axis
     * @param Iyz
     *            the second-order inertia coefficient along the (yz) diagonal
     *            axis
     * @return the corresponding PrincipalAxis3D
     */
    public static final PrincipalAxes3D fromInertiaCoefficients(double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz)
    {
        return fromInertiaCoefficients(new Point3D(), Ixx, Iyy, Izz, Ixy, Ixz, Iyz);
    }
    
    /**
     * Creates a new PrincipalAxis3D instance from a center and the unique
     * coefficients of the inertia matrix. The diagonal coefficients of the
     * inertia matrix are provided first.
     * 
     * @param center
     *            the center of the principal axes
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Izz
     *            the second-order inertia coefficient along the z axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @param Ixz
     *            the second-order inertia coefficient along the (xz) diagonal
     *            axis
     * @param Iyz
     *            the second-order inertia coefficient along the (yz) diagonal
     *            axis
     * @return the corresponding PrincipalAxis3D
     */
    public static final PrincipalAxes3D fromInertiaCoefficients(Point3D center, double Ixx, double Iyy, double Izz, double Ixy, double Ixz, double Iyz)
    {
        // create Matrix populated with inertia coefficients 
        Matrix matrix = new Matrix(3, 3);
        matrix.set(0, 0, Ixx);
        matrix.set(0, 1, Ixy);
        matrix.set(0, 2, Ixz);
        matrix.set(1, 0, Ixy);
        matrix.set(1, 1, Iyy);
        matrix.set(1, 2, Iyz);
        matrix.set(2, 0, Ixz);
        matrix.set(2, 1, Iyz);
        matrix.set(2, 2, Izz);

        // Perform singular-Value Decomposition
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        // Extract singular values
        Matrix values = svd.getS();
        double[] scalings = new double[3];
        scalings[0] = Math.sqrt(values.get(0, 0));
        scalings[1] = Math.sqrt(values.get(1, 1));
        scalings[2] = Math.sqrt(values.get(2, 2));
        
        // retrieve rotation matrix
        Matrix rotMat = svd.getU();
        
        // Ensure (0,0) coefficient is positive, to enforce positive (x>0) direction of first axis
        if (rotMat.get(0, 0) < 0)
        {
            for(int c = 0; c < 2; c++)
            {
                for (int r = 0; r < 3; r++)
                {
                    rotMat.set(r, c, -rotMat.get(r, c));
                }
            }
        }
        
        // concatenate into an instance of PrincipalAxes3D
        return new PrincipalAxes3D(center, rotMat.getArray(), scalings);
    }
    
    
    // ===================================================================
    // Class members
    
    /**
     * The center of the axes.
     */
    private Point3D center;
    
    /**
     * The coefficients of the rotation matrix. first index corresponds to row
     * index of matrix. Second index correspond to matrix column index.
     */
    private double[][] rotationMatrix;
    
    /**
     * The scalings along each canonical axis, as obtained from the square root
     * of the diagonal of the eigen values matrix.
     */
    private double[] scalings;
    
    
    // ===================================================================
    // Constructor
    
    public PrincipalAxes3D(Point3D center, double[][] rotMat, double[] scalings)
    {
        this.center = center;
        this.rotationMatrix = rotMat;
        this.scalings = scalings;
    }
    
    
    // ===================================================================
    // Accessors
    
    /**
     * @return the center
     */
    public Point3D center()
    {
        return center;
    }

    /**
     * @return the rotation matrix
     */
    public double[][] rotationMatrix()
    {
        return rotationMatrix;
    }

    /**
     * @return the scalings
     */
    public double[] scalings()
    {
        return scalings;
    }

}
