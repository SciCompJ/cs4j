package net.sci.array.data.scalar3d;

import static org.junit.Assert.*;
import net.sci.array.Array;
import net.sci.array.type.UInt8;

import org.junit.Test;

public class BufferedUInt8Array3DTest
{

	@Test
	public final void testNewInstance()
	{
		// create array with 2*3*4 = 24 elements 
		UInt8Array3D array = UInt8Array3D.create(4, 3, 2);
		for (int z = 0; z < 2; z++)
		{
			for (int y = 0; y < 3; y++)
			{
				for (int x = 0; x < 4; x++)
				{
					array.setValue(x, y, z, 10);
				}
			}
		}
		
		Array<?> array2 = array.newInstance(new int[]{2, 3, 4});
		assertNotNull(array2);

		Array<?> array3 = array.newInstance(3, 4);
		assertNotNull(array3);
	}
	
	@Test
	public final void testIterator()
	{
		// create array with 2*3*4 = 24 elements 
		UInt8Array3D array = UInt8Array3D.create(4, 3, 2);
		for (int z = 0; z < 2; z++)
		{
			for (int y = 0; y < 3; y++)
			{
				for (int x = 0; x < 4; x++)
				{
					array.setValue(x, y, z, 10);
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
		
		assertEquals(240, sum, .01);
		assertEquals(24, count);
	}

}
