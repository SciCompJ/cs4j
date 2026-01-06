/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * A single row within a multidimensional array of Int32, using run-length
 * encoding for representing int data.
 * 
 * Note that the length of the row is not stored, and that the run indices may
 * be negative. The management of bounds is left to container classes and
 * processing algorithms.
 * 
 * @see Int32Run
 * @see net.sci.array.binary.BinaryRow
 * 
 * @author dlegland
 */
public class Int32Row implements Iterable<Int32Run>
{
    // =============================================================
    // Class fields

    /**
     * The (sorted) collection of Run instances for this row. 
     */
    TreeMap<Integer, Int32Run> runs;
    
    
    // =============================================================
    // Constructors

    /**
     * Creates a new empty binary row.
     */
    public Int32Row()
    {
        this.runs = new TreeMap<>();
    }

    /**
     * Creates a new row of integers by using an existing collection of
     * Int32Run.
     * 
     * @param runs
     *            the set of runs representing this row.
     */
    public Int32Row(Collection<Int32Run> runs)
    {
        this.runs = new TreeMap<>();
        for (Int32Run run : runs)
        {
            this.runs.put(run.left, run);
        }
    }
    
    /**
     * Creates a new row of integers containing a single run.
     */
    public Int32Row(Int32Run run)
    {
        this.runs = new TreeMap<>();
        this.runs.put(run.left, run);
    }
    
    /**
     * Creates a new row of integers by using an existing map of Runs.
     * 
     * @param runs
     *            the map of runs representing this row.
     */
    public Int32Row(Map<Integer, Int32Run> runMap)
    {
        this.runs = new TreeMap<>();
        runs.putAll(runMap);
    }
    
    
    // =============================================================
    // Getter and Setter for data

    /**
     * @return the number of elements with non-zero value within this row.
     */
    public int elementCount()
    {
        int count = 0;
        for (Int32Run run : runs.values())
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
     * Returns the value of the row at the specified position.
     * 
     * @param pos
     *            the index of the element (0-based).
     * @return the value of the row at the specified position.
     */
    public int get(int pos)
    {
        // find the entry with greatest "left" value being lower than or equal to pos
        Entry<Integer, Int32Run> entry = this.runs.floorEntry(pos);
        if (entry == null)
        {
            return 0;
        }
        Int32Run run = entry.getValue();
        return pos <= run.right ? run.value : 0;
    }
    
    public void set(int pos, int value)
    {
        if (value != 0)
        {
            setValue(pos, value);
        }
        else
        {
            setToZero(pos);
        }
    }
    
    private void setValue(int pos, int value)
    {
        // retrieve the "current" run, that starts before position, if it exists
        Int32Run currRun = floorRun(pos);
        // Does current run (if it exist) contains position to update?
        if (currRun != null && currRun.contains(pos))
        {
            // if the run is associated with the same value, nothing to do
            if (currRun.value == value)
            {
                return;
            }

            // in case current run start at pos, we also need to check if
            // the run before can be expanded be one.
            if (currRun.left == pos)
            {
                Int32Run prevRun = floorRun(pos - 1);
                if (prevRun != null && prevRun.contains(pos - 1) && prevRun.value == value)
                {
                    // update previous run by expanding to the right
                    runs.put(prevRun.left, new Int32Run(prevRun.left, pos, value));
                    // if current run is greater than 1, update its left extremity, otherwise simply remove it
                    runs.remove(pos);
                    if (currRun.right > pos)
                    {
                        addNewRun(pos+1, currRun.right, currRun.value);
                    }
                    return;
                }
            }
            
            // otherwise, need to update current run
            // 1. make the current run end just before inserted value
            // (in case current run start at pos, just remove current run)
            runs.remove(currRun.left);
            if (currRun.left < pos)
            {
                addNewRun(currRun.left, pos - 1, currRun.value);
            }
            
            // 2. manage the end of the current run, between pos and currRun.rigth
            if (currRun.right > pos)
            {
                // create new single-position run
                addNewRun(pos, pos, value);
                // add the remaining of previous run after current position
                addNewRun(pos+1, currRun.right, currRun.value);
                return;
            }
            else
            {
                // pos is at the right end of current run:
                // nothing to add from current run, but need to check if we can expand from next run
                // retrieve the run after position, if it exists
                Int32Run nextRun = floorRun(pos + 1);
                if (nextRun != null && nextRun.contains(pos + 1) && nextRun.value == value)
                {
                    // expand the next run from the left
                    runs.remove(pos+1);
                    addNewRun(pos, nextRun.right, value);
                }
                else
                {
                    // create new single-position run
                    addNewRun(pos, pos, value);
                }
                return;
            }
        }
        
        // retrieve the run after position, if it exists
        Int32Run nextRun = floorRun(pos + 1);

        // case of 'joining' addition
        
        // is there a run just before (with same value)?
        if (currRun != null && currRun.contains(pos - 1) && currRun.value == value)
        {
            if (nextRun != null && nextRun.contains(pos + 1)  && nextRun.value == value)
            {
                // merge the two runs separated by the element we just added
                Int32Run newRun = new Int32Run(currRun.left, nextRun.right, value);
                // replace the two runs by the new one
                runs.put(currRun.left, newRun);
                runs.remove(nextRun.left);
                return;
            }
            else
            {
                // replace current run by a new run extended to the right
                Int32Run newRun = new Int32Run(currRun.left, pos, value);
                runs.put(currRun.left, newRun);
                return;
            }
        }
        
        // no run before, but is there a run just after?
        if (nextRun != null && nextRun.contains(pos + 1) && nextRun.value == value)
        {
            // replace the old run by a new run extended to the left
            runs.remove(nextRun.left);
            addNewRun(pos, nextRun.right, value);
            return;
        }
        
        // otherwise, create a one-length run at specified position 
        addNewRun(pos, pos, value);
    }

    private void setToZero(int pos)
    {
        // find the run that contains the position    
        Int32Run run = containingRun(pos);
        
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
                addNewRun(run.left + 1, run.right, run.value);
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
            runs.put(run.left, new Int32Run(run.left, run.right - 1, run.value));
        }
        else
        {
            // Need to split a run in two parts:
            // 1. replace the current run by a new smaller run
            runs.put(run.left, new Int32Run(run.left, pos - 1, run.value));
            // 2. add a new run just after the split 
            addNewRun(pos + 1, run.right, run.value);
        }
    }
    

