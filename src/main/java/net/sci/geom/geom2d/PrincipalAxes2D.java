/**
 * 
 */
package net.sci.geom.geom2d;

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
     * The scalings along each canonical axis, as obtained from the diagonal of
     * the eigen values matrix.
     */
    private double[] scalings;
    
    public PrincipalAxes2D(Point2D center, double[][] rotMat, double[] scalings)
    {
        this.center = center;
        this.rotationMatrix = rotMat;
        this.scalings = scalings;
        
    }

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
     * @return the scalings
     */
    public double[] scalings()
    {
        return scalings;
    }

}
