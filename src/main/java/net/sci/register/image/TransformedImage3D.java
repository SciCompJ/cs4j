/**
 * 
 */
package net.sci.register.image;

import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.interp.LinearInterpolatedArray3D;
import net.sci.array.numeric.interp.ScalarFunction3D;
import net.sci.geom.geom3d.Point3D;
import net.sci.geom.geom3d.Transform3D;

/**
 * Wraps an interpolated 3D image (or more generally a 3D function) and a
 * geometric transform to view the result as a function. Note that the transform
 * is applied in the reverse way: the transform is applied to the point in
 * target reference space, and the transformed coordinates are evaluated in the
 * source space.
 * 
 * @see TransformedImage2D
 * 
 * @author dlegland
 *
 */
public class TransformedImage3D implements ScalarFunction3D
{
    Transform3D transform;

    ScalarFunction3D function;

    public TransformedImage3D(ScalarArray3D<?> image, Transform3D transform)
    {
        this(image, transform, 0.0);
    }

    public TransformedImage3D(ScalarArray3D<?> image, Transform3D transform, double padValue)
    {
        this(new LinearInterpolatedArray3D(image, padValue), transform);
    }

    public TransformedImage3D(ScalarFunction3D image, Transform3D transform)
    {
        this.function = image;
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
        return this.function.evaluate(p.x(), p.y(), p.z());
    }

    public double evaluate(Point3D p)
    {
        p = transform.transform(p);
        return this.function.evaluate(p.x(), p.y(), p.z());
    }
}
