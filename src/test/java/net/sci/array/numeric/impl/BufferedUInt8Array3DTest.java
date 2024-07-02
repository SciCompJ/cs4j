package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.sci.array.Array;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array3D;

public class BufferedUInt8Array3DTest
{
    @Test
    public final void testValues()
    {
        BufferedUInt8Array3D array = new BufferedUInt8Array3D(5, 4, 3);
        array.fillValue(10);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(60, count);
        assertEquals(600.0, sum, 0.1);
    }
    

	@Test
	public final void testNewInstance()
	{
		// create array with 2*3*4 = 24 elements 
		UInt8Array3D array = UInt8Array3D.create(4, 3, 2);
		array.fillValues((x,y,z) -> 10.0);
		
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
        array.fillValues((x,y,z) -> 10.0);

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
