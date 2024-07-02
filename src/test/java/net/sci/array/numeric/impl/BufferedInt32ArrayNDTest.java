package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32ArrayND;

public class BufferedInt32ArrayNDTest
{

	@Test
	public void testGetSet()
	{
		// create array with 2*3*4 = 24 elements 
		Int32Array array = Int32ArrayND.create(4, 3, 2);
		array.fillInt(10);

		// iterate over elements, count and sum them
		int count = 0;
		double sum = 0;
		for (Int32 val : array) 
		{
			sum += val.getValue();
			count++;
		}
	
		// test that sum and count are valid
		assertEquals(240, sum, .01);
		assertEquals(24, count);	
	}
}
