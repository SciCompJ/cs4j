/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.Array;
import net.sci.array.Cursor;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.image.ArrayToArrayImageOperator;
import net.sci.image.process.filter.BoxNeighborhoodRadius;
import net.sci.image.process.filter.Neighborhood;

/**
 * Naive implementation of morphological dilation within a n-dimensional box.
 * 
 * @author dlegland
 *
 */
public final class BoxDilationNaive implements ArrayToArrayImageOperator
{
	int[] radiusList;
	
	
	/**
	 * Creates a new instance of box filter by specifying the list of radius in
	 * each dimension.
	 * 
	 * @param diameters
	 *            the box radius in each dimension
	 */
	public BoxDilationNaive(int[] radiusList)
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
		
		// iterate over 2D positions
		Cursor inputCursor = source.getCursor();
		while (inputCursor.hasNext())
		{
			// iterate position cursor
			inputCursor.forward();
			int[] pos = inputCursor.getPosition();
			
			// init result
			double localMax = Double.NEGATIVE_INFINITY;
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhoodRadius(pos, radiusList);
			for (int[] neighPos : nbg)
			{
				// clamp neighbor position to array bounds
				boolean inside = true;
				for (int d = 0; d < nd; d++)
				{
					// check if current neigbor is within image bounds
					if (neighPos[d] < 0 && neighPos[d] >= sizes[d])
					{
						inside = false;
						break;
					}
				}
				
				if (inside)
				{
					localMax = Math.max(localMax, source.getValue(neighPos));
				}
			}
			
			// setup result in target array
			target.setValue(pos, localMax);
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
		
		// iterate over image pixels
		for(int y = 0; y < sizeY; y++)
		{
			for(int x = 0; x < sizeX; x++)
			{
				// init result
				double localMax = Double.NEGATIVE_INFINITY;
				
				// iterate over neighbors of current pixel
				for (int y2 = y - radiusY; y2 <= y + radiusY; y2++)
				{
					for (int x2 = x - radiusX; x2 <= x + radiusX; x2++)
					{
						// update local max only if pixel is within image bounds 
						if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY)
						{
							localMax = Math.max(localMax, source.getValue(x2, y2));
						}
					}
				}
				
				target.setValue(x, y, localMax);
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
		
		// ensure radius list is large enough
		if (this.radiusList.length < 3)
		{
			throw new RuntimeException("Can not process 3D array with less than three radiuses.");
		}
		
		// get first three radiuses
		int radiusX = this.radiusList[0];
		int radiusY = this.radiusList[1];
		int radiusZ = this.radiusList[2];

		// iterate over image voxels
		for(int z = 0; z < sizeZ; z++)
		{
			for(int y = 0; y < sizeY; y++)
			{
				for(int x = 0; x < sizeX; x++)
				{
					// init result
					double localMax = Double.NEGATIVE_INFINITY;
					
					// iterate over neighbors of current voxel
					for (int z2 = z - radiusZ; z2 <= z + radiusZ; z2++)
					{
						for (int y2 = y - radiusY; y2 <= y + radiusY; y2++)
						{
							for (int x2 = x - radiusX; x2 <= x + radiusX; x2++)
							{
								// update local max only if pixel is within image bounds 
								if (x2 >= 0 && x2 < sizeX && y2 >= 0 && y2 < sizeY && z2 >= 0 && z2 < sizeZ)
								{
									localMax = Math.max(localMax, source.getValue(x2, y2, z2));
								}
							}
						}
					}
					
					target.setValue(x, y, z, localMax);
				}
			}
		}
	}

	/**
	 * Creates a new array the same size and same type as original.
	 */
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return array.newInstance(dims);
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
