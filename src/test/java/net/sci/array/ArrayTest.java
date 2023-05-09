/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.*;

import java.util.function.Function;

import org.junit.Test;

import net.sci.array.generic.GenericArray;


/**
 * @author dlegland
 *
 */
public class ArrayTest
{
    /**
     * Test method for {@link net.sci.array.Array#fill(java.util.function.Function)}.
     */
    @Test
    public final void testFillFunctionOfintT()
    {
        // create an empty array of String
        Array<String> array = GenericArray.create(new int[] {10, 6}, " ");
        
        // populate the array of strings
        String[] digits = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        array.fill((int[] pos) -> digits[pos[0]] + digits[pos[1]]);
        
        assertEquals("AA", array.get(new int[] {0, 0}));
        assertEquals("JF", array.get(new int[] {9, 5}));
    }

    /**
     * Test method for {@link net.sci.array.Array#view(int[], java.util.function.Function)}.
     */
    @Test
    public final void test_view_FlipStringArray()
    {
        // create an empty array of String
        Array<String> array = GenericArray.create(new int[] {10, 6}, " ");
        
        // populate the array of strings
        String[] digits = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        array.fill((int[] pos) -> digits[pos[0]] + digits[pos[1]]);
        
        int[] dims2 = new int[] {10, 6};
        Function<int[], int[]> fun = pos -> new int[] {9-pos[0], 5-pos[1]};
        Array<String> res = array.view(dims2, fun);
        
        assertEquals(10, res.size(0));
        assertEquals(6, res.size(1));
        assertEquals("AA", res.get(new int[] {9, 5}));
        assertEquals("JF", res.get(new int[] {0, 0}));
    }

}
