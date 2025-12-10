/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class AddValueTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.AddValue#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcess_UInt8Array()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x, y) -> y * 10 + x);
        
        ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d(new AddValue(10.0).processScalar(array));
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertEquals(10.0, res.getValue(0,0), 0.01);
        assertEquals(15.0, res.getValue(5,0), 0.01);
        assertEquals(40.0, res.getValue(0,3), 0.01);
        assertEquals(45.0, res.getValue(5,3), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.AddValue#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testView_UInt8Array()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillInts((x, y) -> y * 10 + x);
        
        ScalarArray2D<?> res = ScalarArray2D.wrapScalar2d((ScalarArray<?>) new AddValue(10.0).createView(array));
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        
        assertEquals(10.0, res.getValue(0,0), 0.01);
        assertEquals(15.0, res.getValue(5,0), 0.01);
        assertEquals(40.0, res.getValue(0,3), 0.01);
        assertEquals(45.0, res.getValue(5,3), 0.01);
        
        // updates reference array, and check change on the view
        array.setValue(2, 1, 50.0);
        assertEquals(60.0, res.getValue(2, 1), 0.01);
    }

}
