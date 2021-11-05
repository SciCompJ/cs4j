/**
 * 
 */
package net.sci.array.binary;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * A single row within a multidimensional binary array, using run-length
 * encoding for representing binary data.
 * 
 * Note that the length of the row is not stored, and that the run indices may
 * be negative. The management of bounds is left to container classes and
 * processing algorithms.
 * 
 * @see Run
 * 
 * @author dlegland
 */
public class BinaryRow
{
    // =============================================================
    // Class fields

    /**
     * The (sorted) collection of Run instances for this row. 
     */
    TreeSet<Run> runs;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new empty binary row.
     */
    public BinaryRow()
    {
        this.runs = new TreeSet<>();
    }
    
    /**
     * Creates a new binary row by using an existing tree of Runs.
     * 
     * @param runs
     *            the set of runs representing this row.
     */
    public BinaryRow(TreeSet<Run> runs)
    {
        this.runs = runs;
    }
    
    
    // =============================================================
    // "High-Level" methods for global processing of rows

    /**
     * Computes the dilation this row using another row as structuring element.
     * 
     * @param row
     *            the row to dilate with.
     * @return the result of the dilation of the two rows.
     */
    public BinaryRow dilation(BinaryRow row)
    {
        BinaryRow res = new BinaryRow();
        for (Run run : row.runs)
        {
            BinaryRow resDil = this.dilation(-run.left, run.right);
            res = res.union(resDil);
        }
        return res;
    }
    
    private BinaryRow dilation(int leftDilate, int rightDilate)
    {
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        Iterator<Run> iter = this.runs.iterator();
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
    
    public BinaryRow union(BinaryRow row2)
    {
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        // create structure to iterate over both rows in parallel.
        RunIteratorPair rip = new RunIteratorPair(this, row2);
        
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
            newRight = rip.findRightExtremityOfUnion(newRight);
//            newRight = findRightExtremityOfUnion(rip, rip.run1.right);
            newRuns.add(new Run(newLeft, newRight));
            
            // process next run 
            rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
        }
        
        // create the new row from list of runs
        return new BinaryRow(newRuns);
    }
    
//    private static final int findRightExtremityOfUnion(RunIteratorPair rip, int newRight)
//    {
//        // process the loop until right extremity is found
//        while (true)
//        {
//            // identify the next run in row2 with a right extremity greater than current "newRight" value
//            findNextRunInRun2WithRightExtremityGreaterThanValue(rip, newRight);
//
//            // case of no more run in row2 with extremity after current run in row1 
//            if (rip.run2 == null)
//            {
//                return newRight;
//            }
//
//            // if we have found the current extremity, create new run and iterate 
//            if (rip.run2.left > newRight + 1)
//            {
//                return newRight;
//            }
//
//            // new extremity in run2
//            newRight = rip.run2.right;
//            
//            // as run2 is the new current run, need to swap
//            rip.swap();
//            
//            // iterate
//            rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
//        }
//    }
    
//    private static final Run findNextRunInRun2WithRightExtremityGreaterThanValue(RunIteratorPair rip, int value)
//    {
//        while (true)
//        {
//            if (rip.run2 == null)
//            {
//                return null;
//            }
//            if (rip.run2.right > value)
//            {
//                return rip.run2;
//            }
//            rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
//        }
//        
//    }
    
    
    /**
     * Computes the complement of this row, assuming it starts at index 0. As
     * row do not keep information about their length, it is necessary to
     * specify it as second argument.
     * 
     * @param length
     *            the length of the row.
     * @return a BinaryRow representing the complement of the input row.
     */
    public BinaryRow complement(int length)
    {
        // allocate runs for the new row
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        // case of empty row -> return a new full row
        if (this.isEmpty())
        {
            newRuns.add(new Run(0, length - 1));
            return new BinaryRow(newRuns);
        }
        
        // prepare iteration on runs of input row
        Iterator<Run> runIter = this.runs.iterator();
        Run nextRun = runIter.next();
        
        // new left is the first element set to false in input row
        int newLeft = 0;
        if (this.get(0))
        {
            // if input row start with foreground, update beginning of next run 
            newLeft = nextRun.right + 1;
            nextRun = runIter.hasNext() ? runIter.next() : null;
        }
        
        // iterate over intervals between runs
        while (nextRun != null)
        {
            newRuns.add(new Run(newLeft, nextRun.left - 1));
            newLeft = nextRun.right + 1;
            nextRun = runIter.hasNext() ? runIter.next() : null;
        }
        
        // process interval after the last run
        if (newLeft <= length - 1)
        {
            newRuns.add(new Run(newLeft, length - 1));
        }
        
        return new BinaryRow(newRuns);
    }
    
