package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BufferedFloat32Array2DTest
{
    @Test
    public final void testValues()
    {
        BufferedFloat32Array2D array = new BufferedFloat32Array2D(5, 4);
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
		Float32Array2D array = new BufferedFloat32Array2D(5, 4);
		array.fillValue(1000.10);
		
		int count = 0;
		double sum = 0;
		for (Float32 val : array) 
		{
			sum += val.getValue();
			count++;
		}
        assertEquals(20, count);
		assertEquals(20_002.0, sum, .01);
	}

}
