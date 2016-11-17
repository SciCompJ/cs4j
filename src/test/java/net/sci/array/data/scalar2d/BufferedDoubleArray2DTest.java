package net.sci.array.data.scalar2d;

import static org.junit.Assert.*;
import net.sci.array.type.Float64;

import org.junit.Test;

public class BufferedDoubleArray2DTest
{

	@Test
	public final void testIterator()
	{
		Float64Array2D array = new BufferedFloat64Array2D(5, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0;x < 5;x++)
			{
				array.setValue(x, y, 10);
			}
		}
		
		int count = 0;
		double sum = 0;
		for (Float64 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		
		assertEquals(250, sum, .01);
		assertEquals(25, count);
	}

}
