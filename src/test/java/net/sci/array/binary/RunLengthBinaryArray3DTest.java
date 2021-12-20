/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class RunLengthBinaryArray3DTest
{
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
                    assertTrue(array.getBoolean(x, y, z) == converted.getBoolean(x, y, z));  
                }
            }
        }
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
}
