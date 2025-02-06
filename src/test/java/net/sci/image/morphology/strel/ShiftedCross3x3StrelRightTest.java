/**
 * 
 */
package net.sci.image.morphology.strel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.image.morphology.Strel;

/**
 * 
 */
public class ShiftedCross3x3StrelRightTest
{
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#size()}.
     */
    @Test
    public final void test_size()
    {
        Strel strel = ShiftedCross3x3Strel.RIGHT;
        int[] size = strel.size();
        assertEquals(size[0], 3);
        assertEquals(size[1], 3);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#reverse()}.
     */
    @Test
    public final void test_reverse()
    {
        Strel strel = ShiftedCross3x3Strel.RIGHT;
        assertEquals(strel.reverse(), ShiftedCross3x3Strel.LEFT);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#binaryMask()}.
     */
    @Test
    public final void test_binaryMask()
    {
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        BinaryArray2D mask = strel.binaryMask();
        assertEquals(mask.size(0), 3);
        assertEquals(mask.size(1), 3);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#shifts()}.
     */
    @Test
    public final void test_shifts()
    {
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        int[][] shifts = strel.shifts();
        assertEquals(5, shifts.length);
        assertEquals(2, shifts[1].length);
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#dilation(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void test_dilation_square4x4()
    {
        UInt8Array2D array = create_UInt8Array2D_10x10_Square4x4();
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        
        UInt8Array2D expected = UInt8Array2D.fromIntArray(new int[][] {
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0, 255, 255, 255, 255,   0,   0,   0,   0}, 
            {  0, 255, 255, 255, 255, 255, 255,   0,   0,   0}, 
            {  0, 255, 255, 255, 255, 255, 255,   0,   0,   0}, 
            {  0, 255, 255, 255, 255, 255, 255,   0,   0,   0}, 
            {  0, 255, 255, 255, 255, 255, 255,   0,   0,   0}, 
            {  0,   0, 255, 255, 255, 255,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
        });
        
        UInt8Array2D result = UInt8Array2D.wrap(UInt8Array.wrap(strel.dilation(array)));
        
        for (int[] pos : result.positions())
        {
            int v1 = expected.getInt(pos);
            int v2 = result.getInt(pos);
            assertEquals(v1, v2);
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#dilation(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void test_dilation_square4x4_float32()
    {
        Float32Array2D array = create_Float32Array2D_10x10_Square4x4();
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        
        Float32Array2D expected = Float32Array2D.fromFloatArray(new float[][] {
            {  0,    0,    0,    0,    0,    0,    0,    0,    0,    0}, 
            {  0,    0,    0,    0,    0,    0,    0,    0,    0,    0}, 
            {  0,    0, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0,    0}, 
            {  0, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0}, 
            {  0, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0}, 
            {  0, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0}, 
            {  0, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0}, 
            {  0,    0, 3.2f, 3.2f, 3.2f, 3.2f,    0,    0,    0,    0}, 
            {  0,    0,    0,    0,    0,    0,    0,    0,    0,    0}, 
            {  0,    0,    0,    0,    0,    0,    0,    0,    0,    0}, 
        });
        
        Float32Array2D result = Float32Array2D.wrap(Float32Array.wrap(strel.dilation(array)));
        
        for (int[] pos : result.positions())
        {
            float v1 = expected.getFloat(pos);
            float v2 = result.getFloat(pos);
            assertEquals(v1, v2, 0.01);
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#erosion(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void test_erosion_square4x4()
    {
        UInt8Array2D array = create_UInt8Array2D_10x10_Square4x4();
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        
        UInt8Array2D expected = UInt8Array2D.fromIntArray(new int[][] {
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0, 255, 255,   0,   0,   0,   0,   0}, 
            {  0,   0,   0, 255, 255,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
        });
        
        UInt8Array2D result = UInt8Array2D.wrap(UInt8Array.wrap(strel.erosion(array)));
        
        for (int[] pos : result.positions())
        {
            int v1 = expected.getInt(pos);
            int v2 = result.getInt(pos);
            assertEquals(v1, v2);
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.strel.ShiftedCross3x3Strel.Right#erosion(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void test_erosion_square4x4_float32()
    {
        Float32Array2D array = create_Float32Array2D_10x10_Square4x4();
        Strel2D strel = ShiftedCross3x3Strel.RIGHT;
        
        Float32Array2D expected = Float32Array2D.fromFloatArray(new float[][] {
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0, 3.2f, 3.2f,   0,   0,   0,   0,   0}, 
            {  0,   0,   0, 3.2f, 3.2f,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
            {  0,   0,   0,   0,   0,   0,   0,   0,   0,   0}, 
        });
        
        Float32Array2D result = Float32Array2D.wrap(Float32Array.wrap(strel.erosion(array)));
        
        for (int[] pos : result.positions())
        {
            float v1 = expected.getFloat(pos);
            float v2 = result.getFloat(pos);
            assertEquals(v1, v2, 0.01);
        }
    }

    private UInt8Array2D create_UInt8Array2D_10x10_Square4x4()
    {
        UInt8Array2D array = UInt8Array2D.create(10, 10);
        
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                array.setInt(x, y, 255);
            }
        }
        
        return array;
    }

    private Float32Array2D create_Float32Array2D_10x10_Square4x4()
    {
        Float32Array2D array = Float32Array2D.create(10, 10);
        
        for (int y = 3; y < 7; y++)
        {
            for (int x = 3; x < 7; x++)
            {
                array.setFloat(x, y, 3.2f);
            }
        }
        
        return array;
    }
}
