/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.vector.BufferedDoubleVectorArray2D;
import net.sci.array.data.vector.VectorArray2D;

/**
 * @author dlegland
 *
 */
public class SobelGradient implements ArrayOperator
{

	/**
	 * 
	 */
	public SobelGradient()
	{
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof ScalarArray2D && target instanceof VectorArray2D)
		{
			process2d((ScalarArray2D<?>) source, (VectorArray2D) target);
		}
		else
		{
			throw new RuntimeException("Unable to compute Sobel gradient");
		}
	}

	public void process2d(ScalarArray2D<?> source, VectorArray2D target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		if (target.getSize(0) != sizeX || target.getSize(1) != sizeY)
		{
			throw new IllegalArgumentException("Input image and output image must have same size");
		}
		
		// Sobel kernels for X and Y gradients
		double[][] gradX = new double[][] { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
		double[][] gradY = new double[][] { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

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
						res[0] += val * gradX[iy][ix];
						res[1] += val * gradY[iy][ix];
					}
				}

				// update result image
				target.setValues(x, y, res);
			}
		}

	}
	
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		if (array instanceof ScalarArray2D)
		{
			int size0 = array.getSize(0);
			int size1 = array.getSize(1);
			return new BufferedDoubleVectorArray2D(size0, size1, 2);
		}
		else
		{
			throw new RuntimeException("Unable to create default array for input of class " + array.getClass());
		}
	}

}
