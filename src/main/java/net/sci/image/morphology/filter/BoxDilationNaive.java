/**
 * 
 */
package net.sci.image.morphology.filter;

import net.sci.array.Arrays;
import net.sci.array.Cursor;
import net.sci.array.CursorIterator;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;
import net.sci.array.data.scalar3d.ScalarArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.type.Scalar;
import net.sci.image.ImageArrayOperator;
import net.sci.image.process.filter.BoxNeighborhood;
import net.sci.image.process.filter.Neighborhood;

/**
 * Naive implementation of morphological dilation within a n-dimensional box.
 * 
 * @author dlegland
 *
 */
public final class BoxDilationNaive implements ImageArrayOperator, ScalarArrayOperator
{
    /** The size of the box in each dimension */
    int[] diameters;
	
	/**
	 * Creates a new instance of box filter by specifying the list of diameters in
	 * each dimension.
	 * 
	 * @param diameters
	 *            the box diameter in each dimension
	 */
	public BoxDilationNaive(int[] diameters)
	{
		this.diameters = new int[diameters.length];
		for (int i = 0; i < diameters.length; i++)
		{
			this.diameters[i] = diameters[i];
		}
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
	{
        int nd1 = source.dimensionality();
        int nd2 = target.dimensionality();
        if (nd1 != nd2)
        {
            throw new IllegalArgumentException("Both arrays must have the same dimensionality");
        }
        
        if (!Arrays.isSameSize(source, target))
        {
            throw new IllegalArgumentException("Both arrays must have the same size");
        }
        
		// Choose the best possible implementation, depending on array dimensions
		if (nd1 == 2 && nd2 == 2)
		{
			processScalar2d(ScalarArray2D.wrap(source), ScalarArray2D.wrap(target));
		}
		else if (nd1 == 3 && nd2 == 3)
		{
			processScalar3d(ScalarArray3D.wrap(source), ScalarArray3D.wrap(target));
		}
		else 
		{
		    // use the most generic implementation, also slower
			processScalar((ScalarArray<?>) source, (ScalarArray<?>) target);
		}
	}

	/**
	 * Process scalar arrays of any dimension.
	 * 
	 * @param source
	 *            the source array
	 * @param target
	 *            the target array
	 */
	public void processScalarNd(ScalarArray<?> source, ScalarArray<?> target)
	{
		// get array size (for cropping)
		int nd = source.dimensionality();
		int[] sizes = source.getSize();
		
		// get first two radiuses
		if (this.diameters.length < source.dimensionality())
		{
			throw new RuntimeException("Requires at least as many diameters as array dimensionality");
		}
		
		// iterate over positions
		CursorIterator<? extends Cursor> iter = source.cursorIterator();
		while (iter.hasNext())
		{
			// iterate position cursor
			iter.forward();
			int[] pos = iter.getPosition();
			
			// init result
			double localMax = Double.NEGATIVE_INFINITY;
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhood(pos, diameters);
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
	 * Process the specific case of 2D scalar arrays.
	 * 
	 * @param source
	 *            the source array
	 * @param target
	 *            the target array
	 */
	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		// get size of input array
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
        // check dimensions
        if (this.diameters.length < 2)
        {
            throw new RuntimeException("Can not process 2D array with less than two diameters.");
        }

        // compute the radius extent in each direction
        double diamX = (double) this.diameters[0];
        int rx1 = (int) Math.floor(diamX / 2.0);
        int rx2 = (int) Math.ceil(diamX / 2.0);
        double diamY = (double) this.diameters[1];
        int ry1 = (int) Math.floor(diamY / 2.0);
        int ry2 = (int) Math.ceil(diamY / 2.0);
        
		
		// iterate over image pixels
		for(int y = 0; y < sizeY; y++)
		{
			for(int x = 0; x < sizeX; x++)
			{
				// init result
				double localMax = Double.NEGATIVE_INFINITY;
				
                // iterate over neighbors
                for (int y2 = y - ry1; y2 < y + ry2; y2++)
                {
                    for (int x2 = x - rx1; x2 < x + rx2; x2++)
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
	 * Process the specific case of 3D scalar arrays.
	 * 
	 * @param source
	 *            the source array
	 * @param target
	 *            the target array
	 */
	public void processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
	{
		// get size of input array
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		int sizeZ = source.getSize(2);
		
        // check dimensions
        if (this.diameters.length < 3)
        {
            throw new RuntimeException("Can not process 3D array with less than three diameters.");
        }

        // compute the radius extent in each direction
        double diamX = (double) this.diameters[0];
        int rx1 = (int) Math.floor(diamX / 2.0);
        int rx2 = (int) Math.ceil(diamX / 2.0);
        double diamY = (double) this.diameters[1];
        int ry1 = (int) Math.floor(diamY / 2.0);
        int ry2 = (int) Math.ceil(diamY / 2.0);
        double diamZ = (double) this.diameters[2];
        int rz1 = (int) Math.floor(diamZ / 2.0);
        int rz2 = (int) Math.ceil(diamZ / 2.0);
        
		// iterate over image voxels
		for(int z = 0; z < sizeZ; z++)
		{
			for(int y = 0; y < sizeY; y++)
			{
				for(int x = 0; x < sizeX; x++)
				{
					// init result
					double localMax = Double.NEGATIVE_INFINITY;
					
                    // iterate over neighbors
                    for (int z2 = z - rz1; z2 < z + rz2; z2++)
                    {
                        for (int y2 = y - ry1; y2 < y + ry2; y2++)
                        {
                            for (int x2 = x - rx1; x2 < x + rx2; x2++)
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

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<?> output = array.newInstance(array.getSize());
        processScalar(array, output);
        return output;
    }
}
