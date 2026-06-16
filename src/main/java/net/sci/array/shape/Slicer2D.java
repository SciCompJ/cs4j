/**
 * 
 */
package net.sci.array.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;

/**
 * Extracts a two-dimensional slice from a n-dimensional array by specifying the
 * indices of the dimensions to keep, and the position of an element belonging
 * to the result array. 
 * 
 * This operator is useful for extracting 2D slices from 3D images.
 * 
 * 
 * @see Slicer
 * 
 * @author dlegland
 */
public class Slicer2D extends AlgoStub implements ArrayOperator
{
 	// =============================================================
    // Class members

    /**
     * The dimensions used for slicing, as two dimensions indices.
     */
    int dim0;
    int dim1;
	
	
	/**
     * The position of an element in input array that will be kept in output
     * array.
     */
	int[] refPos;
	
	
    // =============================================================
    // Constructors

	/**
     * Extracts a two-dimensional slice from a n-dimensional array, by specifying
     * the indices of the dimensions to keep, and the position of an element
     * belonging to the result array.
     * 
     * @param dim0
     *            the first dimension of slicing, 0-indexed.
     * @param dim1
     *            the second dimension of slicing, 0-indexed.
     * @param pos
     *            the position (within the original array) of an element
     *            belonging to the sliced array.
     */
	public Slicer2D(int dim0, int dim1, int[] pos)
	{
        this.dim0 = dim0;
        this.dim1 = dim1;
		this.refPos = pos;
	}


	// =============================================================
    // Methods

	@Override
	public <T> Array2D<T> process(Array<T> source)
	{
		int[] newDims = computeOutputArraySize(source);
		Array<T> target = source.newInstance(newDims);
		process(source, target);
		return Array2D.wrap(target);
	}
	
	@SuppressWarnings("unchecked")
    public <T> void process(Array<?> source, Array<?> target)
	{
	    if (source instanceof ScalarArray && target instanceof ScalarArray)
	    {
	        processScalarNd((ScalarArray<?>) source, ScalarArray2D.wrapScalar2d((ScalarArray<?>) target));
	    }
	    else
	    {
	        processNd((Array<? extends T>) source, Array2D.wrap((Array<? super T>) target));
	    }
	}
	
	/**
	 * Creates a new array that can be used as output for processing the given
	 * input array.
	 * 
	 * @param array
	 *            the reference array
	 * @return a new instance of Array that can be used for processing input
	 *         array.
	 */
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] newDims = computeOutputArraySize(array);
		return array.newInstance(newDims);
	}

	private int[] computeOutputArraySize(Array<?> inputArray)
	{
		// number of dimensions of new array
		int nd = inputArray.dimensionality();
		if (nd < 2)
		{
			throw new IllegalArgumentException(String.format(
                    "Slicer can not process array of size %d", nd));
		}
		
		int[] dims = inputArray.size();
        return new int[] { dims[this.dim0], dims[this.dim1] };
	}
	
	public void processScalarNd(ScalarArray<?> source, ScalarArray2D<?> target)
	{
        checkTargetSize(source, target);

        // array dimensions
        int nd = source.dimensionality();

        // create position pointer for source image
        int[] srcPos = new int[nd];
        System.arraycopy(this.refPos, 0, srcPos, 0, nd);

        int sizeX = target.size(0);
        int sizeY = target.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            srcPos[this.dim1] = y;
            
            for (int x = 0; x < sizeX; x++)
            {
                // update coords of pointer in source array
                srcPos[this.dim0] = x;
                // copy value of selected position
                target.setValue(x, y, source.getValue(srcPos));
            }
        }
        this.fireProgressChanged(this, 1, 1);
	}
	
	public <T> void processNd(Array<T> source, Array2D<? super T> target)
	{
	    checkTargetSize(source, target);
	    
        // array dimensions
        int nd = source.dimensionality();
        
        // create position pointer for source image
        int[] srcPos = new int[nd];
        System.arraycopy(this.refPos, 0, srcPos, 0, nd);

        int sizeX = target.size(0);
        int sizeY = target.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            this.fireProgressChanged(this, y, sizeY);
            srcPos[this.dim1] = y;
            
            for (int x = 0; x < sizeX; x++)
            {
                // update coords of pointer in source array
                srcPos[this.dim0] = x;
                // copy value of selected position
                target.set(x, y, source.get(srcPos));
            }
        }
        this.fireProgressChanged(this, 1, 1);
	}

	private void checkTargetSize(Array<?> source, Array<?> target)
	{
	    int[] expDims = computeOutputArraySize(source);
	    if (target.dimensionality() != expDims.length)
	    {
	        throw new IllegalArgumentException("Requires output array with " + expDims.length + " dimensions");
	    }
	    for (int d = 0; d < expDims.length; d++)
	    {
	        if (target.size(d) != expDims[d])
	        {
	            throw new IllegalArgumentException("Wrong dimensions for output array");
	        }
	    }
	}
}
