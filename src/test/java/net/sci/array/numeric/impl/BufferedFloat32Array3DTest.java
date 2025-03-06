package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array3D;

public class BufferedFloat32Array3DTest
{
    @Test
    public final void testValues()
    {
        BufferedFloat32Array3D array = new BufferedFloat32Array3D(5, 4, 3);
        array.fillValue(1000.10);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(60, count);
        assertEquals(60_006.0, sum, .01);
    }
    

	@Test
	public final void testIterator()
	{
		Float32Array3D array = new BufferedFloat32Array3D(5, 4, 3);
		array.fillValue(1000.10);
		
		int count = 0;
		double sum = 0;
		for (Float32 val : array) 
		{
			sum += val.value();
			count++;
		}
        assertEquals(60, count);
		assertEquals(60_006.0, sum, .01);
	}

}
