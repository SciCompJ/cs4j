/**
 * 
 */
package net.sci.image.contrast;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.VectorArray;
import net.sci.array.numeric.VectorArray2D;
import net.sci.array.numeric.VectorArray3D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;

/**
 * Compute the norm of a vector array.
 * 
 * @author David Legland
 *
 */
public class VectorArrayNorm extends AlgoStub implements ImageArrayOperator
{
    /**
     * Overrides the default implementation to update display range at the end
     * of image creation.
     */
    @SuppressWarnings({ "rawtypes"})
    @Override
    public Image process(Image image)
    {
        // retrieve input array and check validity
        Array<?> array = image.getData();
        if (!(array  instanceof VectorArray))
        {
            throw new IllegalArgumentException("Requires an image containing a vector array");
        }
        
        // apply processing on a newly created result array
        ScalarArray<?> result = Float32Array.create(array.size());
        processVector((VectorArray) array, result);

        // create result image and update default display
        Image resImage = new Image(result, image);
        double maxVal = result.maxValue();
        resImage.getDisplaySettings().setDisplayRange(new double[] {-maxVal, maxVal});
        resImage.setName(image.getName() + "-norm");
        return resImage;
    }
    
    public void processVector(VectorArray<?, ?> input, ScalarArray<?> output)
    {
        if (input instanceof VectorArray2D && output instanceof ScalarArray2D)
        {
            processVector2d((VectorArray2D<?, ?>) input, (ScalarArray2D<?>) output);
        }
        else if (input instanceof VectorArray3D && output instanceof ScalarArray3D)
        {
            processVector3d((VectorArray3D<?, ?>) input, (ScalarArray3D<?>) output);
        }

        // use dimension-generic processing
        processVectorNd((VectorArray<?, ?>) input, (ScalarArray<?>) output);
    }

    public void processVector2d(VectorArray2D<?, ?> source, ScalarArray2D<?> target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int nChannels = source.channelCount();

        for (int y = 0; y < sizeY; y++)
        {
            // iterate over pixels of the row
            for (int x = 0; x < sizeX; x++)
            {
                double norm = 0;
                for (int c = 0; c < nChannels; c++)
                {
                    double v = source.getValue(x, y, c);
                    norm += v * v;
                }

                // set up value of gradient norm
                target.setValue(x, y, Math.sqrt(norm));
            }
        }
    }

    public void processVector3d(VectorArray3D<?, ?> source, ScalarArray3D<?> target)
    {
        // get array size
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        int nChannels = source.channelCount();

        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                // iterate over pixels of the row
                for (int x = 0; x < sizeX; x++)
                {
                    double norm = 0;
                    for (int c = 0; c < nChannels; c++)
                    {
                        double v = source.getValue(x, y, z, c);
                        norm += v * v;
                    }

                    // set up value of gradient norm
                    target.setValue(x, y, z, Math.sqrt(norm));
                }
            }
        }
    }

    public void processVectorNd(VectorArray<?, ?> source, ScalarArray<?> target)
    {
        // iterate over vector pixels
        for (int[] pos : target.positions())
        {
            target.setValue(pos, computeNorm(source.getValues(pos)));
        }
    }

    private static final double computeNorm(double[] vector)
    {
        double norm = 0;
        for (double d : vector)
        {
            norm += d * d;
        }
        return Math.sqrt(norm);
    }

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array instanceof VectorArray))
        {
            throw new IllegalArgumentException("Requires 2D ort 3D Vector array");
        }

        ScalarArray<?> norm = Float32Array.create(array.size());
        processVector((VectorArray<?, ?>) array, norm);
        return norm;
    }

    @Override
    public boolean canProcess(Array<?> array)
    {
        return array instanceof VectorArray;
    }
}
