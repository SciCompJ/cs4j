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
        // retrieve the run before position, if it exists
        Entry<Integer, Int32Run> entry = this.runs.floorEntry(pos);
        Int32Run prevRun = entry != null ? entry.getValue() : null;
        
        // Does previous run (if it exist) contains position to update?
        if (prevRun != null && prevRun.contains(pos))
        {
            // if the run is associated with the same value, nothing to do
            if (prevRun.value == value)
            {
                return;
            }

            // otherwise, need to update previous run
            // 1. make the previous run end just before inserted value
            // (in case previous run start at pos, just remove current run)
            runs.remove(prevRun.left);
            if (prevRun.left < pos)
            {
                addNewRun(prevRun.left, pos - 1, prevRun.value);
            }
            
            // 2. manage the end of the previous run, between pos and prevRun.rigth
            if (prevRun.right > pos)
            {
                // create new single-position run
                addNewRun(pos, pos, value);
                // add the remaining of previous run after current position
                addNewRun(pos+1, prevRun.right, prevRun.value);
                return;
            }
            else
            {
                // pos is at the right end of previous run:
                // nothing to add from previous run, but need to check if we can expand from next run
                // retrieve the run after position, if it exists
                entry = runs.ceilingEntry(pos);
                Int32Run nextRun = entry != null ? entry.getValue() : null;
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
        entry = runs.ceilingEntry(pos);
        Int32Run nextRun = entry != null ? entry.getValue() : null;

        // case of 'joining' addition
        
        // is there a run just before (with same value)?
        if (prevRun != null && prevRun.contains(pos - 1) && prevRun.value == value)
        {
            if (nextRun != null && nextRun.contains(pos + 1)  && nextRun.value == value)
            {
                // merge the two runs separated by the element we just added
                Int32Run newRun = new Int32Run(prevRun.left, nextRun.right, value);
                // replace the two runs by the new one
                runs.remove(prevRun.left);
                runs.remove(nextRun.left);
                runs.put(newRun.left, newRun);
                return;
            }
            else
            {
                // replace previous run by a new run extended to the right
                Int32Run newRun = new Int32Run(prevRun.left, pos, value);
                runs.put(prevRun.left, newRun);
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
