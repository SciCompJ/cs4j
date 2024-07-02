/**
 * 
 */
package net.sci.array.binary.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;

/**
 * @author dlegland
 *
 */
public class BinaryMaskTest
{
    /**
     * Test method for {@link net.sci.array.binary.process.BinaryMask#process(net.sci.array.Array, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_UInt8()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 6);
        array.fillInts((x,y) -> y * 10 + x);
        BinaryArray2D mask = BinaryArray2D.create(8, 6);
        mask.fillBooleans((x,y) -> x > 3 && y > 2);
        
        BinaryMask op = new BinaryMask();
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(op.process(array, mask)));
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(0, res.getInt(1, 1));
        assertEquals(0, res.getInt(2, 2));
        assertEquals(array.getInt(6, 5), res.getInt(6, 5));
    }


    /**
     * Test method for {@link net.sci.array.binary.process.BinaryMask#createView(net.sci.array.Array, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testCreateView()
    {
        UInt8Array2D array = UInt8Array2D.create(8, 6);
        array.fillInts((x,y) -> y * 10 + x);
        BinaryArray2D mask = BinaryArray2D.create(8, 6);
        mask.fillBooleans((x,y) -> x > 3 && y > 2);
        
        UInt8Array2D res = UInt8Array2D.wrap(UInt8Array.wrap(BinaryMask.createView(array, mask)));
        
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(0, res.getInt(1, 1));
        assertEquals(0, res.getInt(2, 2));
        assertEquals(array.getInt(6, 5), res.getInt(6, 5));
    }
}
