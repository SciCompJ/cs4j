/**
 * 
 */
package net.sci.register.image;

import net.sci.array.interp.LinearInterpolator3D;
import net.sci.array.interp.ScalarFunction3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Transform3D;

/**
 * @author dlegland
 *
 */
public class TransformedImage3D implements ScalarFunction3D
{
	Transform3D transform;
	
	ScalarFunction3D image;
	
	public TransformedImage3D(ScalarArray3D<?> image, Transform3D transform)
	{
		this.image = new LinearInterpolator3D(image);
		this.transform = transform;
	}

	public TransformedImage3D(ScalarFunction3D image, Transform3D transform)
	{
		this.image = image;
		this.transform = transform;
	}

	public Transform3D getTransform()
	{
		return transform;
	}
	
	@Override
	public double evaluate(double x, double y, double z)
	{
		Point3D p = transform.transform(x, y, z);
		return this.image.evaluate(p.x(), p.y(), p.z());
	}

	public double evaluate(Point3D p)
	{
		p = transform.transform(p);
		return this.image.evaluate(p.x(), p.y(), p.z());
	}
}
