/**
 * 
 */
package net.sci.array.process.shape;

import java.util.function.Function;

import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.ArrayOperator;

/**
 * Rotate a 2D array by 90 degrees counter-clockwise.
 * 
 * @author dlegland
 *
 */
public class Rotate90 implements ArrayOperator
{
	int number = 1;
	
	/**
	* Rotates array by a single 90 degrees rotation.
	*/
	public Rotate90()
	{
	}

	/**
	 * Rotates array by several 90 degrees rotations. Using negative number
	 * rotates in clockwise order.
	 * 
	 * @param number the number of rotations to apply
	 */
	public Rotate90(int number)
	{
		this.number = clampRotationNumber(number);
	}

	private int clampRotationNumber(int number)
    {
        // ensure rotation number is between 0 and 3 using modulo
        number %= 4;
        
        // take care of negative values
        while (number < 0)
            number += 4;
        
        return number;
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
     * @param <T>
     *            the type of the array to process
     * @param array
     *            the array to rotate
     * @return the rotated array
     */
	public <T> Array2D<?> process2d(Array2D<T> array)
	{
		int size0 = array.size(0);
		int size1 = array.size(1);
				
		switch (this.number)
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
                int x2 = y;
                for(int x = 0; x < size0; x++)
                {
                    int y2 = size0 - 1 - x;
                    output.set(array.get(x, y), x2, y2);
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
					output.set(array.get(x, y), x2, y2);
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
                int x2 = size1 - 1 - y;
                for(int x = 0; x < size0; x++)
                {
                    int y2 = x;
                    output.set(array.get(x, y), x2, y2);
                }
			}
			return output;
		}
		default:
			throw new RuntimeException("Problem in choosing rotation number");
		}		
	}
	
    public <T> Array<?> createView(Array<T> array)
    {
        if (array.dimensionality() != 2)
        {
            throw new IllegalArgumentException("Requires a 2D array");
        }
        
        int[] dims = array.size();
        int[] newDims;
        if (number == 1 || number == 3)
        {
            newDims = new int[]{dims[1], dims[0]};
        }
        else
        {
            newDims = new int[]{dims[0], dims[1]};
        }

        Function<int[], int[]> mapping = (int[] pos) ->
        {
            int x = pos[0];
            int y = pos[1];
            
            switch(this.number)
            {
            case 0:
                return new int[] { x, y };
            case 1:
                return new int[] { newDims[1] - 1 - y, x };
            case 2:
                return new int[] { newDims[0] - 1 - x, newDims[1] - 1 - y };
            case 3:
                return new int[] { y, newDims[0] - 1 - x };
            default:
                throw new RuntimeException("Illegal rotation number: " + this.number);
            }
        };
        
        return array.view(newDims, mapping);
    }


	@Override
	public boolean canProcess(Array<?> array)
	{
		return array.dimensionality() == 2;
	}
}
