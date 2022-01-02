package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BufferedFloat64Array3DTest
{
    @Test
    public final void testValues()
    {
        BufferedFloat64Array3D array = new BufferedFloat64Array3D(5, 4, 3);
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
		Float64Array3D array = new BufferedFloat64Array3D(5, 4, 3);
		array.fillValue(1000.10);
		
		int count = 0;
		double sum = 0;
		for (Float64 val : array) 
		{
			sum += val.getValue();
			count++;
		}
        assertEquals(60, count);
		assertEquals(60_006.0, sum, .01);
	}

}
