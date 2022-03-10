/**
 * 
 */
package net.sci.image.process.segment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.scalar.IntArray;
import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class KMeansSegmentationTest
{

    /**
     * Test method for {@link net.sci.image.process.segment.KMeansSegmentation#process(net.sci.array.Array)}.
     */
    @Test
    public final void testProcess()
    {
        UInt8Array2D array = UInt8Array2D.create(30, 30);
        array.fillValues((x,y) -> (double) (x + 6 * y));
        
        KMeansSegmentation algo = new KMeansSegmentation(3);
        IntArray<?> labelMap = algo.process(array);
        
        double[] range = labelMap.valueRange();
        assertEquals(range[0], 0.0, 0.01);
        // difficult to test, as the number of clusters may equal 2 or 3 depending on initial choice of germs
        assertEquals(range[1], 2.0, 0.01);
    }
}
