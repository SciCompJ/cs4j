package net.sci.array.binary;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BinaryTest
{

	@Test
	public void testEquals_TRUE()
	{
		Binary b = new Binary(true);
		assertTrue(b.equals(Binary.TRUE));
	}

	@Test
	public void testIsAssignable()
	{
	    BinaryArray array = BinaryArray2D.create(6, 4);
	    assertTrue(Binary.class.isAssignableFrom(array.dataType()));
	}
}
