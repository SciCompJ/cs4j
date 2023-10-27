/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.AffineTransform2D;
import net.sci.geom.geom2d.Point2D;

/**
 * Parametric model for a translation in two dimensions.
 * 
 * @author dlegland
 *
 */
public class TranslationModel2D extends ParametricTransform2D implements AffineTransform2D
{
    // ===================================================================
    // Constructors
    
	public TranslationModel2D()
	{
		super(new double[]{0, 0});
	}
	
    public TranslationModel2D(double tx, double ty)
    {
        super(new double[]{tx, ty});
    }
    
	public TranslationModel2D(double[] params)
	{
		super(params);
		if (params.length != 2)
		{
			throw new IllegalArgumentException("Requires an input vector with length 2");
		}
	}
	
	
    // ===================================================================
    // Implementation of the AffineTransform2D interface
    
	/* (non-Javadoc)
	 * @see net.sci.geom.geom2d.Transform2d#transform(net.sci.geom.geom2d.Point2d)
	 */
	@Override
	public Point2D transform(Point2D point)
	{
		return new Point2D(
				point.x() + this.parameters[0], 
				point.y() + this.parameters[1]);
	}

	@Override
	public double[][] affineMatrix()
	{
		double tx = this.parameters[0];
		double ty = this.parameters[1];
		double[][] mat = {{1, 0, tx}, {0, 1, ty}, {0, 0, 1}};
		return mat;
	}

	@Override
	public AffineTransform2D inverse()
	{
		double tx = this.parameters[0];
		double ty = this.parameters[1];
		return new TranslationModel2D(new double[]{-tx, -ty});
	}

}
