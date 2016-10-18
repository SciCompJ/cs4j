package net.sci.array.data.scalar2d;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.type.UInt16;

public class BufferedUInt16Array2DTest
{

	@Test
	public final void testIterator()
	{
		UInt16Array2D array = new BufferedUInt16Array2D(6, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 6;x++)
			{
				array.setInt(x, y, 1000);
			}
		}
		
		int count = 0;
		double sum = 0;
		for (UInt16 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(30000, sum, .01);
		assertEquals(30, count);
	}

}
