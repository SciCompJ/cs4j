package net.sci.array.scalar;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BooleanTest
{

	@Test
	public void testEquals_TRUE()
	{
		Binary b = new Binary(true);
		assertTrue(b.equals(Binary.TRUE));
	}

}
