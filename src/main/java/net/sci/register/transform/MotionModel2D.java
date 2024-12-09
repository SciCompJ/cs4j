/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.AffineTransform2D;

/**
 * Parametric model for a motion in two dimensions, composed of a rotation and a
 * translation.
 * 
 * Parameters are as follow:
 * <ul>
 * <li>the translation vector along x-axis</li>
 * <li>the translation vector along y-axis</li>
 * <li>the rotation angle in degrees, counter-clockwise</li>
 * </ul>
 * The rotation is performed before the translation.
 * 
 * @author dlegland
 *
 */
public class MotionModel2D extends ParametricTransform2D implements AffineTransform2D
{
    // ===================================================================
    // Constructors
    
	public MotionModel2D()
	{
		super(new double[]{0, 0, 0});
	}
	
	public MotionModel2D(double[] params)
	{
		super(params);
		if (params.length != 3)
		{
			throw new IllegalArgumentException("Requires an input vector with length 3");
		}
	}
	
	
    // ===================================================================
    // Implementation of the AffineTransform2D interface
    
	@Override
	public double[][] affineMatrix()
	{
	    double theta = Math.toRadians(this.parameters[2]);
	    double cot = Math.cos(theta);
	    double sit = Math.sin(theta);
        double tx = this.parameters[0];
		double ty = this.parameters[1];
		double[][] mat = {
		        {cot, -sit, tx}, 
		        {sit,  cot, ty}, 
		        {0, 0, 1}};
		return mat;
	}

}
