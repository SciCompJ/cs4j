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
	int[] boxSizes;
	
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
		this.boxSizes = new int[diameters.length];
		System.arraycopy(diameters, 0, this.boxSizes, 0, diameters.length);
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
		if (this.boxSizes.length < source.dimensionality())
		{
			throw new RuntimeException("Requires at least as many diameters as array dimensionality");
		}
		
		// compute the normalization constant
		int boxSize = 1;
		for (int diam : this.boxSizes)
		{
			boxSize *= diam;
		}
		
		// iterate over positions of target array
		for (int[] pos : target.positions())
		{
			// init result
			double sum = 0;
			
			// iterate over neighbors
			Neighborhood nbg = new BoxNeighborhood(pos, boxSizes);
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
		if (this.boxSizes.length < 2)
		{
			throw new RuntimeException("Can not process 2D array with less than two diameters.");
		}

		// compute the padding before and after center element in each direction.
		// For each direction, we have:
		//     boxSize = pad0 + pad1 + 1
		int boxSizeX = this.boxSizes[0];
		int padX0 = (boxSizeX - 1) / 2;
		int padX1 = boxSizeX - 1 - padX0;
        int boxSizeY = this.boxSizes[1];
        int padY0 = (boxSizeY - 1) / 2;
        int padY1 = boxSizeY - 1 - padY0;
		
        this.fireStatusChanged(this, "1D box filter in X-direction");
        
        // Compute 1D filter in the X-direction
        double[] values = new double[sizeX];
        double[] padded = new double[sizeX + boxSizeX];
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            // grab values from array
            for (int x = 0; x < sizeX; x++)
            {
                values[x] = source.getValue(x, y);
            }
            
            // pad and filter (re-use input array for filter result)
            filterWithPadding(values, padX0, padX1, padded);
            
            // put result into array
            for (int x = 0; x < sizeX; x++)
            {
                target.setValue(x, y, values[x]);
            }
        }
        
        // use same array as source and target
        // as process is made row by row, this is not a problem (except maybe for numerical accuracy)
        source = target;
        
        this.fireStatusChanged(this, "1D box filter in Y-direction");
        
        // Compute 1D filter in the Y-direction
        values = new double[sizeY];
        padded = new double[sizeY + boxSizeY];
        for (int x = 0; x < sizeX; x++)
        {
            this.fireProgressChanged(this, x, sizeX);
            // grab values from array
            for (int y = 0; y < sizeY; y++)
            {
                values[y] = source.getValue(x, y);
            }
            
            // pad and filter (re-use input array for filter result)
            filterWithPadding(values, padY0, padY1, padded);
            
            // put result into array
            for (int y = 0; y < sizeY; y++)
            {
                target.setValue(x, y, values[y]);
            }
        }
	}

    /**
     * Process the specific case of 2D arrays, using slower but more accurate
     * polynomial computation algorithm.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
     */
    public void processScalar2d_exact(ScalarArray2D<?> source, ScalarArray2D<?> target)
    {
        // get size of input array
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        
        // check dimensions
        if (this.boxSizes.length < 2)
        {
            throw new RuntimeException("Can not process 2D array with less than two diameters.");
        }

        // compute the radius extent in each direction
        double diamX = (double) this.boxSizes[0];
        int rx1 = (int) Math.floor(diamX / 2.0);
        int rx2 = (int) Math.ceil(diamX / 2.0);
        double diamY = (double) this.boxSizes[1];
        int ry1 = (int) Math.floor(diamY / 2.0);
        int ry2 = (int) Math.ceil(diamY / 2.0);
        
        // compute the normalization constant
        int boxSize = 1;
        for (int d : this.boxSizes)
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
        if (this.boxSizes.length < 3)
        {
            throw new RuntimeException("Can not process 3D array with less than three diameters.");
        }

        // compute the padding before and after center element in each direction.
        // For each direction, we have:
        //     boxSize = pad0 + pad1 + 1
        int boxSizeX = this.boxSizes[0];
        int padX0 = (boxSizeX - 1) / 2;
        int padX1 = boxSizeX - 1 - padX0;
        int boxSizeY = this.boxSizes[1];
        int padY0 = (boxSizeY - 1) / 2;
        int padY1 = boxSizeY - 1 - padY0;
        int boxSizeZ = this.boxSizes[2];
        int padZ0 = (boxSizeZ - 1) / 2;
        int padZ1 = boxSizeZ - 1 - padZ0;
        
        this.fireStatusChanged(this, "1D box filter in X-direction");
        
        // Compute 1D filter in the X-direction
        double[] values = new double[sizeX];
        double[] padded = new double[sizeX + boxSizeX];
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int y = 0; y < sizeY; y++)
            {
                // grab values from array
                for (int x = 0; x < sizeX; x++)
                {
                    values[x] = source.getValue(x, y, z);
                }

                // pad and filter (re-use input array for filter result)
                filterWithPadding(values, padX0, padX1, padded);

                // put result into array
                for (int x = 0; x < sizeX; x++)
                {
                    target.setValue(x, y, z, values[x]);
                }
            }
        }
        
        // use same array as source and target
        // as process is made row by row, this is not a problem (except maybe for numerical accuracy)
        source = target;
        

        // Compute 1D filter in the Y-direction
        
        this.fireStatusChanged(this, "1D box filter in Y-direction");

        values = new double[sizeY];
        padded = new double[sizeY + boxSizeY];
        for (int z = 0; z < sizeZ; z++)
        {
            this.fireProgressChanged(this, z, sizeZ);
            for (int x = 0; x < sizeX; x++)
            {
                // grab values from array
                for (int y = 0; y < sizeY; y++)
                {
                    values[y] = source.getValue(x, y, z);
                }

                // pad and filter (re-use input array for filter result)
                filterWithPadding(values, padY0, padY1, padded);

                // put result into array
                for (int y = 0; y < sizeY; y++)
                {
                    target.setValue(x, y, z, values[y]);
                }
            }
        }
        
        
        // Compute 1D filter in the Z-direction
        
        this.fireStatusChanged(this, "1D box filter in Z-direction");
        
        values = new double[sizeZ];
        padded = new double[sizeZ + boxSizeZ];
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            for (int x = 0; x < sizeX; x++)
            {
                // grab values from array
                for (int z = 0; z < sizeZ; z++)
                {
                    values[z] = source.getValue(x, y, z);
                }

                // pad and filter (re-use input array for filter result)
                filterWithPadding(values, padZ0, padZ1, padded);

                // put result into array
                for (int z = 0; z < sizeZ; z++)
                {
                    target.setValue(x, y, z, values[z]);
                }
            }
        }
    }
    
    /**
     * Process the specific case of 3D arrays, using slower but more accurate
     * polynomial computation algorithm.
     * 
     * @param source
     *            the source array
     * @param target
     *            the target array
     */
    public void processScalar3d_exact(ScalarArray3D<?> source, ScalarArray3D<?> target)
    {
        // get size of input array
        int sizeX = source.size(0);
        int sizeY = source.size(1);
        int sizeZ = source.size(2);
        
        // check dimensions
        if (this.boxSizes.length < 3)
        {
            throw new RuntimeException("Can not process 3D array with less than three diameters.");
        }

        // compute the radius extent in each direction
        double diamX = (double) this.boxSizes[0];
        int rx1 = (int) Math.floor(diamX / 2.0);
        int rx2 = (int) Math.ceil(diamX / 2.0);
        double diamY = (double) this.boxSizes[1];
        int ry1 = (int) Math.floor(diamY / 2.0);
        int ry2 = (int) Math.ceil(diamY / 2.0);
        double diamZ = (double) this.boxSizes[2];
        int rz1 = (int) Math.floor(diamZ / 2.0);
        int rz2 = (int) Math.ceil(diamZ / 2.0);
        
        // compute the normalization constant
        int boxSize = 1;
        for (int d : this.boxSizes)
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
    
    private void filterWithPadding(double[] values, int m1, int m2, double[] buffer)
    {
        pad_repeat(values, buffer, m1 + 1, m2);
        filter1d(buffer, values, m1 + m2 + 1);
    }
    
	/**
     * Pads the input array by repeating the first value M1 times at the
     * beginning, and repeating the last value M2 times at the end. The length
     * of the output array ios the length of the input array plus (M1+M2).
     * 
     * @param array
     *            the array to pad
     * @param res
     *            the array to put result values
     * @param m1
     *            the number of repetitions to add in the beginning
     * @param m2
     *            the number of repetitions to add at the end
     * @return the padded array
     */
    private double[] pad_repeat(double[] array, double[] res, int m1, int m2)
	{
	    int n = array.length;
	    
	    for (int i = 0; i < m1; i++)
	    {
	        res[i] = array[0];
	    }
        for (int i = 0; i < array.length; i++)
        {
            res[i + m1] = array[i];
        }
        for (int i = 0; i < m2; i++)
        {
            res[i + m1 + n] = array[n-1];
        }
        return res;
	}
	
	/**
     * Computes 1D box filter on padded source array, and writing results into
     * the target array. The length of the source array must be the length of
     * the target array plus (M+1), where M is the length of the box. The
     * padding values M1 and M2 must satisfy M1+M2+1 = M.
     * 
     * The source array must have the following shape:
     * 
     * S0  ... S0 S0 S1 S2 S3 S4 .... SN-1 SN SN SN SN
     * <- M1+1 -> <-   original array      -> <- M2 ->
     * 
     * @param source
     *            the array containing the values to smooth, with size N+M
     * @param target
     *            the array containing result values, with size N
     * @param m
     *            the size M of the box filter
     * @param m1
     *            the padding to add before center box element
     * @param m2
     *            the padding to add after center box element
     */
	private void filter1d(double[] source, double[] target, int m)
	{
	    int n = target.length;
	    
	    // The sum of values within the moving frame, updated at each iteration.
	    double sum = 0;
        // Initialize the sum to the sum of box values at output position (-1). 
	    // Obtained as the sum of the (M2) first input values, 
	    // plus (M1+1) times the first value (for padding)
	    // Using padded array, this corresponds to the sum of the M first values...
	    for (int i = 0; i < m; i++)
	    {
	        sum += source[i];
	    }
	    
	    // iterate over target values
	    for (int i = 0; i < n; i++)
	    {
	        sum = sum - source[i] + source[i + m];
	        target[i] = sum / m;
	    }
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
