/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArray3DTest
{
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray3D#convert(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testConvert()
    {
        BinaryArray3D array = new BufferedBinaryArray3D(12, 12, 12);
        fillRect(array, 2, 10, 2, 10, 2, 10, true);
        fillRect(array, 4, 8, 4, 8, 4, 8, false);
        array.setBoolean(6, 6, 6, true);
        
        RunLengthBinaryArray3D converted = RunLengthBinaryArray3D.convert(array);
        
        for (int z = 0; z < 12; z++)
        {
            for (int y = 0; y < 12; y++)
            {
                for (int x = 0; x < 12; x++)
                {
                    assertEquals(array.getBoolean(x, y, z), converted.getBoolean(x, y, z));  
                }
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray3D#convert(net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void testConvert_VoxelsAtCorners()
    {
        BinaryArray3D array = new BufferedBinaryArray3D(5, 4, 3);
        array.fillBooleans((x,y,z) -> (x == 0 || x == 4) && (y == 0 || y == 3) && (z == 0 || z == 2));
        
        RunLengthBinaryArray3D converted = RunLengthBinaryArray3D.convert(array);
        
        for (int z = 0; z < 3; z++)
        {
            for (int y = 0; y < 4; y++)
            {
                for (int x = 0; x < 5; x++)
                {
                    assertEquals(array.getBoolean(x, y, z), converted.getBoolean(x, y, z));  
                }
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryArray2D#complement()}.
     */
    @Test
    public final void testComplement_innerRect()
    {
        BinaryArray3D array = new RunLengthBinaryArray3D(5, 4, 3);
        array.fillBooleans((x,y,z) -> (x == 2 || x == 3) && (y == 1 || y == 2) && (z == 1));
        
        BinaryArray3D comp = array.complement();
        
        assertEquals(comp.size(0), array.size(0));
        assertEquals(comp.size(1), array.size(1));
        assertEquals(comp.size(2), array.size(2));
        
        assertTrue(comp.getBoolean(0, 0, 0));
        assertTrue(comp.getBoolean(4, 0, 0));
        assertTrue(comp.getBoolean(0, 3, 0));
        assertTrue(comp.getBoolean(4, 3, 0));
        assertTrue(comp.getBoolean(0, 0, 2));
        assertTrue(comp.getBoolean(4, 0, 2));
        assertTrue(comp.getBoolean(0, 3, 2));
        assertTrue(comp.getBoolean(4, 3, 2));
        
        assertFalse(comp.getBoolean(2, 1, 1));
        assertTrue(comp.getBoolean(0, 1, 1));
        assertTrue(comp.getBoolean(4, 1, 1));
    }

    @Test
    public final void testFill_false()
    {
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(5, 4, 3);
        
        array.fill(false);
        
        assertFalse(array.getBoolean(0, 0, 0));
        assertFalse(array.getBoolean(4, 3, 2));
    }
    
    @Test
    public final void testGetSet()
    {
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(5, 4, 3);
        
        array.setBoolean(1, 1, 1, true);
        array.setBoolean(2, 2, 2, true);
        
        assertTrue(array.getBoolean(1, 1, 1));
        assertTrue(array.getBoolean(2, 2, 2));
    }

    @Test
    public final void testUpdatePixelInSlice()
    {
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(5, 4, 3);
        BinaryArray2D slice = array.slice(1);
    
        slice.setBoolean(3, 2, true);
        
        assertTrue(array.getBoolean(3, 2, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray3D#dilation( net.sci.array.binary.RunLengthBinaryArray3D, net.sci.array.binary.RunLengthBinaryArray3D, int[])}.
     */
    @Test
    public final void testIterator()
    {
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(5, 4, 3);
        array.fill(true);
        
        int count = 0;
        for (Binary binary : array)
        {
            if (binary.getBoolean())
            {
                count++;
            }
        }
        
        assertEquals(60, count);
    }

    private static final void fillRect(BinaryArray3D array, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax, boolean state)
    {
        for (int z = zmin; z <= zmax; z++)
        {
            for (int y = ymin; y <= ymax; y++)
            {
                for (int x = xmin; x <= xmax; x++)
                {
                    array.setBoolean(x, y, z, state);
                }
            }
        }
    }
}
