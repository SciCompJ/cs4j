package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.Array;

public class BufferedUInt8Array2DTest
{
    @Test
    public final void testValues()
    {
        BufferedUInt8Array2D array = new BufferedUInt8Array2D(5, 4);
        array.fillValue(10);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(20, count);
        assertEquals(200.0, sum, 0.1);
    }
    

    @Test
    public final void testIterator()
    {
        UInt8Array2D array = new BufferedUInt8Array2D(6, 5);
        array.fillValue(10);
        
        int count = 0;
        double sum = 0;
        for (UInt8 val : array) 
        {
            sum += val.getValue();
            count++;
        }
        assertEquals(300, sum, .01);
        assertEquals(30, count);
    }

    @Test
    public final void testIntIterator()
    {
        UInt8Array2D array = new BufferedUInt8Array2D(6, 5);
        array.fillValue(200);
        
        UInt8Array.Iterator iter = array.iterator();
        int count = 0;
        double sum = 0;
        while(iter.hasNext()) 
        {
            sum += iter.nextInt();
            count++;
        }
        assertEquals(6000, sum, .01);
        assertEquals(30, count);
    }

	@Test
	public final void testFill()
	{
		UInt8Array2D array = new BufferedUInt8Array2D(6, 5);
		array.fill(new UInt8(10));
		
		int count = 0;
		double sum = 0;
		for (UInt8 val : array) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(300, sum, .01);
		assertEquals(30, count);
	}

	@Test
	public final void testGetFactory()
	{
		UInt8Array2D array = new BufferedUInt8Array2D(6, 5);
		
		Array.Factory<UInt8> factory = array.factory();
		int[] dims = new int[]{4, 3, 2};
		Array<UInt8> array2 = factory.create(dims, new UInt8(10));
		
		int count = 0;
		double sum = 0;
		for (UInt8 val : array2) 
		{
			sum += val.getValue();
			count++;
		}
		assertEquals(240, sum, .01);
		assertEquals(24, count);
	}

}
