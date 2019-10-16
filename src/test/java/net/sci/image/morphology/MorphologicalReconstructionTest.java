/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.*;
import net.sci.array.scalar.BinaryArray2D;
import net.sci.array.scalar.UInt8Array2D;
import net.sci.image.Image;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class MorphologicalReconstructionTest
{

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders(net.sci.image.Image)}.
     */
    @Test
    public final void testKillBordersImage_Binary()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares();
        Image image = new Image(array, Image.Type.BINARY);
        
        Image res = MorphologicalReconstruction.killBorders(image);

        assertTrue(res.getType() == Image.Type.BINARY);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders(net.sci.image.Image)}.
     */
    @Test
    public final void testKillBordersImage_Label()
    {
        UInt8Array2D array = create_UInt8Array2D_NineSquares();
        Image image = new Image(array, Image.Type.LABEL);
        
        Image res = MorphologicalReconstruction.killBorders(image);

        assertTrue(res.getType() == Image.Type.LABEL);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testKillBorders_BinaryArray2D()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares();

        BinaryArray2D res = (BinaryArray2D) MorphologicalReconstruction.killBorders(array);

        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(0, 4));
        assertFalse(res.getBoolean(0, 8));
        assertFalse(res.getBoolean(4, 0));
        assertTrue(res.getBoolean(4, 4));
        assertFalse(res.getBoolean(4, 8));
        assertFalse(res.getBoolean(8, 0));
        assertFalse(res.getBoolean(8, 4));
        assertFalse(res.getBoolean(8, 8));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testKillBorders_UInt8Array2D()
    {
        UInt8Array2D array = create_UInt8Array2D_NineSquares();

        UInt8Array2D res = (UInt8Array2D) MorphologicalReconstruction.killBorders(array);

        assertEquals(res.getInt(0, 0), 0);
        assertEquals(res.getInt(0, 4), 0);
        assertEquals(res.getInt(0, 8), 0);
        assertEquals(res.getInt(4, 0), 0);
        assertEquals(res.getInt(4, 4), 5);
        assertEquals(res.getInt(4, 8), 0);
        assertEquals(res.getInt(8, 0), 0);
        assertEquals(res.getInt(8, 4), 0);
        assertEquals(res.getInt(8, 8), 0);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHolesImage_Binary()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares();
        Image image = new Image(array, Image.Type.BINARY);
        
        Image res = MorphologicalReconstruction.fillHoles(image);

        assertTrue(res.getType() == Image.Type.BINARY);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testFillHoles_BinaryArray2D()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares().complement();

        BinaryArray2D res = (BinaryArray2D) MorphologicalReconstruction.fillHoles(array);

        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(0, 4));
        assertFalse(res.getBoolean(0, 8));
        assertFalse(res.getBoolean(4, 0));
        assertTrue(res.getBoolean(4, 4));
        assertFalse(res.getBoolean(4, 8));
        assertFalse(res.getBoolean(8, 0));
        assertFalse(res.getBoolean(8, 4));
        assertFalse(res.getBoolean(8, 8));
    }

    /**
     * Creates an instance of BinaryArray2D with size 10x10 that contains nine
     * 2x2 squares.
     * 
     * Squares are located at each corner, on each side, and in the middle of
     * the array.
     * 
     * @return a 10x10 BinaryArray2D containing nine 2x2 regions
     */
    private static final BinaryArray2D create_BinaryArray2D_NineSquares()
    {
        BinaryArray2D array = BinaryArray2D.create(10,  10);
        for (int iy = 0; iy <  2; iy++)
        {
            for (int ix = 0; ix <  2; ix++)
            {
                array.setBoolean(ix,     iy,     true);
                array.setBoolean(ix + 4, iy,     true);
                array.setBoolean(ix + 8, iy,     true);
                array.setBoolean(ix,     iy + 4, true);
                array.setBoolean(ix + 4, iy + 4, true);
                array.setBoolean(ix + 8, iy + 4, true);
                array.setBoolean(ix,     iy + 8, true);
                array.setBoolean(ix + 4, iy + 8, true);
                array.setBoolean(ix + 8, iy + 8, true);
            }
        }
        return array;
    }
    
    /**
     * Creates an instance of UInt8Array2D with size 10x10 that contains nine
     * 2x2 squares with different values.
     * 
     * Squares are located at each corner, on each side, and in the middle of
     * the array. Values range from 1 to 9.
     * 
     * @return a 10x10 BinaryArray2D containing nine 2x2 regions
     */
    private static final UInt8Array2D create_UInt8Array2D_NineSquares()
    {
        UInt8Array2D array = UInt8Array2D.create(10,  10);
        for (int iy = 0; iy <  2; iy++)
        {
            for (int ix = 0; ix <  2; ix++)
            {
                array.setInt(ix,     iy,     1);
                array.setInt(ix + 4, iy,     2);
                array.setInt(ix + 8, iy,     3);
                array.setInt(ix,     iy + 4, 4);
                array.setInt(ix + 4, iy + 4, 5);
                array.setInt(ix + 8, iy + 4, 6);
                array.setInt(ix,     iy + 8, 7);
                array.setInt(ix + 4, iy + 8, 8);
                array.setInt(ix + 8, iy + 8, 9);
            }
        }
        return array;
    }
}
