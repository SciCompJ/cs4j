/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.*;

import net.sci.array.Array;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.Image;
import net.sci.image.ImageType;

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
        Image image = new Image(array, ImageType.BINARY);
        
        Image res = MorphologicalReconstruction.killBorders(image);

        assertTrue(res.getType() == ImageType.BINARY);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders(net.sci.image.Image)}.
     */
    @Test
    public final void testKillBordersImage_Label()
    {
        UInt8Array2D array = create_UInt8Array2D_NineSquares();
        Image image = new Image(array, ImageType.LABEL);
        
        Image res = MorphologicalReconstruction.killBorders(image);

        assertTrue(res.getType() == ImageType.LABEL);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testKillBorders_BinaryArray2D()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares();

        BinaryArray2D res = (BinaryArray2D) MorphologicalReconstruction.killBorders2d(array);

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
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#killBorders2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testKillBorders_UInt8Array2D()
    {
        UInt8Array2D array = create_UInt8Array2D_NineSquares();

        UInt8Array2D res = (UInt8Array2D) MorphologicalReconstruction.killBorders2d(array);

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
        Image image = new Image(array, ImageType.BINARY);
        
        Image res = MorphologicalReconstruction.fillHoles(image);

        assertTrue(res.getType() == ImageType.BINARY);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHoles_Array_UInt8_2D()
    {
        UInt8Array2D array = UInt8Array2D.fromIntArray(new int[][] {
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10,  0, 10,  0,  0,  0, 20,  0,  0,  0, 10},
            { 10,  0, 40, 40, 40, 40, 40, 40, 40,  0, 10},
            { 10,  0, 40,  0,  5,  0,  9,  0, 40,  0, 10},
            { 10,  0, 40,  0, 90, 90, 90,  0, 40,  0, 10},
            { 10,  0, 50, 20, 90, 10, 90, 10, 40,  0, 10},
            { 10,  0, 40,  0, 90, 80, 90,  0, 40,  0, 10},
            { 10,  0, 40,  0,  0, 10,  0,  0, 40,  0, 10},
            { 10,  0, 40, 40, 40, 40, 40, 40, 40,  0, 10},
            { 10,  0, 40,  0,  0,  0, 30,  0,  0,  0, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
        });
        
        Array<?> res = MorphologicalReconstruction.fillHoles(array);

        assertEquals(res.dimensionality(), 2);
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.elementClass(), UInt8.class);
        
        // cast
        UInt8Array2D res2 = UInt8Array2D.wrap(UInt8Array.wrap(res));
//        res2.printContent();
        
        // check borders
        assertEquals(10, res2.getInt(5, 0));
        assertEquals(10, res2.getInt(0, 5));
        assertEquals(10, res2.getInt(5, 10));
        assertEquals(10, res2.getInt(10, 5));
        // check first inner ring
        assertEquals(10, res2.getInt(5, 1));
        assertEquals(10, res2.getInt(1, 5));
        assertEquals(10, res2.getInt(5, 9));
        assertEquals(10, res2.getInt(9, 5));
        // check second inner ring
        assertEquals(40, res2.getInt(5, 3));
        assertEquals(40, res2.getInt(3, 5));
        assertEquals(40, res2.getInt(5, 7));
        assertEquals(40, res2.getInt(7, 5));
        // inner-most ring
        assertEquals(80, res2.getInt(5, 5));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHoles_Array_UInt8_3D()
    {
        int[] values = new int[] {10, 0, 50, 0, 90, 0};
        int size = values.length * 2 - 1;
        
        UInt8Array3D array = UInt8Array3D.create(size, size, size);
        for (int[] pos : array.positions())
        {
            int minDist = size;
            for (int d = 0; d < 3; d++)
            {
                int dist = Math.min(pos[d], size - 1 - pos[d]);
                minDist = Math.min(minDist, dist);
            }
            array.setInt(pos, values[minDist]);
        }
        
//        UInt8Array3D.wrap(UInt8Array.wrap(array)).printContent();
        
        Array<?> res = MorphologicalReconstruction.fillHoles(array);

        assertEquals(res.dimensionality(), 3);
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.size(2), array.size(2));
        assertEquals(res.elementClass(), UInt8.class);
        
        // cast
        UInt8Array3D res2 = UInt8Array3D.wrap(UInt8Array.wrap(res));
//        res2.printContent();
        
        // check borders
        assertEquals(10, res2.getInt(5, 5, 0));
        assertEquals(10, res2.getInt(5, 0, 5));
        assertEquals(10, res2.getInt(0, 5, 5));
        assertEquals(10, res2.getInt(5, 5, 10));
        assertEquals(10, res2.getInt(5, 10, 5));
        assertEquals(10, res2.getInt(10, 5, 5));
        // check first inner ring
        assertEquals(10, res2.getInt(1, 5, 5));
        assertEquals(10, res2.getInt(5, 1, 5));
        assertEquals(10, res2.getInt(5, 5, 1));
        assertEquals(10, res2.getInt(9, 5, 5));
        assertEquals(10, res2.getInt(5, 9, 5));
        assertEquals(10, res2.getInt(5, 5, 9));
        // check second inner ring
        assertEquals(50, res2.getInt(3, 5, 5));
        assertEquals(50, res2.getInt(5, 3, 5));
        assertEquals(50, res2.getInt(5, 5, 3));
        assertEquals(50, res2.getInt(7, 5, 5));
        assertEquals(50, res2.getInt(5, 7, 5));
        assertEquals(50, res2.getInt(5, 5, 7));
        // inner-most ring
        assertEquals(90, res2.getInt(5, 5, 5));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHoles_Array_UInt8_4D()
    {
        int nd = 4;
        int[] values = new int[] {10, 0, 50, 0, 90, 0};
        int size = values.length * 2 - 1;
        
        UInt8Array array = UInt8Array.create(size, size, size, size);
        for (int[] pos : array.positions())
        {
            int minDist = size;
            for (int d = 0; d < nd; d++)
            {
                int dist = Math.min(pos[d], size - 1 - pos[d]);
                minDist = Math.min(minDist, dist);
            }
            array.setInt(pos, values[minDist]);
        }
        
        Array<?> res = MorphologicalReconstruction.fillHoles(array);

        assertEquals(res.dimensionality(), nd);
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.size(2), array.size(2));
        assertEquals(res.size(3), array.size(3));
        assertEquals(res.elementClass(), UInt8.class);
        
        // cast
        UInt8Array res2 = UInt8Array.wrap(res);
        
        // check borders
        assertEquals(10, res2.getInt(new int[] {0, 5, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 0, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 0, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 5, 0}));
        assertEquals(10, res2.getInt(new int[] {10, 5, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 10, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 10, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 5, 10}));
        // check first inner ring
        assertEquals(10, res2.getInt(new int[] {1, 5, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 1, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 1, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 5, 1}));
        assertEquals(10, res2.getInt(new int[] {9, 5, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 9, 5, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 9, 5}));
        assertEquals(10, res2.getInt(new int[] {5, 5, 5, 9}));
        // check second inner ring
        assertEquals(50, res2.getInt(new int[] {3, 5, 5, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 3, 5, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 5, 3, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 5, 5, 3}));
        assertEquals(50, res2.getInt(new int[] {7, 5, 5, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 7, 5, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 5, 7, 5}));
        assertEquals(50, res2.getInt(new int[] {5, 5, 5, 7}));
        // inner-most ring
        assertEquals(90, res2.getInt(new int[] {5, 5, 5, 5}));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHoles_Array_Float32_4D()
    {
        int nd = 4;
        int[] values = new int[] {10, 0, 50, 0, 90, 0};
        int size = values.length * 2 - 1;
        
        Float32Array array = Float32Array.create(size, size, size, size);
        for (int[] pos : array.positions())
        {
            int minDist = size;
            for (int d = 0; d < nd; d++)
            {
                int dist = Math.min(pos[d], size - 1 - pos[d]);
                minDist = Math.min(minDist, dist);
            }
            array.setValue(pos, values[minDist]);
        }
        
        Array<?> res = MorphologicalReconstruction.fillHoles(array);

        assertEquals(res.dimensionality(), nd);
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.size(2), array.size(2));
        assertEquals(res.size(3), array.size(3));
        assertEquals(res.elementClass(), Float32.class);
        
        // cast
        Float32Array res2 = Float32Array.wrap(res);
        
        // check borders
        assertEquals(values[0], res2.getValue(new int[] {0, 5, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 0, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 0, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 5, 0}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {10, 5, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 10, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 10, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 5, 10}), 0.01);
        // check first inner ring
        assertEquals(values[0], res2.getValue(new int[] {1, 5, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 1, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 1, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 5, 1}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {9, 5, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 9, 5, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 9, 5}), 0.01);
        assertEquals(values[0], res2.getValue(new int[] {5, 5, 5, 9}), 0.01);
        // check second inner ring
        assertEquals(values[2], res2.getValue(new int[] {3, 5, 5, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 3, 5, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 5, 3, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 5, 5, 3}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {7, 5, 5, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 7, 5, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 5, 7, 5}), 0.01);
        assertEquals(values[2], res2.getValue(new int[] {5, 5, 5, 7}), 0.01);
        // inner-most ring
        assertEquals(values[4], res2.getValue(new int[] {5, 5, 5, 5}), 0.01);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles(net.sci.image.Image)}.
     */
    @Test
    public final void testFillHoles_Array_Float32_2D()
    {
        Float32Array2D array = Float32Array2D.fromFloatArray(new float[][] {
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10,  0, 10,  0,  0,  0, 20,  0,  0,  0, 10},
            { 10,  0, 40, 40, 40, 40, 40, 40, 40,  0, 10},
            { 10,  0, 40,  0,  5,  0,  9,  0, 40,  0, 10},
            { 10,  0, 40,  0, 90, 90, 90,  0, 40,  0, 10},
            { 10,  0, 50, 20, 90, 10, 90, 10, 40,  0, 10},
            { 10,  0, 40,  0, 90, 80, 90,  0, 40,  0, 10},
            { 10,  0, 40,  0,  0, 10,  0,  0, 40,  0, 10},
            { 10,  0, 40, 40, 40, 40, 40, 40, 40,  0, 10},
            { 10,  0, 40,  0,  0,  0, 30,  0,  0,  0, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
        });
        
        Array<?> res = MorphologicalReconstruction.fillHoles(array);

        assertEquals(res.dimensionality(), 2);
        assertEquals(res.size(0), array.size(0));
        assertEquals(res.size(1), array.size(1));
        assertEquals(res.elementClass(), Float32.class);
        
        // cast
        Float32Array2D res2 = Float32Array2D.wrap(Float32Array.wrap(res));
//        res2.printContent();
        
        // check borders
        assertEquals(10, res2.getValue(5, 0), 0.01);
        assertEquals(10, res2.getValue(0, 5), 0.01);
        assertEquals(10, res2.getValue(5, 10), 0.01);
        assertEquals(10, res2.getValue(10, 5), 0.01);
        // check first inner ring
        assertEquals(10, res2.getValue(5, 1), 0.01);
        assertEquals(10, res2.getValue(1, 5), 0.01);
        assertEquals(10, res2.getValue(5, 9), 0.01);
        assertEquals(10, res2.getValue(9, 5), 0.01);
        // check second inner ring
        assertEquals(40, res2.getValue(5, 3), 0.01);
        assertEquals(40, res2.getValue(3, 5), 0.01);
        assertEquals(40, res2.getValue(5, 7), 0.01);
        assertEquals(40, res2.getValue(7, 5), 0.01);
        // inner-most ring
        assertEquals(80, res2.getValue(5, 5), 0.01);
    }

    /**
     * Test method for {@link net.sci.image.morphology.MorphologicalReconstruction#fillHoles2d(net.sci.array.scalar.ScalarArray2D)}.
     */
    @Test
    public final void testFillHoles_BinaryArray2D()
    {
        BinaryArray2D array = create_BinaryArray2D_NineSquares().complement();

        BinaryArray2D res = (BinaryArray2D) MorphologicalReconstruction.fillHoles2d(array);

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
                array.setBoolean(ix,     iy, true);
                array.setBoolean(ix + 4, iy, true);
                array.setBoolean(ix + 8, iy, true);
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
                array.setInt(ix,     iy    , 1);
                array.setInt(ix + 4, iy    , 2);
                array.setInt(ix + 8, iy    , 3);
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
