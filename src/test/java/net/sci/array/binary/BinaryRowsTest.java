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

}
