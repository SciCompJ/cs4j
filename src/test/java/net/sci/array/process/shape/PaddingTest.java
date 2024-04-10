/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;

/**
 * @author dlegland
 *
 */
public class PaddingTest
{

    @Test
    public final void testBinary2D()
    {
        BinaryArray2D array = BinaryArray2D.create(5, 3);
        array.setBoolean(0, 0, true);
        array.setBoolean(4, 0, true);
        array.setBoolean(1, 1, true);
        array.setBoolean(2, 1, true);
        array.setBoolean(3, 1, true);
        array.setBoolean(0, 2, true);
        array.setBoolean(4, 2, true);
//        System.out.println(array);
        
        BinaryArray2D res = BinaryArray2D.wrap(Padding.padBinary(array, 1, false));
//        BinaryArray2D res = BinaryArray2D.wrap(new Padding.Binary(array, 1, false));
        assertEquals(7, res.size(0));
        assertEquals(5, res.size(1));
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 5; x++)
            {
                assertEquals(array.getBoolean(x, y), res.getBoolean(x+1, y+1));
            }
        }
    }
}
