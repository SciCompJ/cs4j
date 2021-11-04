/**
 * 
 */
package net.sci.array.binary;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A collection of static methods for processing binary rows.
 * 
 * @author dlegland
 *
 */
public class BinaryRows
{
    /**
     * Computes the dilation of two binary rows.
     * 
     * @param row1
     *            the first row.
     * @param row2
     *            the second row.
     * @param row2Offset
     *            the index of the origin on the second row.
     * @return the result of the dilation of the two rows.
     */
    public static final BinaryRow dilate(BinaryRow row1, BinaryRow row2, int row2Offset)
    {
        BinaryRow res = new BinaryRow();
        for (Run run : row2.runs)
        {
            BinaryRow resDil = dilate(row1, row2Offset - run.left, run.right - row2Offset);
            res = union(res, resDil);
        }
        return res;
    }
    
    public static final BinaryRow dilate(BinaryRow row, int leftDilate, int rightDilate)
    {
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        Iterator<Run> iter = row.runs.iterator();
        Run currentRun = iter.hasNext() ? iter.next() : null;
        
        while(currentRun != null)
        {
            // init extremities of new run
            int newLeft = currentRun.left - leftDilate;
            int newRight = currentRun.right + rightDilate;
            
            // switch to next run
            currentRun = iter.hasNext() ? iter.next() : null;
            
            // if leftDilate or rightDilate is negative, this may delete some runs
            // here, we simply iterate
            if (newLeft > newRight)
            {
                continue;
            }
            
            // in case of last run, creates new run and finalize 
            if (currentRun == null)
            {
                newRuns.add(new Run(newLeft, newRight));
                break;
            }
            
            // iterate while right extremity overlap with left extremity of next run
            while (currentRun != null && currentRun.left - leftDilate - 1 <= newRight)
            {
                // overlap -> need to update right extremity
                newRight = currentRun.right + rightDilate;
                // and update current run
                currentRun = iter.hasNext() ? iter.next() : null;
            }
            
            newRuns.add(new Run(newLeft, newRight));
        }
        
        // create the new row from list of runs
        return new BinaryRow(newRuns);
    }
    
    public static final BinaryRow union(BinaryRow row1, BinaryRow row2)
    {
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        // create structure to iterate over both rows in parallel.
        RunIteratorPair rip = new RunIteratorPair(row1, row2);
        
        // main iteration, breaking when both run1 and run2 are null
        // (assumes we start from background)
        while(rip.run1 != null || rip.run2 != null)
        {
            // case of first row with no more runs
            if (rip.run1 == null)
            {
                // add remaining runs of row2
                newRuns.add(rip.run2.duplicate());
                while (rip.runs2.hasNext())
                {
                    newRuns.add(rip.runs2.next().duplicate());
                }
                break;
            }
            
            // case of second row with no more runs
            if (rip.run2 == null)
            {
                // add remaining runs of row1
                newRuns.add(rip.run1.duplicate());
                while (rip.runs1.hasNext())
                {
                    newRuns.add(rip.runs1.next().duplicate());
                }
                break;
            }
            
            // From here, there are remaining runs in both row1 and row2 
            
            // start processing with the run with the lowest left value
            // -> enforce run1 to have the lowest left value 
            if (rip.run1.left > rip.run2.left)
            {
                rip.swap();
            }

            // initialize new run with current run1
            int newLeft = rip.run1.left;
            int newRight = rip.run1.right;
            
            // update right value of new run, and add it to list of runs
            newRight = findRightExtremityOfUnion(rip, rip.run1.right);
            newRuns.add(new Run(newLeft, newRight));
            
            // process next run 
            rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
        }
        
        // create the new row from list of runs
        return new BinaryRow(newRuns);
    }
    
    private static final int findRightExtremityOfUnion(RunIteratorPair rip, int newRight)
    {
        // process the loop until right extremity is found
        while (true)
        {
            // identify the next run in row2 with a right extremity greater than current "newRight" value
            findNextRunInRun2WithRightExtremityGreaterThanValue(rip, newRight);

            // case of no more run in row2 with extremity after current run in row1 
            if (rip.run2 == null)
            {
                return newRight;
            }

            // if we have found the current extremity, create new run and iterate 
            if (rip.run2.left > newRight + 1)
            {
                return newRight;
            }

            // new extremity in run2
            newRight = rip.run2.right;
            
            // as run2 is the new current run, need to swap
            rip.swap();
            
            // iterate
            rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
        }
    }
    
