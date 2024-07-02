/**
 * 
 */
package net.sci.array.shape;

import java.util.function.Function;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.ArrayOperator;
import net.sci.array.numeric.ScalarArray;

/**
 * Extract a (nd-1)-dimensional slice from a n-dimensional array, by specifying
 * (1) the dimension to slice and (2) the slice index along this dimension.
 * 
 * This operator is useful for extracting 2D slices from 3D images.
 * 
 * @see Slicer
 * 
 * @author dlegland
 */
public class SimpleSlicer extends AlgoStub implements ArrayOperator
{
    // =============================================================
    // Static methods

    /**
     * Extract a lower dimensional array from an ND array, by specifying the
     * slicing dimension and the slice index along this dimension.
     * 
     * @param <T>
     *            the type of the array
     * @param source
     *            the source array (dimensionality N)
     * @param target
     *            the target array (dimensionality N-1)
     * @param dim
     *            the slicing dimension, between 0 and N-1
     * @param sliceIndex
     *            the position of the slice along specified dimension, between 0
     *            and <code>target.getSize(dim)-1</code>
     */
	public static final <T> void getSlice(Array<? extends T> source, Array<? super T> target, int dim, int sliceIndex)
	{
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[dim] = sliceIndex;

        // iterate over positions of input array
        for (int[] pos : source.positions()) 
        {
            // convert to position in source image
            System.arraycopy(pos, 0, srcPos, 0, dim);
            System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
            
            // copy value of selected position
            target.set(pos, source.get(srcPos));
        }
	}
	
	public static final <T> Array2D<T> slice2d(Array<T> array,
			int dim1, int dim2, int[] refPos)
	{
		// check dimensionality
		int nd = array.dimensionality();
		if (dim1 >= nd || dim2 >= nd)
		{
			throw new IllegalArgumentException("slicing dimensions must be lower than input array dimension");
		}

		// check dimensionality
		if (refPos.length < nd)
		{
			throw new IllegalArgumentException("Reference position must have as many dimension as input array");
		}

		// create position pointer for source image
		int[] srcPos = new int[nd];
		System.arraycopy(refPos, 0, srcPos, 0, nd);
		
		// create position pointer for target image
		int[] pos = new int[2];

		// create output
		int sizeX = array.size(dim1);
		int sizeY = array.size(dim2);
		Array2D<T> result = Array2D.wrap(array.newInstance(new int[]{sizeX, sizeY}));
		
		// iterate over position in target image
		for (int y = 0; y < sizeY; y++)
		{
			srcPos[dim2] = y;
			pos[1] = y;
			
			for (int x = 0; x < sizeX; x++)
			{
				srcPos[dim1] = x;
				pos[0] = x;
				
				// copy value of selected position
				result.set(pos, array.get(srcPos));
			}
		}
		
		return result;
	}
	
	
    // =============================================================
    // Class members

	/**
	 * The slicing dimension, between 0 and nd-1.
	 */
	int dim;
	
    /**
     * The slice index along the slicing dimension, between 0 and array size in
     * slicing dimension minus 1.
     */
	int index;
	
	
	// =============================================================
    // Constructors

	/**
	 * Creates a new instance of Slicer operator, that specifies the dimension
	 * of slicing and the reference slice along that dimension.
	 * 
	 * @param dim
	 *            the dimension of slicing, 0-indexed
	 * @param index
	 *            the index of the slice in the <code>dim</code> dimension,
	 *            starting from 0
	 */
	public SimpleSlicer(int dim, int index)
	{
		this.dim = dim;
		this.index = index;
	}

	
    // =============================================================
    // Methods

	@Override
	public <T> Array<T> process(Array<T> source)
	{
		int[] newDims = computeOutputArraySize(source);
		Array<T> target = source.newInstance(newDims);
		processNd(source, target);
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
	
	public <T> Array<T> createView(Array<T> array)
	{
	    int[] newDims = computeOutputArraySize(array);
	    int nd = newDims.length;
        
        // convert position in view to position in source image
	    Function<int[], int[]> mapping = (int[] pos) -> {
	        int[] srcPos = new int[nd+1];
	        System.arraycopy(pos, 0, srcPos, 0, dim);
            srcPos[dim] = index;
	        System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
	        return srcPos;
	    };
	    
	    return array.reshapeView(newDims, mapping);
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
		int nd = inputArray.dimensionality() - 1;
		
		if (dim > nd)
		{
			throw new IllegalArgumentException(String.format(
					"Slicer in dim %d can not process array of size %d", dim,
					nd + 1));
		}
		int[] dims = new int[nd];
		for (int d = 0; d  < this.dim; d++)
		{
			dims[d] = inputArray.size(d);
		}
		for (int d = this.dim; d < nd; d++)
		{
			dims[d] = inputArray.size(d+1);
		}

		return dims;
	}
	
	public void processScalarNd(ScalarArray<?> source, ScalarArray<?> target)
	{
        checkTargetSize(source, target);

        // create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[this.dim] = this.index;

        for (int[] pos : target.positions()) 
        {
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			// copy value of selected position
			target.setValue(pos, source.getValue(srcPos));
		}
	}
	
	public <T> void processNd(Array<T> source, Array<? super T> target)
	{
	    checkTargetSize(source, target);
	    
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[this.dim] = this.index;

		// iterate over positions in target image
        for (int[] pos : target.positions()) 
        {
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
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
