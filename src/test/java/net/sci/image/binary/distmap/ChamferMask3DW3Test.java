/**
 * 
 */
package net.sci.image.binary.distmap;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray3D;
import net.sci.array.scalar.ScalarArray3D;

/**
 * @author dlegland
 *
 */
public class ChamferMask3DW3Test
{
    @Test
    public final void test_distanceMap()
    {
        // generate test image
        BinaryArray3D image = BinaryArray3D.create(20, 20, 20);
        image.fillBooleans((x,y,z) -> x > 1 && x < 19 && y > 1 && y < 19 && z > 1 && z < 19);
        
        // create algorithm
        ChamferMask3D mask = new ChamferMask3DW3(3, 4, 5);
        ChamferDistanceTransform3D algo = new ChamferDistanceTransform3DUInt16(mask, true);
        
        // compute distance map
        ScalarArray3D<?> result = algo.process3d(image);
        
        // maximum value is in the middle of result
        double middle = result.getValue(10, 10, 10);
        assertEquals(9, middle, 0.1);
    }

    @Test
    public final void test_distanceMap_fromCenter()
    {
        // generate test image
        BinaryArray3D image = BinaryArray3D.create(11, 11, 11);
        image.fill(true);
        image.setBoolean(5, 5, 5, false);
        
        // create algorithm
        ChamferMask3D mask = new ChamferMask3DW3(3, 4, 5);
        ChamferDistanceTransform3D algo = new ChamferDistanceTransform3DUInt16(mask, true);
        
        // compute distance map
        ScalarArray3D<?> result = algo.process3d(image);
        
        assertEquals(1, result.getValue(4, 5, 5), 0.1);
        assertEquals(1, result.getValue(6, 5, 5), 0.1);
        assertEquals(1, result.getValue(4, 4, 5), 0.1); // rounding of 4/3
        assertEquals(Math.round(5.0 / 3.0), result.getValue(4, 4, 4), 0.1);

        // Test some voxels at the cube corners
        int exp = (int) Math.round(5 * 5.0 / 3.0);
        assertEquals(exp, result.getValue( 0,  0,  0), 0.01);
        assertEquals(exp, result.getValue(10,  0,  0), 0.01);
        assertEquals(exp, result.getValue( 0, 10,  0), 0.01);
        assertEquals(exp, result.getValue(10, 10,  0), 0.01);
        assertEquals(exp, result.getValue( 0,  0, 10), 0.01);
        assertEquals(exp, result.getValue(10,  0, 10), 0.01);
        assertEquals(exp, result.getValue( 0, 10, 10), 0.01);
        assertEquals(exp, result.getValue(10, 10, 10), 0.01);
    }
}
