/**
 * 
 */
package net.sci.array.process.shape;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.Array3D;
import net.sci.array.Arrays;
import net.sci.array.scalar.BufferedUInt8Array3D;
import net.sci.array.scalar.ScalarArray3D;
import net.sci.array.scalar.UInt8Array3D;

/**
 * @author dlegland
 *
 */
public class Rotate3D90Test
{
    /**
     * Expected:
     * <pre>
     *   5----6 
     *  /|   /|
     * 1----2 |
     * | 7--|-8
     * |/   |/
     * 3----4
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateX1()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {5, 6, 1, 2, 7, 8, 3, 4});

        Rotate3D90 algo = new Rotate3D90(0, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateX1_NonCubic()
    {
        UInt8Array3D array = createNonCubicArray3d();

        Rotate3D90 algo = new Rotate3D90(0, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        assertEquals(5, rotated.size(0));
        assertEquals(3, rotated.size(1));
        assertEquals(4, rotated.size(2));
    }
    
    /**
     * Expected:
     * <pre>
     *   7----8 
     *  /|   /|
     * 5----6 |
     * | 3--|-4
     * |/   |/
     * 1----2
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateX2()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {7, 8, 5, 6, 3, 4, 1, 2});

        Rotate3D90 algo = new Rotate3D90(0, 2);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Expected:
     * <pre>
     *   3----4 
     *  /|   /|
     * 7----8 |
     * | 1--|-2
     * |/   |/
     * 5----6
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateX3()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {3, 4, 7, 8, 1, 2, 5, 6});

        Rotate3D90 algo = new Rotate3D90(0, 3);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Expected:
     * <pre>
     *   2----6 
     *  /|   /|
     * 4----8 |
     * | 1--|-5
     * |/   |/
     * 3----7
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateY1()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {2, 6, 4, 8, 1, 5, 3, 7});

        Rotate3D90 algo = new Rotate3D90(1, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateY1_NonCubic()
    {
        UInt8Array3D array = createNonCubicArray3d();

        Rotate3D90 algo = new Rotate3D90(1, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        assertEquals(3, rotated.size(0));
        assertEquals(4, rotated.size(1));
        assertEquals(5, rotated.size(2));
    }
    
    /**
     * Expected:
     * <pre>
     *   6----5 
     *  /|   /|
     * 8----7 |
     * | 2--|-1
     * |/   |/
     * 4----3
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateY2()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {6, 5, 8, 7, 2, 1, 4, 3});

        Rotate3D90 algo = new Rotate3D90(1, 2);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Expected:
     * <pre>
     *   5----1
     *  /|   /|
     * 7----3 |
     * | 6--|-2
     * |/   |/
     * 8----4
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateY3()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {5, 1, 7, 3, 6, 2, 8, 4});

        Rotate3D90 algo = new Rotate3D90(1, 3);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Expected:
     * <pre>
     *   3----1 
     *  /|   /|
     * 4----2 |
     * | 7--|-5
     * |/   |/
     * 8----6
     * </pre>
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateZ1()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {3, 1, 4, 2, 7, 5, 8, 6});

        Rotate3D90 algo = new Rotate3D90(2, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateZ1_NonCubic()
    {
        UInt8Array3D array = createNonCubicArray3d();

        Rotate3D90 algo = new Rotate3D90(2, 1);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        assertEquals(4, rotated.size(0));
        assertEquals(5, rotated.size(1));
        assertEquals(3, rotated.size(2));
    }
    
    /**
     * Expected:
     *   4----3 
     *  /|   /|
     * 2----1 |
     * | 8--|-7
     * |/   |/
     * 6----5
     *
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateZ2()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {4, 3, 2, 1, 8, 7, 6, 5});

        Rotate3D90 algo = new Rotate3D90(2, 2);
        Array3D<?> rotated = algo.process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    /**
     * Expected:
     * <pre>
     *   2----4 
     *  /|   /|
     * 1----3 |
     * | 6--|-8
     * |/   |/
     * 5----7
     * </pre>
     * Test method for {@link net.sci.array.process.shape.Rotate3D90#process3d(net.sci.array.Array3D)}.
     */
    @Test
    public final void testRotateZ3()
    {
        UInt8Array3D array = createBasicTestArray();
        UInt8Array3D exp = new BufferedUInt8Array3D(2, 2, 2, new byte[] {2, 4, 1, 3, 6, 8, 5, 7});

        Array3D<?> rotated = new Rotate3D90(2, 3).process3d(array);
        
        assertTrue(rotated instanceof ScalarArray3D);
        checkScalarArrayEquals((ScalarArray3D<?>) rotated, exp, .01);
    }
    
    private void checkScalarArrayEquals(ScalarArray3D<?> array1, ScalarArray3D<?> array2, double tol)
    {
        assertTrue(Arrays.isSameDimensionality(array1, array2));
        assertTrue(Arrays.isSameSize(array1, array2));
        
        for (int[] pos : array1.positions())
        {
            double val1 = array1.getValue(pos);
            double val2 = array2.getValue(pos);
            assertEquals(val1, val2, tol);
        }
    }
    
    /**
     * Create 3D Test array with content:
     * 
     * <pre>
     *   1----2 
     *  /|   /|
     * 3----4 |
     * | 5--|-6
     * |/   |/
     * 7----8
     * </pre>
     */
    private UInt8Array3D createBasicTestArray()
    {
        UInt8Array3D array = UInt8Array3D.create(2, 2, 2);
        array.setInt(0, 0, 0, 1);
        array.setInt(1, 0, 0, 2);
        array.setInt(0, 1, 0, 3);
        array.setInt(1, 1, 0, 4);
        array.setInt(0, 0, 1, 5);
        array.setInt(1, 0, 1, 6);
        array.setInt(0, 1, 1, 7);
        array.setInt(1, 1, 1, 8);
        return array;
    }
    
    private UInt8Array3D createNonCubicArray3d()
    {
        UInt8Array3D array = UInt8Array3D.create(5, 4, 3);
        array.fillValues((x, y, z) -> z * 100.0 + y * 10 + x);
        return array;
    }
    
}
