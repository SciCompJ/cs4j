/**
 * 
 */
package net.sci.array.scalar;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class ScalarArrayTest
{
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray#reshape(int...)}.
     */
    @Test
    public final void testReshape()
    {
        UInt8Array2D array = UInt8Array2D.create(6, 4);
        array.fillValues((x,y) -> (double) x + 10 * y);
        
        double sum0 = 0;
        for (Scalar v : array)
        {
            sum0 += v.getValue();
        }
        
        ScalarArray<?> res = array.reshape(4, 3, 2);
        
        // check element number
        assertEquals(res.elementCount(), 24);
        
        // check last element
        double lastValue = res.getValue(new int[] {3, 2, 1});
        assertEquals(lastValue, 35.0, 0.01);
        
        // check content
        double sum2 = 0;
        for (Scalar v : res)
        {
            sum2 += v.getValue();
        }
        assertEquals(sum0, sum2, .01);
    }
    
    /**
     * Test method for {@link net.sci.array.scalar.ScalarArray#apply(java.util.function.UnaryOperator)}.
     */
    @Test
    public final void testApply()
    {
        Float32Array2D array = Float32Array2D.create(50, 50);
        ScalarArray<Float32> result = array.apply(x -> 50.0);
        assertEquals(50, result.getValue(new int[] {0, 0}), .001);
        assertEquals(50, result.getValue(new int[] {49, 49}), .001);
    }
    
    /**
     * Test method for {@link net.sci.array.Array#reshapeView(int[], java.util.function.Function)}.
     */
    @Test
    public final void test_view_FlipFloat32Array()
    {
        // create an empty array of Float32
        Float32Array2D array = Float32Array2D.create(10, 6);
        
        // populate the array of strings
        array.fillValues((x,y) -> (y * 1.0 + x * 0.1));
        
        int[] dims2 = new int[] {10, 6};
        Function<int[], int[]> fun = pos -> new int[] {9-pos[0], 5-pos[1]};
        ScalarArray<Float32> res = array.reshapeView(dims2, fun);
        
        assertEquals(10, res.size(0));
        assertEquals(6, res.size(1));
        assertEquals(0.0, res.getValue(new int[] {9, 5}), 0.01);
        assertEquals(5.9, res.getValue(new int[] {0, 0}), 0.01);
    }

    
}
