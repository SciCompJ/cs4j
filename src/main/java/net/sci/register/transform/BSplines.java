/**
 * 
 */
package net.sci.register.transform;

/**
 * Static methods for performing computations with cubic BSplines.
 * 
 * Uses the "Free-Form Deformation" formalism proposed by D. Rueckert et al.,
 * (1999) "Nonrigid Registration Using Free-Form Deformations: Application to
 * Breast MR Images", IEEE Transactions on Medical Imaging, vol 16(9).
 * 
 * @see BSplineTransformModel2D
 */
public class BSplines
{
    // =============================================================
    // Interpolation functions

    public static final double beta3_0(double u)
    {
        return cube(1.0 - u) / 6.0;
    }
    
    public static final double beta3_1(double u)
    {
        return (3 * cube(u) - 6 * u * u + 4.0) / 6.0;
    }
    
    public static final double beta3_2(double u)
    {
        return (-3 * cube(u) + 3 * u * u + 3 * u + 1.0) / 6.0;
    }
    
    public static final double beta3_3(double u)
    {
        return cube(u) / 6.0;
    }
    
    
    // =============================================================
    // Derivative functions

    public static final double beta3_0d(double u)
    {
        return -(1.0 - u) * (1.0 - u) / 2.0;
    }
    
    public static final double beta3_1d(double u)
    {
        return 3 * u * u / 2.0 - 2 * u;
    }
    
    public static final double beta3_2d(double u)
    {
        return (-3 * u * u + 2 * u + 1) / 2.0;
    }
    
    public static final double beta3_3d(double u)
    {
        return u * u / 2.0;
    }
    
    
    // =============================================================
    // Local utility functions

    private static final double cube(double u)
    {
        return u * u * u;
    }

    /** 
     * Private constructor to prevent instantiation.
     */
    private BSplines()
    {
    }
}
