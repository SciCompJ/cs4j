/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.scalar.ScalarArray;

/**
 * Extracts a k-dimensional slice from a n-dimensional array by specifying the
 * indices of the dimensions to keep, and the position of an element belonging
 * to the result array. The dimensionality of the output array corresponds to
 * the number of dimensions provided for slicing.
 * 
 * @see SimpleSlicer
 * 
 * @author dlegland
 */
public class Slicer extends AlgoStub implements ArrayOperator
{
 	// =============================================================
    // Class members

    /**
     * The dimensions used for slicing, as an array of dimensions indices.
     */
	int[] dims;
	
	/**
     * The position of an element in input array that will be kept in output
     * array.
     */
	int[] refPos;
	
	
    // =============================================================
    // Constructors

	/**
     * Extracts a k-dimensional slice from a n-dimensional array, by specifying
     * the indices of the dimensions to keep, and the position of an element
     * belonging to the result array.
     * 
     * @param dim
     *            the dimensions of slicing, 0-indexed. Ex.:
     *            <code>new int[]{0, 1}</code>.
     * @param pos
     *            the position (within the original array) of an element
     *            belonging to the sliced array.
     */
	public Slicer(int[] dims, int[] pos)
	{
		this.dims = dims;
		this.refPos = pos;
	}


	// =============================================================
    // Methods

	@Override
	public <T> Array<T> process(Array<T> source)
	{
		int[] newDims = computeOutputArraySize(source);
		Array<T> target = source.newInstance(newDims);
		process(source, target);
		return target;
	}
	
	@SuppressWarnings("unchecked")
    public <T> void process(Array<?> source, Array<?> target)
	{
	    if (source instanceof ScalarArray && target instanceof ScalarArray)
	    {
	        processScalarNd((ScalarArray<?>) source, (ScalarArray<?>) target);
	    }
	    else
	    {
	        processNd((Array<? extends T>) source, (Array<? super T>) target);
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
		int nd2 = this.dims.length;
		
		if (nd2 > nd)
		{
			throw new IllegalArgumentException(String.format(
                    "Slicer can not process array of size %d", nd));
		}
		
		int[] size2 = new int[nd2];
		for (int d = 0; d  < nd2; d++)
		{
			size2[d] = inputArray.size(this.dims[d]);
		}

		return size2;
	}
	
	public void processScalarNd(ScalarArray<?> source, ScalarArray<?> target)
	{
        checkTargetSize(source, target);

        // array dimensions
        int nd = source.dimensionality();
        int nd2 = target.dimensionality();

        // create position pointer for source image
        int[] srcPos = new int[nd];
        System.arraycopy(this.refPos, 0, srcPos, 0, nd);

        for (int[] pos : target.positions()) 
        {
            // update coords of pointer in source array
            for (int d = 0; d < nd2; d++)
            {
                srcPos[this.dims[d]] = pos[d];
            }
			
			// copy value of selected position
			target.setValue(pos, source.getValue(srcPos));
		}
	}
	
	public <T> void processNd(Array<T> source, Array<? super T> target)
	{
	    checkTargetSize(source, target);
	    
        // array dimensions
        int nd = source.dimensionality();
        int nd2 = target.dimensionality();

        // create position pointer for source image
        int[] srcPos = new int[nd];
        System.arraycopy(this.refPos, 0, srcPos, 0, nd);

        for (int[] pos : target.positions()) 
        {
            // update coords of pointer in source array
            for (int d = 0; d < nd2; d++)
            {
                srcPos[this.dims[d]] = pos[d];
            }
			
			// copy value of selected position
			target.set(pos, source.get(srcPos));
		}
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