    /**
     * Returns the number of runs within this row.
     * 
     * @return the number of runs within this row.
     */
    public int runCount()
    {
        return runs.size();
    }
    
    public Collection<Int32Run> runs()
    {
        return Collections.unmodifiableCollection(this.runs.values());
    }
    
    private void addNewRun(int left, int right, int value)
    {
        Int32Run run = new Int32Run(left, right, value);
        this.runs.put(left, run);
    }
    
    /**
     * Returns the right-most run with left extremity lower than or equal to
     * pos, if it exists, or null otherwise.
     * 
     * @param pos
     *            the x-position of an element within the row
     * @return the right-most run that could contain the specified position, or
     *         null if there is no such Run.
     */
    private Int32Run floorRun(int pos)
    {
        Entry<Integer, Int32Run> entry = this.runs.floorEntry(pos);
        return entry != null ? entry.getValue() : null;
    }
    
    /**
     * Returns the run containing the specified position, if it exists, or null
     * otherwise. The containing run must have left extremity lower than or
     * equal to <code>pos</code>, and right extremity greater than or equal to
     * <code>pos</code>.
     * 
     * @param pos
     *            the x-position of an element within the row
     * @return the Run containing the specified position, or null if there is no
     *         such Run (the state at the given position is "false").
     */
    public Int32Run containingRun(int pos)
    {
        // find the entry with greatest "left" value being lower than or equal to pos
        Entry<Integer, Int32Run> entry = this.runs.floorEntry(pos);
        if (entry == null)
        {
            return null;
        }
        Int32Run run = entry.getValue();
        return pos <= run.right ? run : null;
    }
    

    // =============================================================
    // Implementation of the Iterable interface
    
    @Override
    public Iterator<Int32Run> iterator()
    {
        return this.runs.values().iterator();
    }

}
