/**
 * 
 */
package net.sci.image.process;

import net.sci.array.Array;
import net.sci.array.ArrayToArrayOperator;
import net.sci.array.data.Float32Array;
import net.sci.array.data.ScalarArray;
import net.sci.array.data.scalar2d.ScalarArray2D;

/**
 * @author dlegland
 *
 */
public final class BoxFilter3x3 implements ArrayToArrayOperator
{

	/* (non-Javadoc)
	 * @see net.sci.array.ArrayOperator#process(net.sci.array.Array, net.sci.array.Array)
	 */
	@Override
	public void process(Array<?> source, Array<?> target)
	{
		if (source instanceof ScalarArray2D && target instanceof ScalarArray2D )
		{
			processScalar2d((ScalarArray2D<?>) source, (ScalarArray2D<?>) target);
		}
		else
		{
			throw new IllegalArgumentException("Can not process array of class " + source.getClass());
		}

	}

	public void processScalar2d(ScalarArray2D<?> source, ScalarArray2D<?> target)
	{
		int sizeX = source.getSize(0);
		int sizeY = source.getSize(1);
		
		for(int y = 0; y < sizeY; y++)
		{
			for(int x = 0; x < sizeX; x++)
			{
				double sum = 0;
				
				// iterate over neighbors
				for (int y2 = y - 1; y2 <= y + 1; y2++)
				{
					int y2r = Math.min(Math.max(y2, 0), sizeY - 1);
					for (int x2 = x - 1; x2 <= x + 1; x2++)
					{
						int x2r = Math.min(Math.max(x2, 0), sizeX - 1);
						sum += source.getValue(x2r, y2r);
					}
				}
				
				target.setValue(x, y, sum / 9);
			}			
		}
		
	}
	
	public Array<?> createEmptyOutputArray(Array<?> array)
	{
		int[] dims = array.getSize();
		return Float32Array.create(dims);
	}
	
	public boolean canProcess(Array<?> array)
	{
		return array instanceof ScalarArray;
	}

	public boolean canProcess(Array<?> source, Array<?> target)
	{
		return source instanceof ScalarArray && target instanceof ScalarArray;
	}
}
