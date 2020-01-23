package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.BufferedFloat64Array2D;
import net.sci.array.scalar.Float64;
import net.sci.array.scalar.Float64Array2D;

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
				array.setValue(10, x, y);
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
