/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class DownSampleTest
{

    /**
     * Test method for {@link net.sci.array.process.numeric.DownSample#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar_checkSize()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        
        DownSample op = new DownSample(2);
        
        ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d(op.processScalar(array));

        assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
    }

}
