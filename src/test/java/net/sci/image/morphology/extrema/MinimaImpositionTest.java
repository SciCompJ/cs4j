/**
 * 
 */
package net.sci.image.morphology.extrema;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sci.array.Array2D;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Array3D;
import net.sci.array.numeric.ScalarArray;
import net.sci.array.numeric.ScalarArray2D;
import net.sci.array.numeric.ScalarArray3D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.connectivity.Connectivity2D;
import net.sci.image.connectivity.Connectivity3D;
import net.sci.image.morphology.MinimaAndMaxima;

/**
 * 
 */
public class MinimaImpositionTest
{
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processScalar2d_simpleProfile()
    {
        UInt8Array2D array = TestArrays.createSimpleProfileArrray2D();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(3, 2, true);
        minima.setBoolean(9, 2, true);

        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(array, minima);
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);

        boolean[] exp = new boolean[] {false, false, false, true, false, false, false, false, false, true, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima2.getBoolean(x, 2), exp[x]);
        }
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processScalar2d_ramp_uint8()
    {
        UInt8Array2D array = TestArrays.create_ramp_7x5_UInt8();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(array, minima);

        // check markers correspond to minimal values
        assertEquals(0.0, res.getValue(2, 2), 0.01);
        assertEquals(0.0, res.getValue(5, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processScalar2d_ramp_ArrayOfUInt8()
    {
        UInt8Array2D baseArray = TestArrays.create_ramp_7x5_UInt8();
        int[] dims = baseArray.size();
        
        Array2D<UInt8> array = Array2D.create(dims[0], dims[1], UInt8.ZERO);
        array.fill((x,y) -> baseArray.get(x, y));
        BinaryArray2D minima = BinaryArray2D.create(dims[0], dims[1]);
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<UInt8> scalarArray = ScalarArray2D.wrap(ScalarArray.wrap(array));
        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(scalarArray, minima);
        
        // check markers correspond to minimal values
        assertEquals(0.0, res.getValue(2, 2), 0.01);
        assertEquals(0.0, res.getValue(5, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1) > res.getValue(3, 1));
        assertTrue(res.getValue(5, 3) > res.getValue(3, 3));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processScalar2d_ramp_float32()
    {
        Float32Array2D array = TestArrays.create_ramp_7x5_Float32();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(array, minima);
        
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
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar2d(net.sci.array.numeric.ScalarArray2D, net.sci.array.binary.BinaryArray2D)}.
     */
    @Test
    public final void test_processScalar2d_ramp_float64()
    {
        Float64Array2D array = TestArrays.create_ramp_7x5_Float64();
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(2, 2, true);
        minima.setBoolean(5, 2, true);

        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(array, minima);
        
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
    public final void test_processScalar2d_MinimaWithinZeroRegion_uint8()
    {
        int[] profile = new int[] {50, 0, 0, 0, 0, 0, 50};
        UInt8Array2D array = UInt8Array2D.create(profile.length, 5);
        array.fillInts((x,y) -> profile[x]);
        BinaryArray2D minima = BinaryArray2D.create(array.size(0), array.size(1));
        minima.setBoolean(1, 2, true);
        minima.setBoolean(5, 2, true);
    
        ScalarArray2D<?> res = new MinimaImposition(Connectivity2D.C4).processScalar2d(array, minima);
        BinaryArray2D minima2 = MinimaAndMaxima.regionalMinima2d(res, Connectivity2D.C4);
    
        boolean[] exp = new boolean[] {false, true, false, false, false, true, false};
        for (int x = 0; x < array.size(0); x++)
        {
            assertEquals(minima2.getBoolean(x, 2), exp[x]);
        }
    }

    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar3d(net.sci.array.numeric.ScalarArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_processScalar3d_ramp3d_uint8()
    {
        UInt8Array3D array = TestArrays.create_ramp_7x5x5_UInt8();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = new MinimaImposition(Connectivity3D.C6).processScalar3d(array, minima);
    
        // check markers correspond to minimal values
        assertEquals(0.0, res.getValue(2, 2, 2), 0.01);
        assertEquals(0.0, res.getValue(5, 2, 2), 0.01);
        // keep relative ordering of other values
        assertTrue(res.getValue(5, 1, 1) > res.getValue(3, 1, 1));
        assertTrue(res.getValue(5, 3, 1) > res.getValue(3, 3, 1));
    }
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar3d(net.sci.array.numeric.ScalarArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_processScalar3d_ramp3d_float32()
    {
        Float32Array3D array = TestArrays.create_ramp_7x5x5_Float32();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = new MinimaImposition(Connectivity3D.C6).processScalar3d(array, minima);
    
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
    
    /**
     * Test method for {@link net.sci.image.morphology.extrema.MinimaImposition#processScalar3d(net.sci.array.numeric.ScalarArray3D, net.sci.array.binary.BinaryArray3D)}.
     */
    @Test
    public final void test_processScalar3d_ramp3d_float64()
    {
        Float64Array3D array = TestArrays.create_ramp_7x5x5_Float64();
        BinaryArray3D minima = BinaryArray3D.create(array.size(0), array.size(1), array.size(2));
        minima.setBoolean(2, 2, 2, true);
        minima.setBoolean(5, 2, 2, true);
    
        ScalarArray3D<?> res = new MinimaImposition(Connectivity3D.C6).processScalar3d(array, minima);
    
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
}
