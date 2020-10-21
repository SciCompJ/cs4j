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
public class BinaryArray2DTest
{

	/**
	 * Test method for {@link net.sci.array.scalar.BinaryArray2D#getBoolean(int[])}.
	 */
	@Test
	public void testGetBooleanIntArray()
	{
		BinaryArray2D array = BinaryArray2D.create(5, 4);
		array.fill(new Binary(true));
		assertTrue(array.getBoolean(3, 2));
	}

	/**
	 * Test method for {@link net.sci.array.scalar.BinaryArray2D#newInstance(int[])}.
	 */
	@Test
	public void testNewInstance()
	{
		BinaryArray2D array = BinaryArray2D.create(5, 4);
		BinaryArray2D array2 = BinaryArray2D.wrap(array.newInstance(5, 4));
		assertNotNull(array2);
	}

	/**
	 * Test method for {@link net.sci.array.scalar.BinaryArray2D#duplicate()}.
	 */
	@Test
	public void testDuplicate()
	{
		BinaryArray2D array = BinaryArray2D.create(5, 4);
		array.fill(new Binary(true));
		BinaryArray2D dup = array.duplicate();
		assertTrue(dup.getBoolean(3, 2));
	}

	/**
	 * Test method for {@link net.sci.array.scalar.BinaryArray2D#trueElementPositions()}.
	 */
	@Test
	public void testFind()
	{
		BinaryArray2D array = BinaryArray2D.create(5, 4);
		array.setBoolean(1, 1, true);
		array.setBoolean(2, 1, true);
		array.setBoolean(3, 1, true);
		array.setBoolean(1, 2, true);
		int n = 0;
		for(@SuppressWarnings("unused") int[] pos : array.trueElementPositions())
		{
			n++;
		}
		assertEquals(4, n);
	}

}
