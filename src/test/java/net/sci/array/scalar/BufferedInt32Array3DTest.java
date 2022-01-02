package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class BufferedInt32Array3DTest
{
    @Test
    public final void testValues()
    {
        BufferedInt32Array3D array = new BufferedInt32Array3D(5, 4, 3);
        array.fillValue(1000);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(60, count);
        assertEquals(60_000.0, sum, 0.1);
    }
    

	@Test
	public final void testNewInstance()
	{
		// create array with 2*3*4 = 24 elements 
		Int32Array3D array = Int32Array3D.create(5, 4, 3);
		array.fillValues((x,y,z) -> 10.0);
		
		Int32Array array2 = array.newInstance(new int[]{3, 4, 5});
		assertNotNull(array2);
		

		Int32Array array3 = array.newInstance(3, 4);
		assertNotNull(array3);
	}
	
	@Test
	public final void testIterator()
	{
		// create array with 2*3*4 = 24 elements 
		Int32Array3D array = Int32Array3D.create(5, 4, 3);
        array.fillValues((x,y,z) -> 1000.0);

		// iterate over elements, count and sum them
		int count = 0;
		double sum = 0;
		for (Int32 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		
		assertEquals(60_000, sum, .01);
		assertEquals(60, count);
	}

}
