/**
 * 
 */
package net.sci.image.process.filter;

import net.sci.algo.AlgoStub;
import net.sci.array.process.VectorArrayMarginalOperator;
import net.sci.array.scalar.Scalar;
import net.sci.array.scalar.ScalarArray;
import net.sci.array.scalar.ScalarArray2D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.image.ImageArrayOperator;

/**
 * Box filter for multidimensional arrays. Considers a rectangular box, with
 * size defined by side lengths.
 * 
 * @author dlegland
 *
 * @see BoxMedianFilter
 * @see BoxVarianceFilter
*/
public final class BoxFilter extends AlgoStub implements ImageArrayOperator, VectorArrayMarginalOperator
{
    /** The size of the box in each dimension */
	int[] diameters;
	
    /**
     * The factory used to create output array. If set to null (the default), use the factory
     * of the input array.
     */
    ScalarArray.Factory<? extends Scalar> factory = null;
    
	/**
	 * Creates a new instance of box filter by specifying the list of diameters in
	 * each dimension.
	 * 
	 * @param diameters
	 *            the box diameter in each dimension
	 */
	public BoxFilter(int[] diameters)
	{
		this.diameters = new int[diameters.length];
		System.arraycopy(diameters, 0, this.diameters, 0, diameters.length);
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	public void processScalar(ScalarArray<? extends Scalar> source, ScalarArray<? extends Scalar> target)
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
			processScalarNd((ScalarArray<?>) source, (ScalarArray<?>) target);
		}
		else
		{
			throw new IllegalArgumentException("Can not process array of class " + source.getClass());
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
	public void processScalarNd(ScalarArray<? extends Scalar> source, ScalarArray<? extends Scalar> target)
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
		int boxSize = 1;
		for (int diam : this.diameters)
		{
			boxSize *= diam;
		}
		
		// iterate over positions of target array
		for (int[] pos : target.positions())
		{
			// init result
			double sum = 0;
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhood(pos, diameters);
			for (int[] neighPos : nbg)
			{
				// clamp neighbor position to array bounds
				for (int d = 0; d < nd; d++)
				{
				    neighPos[d] = clamp(neighPos[d], 0, sizes[d] - 1);
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
		int boxSize = 1;
		for (int d : this.diameters)
		{
			boxSize *= d;
		}

		for(int y = 0; y < sizeY; y++)
		{
		    this.fireProgressChanged(this, y, sizeY);
			for(int x = 0; x < sizeX; x++)
			{
				double sum = 0;
				
				// iterate over neighbors
				for (int y2 = y - ry1; y2 < y + ry2; y2++)
				{
				    int y2r = clamp(y2, 0, sizeY - 1);
					for (int x2 = x - rx1; x2 < x + rx2; x2++)
					{
						int x2r = clamp(x2, 0, sizeX - 1);
						sum += source.getValue(x2r, y2r);
					}
				}
				target.setValue(x, y, sum / boxSize);
			}
		}
		
        this.fireProgressChanged(this, 0, sizeY);
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
		int boxSize = 1;
		for (int d : this.diameters)
		{
			boxSize *= d;
		}
		
		for(int z = 0; z < sizeZ; z++)
		{
		    this.fireProgressChanged(this, z, sizeZ);
		    
		    for(int y = 0; y < sizeY; y++)
			{
				for(int x = 0; x < sizeX; x++)
				{
					double sum = 0;

					// iterate over neighbors
					for (int z2 = z - rz1; z2 < z + rz2; z2++)
					{
	                    int z2r = clamp(z2, 0, sizeZ - 1);
						for (int y2 = y - ry1; y2 < y + ry2; y2++)
						{
		                    int y2r = clamp(y2, 0, sizeY - 1);
							for (int x2 = x - rx1; x2 < x + rx2; x2++)
							{
			                    int x2r = clamp(x2, 0, sizeX - 1);
			                    sum += source.getValue(x2r, y2r, z2r);
							}
						}
					}
					target.setValue(x, y, z, sum / boxSize);
				}
			}
		}

		this.fireProgressChanged(this, 0, sizeZ);
	}
	
	private static final int clamp(int value, int min, int max)
	{
        if (value < min) return min;
        if (value > max) return max;
        return value;
	}
	
	@Override
    public ScalarArray<?> processScalar(ScalarArray<? extends Scalar> array)
    {
	    // choose the ScalarArray factory for creating result
	    ScalarArray.Factory<? extends Scalar> factory = this.factory;
	    if (factory == null)
	    {
	        factory = array.factory();
	    }
	    
	    // create the output array
        ScalarArray<?> output = factory.create(array.size());
        
        // call the processing method
        processScalar(array, output);
        return output;
    }

    /**
     * Sets up the factory used to create output arrays.
     * 
     * @param factory the factory to set
     */
    public void setFactory(ScalarArray.Factory<? extends Scalar> factory)
    {
        this.factory = factory;
    }
}
