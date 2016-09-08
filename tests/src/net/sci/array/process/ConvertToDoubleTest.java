package net.sci.array.process;

import static org.junit.Assert.*;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar2d.DoubleArray2D;
import net.sci.array.data.scalar2d.UInt8Array2D;

import org.junit.Test;

public class ConvertToDoubleTest
{

	@Test
	public final void test()
	{
		UInt8Array2D array = new BufferedUInt8Array2D(5, 5);
		for (int y = 0; y < 5; y++)
		{
			for (int x = 0;x < 5;x++)
			{
				array.setInt(x, y, 10);
			}
		}

		ConvertToDouble op = new ConvertToDouble();
		DoubleArray2D res = DoubleArray2D.create(5, 5);
		op.process(array, res);

		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				assertEquals(array.getValue(x, y), res.getValue(x, y), .1);
			}
		}
	}

}
