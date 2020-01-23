package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.UInt8;
import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8ArrayND;

public class BufferedUInt8ArrayNDTest
{

	@Test
	public void testGetSet()
	{
		// create array with 2*3*4 = 24 elements 
		UInt8Array array = UInt8ArrayND.create(4, 3, 2);
		for (int z = 0; z < 2; z++)
		{
			for (int y = 0; y < 3; y++)
			{
				for (int x = 0; x < 4; x++)
				{
					array.setInt(10, x, y, z);
				}
			}
		}

		// iterate over elements, count and sum them
		int count = 0;
		double sum = 0;
		for (UInt8 val : array) 
		{
			sum += val.getValue();
			count++;
		}
	
		// test that sum and count are valid
		assertEquals(240, sum, .01);
		assertEquals(24, count);	
	}
}
