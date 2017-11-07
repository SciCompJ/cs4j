/**
 * 
 */
package net.sci.array.process.shape;

import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.data.Array2D;

/**
 * Rotate an array by 90 degrees counter-clockwise.
 * 
 * @author dlegland
 *
 */
public class Rotate90 implements ArrayOperator
{
	int number = 1;
	
	/**
	* Rotate array by a single 90 degrees rotation.
	*/
	public Rotate90()
	{
	}

	/**
	 * Rotate array by several 90 degrees rotations. Using negative number
	 * rotates in clockwise order.
	 */
	public Rotate90(int number)
	{
		this.number = number;
	}

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array)
	 */
	@Override
	public <T> Array<?> process(Array<T> array)
	{
		if (array.dimensionality() == 2)
		{
			return process2d(Array2D.wrap(array));
		}
		
		throw new IllegalArgumentException("Requires a 2D array");
	}

	/**
	 * Rotates the planar array by 90 degrees in clock-wise order.
	 * 
	 * @param array
	 *            the array to rotate
	 * @return the rotated array
	 */
	public <T> Array2D<?> process2d(Array2D<T> array)
	{
		int size0 = array.getSize(0);
		int size1 = array.getSize(1);
		
		// ensure rotation number is between 0 and 3
		int number2 = number % 4;
		while (number2 < 0)
			number2 += 4;
		
		switch (number2)
		{
		case 0:
		{
			// no rotation -> simply duplicate the array
			return array.duplicate();
		}
		case 1:
		{
			// one rotation -> flip coords and reverse one
			Array2D<T> output = (Array2D<T>) array.newInstance(new int[]{size1, size0});
			for(int y = 0; y < size1; y++)
			{
				int x2 = size1 - 1 - y;
				for(int x = 0; x < size0; x++)
				{
					int y2 = x;
					output.set(x2, y2, array.get(x, y));
				}
			}
			return output;
		}
		case 2:
		{
			// two rotations -> reverse each coordinate
			Array2D<T> output = array.duplicate();
			for(int y = 0; y < size1; y++)
			{
				int y2 = size1 - 1 - y;
				for(int x = 0; x < size0; x++)
				{
					int x2 = size0 - 1 - x;
					output.set(x2, y2, array.get(x, y));
				}
			}
			return output;
		}
		case 3:
		{
			// three rotations -> flip coords and reverse one
			Array2D<T> output = (Array2D<T>) array.newInstance(new int[]{size1, size0});
			for(int y = 0; y < size1; y++)
			{
				int x2 = y;
				for(int x = 0; x < size0; x++)
				{
					int y2 = size0 - 1 - x;
					output.set(x2, y2, array.get(x, y));
				}
			}
			return output;
		}
		default:
			throw new RuntimeException("Problem in choosing rotation number");
		}		
	}
	
	@Override
	public boolean canProcess(Array<?> array)
	{
		return array.dimensionality() == 2;
	}
}
