package net.sci.array.data.scalar2d;

import static org.junit.Assert.*;
import net.sci.array.type.UInt8;

import org.junit.Test;

public class BufferedUInt8Array2DTest
{

	@Test
	public final void testIterator()
	{
		UInt8Array2D array = new BufferedUInt8Array2D(5, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 5;x++)
			{
				array.setInt(x, y, 10);
			}
		}
		
		int count = 0;
		double sum = 0;
		for (UInt8 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(250, sum, .01);
		assertEquals(25, count);
	}

}
