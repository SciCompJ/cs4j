/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * Extract a slice from an array, by specifying dimension and slice index along
 * this dimension.
 * 
 * @author dlegland
 *
 */
public class Slicer implements ArrayOperator
{

	int dim;
	
	int index;
	
	/**
	 * 
	 */
	public Slicer(int dim, int index)
	{
		this.dim = dim;
		this.index = index;
	}

	@Override
	public void process(Array<?> source, Array<?> target)
	{
		// TODO: check dims
		
		processDoubleNd(source, target);
	}
	
	/**
	 * Creates a new array that can be used as output for processing the given
	 * input array.
	 * 
	 * @param array
	 *            the reference array
	 * @return a new instance of Array<?> that can be used for processing input
	 *         array.
	 */
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		// number of dimensions of new array
		int nd = array.dimensionality() - 1;
		
		if (dim > nd)
		{
			throw new IllegalArgumentException(String.format(
					"Slicer in dim %d can not process array of size %d", dim,
					nd + 1));
		}
		int[] dims = new int[nd];
		for (int d = 0; d  < this.dim; d++)
		{
			dims[d] = array.getSize(d);
		}
		for (int d = this.dim; d < nd; d++)
		{
			dims[d] = array.getSize(d+1);
		}
		return array.newInstance(dims);
	}

	
	public void processDoubleNd(Array<?> source, Array<?> target)
	{
		Array.Cursor cursor = target.getCursor();
		
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		srcPos[this.dim] = this.index;
		
		while (cursor.hasNext())
		{
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			System.arraycopy(pos, 0, srcPos, 0, this.dim);
			System.arraycopy(pos, this.dim, srcPos, this.dim + 1, nd - this.dim);
			target.setValue(pos, source.getValue(srcPos));
		}
	}


}
