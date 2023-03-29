/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.*;

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

}
