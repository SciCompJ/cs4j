/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;
import net.sci.array.Cursor;
import net.sci.array.data.Array2D;
import net.sci.array.data.Array3D;
import net.sci.array.data.ScalarArray;

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
	    if (input instanceof ScalarArray && output instanceof ScalarArray)
	    {
	        // Try to work on scalar data when possible
	        processScalarNd((ScalarArray<?>) input, (ScalarArray<?>) output);
	    }
	    else
	    {
	        
	    }
        // TODO: how to check array have same type?
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
	@Override
	public <T> Array<T> process(Array<T> array)
	{
		Array<T> result = array.newInstance(array.getSize());
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
	public <T1 extends T2, T2> void process2d(Array2D<T1> input, Array2D<T2> output)
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
					output.set(x, y, input.get(sizeX-1-x, y));
					break;
				case 1: 
					output.set(x, y, input.get(x, sizeY-1-y));
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
	public <T1 extends T2, T2> void process3d(Array3D<T1> input, Array3D<T2> output)
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
						output.set(x, y, z, input.get(sizeX-1-x, y, z));
						break;
					case 1: 
						output.set(x, y, z, input.get(x, sizeY-1-y, z));
						break;
					case 2: 
						output.set(x, y, z, input.get(x, y, sizeZ-1-z));
						break;
					}
				}
			}
		}
	}

	
	// Below are some implementations based on the use of a cursor
	public <T1 extends T2, T2> void processNd(Array<T1> input, Array<T2> output)
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
	

	public void processScalarNd(ScalarArray<?> input, ScalarArray<?> output)
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
	
	public int[] flipPosition(int[] pos, int[] arrayDims)
	{
	    int nd = pos.length;
	    int[] pos2 = new int[nd];
	    System.arraycopy(pos, 0, pos2, 0, nd);
	    pos2[this.dim] = arrayDims[this.dim] - 1 - pos[this.dim];
	    return pos2;
	}

}
