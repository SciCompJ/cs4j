/**
 * 
 */
package net.sci.image.segmentation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class OtsuThresholdTest
{
	/**
	 * Test method for {@link net.sci.image.segmentation.AutoThreshold#processScalar(net.sci.array.scalar.ScalarArray)}.
	 * @throws IOException 
	 */
	@Test
	public final void testProcessScalar() throws IOException
	{
		// create a gray scale array filled with gray and containing a light
		// rectangle in the middle
		UInt8Array2D array = UInt8Array2D.create(10, 10);
		array.fillValue(100);
		for (int y = 3; y < 7; y++)
		{
			for (int x = 3; x < 7; x++)
			{
				array.setValue(x, y, 150);
			}
		}

		// act
        BinaryArray segStem = new OtsuThreshold().processScalar(array);
        
        // assert
        assertFalse(segStem.getBoolean(new int[] {0, 0}));
        assertTrue(segStem.getBoolean(new int[] {5, 5}));
	}
}
