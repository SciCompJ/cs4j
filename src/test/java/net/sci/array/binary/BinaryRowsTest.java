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
public class BinaryRowsTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#dilate(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_dilateRows_singleRun_singleRun()
    {
        BinaryRow row1 = new BinaryRow();
        for (int i = 5; i < 10; i++)
        {
            row1.set(i, true);
        }
        BinaryRow row2 = new BinaryRow();
        for (int i = 0; i < 3; i++)
        {
            row2.set(i, true);
        }
        
        BinaryRow res = BinaryRows.dilate(row1, row2, 1);
        
        assertEquals(1, res.runs.size());
        assertFalse(res.get(3));
        assertTrue(res.get(4));
        assertTrue(res.get(10));
        assertFalse(res.get(11));
    }
    
    @Test
    public final void test_dilate_SingleRun_PositiveLeft_PositiveRight()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 5; i <= 10; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow res = BinaryRows.dilate(row, 2, 3);
                
        assertEquals(1, res.runs.size());
        assertFalse(res.get(2));
        assertTrue(res.get(3));
        assertTrue(res.get(13));
        assertFalse(res.get(14));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void test_dilate_MergeRuns()
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
        BinaryRow res = BinaryRows.dilate(row, 2, 2);
                
        assertEquals(1, res.runs.size());
        assertFalse(res.get(7));
        assertTrue(res.get(8));
        assertTrue(res.get(27));
        assertFalse(res.get(28));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
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
        
        BinaryRow res = BinaryRows.union(row1, row2);
        
        assertEquals(1, res.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
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
        
        BinaryRow res = BinaryRows.union(row1, row2);
        
        assertEquals(1, res.runs.size());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
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
        
        BinaryRow res = BinaryRows.union(row1, row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(2, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
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

        BinaryRow res = BinaryRows.union(row1, row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(2, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#union(net.sci.array.binary.BinaryRow, net.sci.array.binary.BinaryRow)}.
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

        BinaryRow res = BinaryRows.union(row1, row2);
        
        assertEquals(1, res.runs.size());
        Run run1 = res.runs.first();
        assertEquals(3, run1.left);
        assertEquals(11, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#complement(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void testComplementBinaryRow_SingleRunInMiddle()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 3; i < 6; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow res = BinaryRows.complement(row, 10);
        
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
     * Test method for {@link net.sci.array.binary.BinaryRows#complement(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void testComplementBinaryRow_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        BinaryRow res = BinaryRows.complement(row, 10);
        
        assertEquals(1, res.runs.size());
        Iterator<Run> runs = res.runs.iterator();
        Run run1 = runs.next();
        assertEquals(0, run1.left);
        assertEquals(9, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRows#complement(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void testComplementBinaryRow_FullRow()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 0; i < 10; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow res = BinaryRows.complement(row, 10);
        
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
        
        BinaryRow row2 = BinaryRows.crop(row, 2, 18);
        
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
        
        
        BinaryRow row2 = BinaryRows.crop(row, 8, 12);
        
        assertFalse(row2.get(7));
        assertTrue(row2.get(8));
        assertTrue(row2.get(12));
        assertFalse(row2.get(13));
    }
}
