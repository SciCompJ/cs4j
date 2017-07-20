/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;
import net.sci.array.Cursor;

/**
 * Extract a slice from an array, by specifying dimension and slice index along
 * this dimension.
 * 
 * @author dlegland
 *
 */
public class Slicer implements ArrayToArrayOperator
{
	public static final <T> void getSlice(Array<? extends T> source, Array<? super T> target, int dim, int sliceIndex)
	{
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[dim] = sliceIndex;

		// iterate over position in target image
		Cursor cursor = target.getCursor();
		while (cursor.hasNext())
		{
			// get position in target image
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			// copy value of selected position
			target.set(pos, source.get(srcPos));
		}
	}
	
	public static final <T> Array<T> slice2d(Array<T> array,
			int dim1, int dim2, int[] refPos)
	{
		// check dimensionality
		int nd= array.dimensionality();
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
		int sizeX = array.getSize(dim1);
		int sizeY = array.getSize(dim2);
		Array<T> result = array.newInstance(new int[]{sizeX, sizeY});
		
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
	
	int dim;
	
	int index;
	
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
	public Slicer(int dim, int index)
	{
		this.dim = dim;
		this.index = index;
	}

	@Override
	public <T> Array<?> process(Array<T> source)
	{
		int[] newDims = computeOutputArraySize(source);
		Array<T> target = source.newInstance(newDims);
		processNd(source, target);
		return target;
	}
	
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		// TODO: check dims
		processDoubleNd(source, target);
	}
	
	public <T> void processTyped(Array<? extends T> source, Array<? super T> target)
	{
		// TODO: check dims
		processNd(source, target);
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
			dims[d] = inputArray.getSize(d);
		}
		for (int d = this.dim; d < nd; d++)
		{
			dims[d] = inputArray.getSize(d+1);
		}

		return dims;
	}
	
	public void processDoubleNd(Array<?> source, Array<?> target)
	{
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[this.dim] = this.index;

		// iterate over position in target image
		Cursor cursor = target.getCursor();
		while (cursor.hasNext())
		{
			// get position in target image
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			// copy value of selected position
			target.setValue(pos, source.getValue(srcPos));
		}
	}
	
	public <T> void processNd(Array<T> source, Array<? super T> target)
	{
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[this.dim] = this.index;

		// iterate over position in target image
		Cursor cursor = target.getCursor();
		while (cursor.hasNext())
		{
			// get position in target image
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			// copy value of selected position
			target.set(pos, source.get(srcPos));
		}
	}

}
