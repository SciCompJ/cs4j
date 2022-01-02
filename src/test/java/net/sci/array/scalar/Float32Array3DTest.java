/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class Float32Array3DTest
{
    
    /**
     * Test method for {@link net.sci.array.scalar.Float32Array3D#slices()}.
     */
    @Test
    public final void testSlices()
    {
        Float32Array3D array = Float32Array3D.create(5, 4, 3);

        int n = 0;
        for(Float32Array2D slice : array.slices())
        {
            assertEquals(5, slice.size(0));
            assertEquals(4, slice.size(1));
            n++;
        }

        assertEquals(3, n);
    }
  
    
    @Test
    public final void testValues()
    {
        Float32Array3D array = Float32Array3D.create(5, 4, 3);
        array.fillValue(10.1);
        
        int count = 0; 
        double sum = 0.0;
        for (double v : array.values())
        {
            count++;
            sum += v;
        }
        
        assertEquals(60, count);
        assertEquals(606.0, sum, 0.1);
    }
    
    
    /**
     * Test method for {@link net.sci.array.scalar.Float32Array3D#fillValues(net.sci.array.scalar.TriFunction)}.
     */
    @Test
    public final void testPopulate()
    {
        Float32Array3D array = Float32Array3D.create(5, 5, 5);
        array.fillValues((x, y, z) -> Math.hypot(Math.hypot(x - 2, y - 2), z - 2));

        assertEquals(0, array.getValue(2, 2, 2), .1);
        assertEquals(2, array.getValue(0, 2, 2), .1);
        assertEquals(2, array.getValue(4, 2, 2), .1);
        assertEquals(2, array.getValue(2, 0, 2), .1);
        assertEquals(2, array.getValue(2, 4, 2), .1);
        assertEquals(2, array.getValue(2, 2, 0), .1);
        assertEquals(2, array.getValue(2, 2, 4), .1);
    }
    
}
