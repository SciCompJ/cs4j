package net.sci.array.numeric.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Float64Array2D;

public class BufferedFloat64Array2DTest
{
    @Test
    public final void testValues()
    {
        BufferedFloat64Array2D array = new BufferedFloat64Array2D(5, 4);
        array.fillValue(1000.10);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(20, count);
        assertEquals(20_002.0, sum, 0.1);
    }
    

	@Test
	public final void testIterator()
	{
		Float64Array2D array = new BufferedFloat64Array2D(5, 4);
		array.fillValue(1000.10);
		
		int count = 0;
		double sum = 0;
		for (Float64 val : array) 
		{
			sum += val.value();
			count++;
		}
        assertEquals(20, count);
		assertEquals(20_002.0, sum, .01);
	}

}
