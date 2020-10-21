package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BufferedDoubleArray2DTest
{

	@Test
	public final void testIterator()
	{
		Float64Array2D array = new BufferedFloat64Array2D(5, 5);
		array.fillValue(10.0);
		
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
