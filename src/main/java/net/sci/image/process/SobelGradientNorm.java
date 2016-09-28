/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.FloatArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.Image;
import net.sci.image.ImageOperator;

/**
 * Compute norm of Sobel gradient of a scalar image (2D or 3D), without storing
 * intermediate vector image. Result is given in a ScalarArray, with values in
 * float.
 * 
 * @author David Legland
 *
 */
public class SobelGradientNorm implements ImageOperator
{
	@Override
	public void process(Image inputImage, Image outputImage)
	{
		Array<?> inputData = inputImage.getData();
		Array<?> outputData = outputImage.getData();
		
		if (inputData instanceof ScalarArray2D && outputData instanceof ScalarArray2D)
		{
			process2d((ScalarArray2D<?>) inputData, (ScalarArray2D<?>) outputData);
		}
		else if (inputData instanceof ScalarArray3D && outputData instanceof ScalarArray3D)
		{
			process3d((ScalarArray3D<?>) inputData, (ScalarArray3D<?>) outputData);
		}
	}

	public Image createEmptyOutputImage(Image inputImage)
	{
		Array<?> array = inputImage.getData();
		array = FloatArray.create(array.getSize());
		return new Image(array, inputImage);
	}

	public void process2d(ScalarArray2D<?> inputImage, ScalarArray2D<?> outputImage)
	{
		int sizeX = inputImage.getSize(0);
		int sizeY = inputImage.getSize(1);

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
					int y2 = Math.max(Math.min(y + ky - 1, sizeY - 1), 0);
					for (int kx = 0; kx < 3; kx++)
					{
						int x2 = Math.max(Math.min(x + kx - 1, sizeX - 1), 0);
						double val = inputImage.getValue(x2, y2);
						gx = gx + val * maskGradX[ky][kx];
						gy = gy + val * maskGradY[ky][kx];
					}
				}

				gx = gx / 4;
				gy = gy / 4;

				// set up value of gradient norm
				outputImage.setValue(x, y, Math.hypot(gx, gy));
			}
		}
	}

	public void process3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int sizeZ = source.getSize(2);

		float[][][] maskGradX = { { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } }, // z
																				// =
																				// -1
				{ { -2, 0, 2 }, { -4, 0, 4 }, { -2, 0, 2 } }, // z = 0
				{ { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } } }; // z = +1
		float[][][] maskGradY = { { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } }, // z
																				// =
																				// -1
				{ { -2, -4, -2 }, { 0, 0, 0 }, { 2, 4, 2 } }, // z = 0
				{ { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } } }; // z = +1
		float[][][] maskGradZ = {
				{ { -1, -2, -1 }, { -2, -4, -2 }, { -1, -2, -1 } }, // z = -1
				{ { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, // z = 0
				{ { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } } }; // z = +1

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
						int z2 = Math.max(Math.min(z + kz - 1, sizeZ - 1), 0);

						for (int ky = 0; ky < 3; ky++)
						{
							int y2 = Math.max(Math.min(y + ky - 1, sizeY - 1),
									0);
							for (int kx = 0; kx < 3; kx++)
							{
								int x2 = Math.max(
										Math.min(x + kx - 1, sizeX - 1), 0);
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
					target.setValue(x, y, z,
							Math.sqrt(gx * gx + gy * gy + gz * gz));
				}
			}
		}
		
	}

//	@Override
//	public Array process(Array image)
//	{
//		// Check input type validity
//		if (!isApplicableTo(image))
//		{
//			throw new IllegalArgumentException(
//					"Input image must be an instance of ScalarArray");
//		}
//
//		if (image instanceof ScalarArray2D)
//			return applyToScalar2D((ScalarArray2D) image);
//		if (image instanceof ScalarArray3D)
//			return applyToScalar3D((ScalarArray3D) image);
//
//		throw new RuntimeException(
//				"Input image must be an instance of ScalarArray");
//	}


//	public FloatArray3D applyToScalar3D(ScalarArray3D image)
//	{
//		int width = image.getWidth();
//		int height = image.getHeight();
//		int depth = image.getDepth();
//
//		FloatArray3DBuffer res = new FloatArray3DBuffer(width, height, depth);
//
//		float[][][] maskGradX = { { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } }, // z
//																				// =
//																				// -1
//				{ { -2, 0, 2 }, { -4, 0, 4 }, { -2, 0, 2 } }, // z = 0
//				{ { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } } }; // z = +1
//		float[][][] maskGradY = { { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } }, // z
//																				// =
//																				// -1
//				{ { -2, -4, -2 }, { 0, 0, 0 }, { 2, 4, 2 } }, // z = 0
//				{ { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } } }; // z = +1
//		float[][][] maskGradZ = {
//				{ { -1, -2, -1 }, { -2, -4, -2 }, { -1, -2, -1 } }, // z = -1
//				{ { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } }, // z = 0
//				{ { 1, 2, 1 }, { 2, 4, 2 }, { 1, 2, 1 } } }; // z = +1
//
//		// accumulators for gradients in X and Y directions
//		double gx, gy, gz;
//
//		for (int z = 0; z < depth; z++)
//		{
//			for (int y = 0; y < height; y++)
//			{
//				// iterate over pixels of the row
//				for (int x = 0; x < width; x++)
//				{
//					gx = 0;
//					gy = 0;
//					gz = 0;
//
//					// uses replication of border pixel
//					for (int kz = 0; kz < 3; kz++)
//					{
//						int z2 = Math.max(Math.min(z + kz - 1, depth - 1), 0);
//
//						for (int ky = 0; ky < 3; ky++)
//						{
//							int y2 = Math.max(Math.min(y + ky - 1, height - 1),
//									0);
//							for (int kx = 0; kx < 3; kx++)
//							{
//								int x2 = Math.max(
//										Math.min(x + kx - 1, width - 1), 0);
//								double val = image.getValue(x2, y2, z2);
//								gx = gx + val * maskGradX[kz][ky][kx];
//								gy = gy + val * maskGradY[kz][ky][kx];
//								gz = gz + val * maskGradZ[kz][ky][kx];
//							}
//						}
//					}
//
//					gx = gx / 16;
//					gy = gy / 16;
//					gz = gz / 16;
//
//					// set up values of gradient norm
//					res.setValue(x, y, z,
//							Math.sqrt(gx * gx + gy * gy + gz * gz));
//				}
//			}
//		}
//
//		return res;
//	}

}
