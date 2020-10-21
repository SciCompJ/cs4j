/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.scalar.Float32Array;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.vector.VectorArray;
import net.sci.array.vector.VectorArray2D;
import net.sci.array.vector.VectorArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Compute the norm of a vector array.
 * 
 * @author David Legland
 *
 */
public class VectorArrayNorm implements ImageArrayOperator
{
	public void processVector(VectorArray<?> input, ScalarArray<?> output)
	{
//		if (input instanceof VectorArray && output instanceof ScalarArray)
//		{
			processVectorNd((VectorArray<?>) input, (ScalarArray<?>) output);
//		}
//		if (input instanceof VectorArray2D && output instanceof ScalarArray2D)
//		{
//			process2d((VectorArray2D<?>) input, (ScalarArray2D<?>) output);
//		}
//		else if (input instanceof VectorArray3D && output instanceof ScalarArray3D)
//		{
//			process3d((VectorArray3D<?>) input, (ScalarArray3D<?>) output);
//		}
//		else
//		{
//			throw new RuntimeException(
//					"Can not process input of class " + input.getClass()
//							+ " with output of class " + output.getClass());
//		}
	}

	public void processVector2d(VectorArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int nChannels = source.channelNumber();
		
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

	public void processVector3d(VectorArray3D<?> source, ScalarArray3D<?> target)
	{
		// get array size
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);
		int nChannels = source.channelNumber();

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

	public void processVectorNd(VectorArray<?> source, ScalarArray<?> target)
	{
		// iterate over vector pixels
		for (int[] pos : target.positions())
		{
		    target.setValue(pos, computeNorm(source.getValues(pos)));
		}
	}

	private double computeNorm(double[] vector)
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
	    processVector((VectorArray<?>) array, norm);
	    return norm;
    }

    @Override
	public boolean canProcess(Array<?> array)
	{
		return array instanceof VectorArray;
	}
}
