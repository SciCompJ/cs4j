/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.process.VectorArrayMarginalOperator;
import net.sci.array.scalar.ScalarArray;
import net.sci.image.ImageArrayOperator;

/**
 * Computes the median value for binary array in a box neighborhood around each
 * array element. Equivalent to a majority filter.
 * 
 * @author dlegland
 *
 * @see BoxFilter
 * @see VarianceFilterBox
 */
public final class MedianFilterBoxBinary extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator, VectorArrayMarginalOperator
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
	public MedianFilterBoxBinary(int[] diameters)
	{
		this.diameters = new int[diameters.length];
		for (int i = 0; i < diameters.length; i++)
		{
			this.diameters[i] = diameters[i];
		}
	}

	public void processBinary(BinaryArray source, BinaryArray target)
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
            processBinary2d(BinaryArray2D.wrap(source), BinaryArray2D.wrap(target));
        }
        else if (nd1 == 3 && nd2 == 3)
        {
            processBinary3d(BinaryArray3D.wrap(source), BinaryArray3D.wrap(target));
        }
        else 
        {
            // use the most generic implementation, also slower
            processBinaryNd(source, target);
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
	public void processBinary2d(BinaryArray2D source, BinaryArray2D target)
	{
		// get size of input array
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		
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
		
		// compute the normalization constant
		int totalCount = 1;
		for (int d : this.diameters)
		{
			totalCount *= d;
		}
				
		// the position of the median value within the sorted array
		int medianCount = (totalCount - 1) / 2;
		
		for(int y = 0; y < sizeY; y++)
		{
			this.fireProgressChanged(this, y, sizeY);
			
			for(int x = 0; x < sizeX; x++)
			{
				// iterate over neighbors of current pixel
				int count = 0;
				for (int y2 = y - ry1; y2 < y + ry2; y2++)
				{
					int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
					for (int x2 = x - rx1; x2 < x + rx2; x2++)
					{
						int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
						if (source.getBoolean(x2r, y2r))
						{
						    count++;
						}
					}
				}
				
				// sort neighborhood values and keep the median value
				target.setBoolean(x, y, count > medianCount);
			}
		}
	}

	/**
	 * Process the specific case of 3D arrays.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
	 */
	public void processBinary3d(BinaryArray3D source, BinaryArray3D target)
	{
		// get size of input array
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);
		
		// check dimensions
		if (this.diameters.length < 3)
		{
			throw new RuntimeException("Can not process 3D array with less than three diameters.");
		}

		// compute the radius extent in each direction
//		double diamX = (double) this.diameters[0];
//		int rx1 = (int) Math.floor(diamX / 2.0);
//		int rx2 = (int) Math.ceil(diamX / 2.0);
		double diamY = (double) this.diameters[1];
		int ry1 = (int) Math.floor(diamY / 2.0);
		int ry2 = (int) Math.ceil(diamY / 2.0);
		double diamZ = (double) this.diameters[2];
		int rz1 = (int) Math.floor(diamZ / 2.0);
		int rz2 = (int) Math.ceil(diamZ / 2.0);
		
		// compute the normalization constant
		int totalCount = 1;
		for (int diam : this.diameters)
		{
			totalCount *= diam;
		}
		
		// allocate an array of counts for each x-slice of the box
		int[] xCounts = new int[this.diameters[2]];
        
		// the position of the median value within the sorted array
		int medianCount = (totalCount - 1) / 2;
		int boxCount = 0;
		
		for(int z = 0; z < sizeZ; z++)
		{
			this.fireProgressChanged(this, z, sizeZ);
			
			for(int y = 0; y < sizeY; y++)
			{
			    // start a now row: reset the slice counts
			    boxCount = 0;
			    for (int d = 0; d < this.diameters[2]; d++)
			    {
			        xCounts[d] = 0;
			    }
			    
				for(int x = 0; x < sizeX; x++)
				{
				    // update current count and shifts the slice counts by one slice
				    // TODO: should update by duplicating voxels on first YZ slice
				    boxCount -= xCounts[0];
	                for (int d = 0; d < this.diameters[2] - 1; d++)
	                {
	                    xCounts[d] = xCounts[d+1];
	                }
				    
					// count elements on the last slice (x2 = x + rz2 - 1)
	                int x2r = Math.min(x + rz2 - 1, sizeX - 1);
					int count = 0;
					for (int z2 = z - rz1; z2 < z + rz2; z2++)
					{
						int z2r = Math.min(Math.max(z2, 0), sizeZ - 1);
						for (int y2 = y - ry1; y2 < y + ry2; y2++)
						{
							int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
                            if (source.getBoolean(x2r, y2r, z2r))
                            {
                                count++;
                            }
						}
					}
					xCounts[this.diameters[2] - 1] = count;
					boxCount += count;
					
					// sort neighborhood values and keep the median value
					target.setBoolean(x, y, z, boxCount > medianCount);
				}
			}
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
    public void processBinaryNd(BinaryArray source, BinaryArray target)
    {
    	// get array size (for cropping)
    	int nd = source.dimensionality();
    	int[] sizes = source.size();
    	
    	// check dimensions
    	if (this.diameters.length < source.dimensionality())
    	{
    		throw new RuntimeException("Requires at least as many diameters as array dimensionality");
    	}
    	
    	// compute the normalization constant
    	int totalCount = 1;
    	for (int d : this.diameters)
    	{
    		totalCount *= d;
    	}
    	
    	
    	Neighborhood nbg = new BoxNeighborhood(diameters);
    		
    	// the position of the median value within the sorted array
    	int medianCount = (totalCount - 1) / 2;
    	
    	// iterate over positions
        for (int[] pos : target.positions())
        {
    		// iterate over neighbors
    		int count = 0;
    		for (int[] neighPos : nbg.neighbors(pos))
    		{
    			// clamp neighbor position to array bounds
    			for (int d = 0; d < nd; d++)
    			{
    				neighPos[d] = Math.min(Math.max(neighPos[d], 0), sizes[d]-1);
    			}
    							
    			// get value of "clamped" neighbor
                if (source.getBoolean(neighPos))
                {
                    count++;
                }
    		}
    		
    		// sort neighborhood values and keep the median value
    		target.setBoolean(pos, count > medianCount);
    	}
    }

    public BinaryArray processBinary(BinaryArray array)
    {
        BinaryArray result = array.newInstance(array.size());
        processBinary(array, result);
        return result;
    }
    
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary(BinaryArray.wrapScalar(array));
        }
        throw new RuntimeException("Requires a binary array as input");
    }
    
    @Override
    public boolean canProcess(Array<?> array)
    {
        return array instanceof BinaryArray;
    }
}
