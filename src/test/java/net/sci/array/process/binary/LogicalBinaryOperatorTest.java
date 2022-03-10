/**
 * 
 */
package net.sci.array.process.binary;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;

/**
 * @author dlegland
 *
 */
public class LogicalBinaryOperatorTest
{
    /**
     * Test method for {@link net.sci.array.process.binary.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_2d()
    {
        // initialize demo arrays
        BinaryArray2D array1 = BinaryArray2D.create(8, 6);
        array1.fillBooleans((x,y) -> x >= 4);
        BinaryArray2D array2 = BinaryArray2D.create(8, 6);
        array2.fillBooleans((x,y) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(7, 0));
        assertTrue(res.getBoolean(0, 5));
        assertFalse(res.getBoolean(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.process.binary.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_3d()
    {
        // initialize demo arrays
        BinaryArray3D array1 = BinaryArray3D.create(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = BinaryArray3D.create(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(7, 0, 0));
        assertTrue(res.getBoolean(0, 5, 0));
        assertFalse(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertTrue(res.getBoolean(7, 0, 3));
        assertTrue(res.getBoolean(0, 5, 3));
        assertFalse(res.getBoolean(7, 5, 3));
    }

    /**
     * Test method for {@link net.sci.array.process.binary.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_2d_output()
    {
        // initialize demo arrays
        BinaryArray2D array1 = BinaryArray2D.create(8, 6);
        array1.fillBooleans((x,y) -> x >= 4);
        BinaryArray2D array2 = BinaryArray2D.create(8, 6);
        array2.fillBooleans((x,y) -> y >= 3);
        // allocate output array
        BinaryArray2D res = BinaryArray2D.create(8, 6);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
        
        // Apply operator
        op.process(array1, array2, res);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(7, 0));
        assertTrue(res.getBoolean(0, 5));
        assertFalse(res.getBoolean(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.process.binary.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_3d_output()
    {
        // initialize demo arrays
        BinaryArray3D array1 = BinaryArray3D.create(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = BinaryArray3D.create(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(7, 0, 0));
        assertTrue(res.getBoolean(0, 5, 0));
        assertFalse(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertTrue(res.getBoolean(7, 0, 3));
        assertTrue(res.getBoolean(0, 5, 3));
        assertFalse(res.getBoolean(7, 5, 3));
    }
}
