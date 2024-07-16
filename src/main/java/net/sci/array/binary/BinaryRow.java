/**
 * 
 */
package net.sci.array.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

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
public class BinaryRow implements Iterable<Run>
{
    // =============================================================
    // Static methods

    /**
     * Computes the union of two rows.
     * 
     * @param row1
     *            the first row
     * @param row2
     *            the second row
     * @return the result if the union of the two rows
     */
    public static final BinaryRow union(BinaryRow row1, BinaryRow row2)
    {
        // create empty result row
        BinaryRow resRow = new BinaryRow();
        
        // create structure to iterate over both rows in parallel.
        RunIteratorPair rip = new RunIteratorPair(row1, row2);
        
        // main iteration, while there are remaining runs in both row1 and row2,
        // and breaking when either run1 or run2 becomes null
        // (assumes we start from background)
        while(rip.run1 != null && rip.run2 != null)
        {
            resRow.addRun(createNextUnionRun(rip));
        }
        
        // one of the two rows has no more runs, but there may be some runs left
        // in the other
        if (rip.run1 != null)
        {
            // add remaining runs of row1
            resRow.addRun(rip.run1);
            while (rip.runs1.hasNext())
            {
                resRow.addRun(rip.runs1.next());
            }
        } 
        else if (rip.run2 != null)
        {
            // add remaining runs of row2
            resRow.addRun(rip.run2);
            while (rip.runs2.hasNext())
            {
                resRow.addRun(rip.runs2.next());
            }
        }
        
        // create the new row from list of runs
        return resRow;
    }
    
    /**
     * Iterates over the runs until we find the right extremity of the current
     * union of runs, and returns the new run resulting from the union.
     * Requires <code>rip.run1</code> and <code>rip.run2</code> to be non null.
     * 
     * Removes all the runs (in each row) whose right extremity is before or
     * equal to the returned value.
     * 
     * @param rip
     *            the iterator over the pair of runs. The current runs will be
     *            updated during the search.
     * @param newRight
     *            the initial value for finding right extremity of current union
     * @return the position of the right extremity of the union
     */
    private static final Run createNextUnionRun(RunIteratorPair rip)
    {
        // start processing with the run with the lowest left value
        // -> enforce run1 to have the lowest left value 
        if (rip.run1.left > rip.run2.left)
        {
            rip.swap();
        }

        // initialize new run with current run1
        int newLeft = rip.run1.left;
        int newRight = rip.run1.right;
        
        // process next run 
        rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
        
        // process the loop until right extremity is found
        while (true)
        {
            // identify the next run in row2 with a right extremity greater than
            // current "newRight" value
            // (uses short-circuit evaluation)
            while(rip.run2 != null && rip.run2.right <= newRight)
            {
                rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
            }
            
            // if there is no more run in second row, then the current right
            // is the one from current run
            if (rip.run2 == null)
            {
                return new Run(newLeft, newRight);
            }

            // if the next run in second row starts *after* the end of
            // current run (with at least one pixel in between), then we
            // need to stop iteration to create a new run.
            if (rip.run2.left > newRight + 1)
            {
                return new Run(newLeft, newRight);
            }
            
            // run2 ends after run1. Need to update right extremity and
            // check the other runs in first row.
            newRight = rip.run2.right;
            rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
            
            // as run2 is the new current run, need to swap
            rip.swap();
        }
    }
    
