/**
 * 
 */
package net.sci.array.numeric.process;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.color.RGB8Array3D;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class MathBinaryOperatorTest
{
    /**
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_process_uint8_2d()
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
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_process_uint8_3d()
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
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_process_rgb8_2d()
    {
        // initialize demo arrays
        RGB8Array2D array1 = RGB8Array2D.create(8, 6);
        array1.fill((x,y) -> new RGB8(x, 0, x));
        RGB8Array2D array2 = RGB8Array2D.create(8, 6);
        array2.fill((x,y) -> new RGB8(0, y, 0));
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        RGB8Array2D res = (RGB8Array2D) op.process(array1, array2);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));    
        assertEquals(res.size(1), array1.size(1));
        assertEquals(new RGB8(0, 0, 0), res.get(0, 0));
        assertEquals(new RGB8(7, 0, 7), res.get(7, 0));
        assertEquals(new RGB8(0, 5, 0), res.get(0, 5));
        assertEquals(new RGB8(7, 5, 7), res.get(7, 5));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#process(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_process_rgb8_3d()
    {
        // initialize demo arrays
        RGB8Array3D array1 = RGB8Array3D.create(8, 6, 4);
        array1.fill((x,y,z) -> new RGB8(x, 0, z));
        RGB8Array3D array2 = RGB8Array3D.create(8, 6, 4);
        array2.fill((x,y,z) -> new RGB8(0, y, 0));
        // create operator
        MathBinaryOperator op = new MathBinaryOperator((a,b) -> a + b);
        
        // process
        RGB8Array3D res = (RGB8Array3D) op.process(array1, array2);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));    
        assertEquals(res.size(1), array1.size(1));
        assertEquals(new RGB8(0, 0, 0), res.get(0, 0, 0));
        assertEquals(new RGB8(7, 0, 0), res.get(7, 0, 0));
        assertEquals(new RGB8(0, 5, 0), res.get(0, 5, 0));
        assertEquals(new RGB8(7, 5, 0), res.get(7, 5, 0));
        assertEquals(new RGB8(0, 0, 3), res.get(0, 0, 3));
        assertEquals(new RGB8(7, 0, 3), res.get(7, 0, 3));
        assertEquals(new RGB8(0, 5, 3), res.get(0, 5, 3));
        assertEquals(new RGB8(7, 5, 3), res.get(7, 5, 3));
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#processScalar(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_processScalar_uint8_2d_specifyOutput()
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
        op.processScalar(array1, array2, res);
        
        // check validity
        assertEquals(res.size(0), array1.size(0));    
        assertEquals(res.size(1), array1.size(1));
        assertEquals( 0.0, res.getValue(0, 0), 0.01);
        assertEquals( 7.0, res.getValue(7, 0), 0.01);
        assertEquals(50.0, res.getValue(0, 5), 0.01);
        assertEquals(57.0, res.getValue(7, 5), 0.01);
    }

    /**
     * Test method for {@link net.sci.array.numeric.process.MathBinaryOperator#processScalar(net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray, net.sci.array.scalar.ScalarArray)}.
     */
    @Test
    public final void test_processScalar_uint8_3d_specifyOutput()
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
        op.processScalar(array1, array2, res);
        
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
