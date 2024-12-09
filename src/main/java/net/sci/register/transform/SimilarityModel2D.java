/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.AffineTransform2D;

/**
 * Parametric model for a similarity in two dimensions, composed of an isotropic
 * scaling, a rotation and a translation.
 * 
 * Parameters are as follow:
 * <ul>
 * <li>the translation vector along x-axis</li>
 * <li>the translation vector along y-axis</li>
 * <li>the rotation angle in degrees, counter-clockwise</li>
 * <li>the binary logarithm of the scaling factor</li>
 * </ul>
 * For the last parameter, the value 0 corresponds to no scaling, the value +1
 * to a uniform scaling by a factor of 2, and the value -1 to a uniform scaling
 * by a factor of 1/2.
 * 
 * The transforms are performed in that order:
 * <ol>
 * <li>the scaling</li>
 * <li>the rotation</li>
 * <li>the translation</li>
 * </ol>
 * 
 * @author dlegland
 *
 */
public class SimilarityModel2D extends ParametricTransform2D implements AffineTransform2D
{
    // ===================================================================
    // Constructors
    
	public SimilarityModel2D()
	{
		super(new double[]{0, 0, 0, 0});
	}
	
	public SimilarityModel2D(double[] params)
	{
		super(params);
		if (params.length != 4)
		{
			throw new IllegalArgumentException("Requires an input vector with length 4");
		}
	}
	
	
    // ===================================================================
    // Implementation of the AffineTransform2D interface
    
	@Override
	public double[][] affineMatrix()
	{
        double tx = this.parameters[0];
		double ty = this.parameters[1];
        double theta = Math.toRadians(this.parameters[2]);
        double cot = Math.cos(theta);
        double sit = Math.sin(theta);
        double logk = this.parameters[3];
        double k = Math.pow(2.0, logk);
        
		double[][] mat = {
                { k * cot, -k * sit, tx },
                { k * sit,  k * cot, ty },
                {0, 0, 1}};
		return mat;
	}

}
