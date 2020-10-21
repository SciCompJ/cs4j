/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ScalarArrayThresholdViewTest
{

	/**
	 * Test method for {@link net.sci.array.scalar.ScalarArrayThresholdView#getBoolean(int[])}.
	 */
	@Test
	public void testGetBoolean()
	{
		UInt8Array2D base = createTestArray();
		BinaryArray2D array = BinaryArray2D.wrap(new ScalarArrayThresholdView(base, 19));

		assertFalse(array.getBoolean(0, 0));
		assertTrue(array.getBoolean(4, 3));
	}

	/**
	 * Test method for {@link net.sci.array.scalar.ScalarArrayThresholdView#size()}.
	 */
	@Test
	public void testGetSize()
	{
		UInt8Array2D base = createTestArray();
		BinaryArray array = new ScalarArrayThresholdView(base, 19);
		int[] dim = array.size();
		assertEquals(5, dim[0]);
		assertEquals(4, dim[1]);
	}

	/**
	 * Test method for {@link net.sci.array.scalar.ScalarArrayThresholdView#iterator()}.
	 */
	@Test
	public void testIterator()
	{
		UInt8Array2D base = createTestArray();
		BinaryArray array = new ScalarArrayThresholdView(base, 19);
		int count = 0;
		for (Binary b : array)
		{
			if (b.state) count++;
		}
		assertEquals(count, 10);
	}

	private UInt8Array2D createTestArray()
	{
		UInt8Array2D array = UInt8Array2D.create(5, 4);
		array.populateValues((x, y) -> (y * 10.0 + x));
		return array;
	}
}
