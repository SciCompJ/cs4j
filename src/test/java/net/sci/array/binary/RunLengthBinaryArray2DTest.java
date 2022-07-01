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
                assertEquals(array.getBoolean(x, y), converted.getBoolean(x, y));  
            }
        }
    }
    
    
    /**
     * Test method for {@link net.sci.array.binary.RunLengthBinaryArray2D#convert(net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void testConvert_PixelsAtCorners()
    {
        BinaryArray2D array = new BufferedBinaryArray2D(5, 4);
        array.fillBooleans((x,y) -> (x == 0 || x == 4) && (y == 0 || y == 3));
        
        RunLengthBinaryArray2D converted = RunLengthBinaryArray2D.convert(array);
        
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                assertEquals(array.getBoolean(x, y), converted.getBoolean(x, y));  
            }
        }
    }
    

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray2D#complement()}.
     */
    @Test
    public final void testComplement_innerRect()
    {
        BinaryArray2D array = new RunLengthBinaryArray2D(5, 4);
        array.fillBooleans((x,y) -> (x == 2 || x == 3) && (y == 1 || y == 2));
        
        BinaryArray2D comp = array.complement();
        
        assertEquals(comp.size(0), array.size(0));
        assertEquals(comp.size(1), array.size(1));
        
        assertTrue(comp.getBoolean(0, 0));
        assertTrue(comp.getBoolean(4, 0));
        assertTrue(comp.getBoolean(0, 3));
        assertTrue(comp.getBoolean(4, 3));
        assertFalse(comp.getBoolean(2, 1));
        assertTrue(comp.getBoolean(0, 1));
        assertTrue(comp.getBoolean(4, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryArray2D#complement()}.
     */
    @Test
    public final void testComplement_corners()
    {
        BinaryArray2D array = new RunLengthBinaryArray2D(5, 4);
        array.setBoolean(0, 0, true);
        array.setBoolean(4, 0, true);
        array.setBoolean(0, 3, true);
        array.setBoolean(4, 3, true);
        
        BinaryArray2D comp = array.complement();
        
        assertEquals(comp.size(0), array.size(0));
        assertEquals(comp.size(1), array.size(1));
        
        assertFalse(comp.getBoolean(0, 0));
        assertFalse(comp.getBoolean(4, 0));
        assertFalse(comp.getBoolean(0, 3));
        assertFalse(comp.getBoolean(4, 3));
        assertTrue(comp.getBoolean(2, 1));
        assertTrue(comp.getBoolean(2, 0));
        assertTrue(comp.getBoolean(2, 3));
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
}
