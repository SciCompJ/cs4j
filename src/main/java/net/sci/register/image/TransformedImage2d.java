/**
 * 
 */
package net.sci.register.image;

import net.sci.geom.geom2d.Point2d;
import net.sci.geom.geom2d.Transform2d;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.interp.LinearInterpolator2D;
import net.sci.array.interp.ScalarFunction2D;

/**
 * @author dlegland
 *
 */
public class TransformedImage2d implements ScalarFunction2D
{
	Transform2d transform;
	
	ScalarFunction2D image;
	
	public TransformedImage2d(ScalarArray2D<?> image, Transform2d transform)
	{
		this.image = new LinearInterpolator2D(image);
		this.transform = transform;
	}

	public TransformedImage2d(ScalarFunction2D image, Transform2d transform)
	{
		this.image = image;
		this.transform = transform;
	}

	public Transform2d getTransform()
	{
		return transform;
	}
	
	@Override
	public double evaluate(double x, double y)
	{
		Point2d p = new Point2d(x, y);
		p = transform.transform(p);
		return this.image.evaluate(p.getX(), p.getY());
	}

	@Override
	public double evaluate(Point2d p)
	{
		p = transform.transform(p);
		return this.image.evaluate(p.getX(), p.getY());
	}
}
