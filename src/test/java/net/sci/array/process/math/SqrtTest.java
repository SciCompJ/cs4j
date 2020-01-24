package net.sci.array.process.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.process.math.Sqrt;
import net.sci.array.scalar.Float64Array2D;
import net.sci.array.scalar.UInt16Array2D;

public class SqrtTest
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
				double val = index++;
				array.setValue(val * val, x, y);
			}
		}
		
		Sqrt op = new Sqrt();
		Float64Array2D res = Float64Array2D.create(6, 6);
		op.processScalar(array, res);

		index = 0;
		for (int y = 1; y < 5; y++)
		{
			for (int x = 1; x < 5; x++)
			{
				double exp = index++;
				assertEquals(exp, res.getValue(x, y), .1);
			}
		}
	}

}
