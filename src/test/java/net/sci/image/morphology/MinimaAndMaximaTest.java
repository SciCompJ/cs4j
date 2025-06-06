/**
 * 
 */
package net.sci.image.morphology;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.connectivity.Connectivity3D;

/**
 * 
 */
public class MinimaAndMaximaTest
{
    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMaxima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMaxima2d(image, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(9, 1));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(10, 10));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMaxima_ScalarArray2D_C8()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 10, 10, 10, 40, 40, 40, 10, 10, 10, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 20, 20, 20, 10, 10, 10, 30, 30, 30, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMaxima2d(image, Connectivity2D.C8);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertFalse(maxima.getBoolean(1, 1));
        assertFalse(maxima.getBoolean(9, 1));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(10, 10));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMinima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMinima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            {50, 50, 50, 50, 50},
            {50, 10, 50, 50, 50},
            {50, 50, 10, 50, 50},
            {50, 50, 50, 10, 50},
            {50, 50, 20, 30, 50},
            {50, 50, 50, 50, 50},
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMinima2d(image, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(2, 2));
        assertTrue(maxima.getBoolean(3, 3));
        assertTrue(maxima.getBoolean(2, 4));
        assertFalse(maxima.getBoolean(3, 4));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMinima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testRegionalMinima_ScalarArray2D_C8()
    {
        int[][] data = new int[][]{
            {50, 50, 50, 50, 50},
            {50, 10, 50, 50, 50},
            {50, 50, 10, 50, 50},
            {50, 50, 50, 10, 50},
            {50, 50, 20, 30, 50},
            {50, 50, 50, 50, 50},
        };
        
        UInt8Array2D image = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.regionalMinima2d(image, Connectivity2D.C8);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(1, 1));
        assertTrue(maxima.getBoolean(2, 2));
        assertTrue(maxima.getBoolean(3, 3));
        assertFalse(maxima.getBoolean(2, 4));
        assertFalse(maxima.getBoolean(3, 4));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMaxima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 40, 40, 40, 40, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 35, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 50, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 35, 35, 35, 40, 10, 10, 10},
            { 10, 10, 10, 40, 40, 40, 40, 40, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            { 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10}
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D maxima = MinimaAndMaxima.extendedMaxima2d(array, 20, Connectivity2D.C4);
        
        assertFalse(maxima.getBoolean(0, 0));
        assertTrue(maxima.getBoolean(3, 3));
        assertTrue(maxima.getBoolean(7, 3));
        assertTrue(maxima.getBoolean(5, 5));
        assertFalse(maxima.getBoolean(9, 9));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray2D_C4()
    {
        int[][] data = new int[][]{
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 20, 20, 20, 20, 20, 50, 50},
            { 50, 50, 20, 30, 30, 30, 20, 50, 50},
            { 50, 50, 20, 30, 10, 30, 20, 50, 50},
            { 50, 50, 20, 30, 30, 30, 20, 50, 50},
            { 50, 50, 20, 20, 20, 20, 20, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
            { 50, 50, 50, 50, 50, 50, 50, 50, 50},
        };
        UInt8Array2D array = UInt8Array2D.fromIntArray(data);
        
        BinaryArray2D minima = MinimaAndMaxima.extendedMinima2d(array, 30, Connectivity2D.C4);
        
        assertFalse(minima.getBoolean(0, 0));
        assertTrue(minima.getBoolean(2, 2));
        assertTrue(minima.getBoolean(6, 2));
        assertTrue(minima.getBoolean(4, 4));
        assertTrue(minima.getBoolean(2, 6));
        assertTrue(minima.getBoolean(6, 6));
        assertFalse(minima.getBoolean(7, 7));
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray2D_simpleProfile_H10()
    {
        UInt8Array2D array = createSimpleProfileArrray2D();
        
        BinaryArray2D minima = MinimaAndMaxima.extendedMinima2d(array, 10, Connectivity2D.C4);
        
        boolean[] expH10 = new boolean[] {false, true, false, false, false, true, false, true, false, false, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima.getBoolean(x, 2), expH10[x]);
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima2d(net.sci.array.numeric.ScalarArray2D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray2D_simpleProfile_H20()
    {
        UInt8Array2D array = createSimpleProfileArrray2D();
        
        BinaryArray2D minima = MinimaAndMaxima.extendedMinima2d(array, 20, Connectivity2D.C4);
        
        boolean[] expH10 = new boolean[] {false, true, false, false, false, false, false, true, false, false, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima.getBoolean(x, 2), expH10[x]);
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima3d(net.sci.array.numeric.ScalarArray3D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray3D_simpleProfile_H10()
    {
        UInt8Array3D array = createSimpleProfileArrray3D();
        
        BinaryArray3D minima = MinimaAndMaxima.extendedMinima3d(array, 10, Connectivity3D.C6);
        
        boolean[] expH10 = new boolean[] {false, true, false, false, false, true, false, true, false, false, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima.getBoolean(x, 2, 2), expH10[x]);
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.MinimaAndMaxima#regionalMaxima3d(net.sci.array.numeric.ScalarArray3D)}.
     */
    @Test
    public final void testExtendedMinima_ScalarArray3D_simpleProfile_H20()
    {
        UInt8Array3D array = createSimpleProfileArrray3D();
        
        BinaryArray3D minima = MinimaAndMaxima.extendedMinima3d(array, 20, Connectivity3D.C6);
        
        boolean[] expH10 = new boolean[] {false, true, false, false, false, false, false, true, false, false, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima.getBoolean(x, 2, 2), expH10[x]);
        }
    }
    
    @Test
    public final void testImposeMinima_simpleProfile()
    {
        UInt8Array2D array = createSimpleProfileArrray2D();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(3, 2, true);
        minima.setBoolean(9, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMinima2d(array, minima, Connectivity2D.C4);
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);

        boolean[] exp = new boolean[] {false, false, false, true, false, false, false, false, false, true, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima2.getBoolean(x, 2), exp[x]);
        }
    }
    
    @Test
    public final void testImposeMinima_ramp_uint8()
    {
        UInt8Array2D array = create_ramp_7x5_UInt8();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMinima2d(array, minima, Connectivity2D.C4);

        // check markers correspond to minimal values
        assertEquals(0.0, res.getValue(2, 2), 0.01);
        assertEquals(0.0, res.getValue(5, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    @Test
    public final void testImposeMinima_ramp_float32()
    {
        Float32Array2D array = create_ramp_7x5_Float32();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMinima2d(array, minima, Connectivity2D.C4);
        
        // check markers correspond to minimal values
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);
        assertFalse(minima2.getBoolean(1, 2));
        assertTrue(minima2.getBoolean(2, 2));
        assertFalse(minima2.getBoolean(3, 2));
        assertFalse(minima2.getBoolean(4, 2));
        assertTrue(minima2.getBoolean(5, 2));
        assertFalse(minima2.getBoolean(6, 2));
        
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    @Test
    public final void testImposeMinima_ramp_float64()
    {
        Float64Array2D array = create_ramp_7x5_Float64();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMinima2d(array, minima, Connectivity2D.C4);
        
        // check markers correspond to minimal values
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);
        assertFalse(minima2.getBoolean(1, 2));
        assertTrue(minima2.getBoolean(2, 2));
        assertFalse(minima2.getBoolean(3, 2));
        assertFalse(minima2.getBoolean(4, 2));
        assertTrue(minima2.getBoolean(5, 2));
        assertFalse(minima2.getBoolean(6, 2));
        
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    @Test
    public final void testImposeMinima_ramp3d_uint8()
    {
        UInt8Array3D array = create_ramp_7x5x5_UInt8();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = MinimaAndMaxima.imposeMinima3d(array, minima, Connectivity3D.C6);
    
        // check markers correspond to minimal values
        assertEquals(0.0, res.getValue(2, 2, 2), 0.01);
        assertEquals(0.0, res.getValue(5, 2, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }

    @Test
    public final void testImposeMinima_ramp3d_float32()
    {
        Float32Array3D array = create_ramp_7x5x5_Float32();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = MinimaAndMaxima.imposeMinima3d(array, minima, Connectivity3D.C6);
    
        // check markers correspond to minimal values
        BinaryArray3D minima2 = MinimaAndMaxima.regionalMinima3d(res, Connectivity3D.C6);
        assertFalse(minima2.getBoolean(1, 2, 2));
        assertTrue(minima2.getBoolean(2, 2, 2));
        assertFalse(minima2.getBoolean(3, 2, 2));
        assertFalse(minima2.getBoolean(4, 2, 2));
        assertTrue(minima2.getBoolean(5, 2, 2));
        assertFalse(minima2.getBoolean(6, 2, 2));
    
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }

    @Test
    public final void testImposeMinima_ramp3d_float64()
    {
        Float64Array3D array = create_ramp_7x5x5_Float64();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = MinimaAndMaxima.imposeMinima3d(array, minima, Connectivity3D.C6);
    
        // check markers correspond to minimal values
        BinaryArray3D minima2 = MinimaAndMaxima.regionalMinima3d(res, Connectivity3D.C6);
        assertFalse(minima2.getBoolean(1, 2, 2));
        assertTrue(minima2.getBoolean(2, 2, 2));
        assertFalse(minima2.getBoolean(3, 2, 2));
        assertFalse(minima2.getBoolean(4, 2, 2));
        assertTrue(minima2.getBoolean(5, 2, 2));
        assertFalse(minima2.getBoolean(6, 2, 2));
    
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }

    @Test
    public final void testImposeMinima_ScalarArray2D_MinimaWithinZeroRegion()
    {
        int[] profile = new int[] {50, 0, 0, 0, 0, 0, 50};
        UInt8Array2D array = UInt8Array2D.create(profile.length, 5);
        array.fillInts((x,y) -> profile[x]);
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(1, 2, true);
        minima.setBoolean(5, 2, true);
    
        ScalarArray2D<?> res = MinimaAndMaxima.imposeMinima2d(array, minima, Connectivity2D.C4);
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);
    
        boolean[] exp = new boolean[] {false, true, false, false, false, true, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima2.getBoolean(x, 2), exp[x]);
        }
    }

    @Test
    public final void testImposeMaxima_ramp_uint8()
    {
        UInt8Array2D array = create_ramp_7x5_UInt8();
        BinaryArray2D maxima = BinaryArray2D.create(array.size(0), array.size(1));
        maxima.setBoolean(2, 2, true);
        maxima.setBoolean(5, 2, true);
    
        ScalarArray2D<?> res = MinimaAndMaxima.imposeMaxima2d(array, maxima, Connectivity2D.C4);
    
        // check markers correspond to maximal values
        assertEquals(255.0, res.getValue(2, 2), 0.01);
        assertEquals(255.0, res.getValue(5, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }

    @Test
    public final void testImposeMaxima_ramp_float32()
    {
        Float32Array2D array = create_ramp_7x5_Float32();
        BinaryArray2D maxima = BinaryArray2D.create(array.size(0), array.size(1));
        maxima.setBoolean(2, 2, true);
        maxima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMaxima2d(array, maxima, Connectivity2D.C4);
        
        // check markers correspond to minimal values
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMaxima2d(res, Connectivity2D.C4);
        assertFalse(minima2.getBoolean(1, 2));
        assertTrue(minima2.getBoolean(2, 2));
        assertFalse(minima2.getBoolean(3, 2));
        assertFalse(minima2.getBoolean(4, 2));
        assertTrue(minima2.getBoolean(5, 2));
        assertFalse(minima2.getBoolean(6, 2));

        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    @Test
    public final void testImposeMaxima_ramp_float64()
    {
        Float64Array2D array = create_ramp_7x5_Float64();
        BinaryArray2D maxima = BinaryArray2D.create(array.size(0), array.size(1));
        maxima.setBoolean(2, 2, true);
        maxima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = MinimaAndMaxima.imposeMaxima2d(array, maxima, Connectivity2D.C4);
        
        // check markers correspond to minimal values
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMaxima2d(res, Connectivity2D.C4);
        assertFalse(minima2.getBoolean(1, 2));
        assertTrue(minima2.getBoolean(2, 2));
        assertFalse(minima2.getBoolean(3, 2));
        assertFalse(minima2.getBoolean(4, 2));
        assertTrue(minima2.getBoolean(5, 2));
        assertFalse(minima2.getBoolean(6, 2));

        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    @Test
    public final void testImposeMaxima_ramp3d_uint8()
    {
        UInt8Array3D array = create_ramp_7x5x5_UInt8();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);

        ScalarArray3D<?> res = MinimaAndMaxima.imposeMaxima3d(array, minima, Connectivity3D.C6);

        // check markers correspond to minimal values
        assertEquals(255.0, res.getValue(2, 2, 2), 0.01);
        assertEquals(255.0, res.getValue(5, 2, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }
    
    @Test
    public final void testImposeMaxima_ramp3d_float32()
    {
        Float32Array3D array = create_ramp_7x5x5_Float32();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);

        ScalarArray3D<?> res = MinimaAndMaxima.imposeMaxima3d(array, minima, Connectivity3D.C6);

        // check markers correspond to maximal values
        BinaryArray3D minima2 = MinimaAndMaxima.regionalMaxima3d(res, Connectivity3D.C6);
        assertFalse(minima2.getBoolean(1, 2, 2));
        assertTrue(minima2.getBoolean(2, 2, 2));
        assertFalse(minima2.getBoolean(3, 2, 2));
        assertFalse(minima2.getBoolean(4, 2, 2));
        assertTrue(minima2.getBoolean(5, 2, 2));
        assertFalse(minima2.getBoolean(6, 2, 2));

        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }
    
    @Test
    public final void testImposeMaxima_ramp3d_float64()
    {
        Float64Array3D array = create_ramp_7x5x5_Float64();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);

        ScalarArray3D<?> res = MinimaAndMaxima.imposeMaxima3d(array, minima, Connectivity3D.C6);

        // check markers correspond to maximal values
        BinaryArray3D minima2 = MinimaAndMaxima.regionalMaxima3d(res, Connectivity3D.C6);
        assertFalse(minima2.getBoolean(1, 2, 2));
        assertTrue(minima2.getBoolean(2, 2, 2));
        assertFalse(minima2.getBoolean(3, 2, 2));
        assertFalse(minima2.getBoolean(4, 2, 2));
        assertTrue(minima2.getBoolean(5, 2, 2));
        assertFalse(minima2.getBoolean(6, 2, 2));

        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }
    
    private UInt8Array2D createSimpleProfileArrray2D()
    {
        int[] values = new int[] {70, 20, 50, 40, 60, 30, 50, 10, 50, 40, 70};
        int nRows = 5;
        UInt8Array2D array = UInt8Array2D.create(values.length, nRows);
        array.fillInts((x,y) -> values[x]);
        return array;
    }
    
    private UInt8Array3D createSimpleProfileArrray3D()
    {
        int[] values = new int[] {70, 20, 50, 40, 60, 30, 50, 10, 50, 40, 70};
        int nRows = 5;
        UInt8Array3D array = UInt8Array3D.create(values.length, nRows, nRows);
        array.fillInts((x,y,z) -> values[x]);
        return array;
    }
    
    private UInt8Array2D create_ramp_7x5_UInt8()
    {
        int[] values = new int[] {10, 20, 30, 40, 50, 60, 70};
        int nRows = 5;
        UInt8Array2D array = UInt8Array2D.create(values.length, nRows);
        array.fillInts((x,y) -> values[x]);
        return array;
    }
    
    private Float32Array2D create_ramp_7x5_Float32()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float32Array2D array = Float32Array2D.create(values.length, nRows);
        array.fillValues((x,y) -> values[x]);
        return array;
    }
    
    private Float64Array2D create_ramp_7x5_Float64()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float64Array2D array = Float64Array2D.create(values.length, nRows);
        array.fillValues((x,y) -> values[x]);
        return array;
    }
    
    private UInt8Array3D create_ramp_7x5x5_UInt8()
    {
        int[] values = new int[] {10, 20, 30, 40, 50, 60, 70};
        int nRows = 5;
        UInt8Array3D array = UInt8Array3D.create(values.length, nRows, nRows);
        array.fillInts((x,y,z) -> values[x]);
        return array;
    }
    
    private Float32Array3D create_ramp_7x5x5_Float32()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float32Array3D array = Float32Array3D.create(values.length, nRows, nRows);
        array.fillValues((x,y,z) -> values[x]);
        return array;
    }

    private Float64Array3D create_ramp_7x5x5_Float64()
    {
        double[] values = new double[] {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7};
        int nRows = 5;
        Float64Array3D array = Float64Array3D.create(values.length, nRows, nRows);
        array.fillValues((x,y,z) -> values[x]);
        return array;
    }

}
