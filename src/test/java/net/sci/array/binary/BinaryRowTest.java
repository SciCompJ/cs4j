/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryRowTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#dilate(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_dilationRows_singleRun_singleRun()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 5; i < 10; i++)
        {
            row1.set(i, true);
        }
        // 
        BinaryRow row2 = new BinaryRow();
        for (int i = -1; i <= 1; i++)
        {
            row2.set(i, true);
        }
        
        BinaryRow res = row1.dilation(row2);
        
        assertEquals(1, res.runs.size());
        assertFalse(res.get(3));
        assertTrue(res.get(4));
        assertTrue(res.get(10));
        assertFalse(res.get(11));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void test_dilation_MergeRuns()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 0; i <= 5; i++)
        {
            row.set(i + 10, true);
            row.set(i + 20, true);
        }
        assertEquals(2, row.runs.size());
        assertFalse(row.get(16));
       
        // dilation should fill indices 16 and 17 from the left, 
        // and indices 18 and 19 from the right
        BinaryRow row2 = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            row2.set(i, true);
        }
        BinaryRow res = row.dilation(row2);
                
        assertEquals(1, res.runs.size());
        assertFalse(res.get(7));
        assertTrue(res.get(8));
        assertTrue(res.get(27));
        assertFalse(res.get(28));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testUnion_SingleRow1()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 2; i < 4; i++)
        {
            row1.set(i, true);
        }
        BinaryRow row2 = new BinaryRow();
        
        BinaryRow res = row1.union(row2);
        
        assertEquals(1, res.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testUnion_SingleRow2()
    {
        BinaryRow row1 = new BinaryRow();
        BinaryRow row2 = new BinaryRow();
        for (int i = 2; i < 4; i++)
        {
            row2.set(i, true);
        }
        
        BinaryRow res = row1.union(row2);
        
        assertEquals(1, res.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testUnion_Row2WithinRow1()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 2; i < 10; i++)
        {
            row1.set(i, true);
        }
        BinaryRow row2 = new BinaryRow();
        for (int i = 3; i < 5; i++)
        {
            row2.set(i, true);
        }
        for (int i = 7; i < 9; i++)
        {
            row2.set(i, true);
        }
        
        BinaryRow res = row1.union(row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(2, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testUnion_Row1WithinRow2()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 3; i < 5; i++)
        {
            row1.set(i, true);
        }
        for (int i = 7; i < 9; i++)
        {
            row1.set(i, true);
        }
        BinaryRow row2 = new BinaryRow();
        for (int i = 2; i < 10; i++)
        {
            row2.set(i, true);
        }

        BinaryRow res = row1.union(row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(2, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#union(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testUnion_Interleave()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 3; i < 6; i++)
        {
            row1.set(i, true);
        }
        for (int i = 8; i < 12; i++)
        {
            row1.set(i, true);
        }
        BinaryRow row2 = new BinaryRow();
        for (int i = 4; i < 10; i++)
        {
            row2.set(i, true);
        }

        BinaryRow res = row1.union(row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(3, run1.left);
        assertEquals(11, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int)}.
     */
    @Test
    public final void testComplementBinaryRow_SingleRunInMiddle()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 3; i < 6; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow res = row.complement(10);
        
        assertEquals(2, res.runs.size());
        Iterator<Run> runs = res.runs.iterator();
        Run run1 = runs.next();
        assertEquals(0, run1.left);
        assertEquals(2, run1.right);
        Run run2 = runs.next();
        assertEquals(6, run2.left);
        assertEquals(9, run2.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int)}.
     */
    @Test
    public final void testComplementBinaryRow_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        BinaryRow res = row.complement(10);
        
        assertEquals(1, res.runs.size());
        Iterator<Run> runs = res.runs.iterator();
        Run run1 = runs.next();
        assertEquals(0, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int)}.
     */
    @Test
    public final void testComplementBinaryRow_FullRow()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 0; i < 10; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow res = row.complement(10);
        
        assertTrue(res.isEmpty());
    }
    
    @Test
    public final void testCropBinaryRow_Ovr_Within_Ovr()
    {
        // create a binary row with 3 runs:
        // * one from 0 to 4
        // * one from 8 to 12
        // * one from 16 to 20
        BinaryRow row = new BinaryRow();
        for (int i = 0; i <= 4; i++)
        {
            row.set(i, true);
            row.set(i + 8, true);
            row.set(i + 16, true);
        }
        
        BinaryRow row2 = row.crop(2, 18);
        
        assertFalse(row2.get(1));
        assertTrue(row2.get(2));
        assertTrue(row2.get(18));
        assertFalse(row2.get(19));
    }
    
    @Test
    public final void testCropBinaryRow_before_full_after()
    {
        // create a binary row with 3 runs:
        // * one from 0 to 2
        // * one from 6 to 14
        // * one from 18 to 20
        // crop from 8 to 12
        BinaryRow row = new BinaryRow();
        for (int i = 0; i <= 2; i++)
        {
            row.set(i, true);
            row.set(i + 18, true);
        }
        for (int i = 6; i <= 14; i++)
        {
            row.set(i, true);
        }
        
        
        BinaryRow row2 = row.crop(8, 12);
        
        assertFalse(row2.get(7));
        assertTrue(row2.get(8));
        assertTrue(row2.get(12));
        assertFalse(row2.get(13));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Isolated()
    {
        BinaryRow row = createRow();
        
        row.set(15, true);
        
        assertTrue(row.get(15));
        assertEquals(3, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Left()
    {
        BinaryRow row = createRow();
        
        row.set(0, true);
        
        assertTrue(row.get(0));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Right()
    {
        BinaryRow row = createRow();
        
        row.set(10, true);
        
        assertTrue(row.get(10));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Between()
    {
        BinaryRow row = createRow();
        
        row.set(6, true);
        
        assertTrue(row.get(6));
        assertEquals(1, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Within()
    {
        BinaryRow row = createRow();
        
        row.set(4, true);
        
        assertTrue(row.get(4));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Split()
    {
        BinaryRow row = createRow();
        
        row.set(3, false);
        
        assertFalse(row.get(3));
        assertEquals(3, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Left()
    {
        BinaryRow row = createRow();
        
        row.set(1, false);
        
        assertFalse(row.get(1));
        assertEquals(2, row.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_False_Right()
    {
        BinaryRow row = createRow();
        
        row.set(5, false);
        
        assertFalse(row.get(5));
        assertEquals(2, row.runs.size());
    }

    
    /**
     * Generates a simple row composed of two runs, one with 5 elements, the
     * other one with 3 elements.
     * 
     * <pre>
     *  0  1  2  3  4  5  6  7  8  9 10
     *  -  o  o  o  o  o  -  o  o  o  -
     * </pre>
     * 
     * @return the sample row.
     */
    private BinaryRow createRow()
    {
        BinaryRow row = new BinaryRow();
        
        // first run
        row.set(1, true);
        row.set(2, true);
        row.set(3, true);
        row.set(4, true);
        row.set(5, true);
        
        // second run
        row.set(7, true);
        row.set(8, true);
        row.set(9, true);
        
        return row;
    }
}
