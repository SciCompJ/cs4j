package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BufferedUInt8ArrayNDTest
{

	@Test
	public void testGetSet()
	{
		// create array with 2*3*4 = 24 elements 
		UInt8Array array = UInt8ArrayND.create(4, 3, 2);
		array.fillInt(10);

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
