package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.UInt16Array2D;

public class PowerOfTwoTest
{

	@Test
	public void testProcessScalar()
	{
		UInt16Array2D array = UInt16Array2D.create(6, 6);
		int index = 0;
		for (int y = 1; y < 5; y++)
		{
			for (int x = 1; x < 5; x++)
			{
				array.setValue(x, y, index++);
			}
		}
		
		PowerOfTwo op = new PowerOfTwo();
		Float64Array2D res = Float64Array2D.create(6, 6);
		op.processScalar(array, res);

		index = 0;
		for (int y = 1; y < 5; y++)
		{
			for (int x = 1; x < 5; x++)
			{
				double val = index++;
				double exp = val * val;
				assertEquals(exp, res.getValue(x, y), .1);
			}
		}
	}

}
