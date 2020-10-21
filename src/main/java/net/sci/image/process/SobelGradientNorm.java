/**
 * 
 */
package net.sci.image.process;

import static java.lang.Math.max;
import static java.lang.Math.min;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Compute norm of Sobel gradient of a scalar image (2D or 3D), without storing
 * intermediate vector image. Result is given in a ScalarArray, with values in
 * float.
 * 
 * @author David Legland
 *
 */
public class SobelGradientNorm implements ImageArrayOperator
{
    /**
     * Creates a new instance of Sobel Gradient operator.
     */
    public SobelGradientNorm()
    {
    }

    public void processScalar(ScalarArray<?> input, ScalarArray<?> output)
	{
		if (input instanceof ScalarArray2D && output instanceof ScalarArray2D)
		{
			processScalar2d((ScalarArray2D<?>) input, (ScalarArray2D<?>) output);
		}
		else if (input instanceof ScalarArray3D && output instanceof ScalarArray3D)
		{
			processScalar3d((ScalarArray3D<?>) input, (ScalarArray3D<?>) output);
		}
		else
		{
			throw new RuntimeException(
					"Can not process input of class " + input.getClass()
							+ " with output of class " + output.getClass());
		}
	}

	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);

		float[][] maskGradX = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
		float[][] maskGradY = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

		// accumulators for gradients in X and Y directions
		double gx, gy;

		for (int y = 0; y < sizeY; y++)
		{
			// iterate over pixels of the row
			for (int x = 0; x < sizeX; x++)
			{
				gx = 0;
				gy = 0;

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

				gx = gx / 4;
				gy = gy / 4;

				// set up value of gradient norm
				target.setValue(x, y, Math.hypot(gx, gy));
			}
		}
	}

	public void processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
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

		// accumulators for gradients in X, Y and Z directions
		double gx, gy, gz;

		for (int z = 0; z < sizeZ; z++)
		{
			for (int y = 0; y < sizeY; y++)
			{
				// iterate over pixels of the row
				for (int x = 0; x < sizeX; x++)
				{
					gx = 0;
					gy = 0;
					gz = 0;

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
					target.setValue(x, y, z, Math.sqrt(gx * gx + gy * gy + gz * gz));
				}
			}
		}
	}

    @Override
    public <T> Array<?> process(Array<T> array)
    {
        if (!(array instanceof ScalarArray))
        {
            throw new IllegalArgumentException("Requires a scalar array as input");
        }
        ScalarArray<?> result = createEmptyOutputArray((ScalarArray<?>) array);
        processScalar((ScalarArray<?>) array, result);
        return result;
    }

    public ScalarArray<?> createEmptyOutputArray(ScalarArray<?> array)
    {
        return Float32Array.create(array.size());
    }

    public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}
}
