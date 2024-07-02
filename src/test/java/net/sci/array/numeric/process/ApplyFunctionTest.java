/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * 
 */
public class ApplyFunctionTest
{

    /**
     * Test method for {@link net.sci.array.numeric.process.ApplyFunction#createView(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testCreateView()
    {
        UInt8Array2D array = createDistanceToCenterArray();
        ApplyFunction op = new ApplyFunction(v -> v > 100 ? 0 : Math.sqrt(10000 - v*v));
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(op.createView(array)));

        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(100, res.getInt(10, 10));
        assertEquals(0, res.getInt( 0,  0));
        assertEquals(0, res.getInt(19,  0));
        assertEquals(0, res.getInt( 0, 19));
        assertEquals(0, res.getInt(19, 19));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.ApplyFunction#processScalar(net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcessScalar()
    {
        UInt8Array2D array = createDistanceToCenterArray();
        ApplyFunction op = new ApplyFunction(v -> v > 100 ? 0 : Math.sqrt(10000 - v*v));
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(op.process(array)));
        
        assertEquals(array.size(0), res.size(0));
        assertEquals(array.size(1), res.size(1));
        assertEquals(100, res.getInt(10, 10));
        assertEquals(0, res.getInt( 0,  0));
        assertEquals(0, res.getInt(19,  0));
        assertEquals(0, res.getInt( 0, 19));
        assertEquals(0, res.getInt(19, 19));
    }
    
    private static final UInt8Array2D createDistanceToCenterArray()
    {
        UInt8Array2D array = UInt8Array2D.create(20, 20);
        array.fillInts((x,y)-> (int) (10 * Math.hypot(x - 10.0, y - 10.0)));
        return array;
    }
}
