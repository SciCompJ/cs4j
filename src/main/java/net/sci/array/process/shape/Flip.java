/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.Array2D;
import net.sci.array.data.Array3D;

/**
 * @author dlegland
 *
 */
public class Flip implements ArrayOperator
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

	@Override
	public <T> Array<?> process(Array<T> array)
	{
		Array<T> output = array.newInstance(array.getSize());
		process(array, output);
		return output;
	}

	/**
     * Flips the content of the input 2D array, and stores the result in the
     * output array.
     * 
     * @param input
     *            the input array
     * @param output
     *            the output array
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
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
     * @param <T1>
     *            the type of the input array
     * @param <T2>
     *            the type of the output array
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

    public <T1 extends T2, T2> void process(Array<T1> input, Array<T2> output)
    {
        int nd = input.dimensionality();
        int sizeDim = input.getSize(this.dim);
        int[] pos2 = new int[nd];

        // iterate over positions of input array
        for (int[] pos : input.positions()) 
        {
            System.arraycopy(pos, 0, pos2, 0, nd);
            pos2[dim] = sizeDim - 1 - pos[dim];
            output.set(pos2, input.get(pos));
        }
    }
    
//    public <T1 extends T2, T2> void process(Array<T1> input, Array<T2> output)
//    {
//        int nd = input.dimensionality();
//        int sizeDim = input.getSize(this.dim);
//        int[] pos2 = new int[nd];
//
//        // iterate over positions of input array
//        CursorIterator<? extends Cursor> cursIter = input.cursorIterator();
//        while (cursIter.hasNext())
//        {
//            cursIter.forward();
//            int[] pos = cursIter.getPosition();
//            
//            System.arraycopy(pos, 0, pos2, 0, nd);
//            pos[dim] = sizeDim - 1 - pos[dim];
//            output.set(pos2, input.get(pos));
//        }
//    }

//	public int[] flipPosition(int[] pos, int[] arrayDims)
//	{
//	    int nd = pos.length;
//	    int[] pos2 = new int[nd];
//	    System.arraycopy(pos, 0, pos2, 0, nd);
//	    pos2[this.dim] = arrayDims[this.dim] - 1 - pos[this.dim];
//	    return pos2;
//	}

}
