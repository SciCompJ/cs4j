/**
 * 
 */
package net.sci.array.process.math;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.scalar.UInt8Array2D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class MathBinaryOperatorTest
{
    /**
     * Test method for {@link net.sci.array.process.math.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcess_2d()
    {
        // initialize demo arrays
        UInt8Array2D array1 = UInt8Array2D.create(8, 6);
        array1.fillValues((x,y) -> (double) x);
        UInt8Array2D array2 = UInt8Array2D.create(8, 6);
        array2.fillValues((x,y) -> (double) y * 10);
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        UInt8Array2D res = (UInt8Array2D) op.process(array1, array2);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));    
        assertEquals(res.size(1), array1.size(1));
        assertEquals( 0.0, res.getValue(0, 0), 0.01);
        assertEquals( 7.0, res.getValue(7, 0), 0.01);
        assertEquals(50.0, res.getValue(0, 5), 0.01);
        assertEquals(57.0, res.getValue(7, 5), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.process.math.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcess_3d()
    {
        // initialize demo arrays
        UInt8Array3D array1 = UInt8Array3D.create(5, 4, 3);
        array1.fillValues((x,y,z) -> (double) x);
        UInt8Array3D array2 = UInt8Array3D.create(5, 4, 3);
        array2.fillValues((x,y,z) -> (double) y * 10);
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        UInt8Array3D res = (UInt8Array3D) op.process(array1, array2);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));  
        assertEquals(res.size(1), array1.size(1));
        assertEquals(res.size(2), array1.size(2));
        assertEquals( 0.0, res.getValue(0, 0, 0), 0.01);
        assertEquals( 4.0, res.getValue(4, 0, 0), 0.01);
        assertEquals(30.0, res.getValue(0, 3, 0), 0.01);
        assertEquals(34.0, res.getValue(4, 3, 0), 0.01);
        assertEquals( 0.0, res.getValue(0, 0, 2), 0.01);
        assertEquals( 4.0, res.getValue(4, 0, 2), 0.01);
        assertEquals(30.0, res.getValue(0, 3, 2), 0.01);
        assertEquals(34.0, res.getValue(4, 3, 2), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.process.math.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcess_2d_specifyOutput()
    {
        // initialize demo arrays
        UInt8Array2D array1 = UInt8Array2D.create(8, 6);
        array1.fillValues((x,y) -> (double) x);
        UInt8Array2D array2 = UInt8Array2D.create(8, 6);
        array2.fillValues((x,y) -> (double) y * 10);
        // allocate output array
        UInt8Array2D res = UInt8Array2D.create(8, 6);
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        op.process(array1, array2, res);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));    
        assertEquals(res.size(1), array1.size(1));
        assertEquals( 0.0, res.getValue(0, 0), 0.01);
        assertEquals( 7.0, res.getValue(7, 0), 0.01);
        assertEquals(50.0, res.getValue(0, 5), 0.01);
        assertEquals(57.0, res.getValue(7, 5), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.process.math.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void testProcess_3d_specifyOutput()
    {
        // initialize demo arrays
        UInt8Array3D array1 = UInt8Array3D.create(5, 4, 3);
        array1.fillValues((x,y,z) -> (double) x);
        UInt8Array3D array2 = UInt8Array3D.create(5, 4, 3);
        array2.fillValues((x,y,z) -> (double) y * 10);
        // allocate output array
        UInt8Array3D res = UInt8Array3D.create(5, 4, 3);
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        op.process(array1, array2, res);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));  
        assertEquals(res.size(1), array1.size(1));
        assertEquals(res.size(2), array1.size(2));
        assertEquals( 0.0, res.getValue(0, 0, 0), 0.01);
        assertEquals( 4.0, res.getValue(4, 0, 0), 0.01);
        assertEquals(30.0, res.getValue(0, 3, 0), 0.01);
        assertEquals(34.0, res.getValue(4, 3, 0), 0.01);
        assertEquals( 0.0, res.getValue(0, 0, 2), 0.01);
        assertEquals( 4.0, res.getValue(4, 0, 2), 0.01);
        assertEquals(30.0, res.getValue(0, 3, 2), 0.01);
        assertEquals(34.0, res.getValue(4, 3, 2), 0.01);    }
}
