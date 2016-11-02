/**
 * 
 */
package net.sci.array;

import net.sci.array.data.ScalarArray;
import net.sci.array.data.VectorArray;
import net.sci.array.data.scalar3d.FloatArray3D;
import net.sci.array.data.vector.DoubleVectorArray2D;
import net.sci.array.data.vector.VectorArray2D;
import net.sci.array.type.Vector;

/**
 * A collection of static methods operating on arrays.
 * 
 * @author dlegland
 *
 */
public class Arrays
{

	/**
	 * private constructor to prevent instantiation.
	 */
	private Arrays()
	{
	}

	public static VectorArray<?> stackToVector(Array<?> array)
	{
		if (!(array instanceof ScalarArray))
		{
			throw new IllegalArgumentException("Requires a scalar array");
		}
		
		if (array.dimensionality() != 3)
		{
			throw new IllegalArgumentException("Requires a scalar array with three dimensions");
		}
		
		// size and dimension of input array
		int[] dims = array.getSize();
		int nd = dims.length;
		
		// compute size and dimension of output array
		int[] newDims = new int[nd - 1];
		for (int d= 0; d < nd - 1; d++)
		{
			newDims[d] = dims[d];
		}
		
		// create output array
		VectorArray2D<? extends Vector<?>> result = DoubleVectorArray2D.create(newDims[0], newDims[1], dims[2]);
		int[] pos = new int[3];
		for (int c = 0; c < dims[2]; c++)
		{
			pos[2] = c;
			for (int y = 0; y < dims[1]; y++)
			{
				pos[1] = y;
				for (int x = 0; x < dims[0]; x++)
				{
					pos[0] = x;
					result.setValue(x, y, c, array.getValue(pos));
				}
			}
		}
		
		return result;
	}
	
	public static Array<?> vectorArrayToStack(VectorArray<?> array)
	{
		if (array.dimensionality() != 2)
		{
			throw new IllegalArgumentException("Requires a vector array with two dimensions");
		}
		
		// size and dimension of input array
		int[] dims = array.getSize();
		int nd = dims.length;
		int nChannels = array.getVectorLength();
		
		// compute size and dimension of output array
		int[] newDims = new int[nd + 1];
		for (int d= 0; d < nd; d++)
		{
			newDims[d] = dims[d];
		}
		newDims[nd] = nChannels;
		
		// create output array
		FloatArray3D result = FloatArray3D.create(newDims[0], newDims[1], newDims[2]);
		int[] pos = new int[2];
		for (int c = 0; c < nChannels; c++)
		{
			for (int y = 0; y < dims[1]; y++)
			{
				pos[1] = y;
				for (int x = 0; x < dims[0]; x++)
				{
					pos[0] = x;
					result.setValue(x, y, c, array.get(pos).getValue(c));
				}
			}
		}
		
		return result;
	}
}