    /**
     * Computes the intersection of this row with the input row.
     * 
     * @param row2
     *            the row to combine with
     * @return the result if the intersection of this row and the other row.
     */
    public static final BinaryRow intersection(BinaryRow row1, BinaryRow row2)
    {
        // create empty result row
        BinaryRow row = new BinaryRow();
        
        // create structure to iterate over both rows in parallel.
        RunIteratorPair rip = new RunIteratorPair(row1, row2);
        
        // main iteration, breaking when either run1 or run2 is null
        while(rip.run1 != null && rip.run2 != null)
        {
            // start processing with the run with the lowest left value
            // -> enforce run1 to have the lowest left value 
            if (rip.run1.left > rip.run2.left)
            {
                rip.swap();
            }
            
            // if the run in row2 start after the end of run1, there is no intersection
            // and we skip current run1 
            if (rip.run2.left > rip.run1.right)
            {
                // process next run 
                rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
                continue;
            }
            
            // we have: run1.left <= run2.left <= run1.right
            // compute bounds of the new run
            int newLeft = rip.run2.left;
            int newRight;
            if (rip.run1.right < rip.run2.right)
            {
                // run1 ends before run2
                newRight = rip.run1.right;
                rip.run1 = rip.runs1.hasNext() ? rip.runs1.next() : null;
            }
            else
            {
                // run2 ends before run1
                newRight = rip.run2.right;
                rip.run2 = rip.runs2.hasNext() ? rip.runs2.next() : null;
            }
            row.runs.put(newLeft, new Run(newLeft, newRight));
        }
        
        // create the new row from list of runs
        return row;
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The (sorted) collection of Run instances for this row. 
     */
    TreeMap<Integer, Run> runs;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new empty binary row.
     */
    public BinaryRow()
    {
        this.runs = new TreeMap<>();
    }
    
    /**
     * Creates a new binary row by using an existing collection of Runs.
     * 
     * @param runs
     *            the set of runs representing this row.
     */
    public BinaryRow(Collection<Run> runs)
    {
        this.runs = new TreeMap<>();
        for (Run run : runs)
        {
            this.runs.put(run.left, run);
        }
    }
    
    /**
     * Creates a new binary row containing a single run.
     */
    public BinaryRow(Run run)
    {
        this.runs = new TreeMap<>();
        this.runs.put(run.left, run);
    }
    
    /**
     * Creates a new binary row by using an existing map of Runs.
     * 
     * @param runs
     *            the map of runs representing this row.
     */
    public BinaryRow(Map<Integer, Run> runMap)
    {
        this.runs = new TreeMap<>();
        runs.putAll(runMap);
    }
    
    
    // =============================================================
    // "High-Level" methods for global processing of rows
    
    /**
     * Complement the states of the row between the specified (included) bounds.
     * 
     * @param leftBound
     *            the left bound of the range to complement
     * @param rightBound
     *            the right bound of the range to complement
     * @return a new BinaryRow with same values as input row, except within the
     *         range to complement.
     */
    public BinaryRow complement(int leftBound, int rightBound)
    {
        // case of empty row -> return a new row with a single run defined by
        // the argument bounds
        if (this.isEmpty())
        {
            return new BinaryRow(new Run(leftBound, rightBound));
        }
        
        // initialize the map of new runs
        TreeMap<Integer, Run> resRuns = new TreeMap<>();
        
        // the current run
        Run run = null;
        
        // index of the first element set to false in input row
        // (as a possibly negative integer)
        int pos = leftBound;
        
        // prepare iteration on runs of input row
        Iterator<Run> runIter = this.runs.values().iterator();
        
        // process the runs that start before left bound
        while (runIter.hasNext())
        {
            run = runIter.next();
            
            // check if the run is totally before left bound, with an empty
            // space before the start of the range to complement
            if (run.right < leftBound - 1)
            {
                // simply copy the run
                resRuns.put(run.left, run);
                continue;
            }
            
            // special case of a run that ends just before the complement range
            if (run.right == leftBound - 1)
            {
                // need to create a new run with extended length
                int currentLeft = run.left;
                // update current run
                run = runIter.hasNext() ? runIter.next() : null;

                // check if there are other run(s) starting within complement range
                // (or just after, actually)
                if (run == null || run.left > rightBound + 1)
                {
                    resRuns.put(currentLeft, new Run(currentLeft, rightBound));
                    pos = rightBound + 1;
                }
                else
                {
                    // next run start in range to complement;
                    // first extend result run until beginning of input run,
                    resRuns.put(currentLeft, new Run(currentLeft, run.left - 1));
                    // then update current run and current position
                    pos = run.right + 1;
                    run = runIter.hasNext() ? runIter.next() : null;
                }
                
                // by definition, we reached the beginning of complement range
                break;
            }
            
            // case of a run starting before and terminating after the left bound
            if (run.left < leftBound)
            {
                // copy the portion of the run not belonging to bounds
                resRuns.put(run.left, new Run(run.left, leftBound - 1));
                // keep the remaining of current run as new current run
                run = new Run(leftBound, run.right);
            }
            
            break;
        }
        
        // Check the if current run starts at the beginning of the range
        // (otherwise, pos starts after the end of a run).
        if (run != null && run.left == pos)
        {
            // if input row start with foreground, update beginning of next run 
            pos = run.right + 1;
            run = runIter.hasNext() ? runIter.next() : null;
        }
        
        // iterate over intervals between runs
        while(pos <= rightBound && run != null)
        {
            // check if current run reaches right bound
            if (run.left > rightBound)
            {
                break;
            }
            
            // fill he interval between runs
            resRuns.put(pos, new Run(pos, run.left - 1));

            // update iteration
            pos = run.right + 1;
            run = runIter.hasNext() ? runIter.next() : null;
        }
        
        // process interval after the last run
        if (pos <= rightBound)
        {
            resRuns.put(pos, new Run(pos, rightBound));
        }
        
        // add the remaining runs, that start after right bound
        while (run != null)
        {
            // fill he interval between runs
            resRuns.put(run.left, run);

            // update iteration
            run = runIter.hasNext() ? runIter.next() : null;
        }

        return new BinaryRow(resRuns);
    }
    
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
        return complement(0, length - 1);
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
        // create empty result row
        BinaryRow res = new BinaryRow();
        
        // identifies the entries (if they exist) containing run with left
        // extremity before crop bounds
        Entry<Integer,Run> firstEntry = this.runs.floorEntry(left); 
        Entry<Integer,Run> lastEntry = this.runs.floorEntry(right);
        
        // create sub-map
        SortedMap<Integer, Run> subMap = null;
        if (firstEntry == null)
        {
            if (lastEntry == null)
            {
                // no run within the crop -> return empty row
                return new BinaryRow();
            }
            else
            {
                // consider all entries up to "right"
                subMap = runs.headMap(lastEntry.getKey(), true);
            }
        } 
        else
        {
            // consider all entries between left and right
            // (by construction, lastEntry can not be null if firstEntry is
            // defined, while they can be equal)
            subMap = runs.subMap(firstEntry.getKey(), true, lastEntry.getKey(), true);
        }
        
        // iterate over a selection of runs
        for (Run run : subMap.values())
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
            
            res.addNewRun(newLeft, newRight);
        }
        
