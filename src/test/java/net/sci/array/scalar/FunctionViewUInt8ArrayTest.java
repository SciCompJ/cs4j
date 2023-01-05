/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class FunctionViewUInt8ArrayTest
{

    /**
     * Test method for {@link net.sci.array.scalar.FunctionViewUInt8Array#size()}.
     */
    @Test
    public final void testSize()
    {
        UInt8Array2D array = UInt8Array2D.wrap(createArray());
        
        assertEquals(2, array.dimensionality());
        assertEquals(8, array.size(0));
        assertEquals(6, array.size(1));
    }

    /**
     * Test method for {@link net.sci.array.scalar.FunctionViewUInt8Array#getByte(int[])}.
     */
    @Test
    public final void testGetByte()
    {
        UInt8Array2D array = UInt8Array2D.wrap(createArray());
        
        assertEquals((byte)   0, array.getByte(0, 0));
        assertEquals((byte) 200, array.getByte(7, 0));
        assertEquals((byte) 140, array.getByte(0, 5));
        assertEquals((byte) 255, array.getByte(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.scalar.FunctionViewUInt8Array#getInt(int[])}.
     */
    @Test
    public final void testGetInt()
    {
        UInt8Array2D array = UInt8Array2D.wrap(createArray());
        
        assertEquals(  0, array.getInt(0, 0));
        assertEquals(200, array.getInt(7, 0));
        assertEquals(140, array.getInt(0, 5));
        assertEquals(255, array.getInt(7, 5));
    }
    
    private FunctionViewUInt8Array createArray()
    {
        int[] dims = new int[] {8, 6};
        FunctionViewUInt8Array array = new FunctionViewUInt8Array(dims, pos -> (double) 30*pos[0] + 30*pos[1] - 10);
        return array;
    }
}
