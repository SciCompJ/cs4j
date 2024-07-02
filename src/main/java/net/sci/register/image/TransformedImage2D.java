/**
 * 
 */
package net.sci.register.image;

import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.Transform2D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.interp.LinearInterpolator2D;
import net.sci.array.numeric.interp.ScalarFunction2D;

/**
 * @author dlegland
 *
 */
public class TransformedImage2D implements ScalarFunction2D
{
	Transform2D transform;
	
	ScalarFunction2D image;
	
	public TransformedImage2D(ScalarArray2D<?> image, Transform2D transform)
	{
		this.image = new LinearInterpolator2D(image);
		this.transform = transform;
	}

	public TransformedImage2D(ScalarFunction2D image, Transform2D transform)
	{
		this.image = image;
		this.transform = transform;
	}

	public Transform2D getTransform()
	{
		return transform;
	}
	
	@Override
	public double evaluate(double x, double y)
	{
		Point2D p = new Point2D(x, y);
		p = transform.transform(p);
		return this.image.evaluate(p.x(), p.y());
	}

	public double evaluate(Point2D p)
	{
		p = transform.transform(p);
		return this.image.evaluate(p.x(), p.y());
	}
}
