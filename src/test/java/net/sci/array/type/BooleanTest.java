package net.sci.array.type;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.Binary;

public class BooleanTest
{

	@Test
	public void testEquals_TRUE()
	{
		Binary b = new Binary(true);
		assertTrue(b.equals(Binary.TRUE));
	}

}
