/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.Cursor;
import net.sci.array.data.FloatArray;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;

/**
 * Box filter for multidimensional arrays. Considers a square box, with length
 * 2*r+1.
 * 
 * @author dlegland
 *
 */
public final class BoxFilter implements ArrayOperator
{
	int[] radiusList;
	
	
	/**
	 * Creates a new instance of box filter by specifying the list of radius in
	 * each dimension.
	 * 
	 * @param radiusList
	 *            the box radius in each dimension
	 */
	public BoxFilter(int[] radiusList)
	{
		this.radiusList = new int[radiusList.length];
		for (int i = 0; i < radiusList.length; i++)
		{
			this.radiusList[i] = radiusList[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		// Choose the best possible implementation, depending on array dimensions
		if (source instanceof ScalarArray2D && target instanceof ScalarArray2D)
		{
			processScalar2d((ScalarArray2D<?>) source, (ScalarArray2D<?>) target);
		}
		else if (source instanceof ScalarArray3D && target instanceof ScalarArray3D)
		{
			processScalar3d((ScalarArray3D<?>) source, (ScalarArray3D<?>) target);
		}
		else if (source instanceof ScalarArray && target instanceof ScalarArray)
		{
			// most generic implementation, slow...
			processScalar((ScalarArray<?>) source, (ScalarArray<?>) target);
		}
		else
		{
			throw new IllegalArgumentException("Can not process array of class " + source.getClass());
		}

	}

	/**
	 * Process scalar arrays of any dimension.
	 */
	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
	{
		// get array size (for cropping)
		int nd = source.dimensionality();
		int[] sizes = source.getSize();
		
		// get first two radiuses
		if (this.radiusList.length < source.dimensionality())
		{
			throw new RuntimeException("Requires at least as many radiuses as array dimensionality");
		}
		// compute the normalization constant
		int boxSize = 1;
		for (int r : this.radiusList)
		{
			boxSize *= (2 * r + 1);
		}
		
		// iterate over 2D positions
		Cursor inputCursor = source.getCursor();
		while (inputCursor.hasNext())
		{
			// iterate position cursor
			inputCursor.forward();
			int[] pos = inputCursor.getPosition();
			
			// init result
			double sum = 0;
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhood(pos, radiusList);
			for (int[] neighPos : nbg)
			{
				
				// clamp neighbor position to array bounds
				for (int d = 0; d < nd; d++)
				{
					neighPos[d] = Math.min(Math.max(neighPos[d], 0), sizes[d]-1);
				}
								
				// get value of "clamped" neighbor
				sum += source.getValue(neighPos);
			}
			
			// setup result in target array
			target.setValue(pos, sum / boxSize);
		}
	}
	
	/**
	 * Process the specific case of 2D arrays.
	 */
	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		// get size of input array
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		// get first two radiuses
		if (this.radiusList.length < 2)
		{
			throw new RuntimeException("Can not process 2D array with less than two radiuses.");
		}
		int radiusX = this.radiusList[0];
		int radiusY = this.radiusList[1];

		// compute the normalization constant
		int boxSize = 1;
		for (int r : this.radiusList)
		{
			boxSize *= (2 * r + 1);
		}
		
		for(int y = 0; y < sizeY; y++)
		{
			for(int x = 0; x < sizeX; x++)
			{
				double sum = 0;
				
				// iterate over neighbors
				for (int y2 = y - radiusY; y2 <= y + radiusY; y2++)
				{
					int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
					for (int x2 = x - radiusX; x2 <= x + radiusX; x2++)
					{
						int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
						sum += source.getValue(x2r, y2r);
					}
				}
				target.setValue(x, y, sum / boxSize);
			}
		}
	}

	/**
	 * Process the specific case of 3D arrays.
	 */
	public void processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
	{
		// get size of input array
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int sizeZ = source.getSize(2);
		
		// get first three radiuses
		if (this.radiusList.length < 3)
		{
			throw new RuntimeException("Can not process 3D array with less than three radiuses.");
		}
		int radiusX = this.radiusList[0];
		int radiusY = this.radiusList[1];
		int radiusZ = this.radiusList[2];

		// compute the normalization constant
		int boxSize = 1;
		for (int r : this.radiusList)
		{
			boxSize *= (2 * r + 1);
		}
		
		for(int z = 0; z < sizeZ; z++)
		{
			for(int y = 0; y < sizeY; y++)
			{
				for(int x = 0; x < sizeX; x++)
				{
					double sum = 0;

					// iterate over neighbors
					for (int z2 = z - radiusZ; z2 <= z + radiusZ; z2++)
					{
						int z2r = Math.min(Math.max(z2, 0), sizeZ - 1);
						for (int y2 = y - radiusY; y2 <= y + radiusY; y2++)
						{
							int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
							for (int x2 = x - radiusX; x2 <= x + radiusX; x2++)
							{
								int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
								sum += source.getValue(x2r, y2r, z2r);
							}
						}
					}
					target.setValue(x, y, z, sum / boxSize);
				}
			}
		}
	}

	/**
	 * Creates a new array the same size as original, with float type.
	 */
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return FloatArray.create(dims);
	}
	
	public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}

	public boolean canProcess(Array<?> source, Array<?> target)
	{
		return source instanceof ScalarArray && target instanceof ScalarArray;
	}
}
