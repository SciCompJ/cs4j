/**
 * 
 */
package net.sci.image.analyze;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class LabelIntensitiesTest
{

	/**
	 * Test method for {@link net.sci.image.analyze.LabelIntensities#mean(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.IntArray, int[])}.
	 */
	@Test
	public final void testMean()
	{
		UInt8Array2D labelArray = createFourRectArray();
		UInt8Array2D array = UInt8Array2D.create(labelArray.size(0), labelArray.size(1));
		array.fillValue(10);
		
		int[] labels = new int[]{1, 2, 3, 4};

		double[] values = LabelIntensities.mean(array, labelArray, labels);
		assertEquals(4, values.length);
		assertEquals(10, values[0], .01);
		assertEquals(10, values[1], .01);
		assertEquals(10, values[2], .01);
		assertEquals(10, values[3], .01);
	}

	/**
	 * Test method for {@link net.sci.image.analyze.LabelIntensities#sum(net.sci.array.scalar.ScalarArray)}.
	 */
	@Test
	public final void testSum()
	{
		UInt8Array2D labelArray = createFourRectArray();
		UInt8Array2D array = UInt8Array2D.create(labelArray.size(0), labelArray.size(1));
		array.fillValue(10);
		
		int[] labels = new int[]{1, 2, 3, 4};

		double[] values = LabelIntensities.sum(array, labelArray, labels);
		assertEquals(4, values.length);
		assertEquals(40, values[0], .01);
		assertEquals(100, values[1], .01);
		assertEquals(60, values[2], .01);
		assertEquals(150, values[3], .01);
	}

	private UInt8Array2D createFourRectArray()
	{
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        for (int x = 1; x < 3; x++)
        {
            array.setInt(1, x, 1);
            array.setInt(1, x, 2);
            
            array.setInt(3, x, 4);
            array.setInt(3, x, 5);
            array.setInt(3, x, 6);
        }
        for (int x = 4; x < 9; x++)
        {
            array.setInt(2, x, 1);
            array.setInt(2, x, 2);
            
            array.setInt(4, x, 4);
            array.setInt(4, x, 5);
            array.setInt(4, x, 6);
        }
        
        return array;
	}
}
