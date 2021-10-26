/**
 * 
 */
package net.sci.array.binary;

import java.util.TreeSet;


/**
 * @author dlegland
 *
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
    // Methods

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
}

