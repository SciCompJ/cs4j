/**
 * 
 */
package net.sci.array.process.numeric;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array;
import net.sci.array.scalar.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class MaxProjectionTest
{

    /**
     * Test method for {@link net.sci.array.process.numeric.ProjectionOperator#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public void testProcessScalarScalarArrayOfQextendsScalar()
    {
        // Create array with values within 0 and 79
        UInt8Array2D array = UInt8Array2D.create(10, 8);
        array.populateValues((x,y) -> (double) y * 10.0 + x);
        
        // create operator
        MaxProjection op = new MaxProjection(1);
        UInt8Array res = UInt8Array.wrap(op.processScalar(array));
        
        // check size
        assertEquals(2, res.dimensionality());
        assertEquals(10, res.size(0));
        assertEquals(1, res.size(1));

        // Check content
        assertEquals(70.0, res.getValue(0,0), 0.01);
        assertEquals(79.0, res.getValue(9,0), 0.01);
    }

}
