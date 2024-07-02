package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array2D;

public class ConvertToDoubleTest
{

	@Test
	public final void test()
	{
		UInt8Array2D array = new BufferedUInt8Array2D(5, 5);
		array.fillValue(10);

		ConvertToDouble op = new ConvertToDouble();
		ScalarArray2D<?> res = (ScalarArray2D<?>) op.process(array);

		for (int y = 0; y < 5; y++)
		{
			for (int x = 0; x < 5; x++)
			{
				assertEquals(array.getValue(x, y), res.getValue(x, y), .1);
			}
		}
	}

}
