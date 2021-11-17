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
public class RunLengthBinaryArray2DTest
{
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#convert(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testConvert()
    {
        BinaryArray2D array = new BufferedBinaryArray2D(12, 12);
        fillRect(array, 2, 10, 2, 10, true);
        fillRect(array, 4, 8, 4, 8, false);
        array.setBoolean(6, 6, true);
        
        RunLengthBinaryArray2D converted = RunLengthBinaryArray2D.convert(array);
        
        for (int y = 0; y < 12; y++)
        {
            for (int x = 0; x < 12; x++)
            {
                assertTrue(array.getBoolean(x, y) == converted.getBoolean(x, y));  
            }
        }
    }
    
    
    private static final void fillRect(BinaryArray2D array, int xmin, int xmax, int ymin, int ymax, boolean state)
    {
        for (int y = ymin; y <= ymax; y++)
        {
            for (int x = xmin; x <= xmax; x++)
            {
                array.setBoolean(x, y, state);
            }
        }
    }
    
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#dilation( net.sci.array.binary.RunLengthBinaryArray2D, net.sci.array.binary.RunLengthBinaryArray2D, int[])}.
     */
    @Test
    public final void testIterator()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(5, 4);
        array.fill(true);
        
        int count = 0;
        for (Binary binary : array)
        {
            if (binary.getBoolean())
            {
                count++;
            }
        }
        
        assertEquals(20, count);
    }
}