    private static final Run findNextRunInRun2WithRightExtremityGreaterThanValue(RunIteratorPair rip, int value)
    {
        while (true)
        {
            if (rip.run2 == null)
            {
                return null;
            }
            if (rip.run2.right > value)
            {
                return rip.run2;
            }
            rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
        }
        
    }
    
    /**
     * Computes the complement of the specified row. As row do not keep
     * information about their length, it is necessary to specify it as second
     * argument.
     * 
     * @param row
     *            the row to complement.
     * @param length
     *            the length of the row.
     * @return a BinaryRow representing the complement of the input row.
     */
    public static final BinaryRow complement(BinaryRow row, int length)
    {
        // allocate runs for the new row
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        // case of empty row -> return a new full row
        if (row.isEmpty())
        {
            newRuns.add(new Run(0, length - 1));
            return new BinaryRow(newRuns);
        }
        
        // prepare iteration on runs of input row
        Iterator<Run> runs = row.runs.iterator();
        Run nextRun = runs.next();
        
        // new left is the first element set to false in input row
        int newLeft = 0;
        if (row.get(0))
        {
            // if input row start with foreground, update beginning of next run 
            newLeft = nextRun.right + 1;
            nextRun = runs.hasNext() ? runs.next() : null;
        }
        
        // iterate over intervals between runs
        while (nextRun != null)
        {
            newRuns.add(new Run(newLeft, nextRun.left - 1));
            newLeft = nextRun.right + 1;
            nextRun = runs.hasNext() ? runs.next() : null;
        }
        
        // process interval after the last run
        if (newLeft <= length - 1)
        {
            newRuns.add(new Run(newLeft, length - 1));
        }
        
        return new BinaryRow(newRuns);
    }
    
    /**
     * Apply crop on the specified row, ensuring the left (resp. right)
     * extremity of the first (resp. last) run within the row is not lower
     * (resp. greater) than the limit specified by <code>left</code> (resp.
     * <code>right</code>).
     * 
     * @param row
     *            the row to crop
     * @param left
     *            the left bound of the row
     * @param right
     *            the right bound of the row
     * @return a row with all run elements within the [left, right] interval.
     */
    public static final BinaryRow crop(BinaryRow row, int left, int right)
    {
        // allocate runs for the new row
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        for (Run run : row.runs)
        {
            // drop the runs totally before the "left" extremity
            if (run.right < left)
            {
                continue;
            }
            // stop processing the runs when we find one run totally after the right extremity
            if (run.left > right)
            {
                break;
            }
            
            int newLeft = Math.max(run.left, left);
            int newRight = Math.min(run.right, right);
            newRuns.add(new Run(newLeft, newRight));
        }
        
        return new BinaryRow(newRuns);
    }
    
    // TODO: implement a shift(int) method?
    
    /**
     * Private constructor to prevent instantiation.
     */
    private BinaryRows()
    {
    }
    
    /**
     * Keep references to both iterators, as well a reference to current runs in
     * each row.
     */
    static class RunIteratorPair
    {
        Iterator<Run> runs1;
        Iterator<Run> runs2;
        Run run1 = null;
        Run run2 = null;
        
        public RunIteratorPair(BinaryRow row1, BinaryRow row2)
        {
            this(row1.runs.iterator(), row2.runs.iterator());
        }
        
        public RunIteratorPair(Iterator<Run> runs1, Iterator<Run> runs2)
        {
            this.runs1 = runs1;
            this.runs2 = runs2;
            if (this.runs1.hasNext())
            {
                this.run1 = runs1.next();
            }
            if (this.runs2.hasNext())
            {
                this.run2 = runs2.next();
            }
        }

        public RunIteratorPair(Iterator<Run> runs1, Iterator<Run> runs2, Run run1, Run run2)
        {
            this.runs1 = runs1;
            this.runs2 = runs2;
            this.run1 = run1;
            this.run2 = run2;
        }
        
        public void swap()
        {
            Iterator<Run> runs0 = runs1;
            this.runs1 = this.runs2;
            this.runs2 = runs0;
            
            Run run0 = run1;
            this.run1 = this.run2;
            this.run2 = run0;
        }
    }
}
