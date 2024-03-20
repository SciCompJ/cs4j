/**
 * 
 */
package net.sci.image.process;

import static java.lang.Math.max;
import static java.lang.Math.min;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.ImageArrayOperator;

/**
 * Compute norm of Sobel gradient of a scalar image (2D or 3D), without storing
 * intermediate vector image. Result is given in a ScalarArray, with values in
 * float.
 * 
 * @author David Legland
 *
 */
public class SobelGradientNorm extends AlgoStub implements ImageArrayOperator
{
    /**
     * Creates a new instance of Sobel Gradient Norm operator.
     */
    public SobelGradientNorm()
    {
    }

    public double processScalar(ScalarArray<?> source, ScalarArray<?> target)
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
            case 2 -> processScalar2d(ScalarArray2D.wrap(source), ScalarArray2D.wrap(target));
            case 3 -> processScalar3d(ScalarArray3D.wrap(source), ScalarArray3D.wrap(target));
            default -> throw new IllegalArgumentException("Can process Sobel Gradient only on 2D or 3D images");
        };
	}

	public double processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);

		float[][] maskGradX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
		float[][] maskGradY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

		// accumulator for max value of gradient norm
		double hMax = 0.0; 
		
		// iterate over pixels
		for (int y = 0; y < sizeY; y++)
		{
			// iterate over pixels of the row
			for (int x = 0; x < sizeX; x++)
			{
                // accumulators for gradients in X and Y directions
				double gx = 0;
				double gy = 0;

				// uses replication of border pixel
				for (int ky = 0; ky < 3; ky++)
				{
					int y2 = max(min(y + ky - 1, sizeY - 1), 0);
					for (int kx = 0; kx < 3; kx++)
					{
						int x2 = max(min(x + kx - 1, sizeX - 1), 0);
						
						// update gradient for current position 
						double val = source.getValue(x2, y2);
						gx = gx + val * maskGradX[ky][kx];
						gy = gy + val * maskGradY[ky][kx];
					}
				}

				// set up value of gradient norm
				double h = Math.hypot(gx / 4, gy / 4); 
				target.setValue(x, y, h);
				
				// update max over result array
				if (h > hMax) hMax = h;
			}
		}
		
		return hMax;
	}

	public double processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
	{
		// get array size
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);

		// create kernels for Sobel gradient along each direction
		float[][][] maskGradX = { 
				{ { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } }, 	// z = -1
				{ { -2, 0, 2 }, { -4, 0, 4 }, { -2, 0, 2 } }, 	// z =  0
				{ { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } } }; // z = +1
		float[][][] maskGradY = { 
				{ { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } }, 	// z = -1
				{ { -2, -4, -2 }, { 0, 0, 0 }, { 2, 4, 2 } }, 	// z =  0
				{ { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } } }; // z = +1
		float[][][] maskGradZ = {
				{ { -1, -2, -1 }, { -2, -4, -2 }, { -1, -2, -1 } }, // z = -1
				{ { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, 			// z =  0
				{ { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } } }; 		// z = +1

        // accumulator for max value of gradient norm
        double hMax = 0.0; 
        
        // iterate over pixels
		for (int z = 0; z < sizeZ; z++)
		{
			for (int y = 0; y < sizeY; y++)
			{
				// iterate over pixels of the row
				for (int x = 0; x < sizeX; x++)
				{
				    // accumulators for gradients in X, Y and Z directions
			        double gx = 0;
					double gy = 0;
					double gz = 0;

					// uses replication of border pixel
					for (int kz = 0; kz < 3; kz++)
					{
						int z2 = max(min(z + kz - 1, sizeZ - 1), 0);

						for (int ky = 0; ky < 3; ky++)
						{
							int y2 = max(min(y + ky - 1, sizeY - 1), 0);
							for (int kx = 0; kx < 3; kx++)
							{
								int x2 = max(min(x + kx - 1, sizeX - 1), 0);
								
								// update gradient for current position 
								double val = source.getValue(x2, y2, z2);
								gx = gx + val * maskGradX[kz][ky][kx];
								gy = gy + val * maskGradY[kz][ky][kx];
								gz = gz + val * maskGradZ[kz][ky][kx];
							}
						}
					}

					gx = gx / 16;
					gy = gy / 16;
					gz = gz / 16;

					// set up values of gradient norm
					double h = Math.sqrt(gx * gx + gy * gy + gz * gz);
					target.setValue(x, y, z, h);
	                
	                // update max over result array
	                if (h > hMax) hMax = h;
				}
			}
		}
		
		return hMax;
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
        ScalarArray<?> result = createEmptyOutputArray(array);
        double maxVal = processScalar(ScalarArray.wrap((Array<Scalar>) array), result);
        
        // create result image and update default display
        Image resImage = new Image(result, image);
        resImage.getDisplaySettings().setDisplayRange(new double[] {0, maxVal});
        return resImage;
    }
    
    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        ScalarArray<?> result = createEmptyOutputArray(array);
        processScalar((ScalarArray<?>) array, result);
        return result;
    }

    public ScalarArray<?> createEmptyOutputArray(Array<?> array)
    {
        return Float32Array.create(array.size());
    }

    public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}
}
