/**
 * 
 */
package net.sci.image.analyze;

import static org.junit.Assert.*;
import net.sci.array.data.scalar2d.UInt8Array2D;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class LabelIntensitiesTest
{

	/**
	 * Test method for {@link net.sci.image.analyze.LabelIntensities#mean(net.sci.array.data.ScalarArray, net.sci.array.data.IntArray, int[])}.
	 */
	@Test
	public final void testMean()
	{
		UInt8Array2D labelArray = createFourRectArray();
		UInt8Array2D array = UInt8Array2D.create(labelArray.getSize(0), labelArray.getSize(1));
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
	 * Test method for {@link net.sci.image.analyze.LabelIntensities#sum(net.sci.array.data.ScalarArray)}.
	 */
	@Test
	public final void testSum()
	{
		UInt8Array2D labelArray = createFourRectArray();
		UInt8Array2D array = UInt8Array2D.create(labelArray.getSize(0), labelArray.getSize(1));
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
			array.setInt(x, 1, 1);
			array.setInt(x, 2, 1);
			
			array.setInt(x, 4, 3);
			array.setInt(x, 5, 3);
			array.setInt(x, 6, 3);
		}
		for (int x = 4; x < 9; x++)
		{
			array.setInt(x, 1, 2);
			array.setInt(x, 2, 2);
			
			array.setInt(x, 4, 4);
			array.setInt(x, 5, 4);
			array.setInt(x, 6, 4);
		}
		
		return array;
	}
}