        return res;
    }
    
    /**
     * Creates a new row with all runs shifted to the right by the given amount.
     * The shifted runs are obtained by adding the given amount to the left and
     * right extremities of the run.
     * 
     * @param shift
     *            the number of elements to shift
     * @return the shifted row
     */
    public BinaryRow shift(int shift)
    {
        BinaryRow row = new BinaryRow();
        for (Run run : this.runs.values())
        {
            row.addNewRun(run.left + shift, run.right + shift);
        }
        return row;
    }
    
    
    // =============================================================
    // Range management

    /**
     * Checks if the row contains all the elements between the positions
     * <code>x1</code> (included) and <code>x2</code> (included).
     * 
     * @param x1
     *            the first element in the range
     * @param x2
     *            the last element in the range
     * @return true is the region contains all the elements
     */
    public boolean containsRange(int x1, int x2)
    {
        // check if a single run contains both extremities of the range
        Run run = containingRun(x1);
        if (run == null)
        {
            return false;
        }
        return run.right >= x2;
    }
    
    public void setRange(int x1, int x2, boolean state)
    {
        if (state)
        {
            setRangeTrue(x1, x2);
        }
        else
        {
            setRangeFalse(x1, x2);
        }
    }
    
    private void setRangeTrue(int x1, int x2)
    {
        int newLeft = x1;
        int newRight = x2;
            
        Run run0 = containingRun(x1 - 1);
        if (run0 != null)
        {
            if (run0.contains(x2)) 
            {
                // nothing to do
                return;
            }

            // remove the run that contains x1
            runs.remove(run0.left);
            // and keep start position of the new run
            newLeft = run0.left;
        }
        
        // need to remove all the runs that start between x1 and x2
        Entry<Integer, Run> entry = runs.ceilingEntry(x1 + 1);
        while(entry != null)
        {
            // stop iteration when current run reaches the right extremity of range
            Run run = entry.getValue();
            if (run.right > x2 + 1)
            {
                // if current run contains the right extremity of range, update the right limit of the new run
                if (run.left <= x2 + 1)
                {
                    newRight = run.right;
                    runs.remove(run.left);
                }
                break;
            }

            // remove all entries that ends before x2
            runs.remove(run.left);
            entry = runs.ceilingEntry(run.right + 1);
        }

        // add a new run with the new interval
        addNewRun(newLeft, newRight);
    }
    
    private void setRangeFalse(int x1, int x2)
    {
        if (runs.isEmpty()) return;
            
        // first, need to identify first run that contains the beginning of the range
        Run extremityRun = containingRun(x1);
        if (extremityRun != null)
        {
            // remove current run from the tree
            runs.remove(extremityRun.left);
            
            // add the beginning of the run as new run
            if (extremityRun.left < x1)
            {
                addNewRun(extremityRun.left, x1 - 1);
            }
            // check if we need to add the end of the run as new run
            if (extremityRun.right > x2)
            {
                addNewRun(x2 + 1, extremityRun.right);
                // as we reach the end of the range, we can terminate.
                return;
            }
        }
        
        // remove all the runs totally included within range
        ArrayList<Integer> keysToRemove = new ArrayList<Integer>();
        extremityRun = null;
        for (Run run : runs.tailMap(x1).values())
        {
            // remove inner runs 
            if (run.left <= x2)
            {
                keysToRemove.add(run.left);
                
                // if iterate within the range, we can update the extremity run to
                // keep reference to the last run within range
                extremityRun = run;
            }
            if (run.right > x2)
            {
                break;
            }
        }
        for (int key : keysToRemove)
        {
            runs.remove(key);
        }

        // manage the case of a run that contains the end of the range
        // this can be the run found at the beginning
        if (extremityRun != null)
        {
            if (extremityRun.right > x2)
            {
                // add the complement of the initial run
                addNewRun(x2 + 1, extremityRun.right);
            }
        }
    }
    
    
    // =============================================================
    // Getter and Setter for data

    /**
     * @return the number of elements with value TRUE within this row.
     */
    public int elementCount()
    {
        int count = 0;
        for (Run run : runs.values())
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
        // find the entry with greatest "left" value being lower than or equal to pos
        Entry<Integer, Run> entry = this.runs.floorEntry(pos);
        if (entry == null)
        {
            return false;
        }
        Run run = entry.getValue();
        return pos <= run.right;
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
        // retrieve the run before position, if it exists
        Run prevRun = null;
        Entry<Integer, Run> entry = this.runs.floorEntry(pos);
        if (entry != null)
        {
            prevRun = entry.getValue();
            // if the run contains the position, nothing to do.
            if (prevRun.contains(pos))
            {
                return;
            }
        }
        
        // retrieve the run after position, if it exists
        entry = runs.ceilingEntry(pos);
        Run nextRun = entry != null ? entry.getValue() : null;
        
        // is there a run just before?
        if (prevRun != null && prevRun.contains(pos - 1))
        {
            if (nextRun != null && nextRun.contains(pos + 1))
            {
                // merge the two runs separated by the element we just added
                Run newRun = new Run(prevRun.left, nextRun.right);
                // replace the two runs by the new one
                runs.remove(prevRun.left);
                runs.remove(nextRun.left);
                runs.put(newRun.left, newRun);
                return;
            }
            else
            {
                // replace previous run by a new run extended to the right
                Run newRun = new Run(prevRun.left, prevRun.right + 1);
                runs.put(prevRun.left, newRun);
                return;
            }
        }
        
        // is there a run just after?
        if (nextRun != null && nextRun.contains(pos + 1))
        {
            // replace the old run by a new run extended to the left
            runs.remove(nextRun.left);
            addNewRun(nextRun.left - 1, nextRun.right);
            return;
        }
        
        // otherwise, create a one-length run at specified position 
        addNewRun(pos, pos);
    }

    private void setFalse(int pos)
    {
        // find the run that contains the position    
        Run run = containingRun(pos);
        
        // case of position outside any run 
        if (run == null)
        {
            return;
        }
        
        if (run.left == pos)
        {
            if (run.left != run.right)
            {
                // replace the old run by a new run eroded from the left
                runs.remove(run.left);
                addNewRun(run.left + 1, run.right);
            }
            else
            {
                // case of a one-length run -> remove it
                runs.remove(run.left);
            }
        }
        else if (pos == run.right)
        {
            // replace the current run by a new one eroded from the right
            runs.put(run.left, new Run(run.left, run.right - 1));
        }
        else
        {
            // Need to split a run in two parts:
            // 1. replace the current run by a new smaller run
            runs.put(run.left, new Run(run.left, pos - 1));
            // 2. add a new run just after the split 
            addNewRun(pos + 1, run.right);
        }
    }
    
    /**
     * @return the number of runs within this row.
     */
    public int runCount()
    {
        return runs.size();
    }
    
    public Collection<Run> runs()
    {
        return Collections.unmodifiableCollection(this.runs.values());
    }
    
    private void addRun(Run run)
    {
        this.runs.put(run.left, run);
    }
    
    private void addNewRun(int left, int right)
    {
        Run run = new Run(left, right);
        this.runs.put(left, run);
    }
    
    /**
     * @param run
     *            a query run
     * @return all the runs within the row with an element contained inside the
     *         query run.
     */
    public Collection<Run> intersectingRuns(Run run)
    {
        ArrayList<Run> res = new ArrayList<>();
        
        for (Run rowRun : runs.values())
        {
            if ((rowRun.left <= run.left && rowRun.right >= run.left) ||
                    (rowRun.left >= run.left && rowRun.left <= run.right))
            {
                res.add(rowRun);
            }
        }
        return res;
    }
    
    /**
     * @param posMin
     *            a position within the row
     * @param posMax
     *            a position within the row, greater than or equal to posMin
     * @return all the runs within the row with an element between posMin and
     *         posMax.
     */
    public Collection<Run> containingRuns(int posMin, int posMax)
    {
        ArrayList<Run> res = new ArrayList<>();
        
        // need to check if there is a run that contains posMin while starting before
        Map.Entry<Integer,Run> entry = runs.lowerEntry(posMin);
        if (entry != null)
        {
            Run run = entry.getValue();
            if (run.right > posMin)
            {
                res.add(run);
            }
        }
        
        // add the runs that start after posMin
        // (and start before posMax+1)
        for (Run run : runs.tailMap(posMin).values())
        {
            if (run.left > posMax)
            {
                break;
            }
            res.add(run);
        }
        return res;
    }
    
    /**
     * @param pos
     *            the x-position of an element within the row
     * @return the Run containing the specified position, or null if there is no
     *         such Run (the state at the given position is "false").
     */
    public Run containingRun(int pos)
    {
        // find the entry with greatest "left" value being lower than or equal to pos
        Entry<Integer, Run> entry = this.runs.floorEntry(pos);
        if (entry == null)
        {
            return null;
        }
        Run run = entry.getValue();
        return pos <= run.right ? run : null;
    }
    
    public BinaryRow duplicate()
    {
        BinaryRow row = new BinaryRow();
        for (Run run : this.runs.values())
        {
            row.runs.put(run.left, run.duplicate());
        }
        return row;
    }
    
    @Override
    public Iterator<Run> iterator()
    {
        return this.runs.values().iterator();
    }

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("BinaryRow with %d run%s: ", runs.size(), runs.size()!=1 ? "s" : ""));
        Iterator<Run> iter = runs.values().iterator();
        if (iter.hasNext())
        {
            sb.append("{");
            sb.append(iter.next().toString());
            while(iter.hasNext())
            {
                sb.append(", ");
                sb.append(iter.next().toString());
            }
            sb.append("}");
        }
        return sb.toString();
    }


    // =============================================================
    // Inner class implementations
    
    /**
     * Keep references to both iterators, as well as references to the current
     * run in each row.
     * 
     * Used to compute the union or the intersection of two BinaryRow instances. 
     */
    private static final class RunIteratorPair
    {
        Iterator<Run> runs1;
        Iterator<Run> runs2;
        Run run1 = null;
        Run run2 = null;
        
        public RunIteratorPair(BinaryRow row1, BinaryRow row2)
        {
            this(row1.runs.values().iterator(), row2.runs.values().iterator());
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
