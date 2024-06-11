/**
 * 
 */
package net.sci.array;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sci.util.MathUtils;

/**
 * 
 */
public class ReverseOrderPositionIteratorTest
{
    @Test
    public final void test_2d()
    {
        int[] dims = new int[] {4, 3};
        ReverseOrderPositionIterator iter = new ReverseOrderPositionIterator(dims);
        
        int count = 0;
        while(iter.hasNext())
        {
            @SuppressWarnings("unused")
            int[] pos = iter.next();
//            System.out.println(String.format("pos = [%d,%d]", pos[0], pos[1]));
            count++;
        }
        
        assertEquals(count, (int) MathUtils.prod(dims));
    }

    @Test
    public final void test_3d()
    {
        int[] dims = new int[] {4, 3, 2};
        ReverseOrderPositionIterator iter = new ReverseOrderPositionIterator(dims);
        
        int count = 0;
        while(iter.hasNext())
        {
            @SuppressWarnings("unused")
            int[] pos = iter.next();
//            System.out.println(String.format("pos = [%d,%d,%d]", pos[0], pos[1], pos[2]));
            count++;
        }
        
        assertEquals(count, (int) MathUtils.prod(dims));
    }

}
