/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.vector.Float32VectorArray2D;
import net.sci.array.vector.Float32VectorArray3D;
import net.sci.array.vector.VectorArray;
import net.sci.array.vector.VectorArray2D;
import net.sci.array.vector.VectorArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Compute gradient of a scalar array using Sobel coefficients, using a
 * VectorArray for representing the result.
 * 
 * @author dlegland
 */
public class SobelGradient implements ImageArrayOperator
{
	/**
	 * Creates a new instance of Sobel Gradient operator.
	 */
	public SobelGradient()
	{
	}

    public void processScalar(ScalarArray<?> source, VectorArray<?> target)
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
        
        // Choose the best possible implementation, depending on array dimensions
        if (nd1 == 2 && nd2 == 2)
        {
            processScalar2d(ScalarArray2D.wrap(source), VectorArray2D.wrap(target));
        }
        else if (nd1 == 3 && nd2 == 3)
        {
            processScalar3d(ScalarArray3D.wrap(source), VectorArray3D.wrap(target));
        }
        else 
        {
            throw new IllegalArgumentException("Can process Sobel Gradient only on 2D or 3D images");
        }
    }


	public void processScalar2d(ScalarArray2D<?> source, VectorArray2D<?> target)
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
						res[0] += val * gradX[iy][ix] / 8;
						res[1] += val * gradY[iy][ix] / 8;
					}
				}

				// update result image
				target.setValues(x, y, res);
			}
		}
	}
	
	public void processScalar3d(ScalarArray3D<?> source, VectorArray3D<?> target)
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
								res[0] += val * gradX[iz][iy][ix] / 32;
								res[1] += val * gradY[iz][iy][ix] / 32;
								res[2] += val * gradZ[iz][iy][ix] / 32;
							}
						}
					}
					

					// update result image
					target.setValues(x, y, z, res);
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
        VectorArray<?> result = createEmptyOutputArray(array);
        processScalar((ScalarArray<?>) array, result);
        return result;
    }

    private VectorArray<?> createEmptyOutputArray(Array<?> array)
    {
        if (array instanceof ScalarArray2D)
        {
            int size0 = array.getSize(0);
            int size1 = array.getSize(1);
            return Float32VectorArray2D.create(size0, size1, 2);
        }
        else if (array instanceof ScalarArray3D)
        {
            int size0 = array.getSize(0);
            int size1 = array.getSize(1);
            int size2 = array.getSize(2);
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
