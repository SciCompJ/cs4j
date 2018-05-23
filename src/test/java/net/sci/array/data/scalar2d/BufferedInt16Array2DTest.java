package net.sci.array.data.scalar2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.BufferedInt16Array2D;
import net.sci.array.scalar.Int16;
import net.sci.array.scalar.Int16Array2D;

public class BufferedInt16Array2DTest
{

	@Test
	public final void testIterator()
	{
		Int16Array2D array = new BufferedInt16Array2D(6, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 6; x++)
			{
				array.setInt(x, y, 1000);
			}
		}
		
		int count = 0;
		double sum = 0;
		for (Int16 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(30000, sum, .01);
		assertEquals(30, count);
	}

}
