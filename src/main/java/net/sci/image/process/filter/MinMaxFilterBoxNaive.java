/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.Arrays;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.process.VectorArrayMarginalOperator;
import net.sci.image.ImageArrayOperator;

/**
 * Naive implementation of min/max filtering within a n-dimensional box, used
 * for validating box processing.
 * 
 * @author dlegland
 *
 * @see BoxFilter
 * @see MedianFilterBox
 */
public final class MinMaxFilterBoxNaive extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator, VectorArrayMarginalOperator
{
	public enum Type 
	{
		MIN,
		MAX
	};
	
	Type type = Type.MAX;

    /** The size of the box in each dimension */
	int[] diameters;

	
	/**
     * Creates a new instance of box filter by specifying the list of diameters
     * in each dimension.
     * 
     * @param diameters
     *            the box diameter in each dimension
     * @param type
     *            filter (MAX or MIN)
     */
	public MinMaxFilterBoxNaive(Type type, int[] diameters)
	{
		this.type = type;
		
		this.diameters = new int[diameters.length];
		System.arraycopy(diameters, 0, this.diameters, 0, diameters.length);
	}

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
            processScalarNd(source, target);
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
		int[] sizes = source.size();
		
		// get the sign for min/max computations
		int sign = this.type == Type.MAX ? +1 : -1;
		
		// check dimensions
		if (this.diameters.length < source.dimensionality())
		{
			throw new RuntimeException("Requires at least as many diameters as array dimensionality");
		}
		
        Neighborhood nbg = new BoxNeighborhood(diameters);
        
		// iterate over positions
        for (int[] pos : target.positions())
        {
			// init result
			double localMax = Double.NEGATIVE_INFINITY;
			
			// iterate over neighbors
			for (int[] neighPos : nbg.neighbors(pos))
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
					localMax = Math.max(localMax, source.getValue(neighPos) * sign);
				}
			}
			
			// setup result in target array
			target.setValue(pos, localMax * sign);
		}
	}
	
	/**
	 * Process the specific case of 2D arrays.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
	 */
	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		// get size of input array
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		
		// get the sign for min/max computations
		int sign = this.type == Type.MAX ? +1 : -1;
		
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
		
		for(int y = 0; y < sizeY; y++)
		{
		    this.fireProgressChanged(this, y, sizeY);
		    
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
							localMax = Math.max(localMax, source.getValue(x2, y2) * sign);
						}
					}
				}

				target.setValue(x, y, localMax * sign);
			}
		}
		
		this.fireProgressChanged(this, sizeY, sizeY);
	}

	/**
	 * Process the specific case of 3D arrays.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
	 */
	public void processScalar3d(ScalarArray3D<?> source, ScalarArray3D<?> target)
	{
		// get size of input array
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);
		
		// get the sign for min/max computations
		int sign = this.type == Type.MAX ? +1 : -1;
		
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
		
		for(int z = 0; z < sizeZ; z++)
		{
		    this.fireProgressChanged(this, z, sizeZ);
            
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
									localMax = Math.max(localMax, source.getValue(x2, y2, z2) * sign);
								}
							}
						}
					}

					target.setValue(x, y, z, localMax * sign);
				}
			}
		}
		
		this.fireProgressChanged(this, sizeZ, sizeZ);        
	}
	
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> result = array.newInstance(array.size());
        processScalar(array, result);
        return result;
    }
}
