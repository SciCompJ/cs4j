/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;

/**
 * @author dlegland
 *
 */
public class OrthogonalProjection implements ArrayOperator
{
	int dim;
	
	// uses max projection by default
	
	/**
	 * 
	 */
	public OrthogonalProjection(int dim)
	{
		this.dim = dim;
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

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		// TODO: requires scalar arrays
		
		// create position pointer for source image
		int nd = target.dimensionality();
		int[] srcPos = new int[nd + 1];
		
		int indMax = source.getSize(this.dim);
		
		// iterate over position in target image
		Array.Cursor cursor = target.getCursor();
		while (cursor.hasNext())
		{
			// get position in target image
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			// convert to position in source image
			System.arraycopy(pos, 0, srcPos, 0, dim);
			System.arraycopy(pos, dim, srcPos, dim + 1, nd - dim);
			
			double maxValue = Double.NEGATIVE_INFINITY;
			
			// iterate over current line
			for (int i = 0; i < indMax; i++)
			{
				srcPos[this.dim] = i;
				maxValue = Math.max(maxValue, source.getValue(srcPos));
			}
			
			// copy value of selected position
			target.setValue(pos, maxValue);
		}
	}

}
