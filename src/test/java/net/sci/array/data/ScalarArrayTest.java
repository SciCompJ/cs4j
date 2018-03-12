/**
 * 
 */
package net.sci.array.data;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.array.data.scalar2d.Float32Array2D;
import net.sci.array.type.Float32;

/**
 * @author dlegland
 *
 */
public class ScalarArrayTest
{
    
    /**
     * Test method for {@link net.sci.array.data.ScalarArray#apply(java.util.function.UnaryOperator)}.
     */
    @Test
    public final void testApply()
    {
        Float32Array2D array = Float32Array2D.create(50, 50);
        ScalarArray<Float32> result = array.apply(x -> 50.0);
        assertEquals(50, result.getValue(new int[] {0, 0}), .001);
        assertEquals(50, result.getValue(new int[] {49, 49}), .001);
    }  
}
