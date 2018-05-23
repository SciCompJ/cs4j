/**
 * 
 */
package net.sci.image.process.filter;

import java.util.Arrays;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.process.ScalarArrayOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Computes the median value in a box neighborhood around each array element.
 * 
 * @author dlegland
 *
 * @see BoxFilter
 * @see BoxVarianceFilter
 */
public final class BoxMedianFilter extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator
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
	public BoxMedianFilter(int[] diameters)
	{
		this.diameters = new int[diameters.length];
		for (int i = 0; i < diameters.length; i++)
		{
			this.diameters[i] = diameters[i];
		}
	}

	public void processScalar(ScalarArray<?> source, ScalarArray<?> target)
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
		int[] sizes = source.getSize();
		
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
		
		// the position of the median value within the sorted array
		int medianCount = (totalCount - 1) / 2;
		double[] values = new double[totalCount];
		
		// iterate over positions
        Array.PositionIterator iter = source.positionIterator();
        while (iter.hasNext())
        {
            // iterate position cursor
            int[] pos = iter.next();
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhood(pos, diameters);
			int count = 0;
			for (int[] neighPos : nbg)
			{
				// clamp neighbor position to array bounds
				for (int d = 0; d < nd; d++)
				{
					neighPos[d] = Math.min(Math.max(neighPos[d], 0), sizes[d]-1);
				}
								
				// get value of "clamped" neighbor
				values[count++] = source.getValue(neighPos); 
			}
			
			// sort neighborhood values and keep the median value
			Arrays.parallelSort(values);
			target.setValue(pos, values[medianCount]);
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
		
		// compute the normalization constant
		int totalCount = 1;
		for (int d : this.diameters)
		{
			totalCount *= d;
		}
				
		// the position of the median value within the sorted array
		int medianCount = (totalCount - 1) / 2;
		double[] values = new double[totalCount];
		
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
						values[count++] = source.getValue(x2r, y2r); 
					}
				}
				
				// sort neighborhood values and keep the median value
				Arrays.parallelSort(values);
				target.setValue(x, y, values[medianCount]);
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
		
		// compute the normalization constant
		int totalCount = 1;
		for (int diam : this.diameters)
		{
			totalCount *= diam;
		}
		
		// the position of the median value within the sorted array
		int medianCount = (totalCount - 1) / 2;
		double[] values = new double[totalCount];
		
		for(int z = 0; z < sizeZ; z++)
		{
			this.fireProgressChanged(this, z, sizeZ);
			
			for(int y = 0; y < sizeY; y++)
			{
				for(int x = 0; x < sizeX; x++)
				{
					// iterate over neighbors of current pixel
					int count = 0;
					for (int z2 = z - rz1; z2 < z + rz2; z2++)
					{
						int z2r = Math.min(Math.max(z2, 0), sizeZ - 1);
						for (int y2 = y - ry1; y2 < y + ry2; y2++)
						{
							int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
							for (int x2 = x - rx1; x2 < x + rx2; x2++)
							{
								int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
								values[count++] = source.getValue(x2r, y2r, z2r); 
							}
						}
					}
					
					// sort neighborhood values and keep the median value
					Arrays.parallelSort(values);
					target.setValue(x, y, z, values[medianCount]);
				}
			}
		}
	}

    @Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
        ScalarArray<?> result = array.newInstance(array.getSize());
        processScalar(array, result);
        return result;
    }
}
