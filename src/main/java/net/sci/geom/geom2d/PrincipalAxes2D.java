/**
 * 
 */
package net.sci.geom.geom2d;

import static java.lang.Math.atan2;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Stores the results of principal axes computation.
 * 
 * @see net.sci.geom.geom2d.Ellipse2D
 * 
 * @author dlegland
 *
 */
public class PrincipalAxes2D
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
    public static final PrincipalAxes2D fromPoints(Iterable<Point2D> points)
    {
        // compute centroid
        double xc = 0, yc = 0;
        int n = 0;
        for (Point2D p : points)
        {
            xc += p.x();
            yc += p.y();
            n++;
        }
        
        // normalize by point count
        xc /= n;
        yc /= n;
        
        // compute second-order coefficients
        double Ixx = 0, Ixy = 0, Iyy = 0;
        for (Point2D p : points)
        {
            double x = p.x() - xc;
            double y = p.y() - yc;
            Ixx += x * x;
            Ixy += x * y;
            Iyy += y * y;
        }
        
        // normalize by point count
        Ixx /= n;
        Ixy /= n;
        Iyy /= n;
        
        // convert inertia coefficients to PrincipalAxes
        Point2D center = new Point2D(xc, yc);
        return PrincipalAxes2D.fromInertiaCoefficients(center, Ixx, Iyy, Ixy);
    }
    
    /**
     * Creates a new centered PrincipalAxis2D instance from a center and the
     * unique coefficients of the inertia matrix. The diagonal coefficients of
     * the inertia matrix are provided first.
     * 
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @return the corresponding PrincipalAxis2D
     */
    public static final PrincipalAxes2D fromInertiaCoefficients(double Ixx, double Iyy, double Ixy)
    {
        return fromInertiaCoefficients(new Point2D(), Ixx, Iyy, Ixy);
    }
    
    /**
     * Creates a new PrincipalAxis2D instance from a center and the unique
     * coefficients of the inertia matrix. The diagonal coefficients of the
     * inertia matrix are provided first.
     * 
     * @param center
     *            the center of the principal axes
     * @param Ixx
     *            the second-order inertia coefficient along the x axis
     * @param Iyy
     *            the second-order inertia coefficient along the y axis
     * @param Ixy
     *            the second-order inertia coefficient along the (xy) diagonal
     *            axis
     * @return the corresponding PrincipalAxis2D
     */
    public static final PrincipalAxes2D fromInertiaCoefficients(Point2D center, double Ixx, double Iyy, double Ixy)
    {
        // create Matrix populated with inertia coefficients 
        Matrix matrix = new Matrix(2, 2);
        matrix.set(0, 0, Ixx);
        matrix.set(0, 1, Ixy);
        matrix.set(1, 0, Ixy);
        matrix.set(1, 1, Iyy);

        // Perform singular-Value Decomposition
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);

        // Extract singular values
        Matrix values = svd.getS();
        double[] scalings = new double[2];
        scalings[0] = Math.sqrt(values.get(0, 0));
        scalings[1] = Math.sqrt(values.get(1, 1));
        
        // retrieve rotation matrix
        double[][] rotMat = svd.getU().getArray();

        // concatenate
        return new PrincipalAxes2D(center, rotMat, scalings);
    }
    
    
    // ===================================================================
    // Class variables
    
    /**
     * The center of the axes.
     */
    private Point2D center;
    
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
    
    public PrincipalAxes2D(Point2D center, double[][] rotMat, double[] scalings)
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
    public Point2D center()
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
     * Returns the rotation angle associated to this PrincipalAxes2D instance.
     * 
     * @return the rotation of this axis with respect to the horizontal axis, in
     *         radians, counter-clockwise.
     */
    public double rotationAngle()
    {
        return atan2(rotationMatrix[1][0], rotationMatrix[0][0]);
    }

    /**
     * @return the scalings
     */
    public double[] scalings()
    {
        return scalings;
    }

}
