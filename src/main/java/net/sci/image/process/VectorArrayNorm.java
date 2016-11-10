/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.data.FloatArray;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.VectorArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.data.vector.VectorArray3D;
import net.sci.array.type.Vector;
import net.sci.image.ImageArrayOperator;

/**
 * Compute the norm of a vector array.
 * 
 * @author David Legland
 *
 */
public class VectorArrayNorm implements ImageArrayOperator
{
	@Override
	public void process(Array<?> input, Array<?> output)
	{
		if (input instanceof VectorArray && output instanceof ScalarArray)
		{
			processNd((VectorArray<?>) input, (ScalarArray<?>) output);
		}
//		if (input instanceof VectorArray2D && output instanceof ScalarArray2D)
//		{
//			process2d((VectorArray2D<?>) input, (ScalarArray2D<?>) output);
//		}
//		else if (input instanceof VectorArray3D && output instanceof ScalarArray3D)
//		{
//			process3d((VectorArray3D<?>) input, (ScalarArray3D<?>) output);
//		}
		else
		{
			throw new RuntimeException(
					"Can not process input of class " + input.getClass()
							+ " with output of class " + output.getClass());
		}
	}

	public void process2d(VectorArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int nChannels = source.getVectorLength();
		
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

	public void process3d(VectorArray3D<?> source, ScalarArray3D<?> target)
	{
		// get array size
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int sizeZ = source.getSize(2);
		int nChannels = source.getVectorLength();

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

	public void processNd(VectorArray<?> source, ScalarArray<?> target)
	{
		// create iterators
		VectorArray.Iterator<? extends Vector<?>> sourceIterator = source.iterator();
		ScalarArray.Iterator<?> targetIterator = target.iterator();
		
		while (targetIterator.hasNext() && sourceIterator.hasNext())
		{
			// extract the next vector
			Vector<?> vector = sourceIterator.next();
			
			// compute norm of current element
			double norm = 0;
			for (double d : vector.getValues())
			{
				norm += d * d;
			}
			
			// update target
			targetIterator.forward();
			targetIterator.setValue(Math.sqrt(norm));
		}
	}

	@Override
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		return FloatArray.create(array.getSize());
	}

	@Override
	public boolean canProcess(Array<?> array)
	{
		return array instanceof VectorArray;
	}
	
	@Override
	public boolean canProcess(Array<?> source, Array<?> target)
	{
		if (!(source instanceof VectorArray)) return false;
		if (!(target instanceof ScalarArray)) return false;
		return source.dimensionality() == target.dimensionality();
	}
}
