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
    public static final BinaryRow union(BinaryRow row1, BinaryRow row2)
    {
        RunIteratorPair rip = new RunIteratorPair(row1, row2);
        
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        // main iteration, breaking when both run1 and run2 are null
        // (assumes we start from background)
        do
        {
            // if both rows are empty, return new empty row
            if (rip.run1 == null && rip.run2 == null)
            {
                break;
            }

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

            int newLeft = rip.run1.left;
            int newRight = rip.run1.right;
            
            newRight = findRightExtremityOfUnion(rip, rip.run1.right);
            
            newRuns.add(new Run(newLeft, newRight));
            continue;

//            //            while 
//
//            // identify the next position with 0 value
//
//            // identify the next run in row2 with a right extremity greater than current "newRight" value
//            rip.run2 = findNextRunWithRightExtremityGreaterThanValue(rip.runs2, newRight);
//            
//            // case of no more run in row2 with extremity after current run in row1 
//            if (rip.run2 == null)
//            {
//                newRuns.add(new Run(newLeft, newRight));
//                continue;
//            }
//
//            // if we have found the current extremity, create new run and iterate 
//            if (rip.run2.left > newRight + 1)
//            {
//                newRuns.add(new Run(newLeft, newRight));
//                continue;
//            }
//
//            // new extremity in run2
//            newRight = rip.run2.right;
     
        } while (true);
        
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
            rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
            
            // as run2 is the new current run, need to swap
            rip.swap();
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
    
//
//    private static final Run findNextRunWithRightExtremityGreaterThanValue(Iterator<Run> iter, int value)
//    {
//        while (iter.hasNext())
//        {
//            Run run = iter.next();
//            if (run.right > value)
//            {
//                return run;
//            }
//        }
//        return null;
//    }
    
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
