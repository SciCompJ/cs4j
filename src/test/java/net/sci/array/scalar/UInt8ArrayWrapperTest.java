package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UInt8ArrayWrapperTest
{
	@Test
	public final void testCreate()
	{
		// create base 8x6=48 array, containing 4x6 = 24 values set at 100.
		IntArray2D<?> array = new BufferedUInt8Array2D(8, 6);
		for (int y = 1; y < 5; y++)
		{
			for (int x = 1; x < 7; x++)
			{
				array.setInt(100, x, y);
			}
		}
		
		UInt8Array wrap = UInt8Array.wrap(array);
		assertEquals(2, wrap.dimensionality());
		assertEquals(8, wrap.size(0));
		assertEquals(6, wrap.size(1));
	}

	@Test
	public final void testIterator()
	{
		// create base 8x6=48 array, containing 4x6 = 24 values set at 100.
		IntArray2D<?> array = new BufferedUInt8Array2D(8, 6);
		for (int y = 1; y < 5; y++)
		{
			for (int x = 1; x < 7; x++)
			{
				array.setInt(100, x, y);
			}
		}
		
		UInt8Array wrap = UInt8Array.wrap(array);
		assertEquals(2, wrap.dimensionality());
		assertEquals(8, wrap.size(0));
		assertEquals(6, wrap.size(1));
		
		int count = 0;
		double sum = 0;
		for (UInt8 val : wrap) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(2400, sum, .01);
		assertEquals(48, count);
	}

	@Test
	public final void testFill()
	{
		// create base empty 6x5 array, containing 30 elements
		IntArray2D<?> array = new BufferedUInt8Array2D(6, 5);
		UInt8Array wrap = UInt8Array.wrap(array);
		
		wrap.fill(new UInt8(100));
		
		int count = 0;
		double sum = 0;
		for (UInt8 val : wrap) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(3000, sum, .01);
		assertEquals(30, count);
	}
}