    /**
     * Applies crop on the specified row, ensuring the left (resp. right)
     * extremity of the first (resp. last) run within the row is not lower
     * (resp. greater) than the limit specified by <code>left</code> (resp.
     * <code>right</code>).
     * 
     * @param left
     *            the left bound of the result row
     * @param right
     *            the right bound of the result row
     * @return a row with all run elements within the [left, right] interval.
     */
    public BinaryRow crop(int left, int right)
    {
        // allocate runs for the new row
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        for (Run run : this.runs)
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
    
    /**
     * Creates a new row with all runs shifted to the left by the given amount.
     * 
     * @param shift
     *            the number of elements to shift
     * @return the shifted row
     */
    public BinaryRow shiftToLeft(int shift)
    {
        TreeSet<Run> newRuns = new TreeSet<Run>();
        for (Run run : this.runs)
        {
            newRuns.add(new Run(run.left - shift, run.right - shift));
        }
        return new BinaryRow(newRuns);
    }
    
    
    // =============================================================
    // Getter and Setter for data

    /**
     * @return the number of elements with value TRUE within this row.
     */
    public int elementCount()
    {
        int count = 0;
        for (Run run : runs)
        {
            count += run.length();
        }
        return count;
    }
    
    public boolean isEmpty()
    {
        return this.runs.isEmpty();
    }
    
    /**
     * Checks if the row contains the element at the specified position.
     * 
     * @param pos
     *            the index of the element (0-based).
     * @return true if the row contains the element at the specified
     *         position.
     */
    public boolean get(int pos)
    {
        for (Run run : runs)
        {
            if (run.contains(pos))
            {
                return true;
            }
        }
        return false;
    }
    
    public void set(int pos, boolean state)
    {
        if (state)
        {
            setTrue(pos);
        }
        else
        {
            setFalse(pos);
        }
    }
    
    private void setTrue(int pos)
    {
        // the two runs before and after, both can be null.
        Run prevRun = null;
        Run nextRun = null;
        
        // iterate over runs until we go ahead of last run
        for (Run run : runs)
        {
            if (run.left > pos)
            {
                nextRun = run;
                break;
            }
            
            if (run.contains(pos))
            {
                // already within a run, nothing to do...
                return;
            }
            
            // keep for further processing
            prevRun = run;
        }
        
        // is there a run just before?
        if (prevRun != null && prevRun.contains(pos - 1))
        {
            if (nextRun != null && nextRun.contains(pos + 1))
            {
                // merge the two runs separated by the element we just add 
                prevRun.right = nextRun.right;
                runs.remove(nextRun);
                return;
            }
            else
            {
                // append the element to the run just before
                prevRun.right++;
                return;
            }
        }
        
        // is there a run just after?
        if (nextRun != null && nextRun.contains(pos + 1))
        {
            nextRun.left--;
            return;
        }
        
        // otherwise, create a one-length run at specified position 
        Run run = new Run(pos, pos);
        this.runs.add(run);
    }

    private void setFalse(int pos)
    {
        // find the run that contains the position    
        Run run = findContainingRun(pos);
        
        // case of position outside any run 
        if (run == null)
        {
            return;
        }
        
        if (run.left == pos)
        {
            if (run.left != run.right)
            {
                // remove element at the beginning of a run
                run.left++;
            }
            else
            {
                // case of a one-length run -> remove it
                runs.remove(run);
            }
        }
        else if (pos == run.right)
        {
            // remove element at the end of a run
            run.right--;
        }
        else
        {
            // remove element in the middle of a run
            runs.add(new Run(pos + 1, run.right));
            run.right = pos - 1;
        }
    }
    
    // find the first run whose end is strictly after position
    // or null if there is not.
    private Run findContainingRun(int pos)
    {
        for (Run run : runs)
        {
            if (run.left > pos)
            {
                // as runs are sorted according to the left coordinate, we know
                // there is no other run after this one.
                return null;
            }
            if (pos <= run.right)
            {
                return run;
            }
        }
        return null;
    }
    
    public BinaryRow duplicate()
    {
        BinaryRow row = new BinaryRow();
        for (Run run : this.runs)
        {
            row.runs.add(run.duplicate());
        }
        return row;
    }

    /**
     * Keep references to both iterators, as well as references to current runs in
     * each row.
     */
    private static class RunIteratorPair
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
        
        public int findRightExtremityOfUnion(int newRight)
        {
            // process the loop until right extremity is found
            while (true)
            {
                // identify the next run in row2 with a right extremity greater than current "newRight" value
                findNextRunInRun2WithRightExtremityGreaterThanValue(newRight);

                // case of no more run in row2 with extremity after current run in row1 
                if (this.run2 == null)
                {
                    return newRight;
                }

                // if we have found the current extremity, create new run and iterate 
                if (this.run2.left > newRight + 1)
                {
                    return newRight;
                }

                // new extremity in run2
                newRight = this.run2.right;
                
                // as run2 is the new current run, need to swap
                this.swap();
                
                // iterate
                this.run1 = this.runs1.hasNext() ? this.runs1.next() : null;
            }
        }

        private Run findNextRunInRun2WithRightExtremityGreaterThanValue(int value)
        {
            while (true)
            {
                if (this.run2 == null)
                {
                    return null;
                }
                if (this.run2.right > value)
                {
                    return this.run2;
                }
                this.run2 = this.runs2.hasNext() ? this.runs2.next() : null;
            }
            
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

