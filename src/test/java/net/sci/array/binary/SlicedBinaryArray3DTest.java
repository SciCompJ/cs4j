/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class SlicedBinaryArray3DTest
{

    /**
     * Test method for {@link net.sci.array.binary.SlicedBinaryArray3D#setBoolean(int, int, int, boolean)}.
     */
    @Test
    public final void testSetBooleanIntIntIntBoolean()
    {
        BinaryArray3D array = new SlicedBinaryArray3D(5, 4, 3);
        
        array.setBoolean(0, 0, 0, true);
        array.setBoolean(3, 2, 1, true);
        
        assertTrue(array.getBoolean(0, 0, 0));
        assertTrue(array.getBoolean(3, 2, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.SlicedBinaryArray3D#setBoolean(int, int, int, boolean)}.
     */
    @Test
    public final void testSetBooleanFromSlice()
    {
        BinaryArray3D array = new SlicedBinaryArray3D(5, 4, 3);
        BinaryArray2D slice = array.slice(1);

        slice.setBoolean(3, 2, true);
        
        assertTrue(array.getBoolean(3, 2, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.SlicedBinaryArray3D#convert(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testConvert()
    {
        BinaryArray3D array = new BufferedBinaryArray3D(5, 4, 3);
        array.setBoolean(0, 0, 0, true);
        array.setBoolean(4, 0, 0, true);
        array.setBoolean(0, 3, 0, true);
        array.setBoolean(0, 0, 2, true);
        array.setBoolean(4, 3, 2, true);
        
        BinaryArray3D res = SlicedBinaryArray3D.convert(array);
        
        assertTrue(res instanceof SlicedBinaryArray3D);
        assertEquals(5, res.size(0));
        assertEquals(4, res.size(1));
        assertEquals(3, res.size(2));
        assertTrue(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(4, 0, 0));
        assertTrue(res.getBoolean(0, 3, 0));
        assertTrue(res.getBoolean(0, 0, 2));
        assertTrue(res.getBoolean(4, 3, 2));
    }

}
