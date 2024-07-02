/**
 * 
 */
package net.sci.array.binary.process;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
public class LogicalBinaryOperatorTest
{
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void testProcess_XOR_2d()
    {
        // initialize demo arrays
        BinaryArray2D array1 = BinaryArray2D.create(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = BinaryArray2D.create(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = new LogicalBinaryOperator((a,b) -> a ^ b);
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(6, 4));
        assertFalse(res.getBoolean(3, 2));
        assertFalse(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
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
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
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
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
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
    
    
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_AND_process_2d_buffer()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new BufferedBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new BufferedBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertFalse(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(6, 4));
        assertTrue(res.getBoolean(3, 2));
        assertTrue(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_AND_process_2d_rle()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertFalse(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(6, 4));
        assertTrue(res.getBoolean(3, 2));
        assertTrue(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_AND_process_3d_buffer()
    {
        // initialize demo arrays
        BinaryArray3D array1 = new BufferedBinaryArray3D(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = new BufferedBinaryArray3D(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND;
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(7, 0, 0));
        assertFalse(res.getBoolean(0, 5, 0));
        assertTrue(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertFalse(res.getBoolean(7, 0, 3));
        assertFalse(res.getBoolean(0, 5, 3));
        assertTrue(res.getBoolean(7, 5, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_AND_process_3d_rle()
    {
        // initialize demo arrays
        BinaryArray3D array1 = new RunLengthBinaryArray3D(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = new RunLengthBinaryArray3D(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND;
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertFalse(res.getBoolean(7, 0, 0));
        assertFalse(res.getBoolean(0, 5, 0));
        assertTrue(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertFalse(res.getBoolean(7, 0, 3));
        assertFalse(res.getBoolean(0, 5, 3));
        assertTrue(res.getBoolean(7, 5, 3));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_OR_process_2d_buffer()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new BufferedBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new BufferedBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.OR;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(6, 4));
        assertTrue(res.getBoolean(3, 2));
        assertTrue(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_OR_process_2d_rle()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.OR;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(6, 4));
        assertTrue(res.getBoolean(3, 2));
        assertTrue(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_OR_process_3d_buffer()
    {
        // initialize demo arrays
        BinaryArray3D array1 = new BufferedBinaryArray3D(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = new BufferedBinaryArray3D(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.OR;
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(7, 0, 0));
        assertTrue(res.getBoolean(0, 5, 0));
        assertTrue(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertTrue(res.getBoolean(7, 0, 3));
        assertTrue(res.getBoolean(0, 5, 3));
        assertTrue(res.getBoolean(7, 5, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_OR_process_3d_rle()
    {
        // initialize demo arrays
        BinaryArray3D array1 = new RunLengthBinaryArray3D(8, 6, 4);
        array1.fillBooleans((x,y,z) -> x >= 4);
        BinaryArray3D array2 = new RunLengthBinaryArray3D(8, 6, 4);
        array2.fillBooleans((x,y,z) -> y >= 3);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.OR;
        
        // Apply operator and display result
        BinaryArray3D res = (BinaryArray3D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertEquals(res.size(2), res.size(2));
        assertFalse(res.getBoolean(0, 0, 0));
        assertTrue(res.getBoolean(7, 0, 0));
        assertTrue(res.getBoolean(0, 5, 0));
        assertTrue(res.getBoolean(7, 5, 0));
        assertFalse(res.getBoolean(0, 0, 3));
        assertTrue(res.getBoolean(7, 0, 3));
        assertTrue(res.getBoolean(0, 5, 3));
        assertTrue(res.getBoolean(7, 5, 3));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_buffer()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new BufferedBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new BufferedBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertTrue(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(6, 4));
        assertFalse(res.getBoolean(3, 2));
        assertFalse(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(8, 6);
        array1.fillBooleans((x,y) -> x >=1 && x <= 4 && y >= 1 && y <= 3);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(8, 6);
        array2.fillBooleans((x,y) -> x >=3 && x <= 6 && y >= 2 && y <= 4);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        assertFalse(res.getBoolean(0, 0));
        assertFalse(res.getBoolean(7, 5));
        assertTrue(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(6, 4));
        assertFalse(res.getBoolean(3, 2));
        assertFalse(res.getBoolean(4, 3));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_run2_over_run1()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(20, 4);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(20, 4);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array1.setBoolean(x, y, true);
                array1.setBoolean(x + 6, y, true);
                array1.setBoolean(x + 12, y, true);
                array2.setBoolean(x + 6, y, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // within mask 2 must be false
        assertTrue(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(7, 1));
        assertTrue(res.getBoolean(13, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_runs1_before_runs2()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(20, 4);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(20, 4);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array1.setBoolean(x, y, true);
                array1.setBoolean(x + 6, y, true);
                array2.setBoolean(x + 10, y, true);
                array2.setBoolean(x + 16, y, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // within mask 2 must be false
        assertTrue(res.getBoolean(1, 1));
        assertTrue(res.getBoolean(7, 1));
        assertFalse(res.getBoolean(11, 1));
        assertFalse(res.getBoolean(17, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_runs2_before_runs1()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(20, 4);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(20, 4);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array2.setBoolean(x, y, true);
                array2.setBoolean(x + 6, y, true);
                array1.setBoolean(x + 10, y, true);
                array1.setBoolean(x + 16, y, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // within mask 2 must be false
        assertFalse(res.getBoolean(1, 1));
        assertFalse(res.getBoolean(7, 1));
        assertTrue(res.getBoolean(11, 1));
        assertTrue(res.getBoolean(17, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_run2_between_runs1()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(12, 4);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(12, 4);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array1.setBoolean(x, y, true);
                array1.setBoolean(x + 8, y, true);
                array2.setBoolean(x + 4, y, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // within mask 2 must be false
        assertFalse(res.getBoolean(4, 1));
        assertFalse(res.getBoolean(7, 1));
        // within mask 1 must be true
        assertTrue(res.getBoolean(3, 1));
        assertTrue(res.getBoolean(8, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_run1_between_runs2()
    {
        // initialize demo arrays
        BinaryArray2D array1 = new RunLengthBinaryArray2D(12, 4);
        BinaryArray2D array2 = new RunLengthBinaryArray2D(12, 4);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array2.setBoolean(x, y, true);
                array2.setBoolean(x + 8, y, true);
                array1.setBoolean(x + 4, y, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // within mask 2 must be false
        assertTrue(res.getBoolean(4, 1));
        assertTrue(res.getBoolean(7, 1));
        // within mask 1 must be true
        assertFalse(res.getBoolean(3, 1));
        assertFalse(res.getBoolean(8, 1));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_heightSquares_SingleSquare()
    {
        // initialize demo arrays
        BinaryArray2D array1 = RunLengthBinaryArray2D.convert(createEightSquaresArray());
        BinaryArray2D array2 = new RunLengthBinaryArray2D(22, 10);
        array2.fillBooleans((x,y) -> x >= 8 && x <= 13 && y >= 2 && y <= 7);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // the four corners must be true
        assertTrue(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(21, 0));
        assertTrue(res.getBoolean(0, 9));
        assertTrue(res.getBoolean(21, 9));
        // within mask 2 must be false
        assertFalse(res.getBoolean(8, 2));
        assertFalse(res.getBoolean(13, 2));
        assertFalse(res.getBoolean(8, 7));
        assertFalse(res.getBoolean(13, 7));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_heightSquares_LargeRectangle()
    {
        // initialize demo arrays
        BinaryArray2D array1 = RunLengthBinaryArray2D.convert(createEightSquaresArray());
        BinaryArray2D array2 = new RunLengthBinaryArray2D(22, 10);
        array2.fillBooleans((x,y) -> x >= 2 && x <= 19 && y >= 2 && y <= 7);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;

        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);

        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // the four corners must be true
        assertTrue(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(21, 0));
        assertTrue(res.getBoolean(0, 9));
        assertTrue(res.getBoolean(21, 9));
        // within mask 2 must be false
        assertFalse(res.getBoolean(2, 2));
        assertFalse(res.getBoolean(19, 2));
        assertFalse(res.getBoolean(3, 7));
        assertFalse(res.getBoolean(19, 7));
    }

    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_heightSquares_LargeRectangle2()
    {
        // initialize demo arrays
        BinaryArray2D array1 = RunLengthBinaryArray2D.convert(createEightSquaresArray());
        BinaryArray2D array2 = new RunLengthBinaryArray2D(22, 10);
        array2.fillBooleans((x,y) -> x >= 5 && x <= 16 && y >= 2 && y <= 7);
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // the four corners must be true
        assertTrue(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(21, 0));
        assertTrue(res.getBoolean(0, 9));
        assertTrue(res.getBoolean(21, 9));
        // within mask 2 must be false
        assertFalse(res.getBoolean(7, 3));
        assertFalse(res.getBoolean(13, 2));
    }


    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_heightSquares_ThreeSquares()
    {
        // initialize demo arrays
        BinaryArray2D array1 = RunLengthBinaryArray2D.convert(createEightSquaresArray());
        BinaryArray2D array2 = new RunLengthBinaryArray2D(22, 10);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array2.setBoolean(x + 3, y + 3, true);
                array2.setBoolean(x + 9, y + 3, true);
                array2.setBoolean(x + 15, y + 3, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // the four corners must be true
        assertTrue(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(21, 0));
        assertTrue(res.getBoolean(0, 9));
        assertTrue(res.getBoolean(21, 9));
        // within mask 2 must be false
        assertFalse(res.getBoolean(3, 3));
        assertFalse(res.getBoolean(6, 3));
        assertFalse(res.getBoolean(9, 3));
        assertFalse(res.getBoolean(12, 3));
        assertFalse(res.getBoolean(15, 3));
        assertFalse(res.getBoolean(18, 3));
        // within array1 and outside mask must be true
        assertTrue(res.getBoolean(7, 3));
        assertTrue(res.getBoolean(13, 3));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.process.LogicalBinaryOperator#process(net.sci.array.binary.BinaryArray, net.sci.array.binary.BinaryArray)}.
     */
    @Test
    public final void test_ANDNOT_process_2d_rle_heightSquares_TwoVerticalRectangles()
    {
        // initialize demo arrays
        BinaryArray2D array1 = RunLengthBinaryArray2D.convert(createEightSquaresArray());
        BinaryArray2D array2 = new RunLengthBinaryArray2D(22, 10);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 2; x++)
            {
                array2.setBoolean(x + 1, y + 3, true);
                array2.setBoolean(x + 19, y + 3, true);
            }
        }
        // create operator to compute exclusive or from two arrays 
        LogicalBinaryOperator op = LogicalBinaryOperator.AND_NOT;
        
        // Apply operator and display result
        BinaryArray2D res = (BinaryArray2D) op.process(array1, array2);
        
        assertEquals(res.size(0), res.size(0));
        assertEquals(res.size(1), res.size(1));
        // the four corners must be true
        assertTrue(res.getBoolean(0, 0));
        assertTrue(res.getBoolean(21, 0));
        assertTrue(res.getBoolean(0, 9));
        assertTrue(res.getBoolean(21, 9));
        // within mask 2 must be false
        assertFalse(res.getBoolean(1, 3));
        assertFalse(res.getBoolean(2, 3));
        assertFalse(res.getBoolean(19, 3));
        assertFalse(res.getBoolean(20, 3));
        // within array1 and outside mask must be true
        assertTrue(res.getBoolean(3, 3));
        assertTrue(res.getBoolean(6, 3));
        assertTrue(res.getBoolean(9, 3));
        assertTrue(res.getBoolean(12, 3));
        assertTrue(res.getBoolean(15, 3));
        assertTrue(res.getBoolean(18, 3));
    }
    
    private BinaryArray2D createEightSquaresArray()
    {
        BinaryArray2D array = new RunLengthBinaryArray2D(22, 10);
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                array.setBoolean(x, y, true);
                array.setBoolean(x + 6, y, true);
                array.setBoolean(x + 12, y, true);
                array.setBoolean(x + 18, y, true);

                array.setBoolean(x, y + 6, true);
                array.setBoolean(x + 6, y + 6, true);
                array.setBoolean(x + 12, y + 6, true);
                array.setBoolean(x + 18, y + 6, true);
            }
        }
        return array;
    }

}
