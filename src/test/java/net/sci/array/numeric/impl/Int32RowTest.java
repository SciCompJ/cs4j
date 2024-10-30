/**
 * 
 */
package net.sci.array.numeric.impl;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class Int32RowTest
{

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#elementCount()}.
     */
    @Test
    public final void test_elementCount()
    {
        Int32Row row = createRow();
        assertEquals(9, row.elementCount());
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_inbetween_diff()
    {
        Int32Row row = createRow();
        row.set(5, 5);
        assertEquals(2, row.get(4));
        assertEquals(5, row.get(5));
        assertEquals(3, row.get(6));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_inbetween_sameLeft()
    {
        Int32Row row = createRow();
        row.set(5, 2);
        assertEquals(2, row.get(4));
        assertEquals(2, row.get(5));
        assertEquals(3, row.get(6));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_inbetween_sameRight()
    {
        Int32Row row = createRow();
        row.set(5, 3);
        assertEquals(2, row.get(4));
        assertEquals(3, row.get(5));
        assertEquals(3, row.get(6));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_inBetween_Merge()
    {
        int[] values = new int[] {0, 2, 2, 3, 3, 3, 0, 3, 3};
        Int32Row row = new Int32Row();
        for(int i = 0; i < values.length; i++) 
        {
            row.set(i, values[i]);
        }

        row.set(6, 3);
        assertEquals(3, row.get(5));
        assertEquals(3, row.get(6));
        assertEquals(3, row.get(7));
    }
    
    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_updateSingleton()
    {
        Int32Row row = createRow();
        row.set(1, 5);
        assertEquals(0, row.get(0));
        assertEquals(5, row.get(1));
        assertEquals(0, row.get(2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_updateRight()
    {
        Int32Row row = createRow();
        row.set(9, 5);
        assertEquals(3, row.get(8));
        assertEquals(5, row.get(9));
        assertEquals(0, row.get(10));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_updateLeft()
    {
        Int32Row row = createRow();
        row.set(6, 5);
        assertEquals(0, row.get(5));
        assertEquals(5, row.get(6));
        assertEquals(3, row.get(7));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_set_expandLeft()
    {
        int[] values = new int[] {0, 2, 2, 3, 3, 3, 0, 3, 3};
        Int32Row row = new Int32Row();
        for(int i = 0; i < values.length; i++) 
        {
            row.set(i, values[i]);
        }
        assertEquals(3, row.runCount());

        row.set(2, 3);
        assertEquals(2, row.get(1));
        assertEquals(3, row.get(2));
        assertEquals(3, row.get(3));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_expandRightBeforeCurrentRun()
    {
        Int32Row row = new Int32Row();
        row.set(2, 2);
        row.set(3, 2);
        row.set(4, 3);
        row.set(5, 3);
        assertEquals(2, row.runCount());
        
        // this should replace the run (2,3,2), instead of creating a new one
        row.set(4, 2);
        
        assertEquals(2, row.get(3));
        assertEquals(2, row.get(4));
        assertEquals(3, row.get(5));
        assertEquals(2, row.runCount());
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_zero()
    {
        // use a row with several runs of various length:
        //  0  1  2  3  4  5  6  7  8  9 10 11 12
        //  0  1  0  2  2  0  3  3  3  3  0  4  4
        Int32Row row = createRow();
        row.set(5, 0);
        assertEquals(2, row.get(4));
        assertEquals(0, row.get(5));
        assertEquals(3, row.get(6));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_singleton()
    {
        Int32Row row = createRow();
        row.set(1, 0);
        assertEquals(0, row.get(0));
        assertEquals(0, row.get(1));
        assertEquals(0, row.get(2));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_updateLeft()
    {
        Int32Row row = createRow();
        row.set(6, 0);
        assertEquals(0, row.get(5));
        assertEquals(0, row.get(6));
        assertEquals(3, row.get(7));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_updateRight()
    {
        Int32Row row = createRow();
        row.set(9, 0);
        assertEquals(3, row.get(8));
        assertEquals(0, row.get(9));
        assertEquals(0, row.get(10));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#set(int, int)}.
     */
    @Test
    public final void test_setZero_updateMiddle()
    {
        Int32Row row = createRow();
        row.set(7, 0);
        assertEquals(3, row.get(6));
        assertEquals(0, row.get(7));
        assertEquals(3, row.get(8));
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#get(int)}.
     */
    @Test
    public final void test_get()
    {
        // use a row with several runs of various length:
        //  0  1  2  3  4  5  6  7  8  9 10 11 12
        //  0  1  0  2  2  0  3  3  3  3  0  4  4
        Int32Row row = createRow();

        assertEquals(row.get(0), 0);
        assertEquals(row.get(1), 1);
        assertEquals(row.get(2), 0);
        assertEquals(row.get(3), 2);
        assertEquals(row.get(4), 2);
        assertEquals(row.get(5), 0);
        assertEquals(row.get(6), 3);
        assertEquals(row.get(7), 3);
        assertEquals(row.get(8), 3);
        assertEquals(row.get(9), 3);
        assertEquals(row.get(10), 0);
        assertEquals(row.get(11), 4);
        assertEquals(row.get(12), 4);
    }

    /**
     * Test method for {@link net.sci.array.numeric.impl.Int32Row#containingRun(int)}.
     */
    @Test
    public final void test_containingRun()
    {
        Int32Row row = createRow();
        
        Int32Run run1 = row.containingRun(1);
        assertEquals(run1.left, 1);
        assertEquals(run1.right, 1);
        assertEquals(run1.value, 1);
        
        Int32Run run2 = row.containingRun(3);
        assertEquals(run2.left, 3);
        assertEquals(run2.right, 4);
        assertEquals(run2.value, 2);
        
        Int32Run run3 = row.containingRun(7);
        assertEquals(run3.left, 6);
        assertEquals(run3.right, 9);
        assertEquals(run3.value, 3);
        
        Int32Run run4 = row.containingRun(12);
        assertEquals(run4.left, 11);
        assertEquals(run4.right, 12);
        assertEquals(run4.value, 4);
    }

    /**
     * 0  1  2  3  4  5  6  7  8  9 10 11 12
     * 0  1  0  2  2  0  3  3  3  3  0  4  4
     */
    private static final Int32Row createRow()
    {
        // use a row with several runs of various length:
        //  0  1  2  3  4  5  6  7  8  9 10 11 12
        //  0  1  0  2  2  0  3  3  3  3  0  4  4
        Int32Row row = new Int32Row();
        row.set(1, 1);
        row.set(3, 2);
        row.set(4, 2);
        row.set(6, 3);
        row.set(7, 3);
        row.set(8, 3);
        row.set(9, 3);
        row.set(11, 4);
        row.set(12, 4);
        return row;
    }
}
