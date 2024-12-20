/**
 * 
 */
package net.sci.image.filtering;

import net.sci.algo.AlgoStub;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.process.ScalarArrayOperator;
import net.sci.array.numeric.process.VectorArrayMarginalOperator;
import net.sci.image.ImageArrayOperator;

/**
 * Computes the variance in a box neighborhood around each array element.
 * 
 * @author dlegland
 *
 * @see MedianFilterBox
 * @see VarianceFilterBox
 */
public final class VarianceFilterBox extends AlgoStub implements ImageArrayOperator, ScalarArrayOperator, VectorArrayMarginalOperator
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
	public VarianceFilterBox(int[] diameters)
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
		int[] sizes = source.size();
		
		// get first two radiuses
		if (this.diameters.length < source.dimensionality())
		{
			throw new RuntimeException("Requires at least as many diameters as array dimensionality");
		}
		
		// compute the normalization constant
		int totalCount = 1;
		for (int diam : this.diameters)
		{
			totalCount *= diam;
		}
		
		Neighborhood nbg = new BoxNeighborhood(diameters);
        
		double[] values = new double[totalCount];
		
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
				values[count++] = source.getValue(neighPos); 
			}
			
			// compute variance value
			target.setValue(pos, variance(values));
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
		
		// get first two radiuses
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
		int boxSize = 1;
		for (int d : this.diameters)
		{
			boxSize *= d;
		}
		
		double[] values = new double[boxSize];
		
		for(int y = 0; y < sizeY; y++)
		{
			this.fireProgressChanged(this, y, sizeY);
			
			for(int x = 0; x < sizeX; x++)
			{
				// iterate over neighbors of current pixel
				int count = 0;
				// iterate over neighbors
				for (int y2 = y - ry1; y2 < y + ry2; y2++)
				{
					int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
					for (int x2 = x - rx1; x2 < x + rx2; x2++)
					{
						int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
						values[count++] = source.getValue(x2r, y2r); 
					}
				}
				
				// compute variance value
				target.setValue(x, y, variance(values));
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
		int sizeX = source.size(0);
		int sizeY = source.size(1);
		int sizeZ = source.size(2);
		
		// get first three radiuses
		if (this.diameters.length < 3)
		{
			throw new RuntimeException("Can not process 3D array with less than three radiuses.");
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
		for (int r : this.diameters)
		{
			totalCount *= (2 * r + 1);
		}
		
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
					
					// compute variance value
					target.setValue(x, y, z, variance(values));
				}
			}
		}
	}

	/**
	 * Computes the variance of the values within the input array.
	 * 
	 * @param values an array of values
	 * @return the variance of the values in the array
	 */
	private double variance(double[] values)
	{
		int n = values.length;
		
		// compute mean
		double mean = 0;
		for (double v : values)
		{
			mean += v;
		}
		mean /= n;
		
		// compute variance
		double var = 0;
		for (double v : values)
		{
			v = v - mean;
			var += v * v;
		}
		var /= n;
		
		return var;
	}
	
    @Override
    public ScalarArray<?> processScalar(ScalarArray<?> array)
    {
        ScalarArray<?> result = array.newInstance(array.size());
        processScalar(array, result);
        return result;
    }
}
