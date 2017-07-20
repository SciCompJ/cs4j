/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;
import net.sci.array.Cursor;
import net.sci.array.data.Array2D;
import net.sci.array.data.Array3D;

/**
 * @author dlegland
 *
 */
public class Flip implements ArrayToArrayOperator
{
	int dim;
	
	/**
	 * Creates a new instance of Flip operator, that specifies the dimension of
	 * flip.
	 * 
	 * @param dim
	 *            the dimension to flip, between 0 and the array
	 *            dimensionality minus one
	 */
	public Flip(int dim)
	{
		this.dim = dim;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> input, Array<?> output)
	{
		// TODO: how to check array have same type?
		processDoubleNd(input, output);
//		if (input instanceof Array2D && output instanceof Array2D)
//		{
//			process2d((Array2D<?>) input, (Array2D<?>) output);
//		}
//		else if (input instanceof Array3D && output instanceof Array3D)
//		{
//			process3d((Array3D<?>) input, (Array3D<?>) output);
//		}
	}

	/**
	 * Processes the given array, and returns a new Array containing the result.
	 * 
	 * @param array
	 *            the input array
	 * @return the result of operator
	 */
	public <T> Array<?> process(Array<T> array)
	{
		Array<T> result = array.duplicate();
		processSameType(array, result);
		return result;
	}
	
	/**
	 * Processes the given array, and returns a new Array containing the result.
	 * 
	 * @param array
	 *            the input array
	 * @param T
	 *            the data type of the array
	 * @return the result of operator
	 */
	public <T> Array<?> processT(Array<T> array)
	{
		Array<T> result = array.duplicate();
		processSameType(array, result);
		return result;
	}

	/**
	 * Flips the content of the input 2D array, and stores the result in the
	 * output array.
	 * 
	 * @param input
	 *            the input array
	 * @param output
	 *            the output array
	 */
	public <T1, T2 extends T1> void process2d (Array2D<T1> input, Array2D<T2> output)
	{
		// get image size
		int sizeX = input.getSize(0);
		int sizeY = input.getSize(1);
		
		// iterate over pixels
		for (int y = 0; y < sizeY; y++)
		{
			for (int x = 0; x < sizeX; x++)
			{
				switch(dim)
				{
				case 0: 
					output.setValue(x, y, input.getValue(sizeX-1-x, y));
					break;
				case 1: 
					output.setValue(x, y, input.getValue(x, sizeY-1-y));
					break;
				}
			}
		}
	}

	/**
	 * Flips the content of the input 3D array, and stores the result in the
	 * output array.
	 * 
	 * @param input
	 *            the input array
	 * @param output
	 *            the output array
	 */
	public void process3d(Array3D<?> input, Array3D<?> output)
	{
		// get image size
		int sizeX = input.getSize(0);
		int sizeY = input.getSize(1);
		int sizeZ = input.getSize(2);

		// iterate over pixels
		for (int z = 0; z < sizeZ; z++)
		{
			for (int y = 0; y < sizeY; y++)
			{
				for (int x = 0; x < sizeX; x++)
				{
					switch(dim)
					{
					case 0: 
						output.setValue(x, y, z, input.getValue(sizeX-1-x, y, z));
						break;
					case 1: 
						output.setValue(x, y, z, input.getValue(x, sizeY-1-y, z));
						break;
					case 2: 
						output.setValue(x, y, z, input.getValue(x, y, sizeZ-1-z));
						break;
					}
				}
			}
		}
	}

	
	// Below are some implementations based on the use of a cursor
	
	public void processDoubleNd(Array<?> input, Array<?> output)
	{
		Cursor cursor = input.getCursor();
		
		int nd = input.dimensionality();
		int sizeDim = input.getSize(this.dim);
		int[] pos2 = new int[nd];
		
		while (cursor.hasNext())
		{
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			System.arraycopy(pos, 0, pos2, 0, nd);
			pos[dim] = sizeDim - 1 - pos[dim];
			output.setValue(pos2, input.getValue(pos));
		}
	}

	public <T> void processSameType(Array<T> input, Array<T> output)
	{
		Cursor cursor = input.getCursor();
		
		int nd = input.dimensionality();
		int sizeDim = input.getSize(this.dim);
		int[] pos2 = new int[nd];
		
		while (cursor.hasNext())
		{
			cursor.forward();
			int[] pos = cursor.getPosition();
			
			System.arraycopy(pos, 0, pos2, 0, nd);
			pos[dim] = sizeDim - 1 - pos[dim];
			output.set(pos2, input.get(pos));
		}
	}

}
