/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class MinProjectionTest
{

    /**
     * Test method for {@link net.sci.array.numeric.process.ProjectionOperator#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public void testProcessScalarScalarArrayOfQextendsScalar()
    {
        // Create array with values within 0 and 79
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        array.fillValues((x,y) -> (double) y * 10.0 + x);
        
        // create operator
        MinProjection op = new MinProjection(1);
        UInt8Array res = UInt8Array.wrap(op.processScalar(array));
        
        // check size
        assertEquals(2, res.dimensionality());
        assertEquals(10, res.size(0));
        assertEquals(1, res.size(1));

        // Check content
        assertEquals(0.0, res.getValue(new int[] {0,0}), 0.01);
        assertEquals(9.0, res.getValue(new int[] {9,0}), 0.01);
    }

}
