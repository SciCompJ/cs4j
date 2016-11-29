package net.sci.array.type;

import static org.junit.Assert.*;

import org.junit.Test;

public class BooleanTest
{

	@Test
	public void testEquals_TRUE()
	{
		Boolean b = new Boolean(true);
		
		assertTrue(b.equals(Boolean.TRUE));
		assertTrue(b == Boolean.TRUE);
	}

}
