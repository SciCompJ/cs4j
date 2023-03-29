/**
 * 
 */
package net.sci.array.process.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BufferedBinaryArray2D;
import net.sci.array.binary.BufferedBinaryArray3D;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;

/**
 * @author dlegland
 *
 */
public class ComplementTest
{
    /**
     * Test method for {@link net.sci.array.process.binary.Complement#process(net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_2d_Buffered()
    {
        BufferedBinaryArray2D array = new BufferedBinaryArray2D(8, 6);
        array.setBoolean(2, 1, true);
        array.setBoolean(3, 1, true);
        array.setBoolean(4, 1, true);
        array.setBoolean(3, 2, true);
        array.setBoolean(2, 3, true);
        array.setBoolean(3, 3, true);
        array.setBoolean(4, 3, true);
        array.setBoolean(5, 3, true);
        array.setBoolean(2, 5, true);

        BinaryArray2D res = BinaryArray2D.wrap(new Complement().process(array));
        
        assertEquals(8, res.size(0));
        assertEquals(6, res.size(1));
        
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                assertNotEquals(res.getBoolean(x, y), array.getBoolean(x, y));
            }
        }
    }

    /**
     * Test method for {@link net.sci.array.process.binary.Complement#process(net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_2d_RunLength()
    {
        RunLengthBinaryArray2D array = new RunLengthBinaryArray2D(8, 6);
        array.setBoolean(2, 1, true);
        array.setBoolean(3, 1, true);
        array.setBoolean(4, 1, true);
        array.setBoolean(3, 2, true);
        array.setBoolean(2, 3, true);
        array.setBoolean(3, 3, true);
        array.setBoolean(4, 3, true);
        array.setBoolean(5, 3, true);
        array.setBoolean(2, 5, true);

        BinaryArray res = new Complement().process(array);
        BinaryArray2D res2d = BinaryArray2D.wrap(res);
        
        assertEquals(8, res2d.size(0));
        assertEquals(6, res2d.size(1));
        
        for (int y = 0; y < 6; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                assertNotEquals(res2d.getBoolean(x, y), array.getBoolean(x, y));
            }
        }
    }

    /**
     * Test method for {@link net.sci.array.process.binary.Complement#process(net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_3d_Buffered()
    {
        BufferedBinaryArray3D array = new BufferedBinaryArray3D(8, 6, 4);
        array.fillBooleans((x,y,z) -> x >= 1 && x <= 6 && y >= 1 && y <= 4 && y >= 1 && y <= 2);

        BinaryArray res = new Complement().process(array);
        BinaryArray3D res2d = BinaryArray3D.wrap(res);
        
        assertEquals(8, res2d.size(0));
        assertEquals(6, res2d.size(1));
        assertEquals(4, res2d.size(2));
        
        for (int z = 0; z < 4; z++)
        {
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    assertNotEquals(res2d.getBoolean(x, y, z), array.getBoolean(x, y, z));
                }
            }
        }
    }

    /**
     * Test method for {@link net.sci.array.process.binary.Complement#process(net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_3d_RunLength()
    {
        RunLengthBinaryArray3D array = new RunLengthBinaryArray3D(8, 6, 4);
        array.fillBooleans((x,y,z) -> x >= 1 && x <= 6 && y >= 1 && y <= 4 && y >= 1 && y <= 2);

        BinaryArray res = new Complement().process(array);
        BinaryArray3D res2d = BinaryArray3D.wrap(res);
        
        assertEquals(8, res2d.size(0));
        assertEquals(6, res2d.size(1));
        assertEquals(4, res2d.size(2));
        
        for (int z = 0; z < 4; z++)
        {
            for (int y = 0; y < 6; y++)
            {
                for (int x = 0; x < 8; x++)
                {
                    assertNotEquals(res2d.getBoolean(x, y, z), array.getBoolean(x, y, z));
                }
            }
        }
    }


}
