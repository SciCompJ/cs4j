/**
 * 
 */
package net.sci.image.region;

import java.util.HashMap;
import java.util.Iterator;

import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;

/**
 * A 3D region within an array, without explicit specification of array bounds.
 * 
 * @see BinaryRegion2D
 */
public class BinaryRegion3D implements BinaryRegion
{
    // =============================================================
    // Class fields
    
    /**
     * The collection of 2D binary regions that compose this 3D region.
     */
    HashMap<Integer, HashMap<Integer, BinaryRow>> slices;
    
    
    // =============================================================
    // Constructors

    /**
     * Empty constructor
     */
    public BinaryRegion3D()
    {
        this.slices = new HashMap<>();
    }
    
    
    // =============================================================
    // Getter and Setter
    
    /**
     * Returns a boolean flag indicating whether the region contains the
     * specified position.
     * 
     * @param x
     *            the x-coordinate of the region.
     * @param y
     *            the y-coordinate of the region
     * @param z
     *            the z-coordinate of the region
     * @return true if the region contains the specified coordinates
     */
    public boolean get(int x, int y, int z)
    {
        HashMap<Integer, BinaryRow> slice = slices.get(z);
        if (slice == null) return false;
        BinaryRow row = slice.get(y);
        if (row == null) return false;
        return row.get(x);
    }

    /**
     * Updates the content of the region for the specified position.
     * 
     * @param x
     *            the x-coordinate of the region.
     * @param y
     *            the y-coordinate of the region
     * @param z
     *            the z-coordinate of the region
     * @param state
     *            the new state of the region for the specified position
     */
    public void set(int x, int y, int z, boolean state)
    {
        if (state)
        {
            setTrue(x, y, z);
        }
        else
        {
            setFalse(x, y, z);
        }
    }

    private void setTrue(int x, int y, int z)
    {
        HashMap<Integer, BinaryRow> slice = slices.get(z);
        if (slice == null)
        {
            slice = new HashMap<Integer, BinaryRow>();
            slices.put(z, slice);
        }
        
        BinaryRow row = slice.get(y);
        if (row == null)
        {
            row = new BinaryRow(new Run(x, x));
            slice.put(y, row);
        }
        else
        {
            row.set(x, true);
        }
    }

    private void setFalse(int x, int y, int z)
    {
        HashMap<Integer, BinaryRow> slice = slices.get(z);
        if (slice == null) return;
        BinaryRow row = slice.get(y);
        if (row == null) return;
        row.set(x, false);
        if (row.isEmpty())
        {
            slice.remove(y);
            if (slice.isEmpty())
            {
                slices.remove(z);
            }
        }
    }


    // =============================================================
    // Methods implementing the BinaryRegion interface
    
    public int[][] intBounds()
    {
        // initializes with arbitrary large values
        int xmin = Integer.MAX_VALUE;
        int xmax = Integer.MIN_VALUE;
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;
        int zmin = Integer.MAX_VALUE;
        int zmax = Integer.MIN_VALUE;
        
        // iterate over all rows
        for (HashMap.Entry<Integer, HashMap<Integer, BinaryRow>> slice : slices.entrySet())
        {
            // update z bounds
            int z = slice.getKey();
            zmin = Math.min(z, zmin);
            zmax = Math.max(z, zmax);
            
            HashMap<Integer, BinaryRow> rows = slice.getValue();
            for (HashMap.Entry<Integer, BinaryRow> row : rows.entrySet())
            {
                // update y bounds
                int y = row.getKey();
                ymin = Math.min(y, ymin);
                ymax = Math.max(y, ymax);
                
                // update x bounds
                int[] bnd = row.getValue().intBounds();
                xmin = Math.min(xmin, bnd[0]);
                xmax = Math.max(xmax, bnd[1]);
            }
        }
        
        // create bounds array
        return new int[][] { { xmin, xmax }, { ymin, ymax }, { zmin, zmax } };
    }
    
    public boolean isEmpty()
    {
        return this.slices.isEmpty();
    }

    /**
     * Counts the number of elements within this binary region.
     * 
     * @return the number of elements within this binary region.
     */
    public long elementCount()
    {
        long count = 0;
        for (HashMap<Integer, BinaryRow> slice : slices.values())
        {
            for (BinaryRow row : slice.values())
            {
                count += row.elementCount();
            }
        }
        return count;
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
        return 3;
    }
    
    
    // =============================================================
    // Implementation of an iterator class over positions
    
    private class PositionIterator implements BinaryRegion.PositionIterator
    {
        /**
         * Iterator over the rows. Uses HashMap entry in order to keep row index
         * information.
         */
        Iterator<HashMap.Entry<Integer, HashMap<Integer, BinaryRow>>> sliceIterator;
        HashMap.Entry<Integer, HashMap<Integer, BinaryRow>> currentSlice;
        /**
         * Iterator over the rows. Uses HashMap entry in order to keep row index
         * information.
         */
        Iterator<HashMap.Entry<Integer, BinaryRow>> rowIterator;
        HashMap.Entry<Integer, BinaryRow> currentRow;
        
        Iterator<Run> runIterator;
        Run currentRun = null;
        /** index of position within the run. If equals to -1, no more position.*/
        int posIndex = -1;
        
        public PositionIterator()
        {
            // setup row iterator and current row
            sliceIterator = slices.entrySet().iterator();
            if (!sliceIterator.hasNext()) return;
            currentSlice = sliceIterator.next();
            
            // setup row iterator and current row
            rowIterator = currentSlice.getValue().entrySet().iterator();
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
            int[] pos = new int[3];
            next(pos);
            return pos;
        }
        
        public void next(int[] pos)
        {
            pos[0] = this.currentRun.left + posIndex;
            pos[1] = this.currentRow.getKey();
            pos[2] = this.currentSlice.getKey();
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
            else if (sliceIterator.hasNext())
            {
                // switch to next slice
                nextSlice();
            }
        }
        
        private void nextRow()
        {
            currentRow = rowIterator.next();
            runIterator = currentRow.getValue().iterator();
        }
        
        private void nextSlice()
        {
            currentSlice = sliceIterator.next();
            rowIterator = currentSlice.getValue().entrySet().iterator();
            if (rowIterator.hasNext())
            {
                nextRow();
                nextRun();
            }
        }
    }
}
