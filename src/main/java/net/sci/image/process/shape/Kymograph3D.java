/**
 * 
 */
package net.sci.image.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.interp.LinearInterpolator2D;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.geom.geom2d.Point2D;
import net.sci.geom.geom2d.polygon.Polyline2D;
import net.sci.image.ImageArrayOperator;

/**
 * A basic implementation of kymograph, that extract a 2D image from a 3D image
 * and a polyline.
 * 
 * @author dlegland
 *
 */
public class Kymograph3D extends AlgoStub implements ImageArrayOperator
{
    Polyline2D curve;
    
    double stepSize = 1.0;
    
    
    public Kymograph3D(Polyline2D curve)
    {
        this.curve = curve;
    }
    
    public Kymograph3D(Polyline2D curve, double stepSize)
    {
        this.curve = curve;
        this.stepSize = stepSize;
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (array.dimensionality() != 3)
        {
            throw new IllegalArgumentException("Requires an array of dimensionality 3");
        }
        
        Polyline2D curve2 = curve.resampleBySpacing(stepSize);
        int nv = curve2.vertexCount();
        
        if (array instanceof ScalarArray<?>)
        {
            ScalarArray3D<?> scalar3d = ScalarArray3D.wrap((ScalarArray<?>) array);
            int nz = scalar3d.size(2);
            ScalarArray2D<?> result = ScalarArray2D.wrap(scalar3d.newInstance(nv, nz)); 
            processScalar3d(scalar3d, curve2, result);
            return result;
        }
        else
        {
            throw new RuntimeException("Unable to compute array kymograph");
        }
    }
    
    private void processScalar3d(ScalarArray3D<?> source, Polyline2D curve, ScalarArray2D<?> target)
    {
        int nv = curve.vertexCount();
        if (target.size(0) != nv)
        {
            throw new IllegalArgumentException(
                    "target array must have size(0) equal to vertex number (" + nv + ")");
        }
        int nz = source.size(2);
        if (target.size(1) != nz)
        {
            throw new IllegalArgumentException(
                    "target array must have size(1) equal to slice number (" + nz + ")");
        }

        // iterate over slices
        for (int z = 0; z < nz; z++)
        {
            // create interpolator on slice
            ScalarArray2D<?> slice = source.slice(z);
            LinearInterpolator2D interp = new LinearInterpolator2D(slice);
            
            // iterate on polyline vertices
            for (int iv = 0; iv < nv; iv++)
            {
                Point2D pos = curve.vertexPosition(iv);
                double value = interp.evaluate(pos.x(), pos.y());
                target.setValue(iv, z, value);
            }
        }
    }
    
    
    public boolean canProcess(Array<?> array)
    {
        if (array.dimensionality() != 3)
            return false;
        return array instanceof ScalarArray;
    }
}
