/**
 * 
 */
package net.sci.image.region;

import java.util.HashMap;
import java.util.Iterator;

import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;

/**
 * A region within an array, without explicit specification of array bounds.
 * 
 * @see BinaryRow
 */
public class BinaryRegion2D implements BinaryRegion
{
    // =============================================================
    // Class fields
    
    /**
     * The collection of binary rows that compose the region.
     */
    HashMap<Integer, BinaryRow> rows;
    
    
    // =============================================================
    // Constructors

    /**
     * Empty constructor
     */
    public BinaryRegion2D()
    {
        this.rows = new HashMap<Integer, BinaryRow>();
    }
    
    
    // =============================================================
    // Methods
    
    public boolean isEmpty()
    {
        return this.rows.isEmpty();
    }

    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount()
    {
        long count = 0;
        for (BinaryRow row : rows.values())
        {
            count += row.elementCount();
        }
        return count;
    }
    
    public boolean get(int x, int y)
    {
        BinaryRow row = rows.get(y);
        if (row == null) return false;
        return row.get(x);
    }
    
    public void set(int x, int y, boolean state)
    {
        if (rows.containsKey(y))
        {
            // update existing row
            BinaryRow row = rows.get(y);
            row.set(x, state);
            
            // in case row becomes empty, removes it from row array
            if (row.isEmpty())
            {
                rows.remove(y);
            }
        }
        else
        {
            // if state is false and row is empty, nothing changes
            if (!state) return;
            
            // create a new row containing only the specified element
            BinaryRow row = new BinaryRow();
            row.set(x, true);
            rows.put(y, row);
        }
    }
    
    /**
     * Allows to iterate over positions in a region.
     * 
     * {@snippet :
     * for (int[] pos : region.positions())
     * {
     *     doProcessing(array, pos);
     * }
     * }
     * </pre>
     * 
     * @see #positionIterator()
     * 
     * @return an Iterable over region positions.
     */
    @Override
    public Iterable<int[]> positions()
    {
        return new Iterable<int[]>()
        {
            public java.util.Iterator<int[]> iterator()
            {
                return positionIterator();
            }
        };
    }
    
    public BinaryRegion.PositionIterator positionIterator()
    {
        return new PositionIterator();
    }
    
    
    // =============================================================
    // Implementation of the Dimensional interface

    @Override
    public int dimensionality()
    {
        return 2;
    }
    
    
    // =============================================================
    // Implementation of an iterator class over positions
    
    private class PositionIterator implements BinaryRegion.PositionIterator
    {
        /**
         * Iterator over the rows. Uses HashMap entry in order to keep row index
         * information.
         */
        Iterator<HashMap.Entry<Integer,BinaryRow>> rowIterator;
        HashMap.Entry<Integer,BinaryRow> currentRow;
        Iterator<Run> runIterator;
        Run currentRun = null;
        /** index of position within the run. If equals to -1, no more position.*/
        int posIndex = -1;
        
        public PositionIterator()
        {
            // setup row iterator and current row
            rowIterator = rows.entrySet().iterator();
            if (!rowIterator.hasNext()) return;
            currentRow = rowIterator.next();
            
            // setup run iterator and current run
            runIterator = currentRow.getValue().iterator();
            if (!runIterator.hasNext()) return;
            currentRun = runIterator.next();
            posIndex = 0;
        }
        
        @Override
        public boolean hasNext()
        {
            return posIndex >= 0;
        }

        @Override
        public int[] next()
        {
            int[] pos = new int[2];
            next(pos);
            return pos;
        }
        
        public void next(int[] pos)
        {
            pos[0] = this.currentRun.left + posIndex;
            pos[1] = this.currentRow.getKey();
            incrementPosition();
        }
        
        private void incrementPosition()
        {
            posIndex++;
            if (posIndex >= currentRun.length()) nextRun();
        }
        
        private void nextRun()
        {
            posIndex = -1;
            if (runIterator.hasNext()) 
            {
                currentRun = runIterator.next();
                posIndex = 0;
            }
            else if (rowIterator.hasNext())
            {
                // switch to next row, and call again nextRun()
                nextRow();
                nextRun();
            }
        }
        
        private void nextRow()
        {
            currentRow = rowIterator.next();
            runIterator = currentRow.getValue().iterator();
        }
    }
}
