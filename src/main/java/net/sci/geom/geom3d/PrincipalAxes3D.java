/**
 * 
 */
package net.sci.geom.geom3d;

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
     * The scalings along each canonical axis, as obtained from the diagonal of
     * the eigen values matrix.
     */
    private double[] scalings;
    
    public PrincipalAxes3D(Point3D center, double[][] rotMat, double[] scalings)
    {
        this.center = center;
        this.rotationMatrix = rotMat;
        this.scalings = scalings;
        
    }

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
