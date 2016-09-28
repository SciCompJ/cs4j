/**
 * 
 */
package net.sci.register.transform;

import net.sci.geom.geom2d.AffineTransform2d;
import net.sci.geom.geom2d.Point2d;

/**
 * @author dlegland
 *
 */
public class TranslationModel2d extends ParametricTransform2d implements AffineTransform2d
{
	public TranslationModel2d()
	{
		super(new double[]{0, 0});
	}
	
	public TranslationModel2d(double[] params)
	{
		super(params);
		if (params.length != 2)
		{
			throw new IllegalArgumentException("Requires an image oflength 2");
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sci.geom.geom2d.Transform2d#transform(net.sci.geom.geom2d.Point2d)
	 */
	@Override
	public Point2d transform(Point2d point)
	{
		return new Point2d(
				point.getX() + this.parameters[0], 
				point.getY() + this.parameters[1]);
	}

	@Override
	public double[][] getMatrix()
	{
		double tx = this.parameters[0];
		double ty = this.parameters[1];
		double[][] mat = {{1, 0, tx}, {0, 1, ty}, {0, 0, 1}};
		return mat;
	}

	@Override
	public AffineTransform2d invert()
	{
		double tx = this.parameters[0];
		double ty = this.parameters[1];
		return new TranslationModel2d(new double[]{-tx, -ty});
	}

}
