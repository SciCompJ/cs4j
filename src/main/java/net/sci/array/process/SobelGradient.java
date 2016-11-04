/**
 * 
 */
package net.sci.array.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.VectorArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.array.data.vector.FloatVectorArray2D;
import net.sci.array.data.vector.FloatVectorArray3D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.data.vector.VectorArray3D;

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
			process2d((ScalarArray2D<?>) source, (VectorArray2D<?>) target);
		}
		else if (source instanceof ScalarArray3D && target instanceof VectorArray3D)
		{
			process3d((ScalarArray3D<?>) source, (VectorArray3D<?>) target);
		}
		else
		{
			throw new RuntimeException("Unable to compute Sobel gradient");
		}
	}

	public void process2d(ScalarArray2D<?> source, VectorArray2D<?> target)
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
	
	public void process3d(ScalarArray3D<?> source, VectorArray3D<?> target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int sizeZ = source.getSize(2);
		
		if (target.getSize(0) != sizeX || target.getSize(1) != sizeY || target.getSize(2) != sizeZ)
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

		// Iterate over image pixels
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
								res[0] += val * gradX[iz][iy][ix];
								res[1] += val * gradY[iz][iy][ix];
								res[2] += val * gradZ[iz][iy][ix];
							}
						}
					}
					

					// update result image
					target.setValues(x, y, z, res);
				}
			}
		}
	}
	
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		if (array instanceof ScalarArray2D)
		{
			int size0 = array.getSize(0);
			int size1 = array.getSize(1);
			return FloatVectorArray2D.create(size0, size1, 2);
		}
		else if (array instanceof ScalarArray3D)
		{
			int size0 = array.getSize(0);
			int size1 = array.getSize(1);
			int size2 = array.getSize(2);
			return FloatVectorArray3D.create(size0, size1, size2, 3);
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
	
	public boolean canProcess(Array<?> source, Array<?> target)
	{
		if (!(source instanceof ScalarArray)) return false;
		if (!(target instanceof VectorArray)) return false;
		if(source.dimensionality() != target.dimensionality()) return false;
		return true;
	}
}
