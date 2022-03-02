/**
 * 
 */
package net.sci.array.binary;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

/**
 * @author dlegland
 *
 */
public class BinaryRowTest
{
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#dilation(net.sci.array.binary.BinaryRow, int)}.
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
     * Test method for {@link net.sci.array.binary.BinaryRow#dilation(net.sci.array.binary.BinaryRow)}.
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
     * Test method for {@link net.sci.array.binary.BinaryRow#erosion(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_erosion_singleRun_singleRun()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 5; i <= 15; i++)
        {
            row.set(i, true);
        }
        
        BinaryRow strel = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            strel.set(i, true);
        }
        
        BinaryRow res = row.erosion(strel);
        
        assertEquals(1, res.runs.size());
        assertFalse(res.get(6));
        assertTrue(res.get(7));
        assertTrue(res.get(13));
        assertFalse(res.get(14));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#erosion(net.sci.array.binary.BinaryRow, int)}.
     */
    @Test
    public final void test_erosion_TwoRuns_singleRun()
    {
        BinaryRow row = new BinaryRow();
        for (int i = 5; i <= 10; i++)
        {
            row.set(i, true);
            row.set(i + 10, true);
        }
        
        BinaryRow strel = new BinaryRow();
        for (int i = -2; i <= 2; i++)
        {
            strel.set(i, true);
        }
        
        BinaryRow res = row.erosion(strel);
        
        assertEquals(2, res.runs.size());
        assertFalse(res.get(6));
        assertTrue(res.get(7));
        assertTrue(res.get(8));
        assertFalse(res.get(9));
        assertFalse(res.get(16));
        assertTrue(res.get(17));
        assertTrue(res.get(18));
        assertFalse(res.get(19));
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
        Run run1 = res.runs.firstEntry().getValue();
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
        Run run1 = res.runs.firstEntry().getValue();
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
        Run run1 = res.runs.firstEntry().getValue();
        assertEquals(3, run1.left);
        assertEquals(11, run1.right);
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#intersection(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testIntersection_TwoEmptyRow()
    {
        BinaryRow row1 = new BinaryRow();
        BinaryRow row2 = new BinaryRow();

        BinaryRow res = row1.intersection(row2);
        
        assertTrue(res.isEmpty());
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#intersection(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testIntersection_SingleIntersect()
    {
        BinaryRow row1 = new BinaryRow();
        row1.setRange(2, 5, true);
        BinaryRow row2 = new BinaryRow();
        row2.setRange(4, 7, true);

        BinaryRow res = row1.intersection(row2);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 4, 5));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#intersection(net.sci.array.binary.BinaryRow)}.
     */
    @Test
    public final void testIntersection_ComplexIntersect()
    {
        BinaryRow row1 = new BinaryRow();
        row1.setRange(2, 5, true);
        row1.setRange(14, 27, true);
        row1.setRange(32, 39, true);
        
        BinaryRow row2 = new BinaryRow();
        row2.setRange( 8, 11, true);
        row2.setRange(16, 19, true);
        row2.setRange(24, 33, true);

        BinaryRow res = row1.intersection(row2);
        
        assertEquals(3, res.runs.size());
        assertTrue(containsRun(res, 16, 19));
        assertTrue(containsRun(res, 24, 27));
        assertTrue(containsRun(res, 32, 33));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_SingleRunInMiddle()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(3, 5, true);
        
        BinaryRow res = row.complement(0, 9);
        
        assertEquals(2, res.runs.size());
        assertTrue(containsRun(res, 0, 2));
        assertTrue(containsRun(res, 6, 9));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        BinaryRow res = row.complement(0, 9);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 0, 9));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int)}.
     */
    @Test
    public final void testComplementInt_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        BinaryRow res = row.complement(10);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 0, 9));
    }

    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_FullRow()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 9, true);
        
        BinaryRow res = row.complement(10);
        
        assertTrue(res.isEmpty());
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_BetweenTwoRuns()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 2, true);
        row.setRange(12, 15, true);
        
        BinaryRow res = row.complement(5, 8);
        
        assertEquals(3, res.runs.size());
        assertTrue(containsRun(res, 0, 2));
        assertTrue(containsRun(res, 5, 8));
        assertTrue(containsRun(res, 12, 15));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_LargerBounds()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(3, 6, true);
        
        BinaryRow res = row.complement(-5, 15);
        
        assertEquals(2, res.runs.size());
        assertTrue(containsRun(res, -5, 2));
        assertTrue(containsRun(res, 7, 15));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_FullRow_LargerBounds()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 9, true);
        
        BinaryRow res = row.complement(-5, 15);
        
        assertEquals(2, res.runs.size());
        assertTrue(containsRun(res, -5, -1));
        assertTrue(containsRun(res, 10, 15));
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#complement(int, int)}.
     */
    @Test
    public final void testComplement_AfterRun()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 4, true);
        
        BinaryRow res = row.complement(5, 9);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 0, 9));
    }
    
    @Test
    public final void testCropBinaryRow_Ovr_Within_Ovr()
    {
        // create a binary row with 3 runs
        BinaryRow row = new BinaryRow();
        row.setRange(0, 4, true);
        row.setRange(8, 12, true);
        row.setRange(16, 20, true);
        
        BinaryRow res = row.crop(2, 18);
        
        assertEquals(3, res.runs.size());
        assertTrue(containsRun(res, 2, 4));
        assertTrue(containsRun(res, 8, 12));
        assertTrue(containsRun(res, 16, 18));
    }
    
    @Test
    public final void testCropBinaryRow_before_full_after()
    {
        // create a binary row with 3 runs
        BinaryRow row = new BinaryRow();
        row.setRange(0, 2, true);
        row.setRange(6, 14, true);
        row.setRange(18, 20, true);
        
        BinaryRow res = row.crop(8, 12);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 8, 12));
    }
    
    @Test
    public final void testCrop_EmptyPortion_BeforeOthers()
    {
        // create a binary row with 2 runs
        BinaryRow row = new BinaryRow();
        row.setRange(8, 12, true);
        row.setRange(16, 20, true);
        
        // crop the beginning -> should be empty
        BinaryRow res = row.crop(0, 6);
        
        assertTrue(res.isEmpty());
    }
    
    @Test
    public final void testCrop_EmptyPortion_AfterOthers()
    {
        // create a binary row with 2 runs
        BinaryRow row = new BinaryRow();
        row.setRange(0, 4, true);
        row.setRange(8, 12, true);
        
        // crop the end -> should be empty
        BinaryRow res = row.crop(14, 20);
        
        assertTrue(res.isEmpty());
    }
    
    @Test
    public final void testCrop_EmptyPortion_BetweenOthers()
    {
        // create a binary row with 2 runs
        BinaryRow row = new BinaryRow();
        row.setRange(0, 4, true);
        row.setRange(16, 20, true);
        
        // crop in between -> should be empty
        BinaryRow res = row.crop(6, 14);
        
        assertTrue(res.isEmpty());
    }
    
    @Test
    public final void testCrop_Head()
    {
        // create a row with a single run
        BinaryRow row = new BinaryRow();
        row.setRange(8, 12, true);
        
        // crop-> should be empty
        BinaryRow res = row.crop(0, 10);
        
        assertEquals(1, res.runs.size());
        assertTrue(res.get(8));
        assertTrue(res.get(10));    
    }
    
    @Test
    public final void testCrop_Tail()
    {
        // create a row with a single run
        BinaryRow row = new BinaryRow();
        for (int i = 8; i <= 12; i++)
        {
            row.set(i, true);
        }
        
        // crop-> should obtain a single run
        BinaryRow res = row.crop(10, 20);
        
        assertEquals(1, res.runs.size());
        assertTrue(containsRun(res, 10, 12));
    }
    
    @Test
    public final void testCrop_EmptyRow()
    {
        // create an empty row
        BinaryRow row = new BinaryRow();
        
        // crop-> should be empty
        BinaryRow res = row.crop(6, 14);
        
        assertTrue(res.isEmpty());
    }
    
    @Test
    public final void testSetRange_True_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        
        assertEquals(1, row.runs.size());
        assertTrue(containsRun(row, 5, 15));
    }
    
    @Test
    public final void testSetRange_True_EmptyRange()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(1, true);
        row.set(20, true);
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(4));
        assertTrue(row.get(5));
        assertTrue(row.get(15));
        assertFalse(row.get(16));
    }
    
    @Test
    public final void testSetRange_True_SeveralRunsWithin()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(1, true);
        row.set(20, true);
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(4));
        assertTrue(row.get(5));
        assertTrue(row.get(15));
        assertFalse(row.get(16));
    }
    
    @Test
    public final void testSetRange_True_JustAfterAnotherRun()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(3, true);
        row.set(4, true);
        row.set(20, true);
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(2));
        assertTrue(row.get(3));
        assertTrue(row.get(15));
        assertFalse(row.get(16));
    }
    
    @Test
    public final void testSetRange_True_IntersectFirst()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(3, true);
        row.set(4, true);
        row.set(5, true);
        row.set(6, true);
        row.set(20, true);
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(2));
        assertTrue(row.get(3));
        assertTrue(row.get(15));
        assertFalse(row.get(16));
    }
    
    @Test
    public final void testSetRange_True_IntersectLast()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true);
        row.set(1, true);
        row.set(14, true);
        row.set(15, true);
        row.set(16, true);
        row.set(17, true);
        row.set(20, true);
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(4));
        assertTrue(row.get(5));
        assertTrue(row.get(17));
        assertFalse(row.get(18));
    }
    
    @Test
    public final void testSetRange_True_JustBeforeAnotherRun()
    {
        // Create a new row, with runs before and after
        BinaryRow row = new BinaryRow();
        row.set(0, true); // run 1
        row.set(1, true);
        row.set(16, true); // run 2
        row.set(17, true);
        row.set(20, true); // run 3
        row.set(21, true);
        
        row.setRange(5, 15, true);
        
        assertFalse(row.isEmpty());
        assertEquals(3, row.runs.size());
        assertFalse(row.get(4));
        assertTrue(row.get(5));
        assertTrue(row.get(17));
        assertFalse(row.get(18));
    }
    
    
    @Test
    public final void testSetRange_False_EmptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        row.setRange(5, 15, false);
        
        assertTrue(row.isEmpty());
    }
    
    @Test
    public final void testSetRange_False_WithinSingleRun()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 15, true);
        
        row.setRange(8, 12, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(7));
        assertFalse(row.get(8));
        assertFalse(row.get(12));
        assertTrue(row.get(13));
    }
    
    @Test
    public final void testSetRange_False_BetweeenTwoRuns()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 8, true);
        row.setRange(18, 25, true);
        
        row.setRange(10, 15, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(8));
        assertFalse(row.get(9));
        assertFalse(row.get(17));
        assertTrue(row.get(18));
    }
    
    
    @Test
    public final void testSetRange_False_remainBefore_removeBetween_remainAfter()
    {
        // create a row with several runs, before, within and after the range
        BinaryRow row = new BinaryRow();
        row.setRange(0, 2, true);
        row.setRange(5, 8, true);
        row.setRange(11, 13, true);
        row.setRange(16, 18, true);
        row.setRange(22, 25, true);
        row.setRange(30, 35, true);

        row.setRange(10, 20, false);
        
        assertFalse(row.isEmpty());
        assertEquals(4, row.runs.size());
        assertTrue(row.get(8));
        assertFalse(row.get(9));
        assertFalse(row.get(12));
        assertFalse(row.get(17));
        assertFalse(row.get(21));
        assertTrue(row.get(22));
    }
    
    @Test
    public final void testSetRange_False_CropExtremityRuns()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 15, true);
        row.setRange(25, 35, true);
        
        row.setRange(10, 30, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(9));
        assertFalse(row.get(10));
        assertFalse(row.get(30));
        assertTrue(row.get(31));
    }
    
    @Test
    public final void testSetRange_False_CropExtremityRuns_andRemoveWithin()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 12, true);
        row.setRange(16, 18, true);
        row.setRange(21, 24, true);
        row.setRange(28, 35, true);
        
        row.setRange(10, 30, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(9));
        assertFalse(row.get(10));
        assertFalse(row.get(30));
        assertTrue(row.get(31));
    }
    

    @Test
    public final void testSetRange_False_CropFirstRun_runsAfterRemain()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 15, true);
        row.setRange(25, 35, true);
        
        row.setRange(10, 20, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(9));
        assertFalse(row.get(10));
        assertFalse(row.get(24));
        assertTrue(row.get(25));
    }
    

    @Test
    public final void testSetRange_False_CropLastRun_runsBeforeRemain()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 8, true);
        row.setRange(15, 25, true);
        
        row.setRange(10, 20, false);
        
        assertFalse(row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(8));
        assertFalse(row.get(9));
        assertFalse(row.get(20));
        assertTrue(row.get(21));
    }
    
    @Test
    public final void testSetRange_False_cropFirst_removeWithin_remainAfter()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 12, true);
        row.setRange(16, 18, true);
        row.setRange(25, 30, true);
        
        row.setRange(10, 20, false);
        
        assertTrue(!row.isEmpty());
        assertEquals(2, row.runs.size());
        assertTrue(row.get(9));
        assertFalse(row.get(10));
        assertFalse(row.get(24));
        assertTrue(row.get(25));
    }
    

    @Test
    public final void testSetRange_False_remainBefore_removeWithin_cropLast_remainAfter()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(5, 8, true);
        row.setRange(12, 15, true);
        row.setRange(18, 22, true);
        row.setRange(25, 30, true);
        
        row.setRange(10, 20, false);
        
        assertTrue(!row.isEmpty());
        assertEquals(3, row.runs.size());
        assertTrue(row.get(8));
        assertFalse(row.get(9));
        assertFalse(row.get(13));
        assertFalse(row.get(20));
        assertTrue(row.get(21));
        assertTrue(row.get(25));
    }
    


    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#set(int, boolean)}.
     */
    @Test
    public final void testSet_True_Isolated()
    {
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
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
        BinaryRow row = createRow_twoRuns();
        
        row.set(5, false);
        
        assertFalse(row.get(5));
        assertEquals(2, row.runs.size());
    }
    
    /**
     * Test method for {@link net.sci.array.binary.BinaryRow#get(int, int)}.
     */
    @Test
    public final void testGet_IntInt()
    {
        // use a row with several runs of various length:
        //  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 
        //  0  1  0  1  1  0  1  0  1  1  1  0  1  1  0  1  0
        BinaryRow row = new BinaryRow();
        row.set( 1, true);
        row.set( 3, true);
        row.set( 4, true);
        row.set( 6, true);
        row.set( 8, true);
        row.set( 9, true);
        row.set(10, true);
        row.set(12, true);
        row.set(13, true);
        row.set(15, true);
        
        assertFalse(row.get(0));
        assertTrue(row.get(1));
        assertFalse(row.get(2));
        assertTrue(row.get(3));
        assertTrue(row.get(4));
        assertFalse(row.get(5));
        assertTrue(row.get(6));
        assertFalse(row.get(7));
        assertTrue(row.get(8));
        assertTrue(row.get(9));
        assertTrue(row.get(10));
        assertFalse(row.get(11));
        assertTrue(row.get(12));
        assertTrue(row.get(13));
        assertFalse(row.get(14));
        assertTrue(row.get(15));
        assertFalse(row.get(16));
    }
    
    
    @Test
    public final void testContainingRuns_emptyRow()
    {
        BinaryRow row = new BinaryRow();
        
        Collection<Run> runs = row.containingRuns(5, 15);
        
        assertTrue(runs.isEmpty());
    }
    
    @Test
    public final void testContainingRuns_before_within_after()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 5, true);
        row.setRange(12, 14, true);
        row.setRange(16, 18, true);
        row.setRange(25, 30, true);
        
        Collection<Run> runs = row.containingRuns(10, 20);
        
        assertFalse(runs.isEmpty());
        assertEquals(2, runs.size());
    }
    
    @Test
    public final void testContainingRuns_before_cropFirst_within_cropLast_after()
    {
        BinaryRow row = new BinaryRow();
        row.setRange(0, 5, true);
        row.setRange(8, 12, true);
        row.setRange(14, 16, true);
        row.setRange(18, 22, true);
        row.setRange(25, 30, true);
        
        Collection<Run> runs = row.containingRuns(10, 20);
        
        assertFalse(runs.isEmpty());
        assertEquals(3, runs.size());
    }
    
    
    /**
     * Returns true if all following conditions are met:
     * <ul>
     * <li>the last element before the run (left-1) is set to false</li>
     * <li>all the elements within bounds (inclusive) are set to true</li>
     * <li>the first element after the run (right+1) is set to false</li>
     * </ul>
     * 
     * @param row
     *            the binary row to check
     * @param left
     *            the left bound of the run
     * @param right
     *            the right bound of the run
     * @return true if row contains a run with the given bounds
     */
    private boolean containsRun(BinaryRow row, int left, int right)
    {
        if (row.get(left-1)) return false;
        if (!row.containsRange(left, right)) return false;
        if (row.get(right+1)) return false;
        return true;
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
    private BinaryRow createRow_twoRuns()
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
