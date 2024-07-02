/**
 * 
 */
package net.sci.image.process;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.numeric.Float32VectorArray2D;
import net.sci.array.numeric.Float32VectorArray3D;
import net.sci.array.numeric.Scalar;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.VectorArray;
import net.sci.array.numeric.VectorArray2D;
import net.sci.array.numeric.VectorArray3D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;

/**
 * Compute gradient of a scalar array using Sobel coefficients, using a
 * VectorArray for representing the result.
 * 
 * @author dlegland
 */
public class SobelGradient extends AlgoStub implements ImageArrayOperator
{
    /**
     * Creates a new instance of Sobel Gradient operator.
     */
    public SobelGradient()
    {
    }

    public double processScalar(ScalarArray<?> source, VectorArray<?,?> target)
    {
        int nd1 = source.dimensionality();
        int nd2 = target.dimensionality();
        if (nd1 != nd2)
        {
            throw new IllegalArgumentException("Both arrays must have the same dimensionality");
        }
        
        if (!net.sci.array.Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have the same size");
        }
        
        // Choose the most appropriate implementation, depending on array dimensions
        return switch (nd1)
        {
            case 2 -> processScalar2d(ScalarArray2D.wrap(source), VectorArray2D.wrap(target));
            case 3 -> processScalar3d(ScalarArray3D.wrap(source), VectorArray3D.wrap(target));
            default -> throw new IllegalArgumentException("Can process Sobel Gradient only on 2D or 3D images");
        };
    }

    public double processScalar2d(ScalarArray2D<?> source, VectorArray2D<?, ?> target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);

        if (target.size(0) != sizeX || target.size(1) != sizeY)
        {
            throw new IllegalArgumentException("Input image and output image must have same size");
        }

        // Sobel kernels for X and Y gradients
        double[][] gradX = new double[][] { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        double[][] gradY = new double[][] { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

        // for each pixel, compute square of module and keep max value
        double maxAbsValue = Double.NEGATIVE_INFINITY;

        // Iterate over image pixels
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                // current vector values
                double[] res = new double[2];

                // iterate over current pixel neighbors
                for (int iy = 0; iy < 3; iy++)
                {
                    int y2 = Math.min(Math.max(y + iy - 1, 0), sizeY - 1);
                    for (int ix = 0; ix < 3; ix++)
                    {
                        int x2 = Math.min(Math.max(x + ix - 1, 0), sizeX - 1);
                        double val = source.getValue(x2, y2);
                        res[0] += val * gradX[iy][ix] / 8;
                        res[1] += val * gradY[iy][ix] / 8;
                    }
                }

                // update gradient array
                target.setValues(x, y, res);

                // update max of absolute value
                double h2 = Math.max(Math.abs(res[0]), Math.abs(res[1]));
                if (h2 > maxAbsValue) maxAbsValue = h2;
            }
        }

        return maxAbsValue;
    }
    
    public double processScalar3d(ScalarArray3D<?> source, VectorArray3D<?,?> target)
    {
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        if (target.size(0) != sizeX || target.size(1) != sizeY || target.size(2) != sizeZ)
        {
            throw new IllegalArgumentException("Input image and output image must have same size");
        }
        
        // Sobel kernels for X, Y and Z gradients
        double[][][] gradX = new double[][][] { 
                { { -1,  0,  1}, { -2,  0,  2}, { -1,  0,  1}}, 
                { { -2,  0,  2}, { -4,  0,  4}, { -2,  0,  2}}, 
                { { -1,  0,  1}, { -2,  0,  2}, { -1,  0,  1}}, 
                };
        double[][][] gradY = new double[][][] { 
                { { -1, -2, -1}, {  0,  0,  0}, {  1,  2,  1}}, 
                { { -2, -4, -2}, {  0,  0,  0}, {  2,  4,  2}}, 
                { { -1, -2, -1}, {  0,  0,  0}, {  1,  2,  1}}, 
                };
        double[][][] gradZ = new double[][][] { 
                { { -1, -2, -1}, { -2, -4, -2}, { -1, -2, -1}}, 
                { {  0,  0,  0}, {  0,  0,  0}, {  0,  0,  0}}, 
                { {  1,  2,  1}, {  2,  4,  2}, {  1,  2,  1}}, 
                };

        // for each voxel, compute square of module and keep max value
        double maxAbsValue = Double.NEGATIVE_INFINITY;

        // Iterate over image voxels
        for (int z = 0; z < sizeZ; z++)
        {
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    // current vector values
                    double[] res = new double[3];

                    // iterate over current pixel neighbors
                    for (int iz = 0; iz < 3; iz++)
                    {
                        int z2 = Math.min(Math.max(z + iz - 1, 0), sizeZ - 1);
                        for (int iy = 0; iy < 3; iy++)
                        {
                            int y2 = Math.min(Math.max(y + iy - 1, 0), sizeY - 1);
                            for (int ix = 0; ix < 3; ix++)
                            {
                                int x2 = Math.min(Math.max(x + ix - 1, 0), sizeX - 1);
                                double val = source.getValue(x2, y2, z2);
                                res[0] += val * gradX[iz][iy][ix] / 32;
                                res[1] += val * gradY[iz][iy][ix] / 32;
                                res[2] += val * gradZ[iz][iy][ix] / 32;
                            }
                        }
                    }

                    // update gradient array
                    target.setValues(x, y, z, res);
                    
                    // update max of absolute value
                    double h2 = Math.abs(res[0]);
                    h2 = Math.max(h2, Math.abs(res[1]));
                    h2 = Math.max(h2, Math.abs(res[2]));
                    if (h2 > maxAbsValue) maxAbsValue = h2;
                }
            }
        }
        
        return maxAbsValue;
    }
    
    /**
     * Overrides the default implementation to update display range at the end
     * of image creation.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Image process(Image image)
    {
        // retrieve input array and check validity
        Array<?> array = image.getData();
        if (!(array.sampleElement() instanceof Scalar<?>))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        
        // apply processing on a newly created result array
        VectorArray<?,?> result = createEmptyOutputArray(array);
        double maxVal = processScalar(ScalarArray.wrap((Array<Scalar>) array), result);
        
        // create result image and update default display
        Image resImage = new Image(result, image);
        resImage.getDisplaySettings().setDisplayRange(new double[] {-maxVal, maxVal});
        return resImage;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array.sampleElement() instanceof Scalar<?>))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        VectorArray<?,?> result = createEmptyOutputArray(array);
        processScalar(ScalarArray.wrap((Array<Scalar>) array), result);
        return result;
    }

    private VectorArray<?,?> createEmptyOutputArray(Array<?> array)
    {
        if (array.dimensionality() == 2 && array.sampleElement() instanceof Scalar<?>)
        {
            int size0 = array.size(0);
            int size1 = array.size(1);
            return Float32VectorArray2D.create(size0, size1, 2);
        }
        else if (array.dimensionality() == 3 && array.sampleElement() instanceof Scalar<?>)
        {
            int size0 = array.size(0);
            int size1 = array.size(1);
            int size2 = array.size(2);
            return Float32VectorArray3D.create(size0, size1, size2, 3);
        }
        else
        {
            throw new RuntimeException("Unable to create default array for input of class " + array.getClass());
        }
    }

    public boolean canProcess(Array<?> array)
    {
        return array instanceof ScalarArray;
    }
}
