/**
 * 
 */
package net.sci.array.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Concrete implementation of BinaryArray2D that stores inner data in a
 * collection of "run-lengths", defined by a position and a length. Such a
 * storage is expected to be more efficient (requiring less memory) than buffer
 * storage for binary arrays with "compact" regions.
 * 
 * @author dlegland
 *
 */
public class RunLengthBinaryArray2D extends BinaryArray2D
{
    // =============================================================
    // Static methods
    
    /**
     * Converts the input array into a run-length-based binary array, or returns
     * the class-casted input array if it is already an instance of
     * RunLengthBinaryArray2D.
     * 
     * @param array
     *            the input binary array
     * @return a RunLengthBinaryArray2D instance
     */
    public static final RunLengthBinaryArray2D convert(BinaryArray2D array)
    {
        if (array instanceof RunLengthBinaryArray2D)
        {
            return (RunLengthBinaryArray2D) array;
        }
        
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // start with naive algorithm
        for (int y = 0; y < sizeY; y++)
        {
            for (int x = 0; x < sizeX; x++)
            {
                if (array.getBoolean(x, y))
                {
                    res.setBoolean(x, y, true);
                }
            }
        }
        
        return res;
    }

    public static final RunLengthBinaryArray2D dilation(RunLengthBinaryArray2D array, RunLengthBinaryArray2D strel, int[] strelOffset)
    {
        // array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create result array
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // iterate over rows of result array
        for (int yres = 0; yres < sizeY; yres++)
        {
            // initialize empty result row
            BinaryRow resRow = new BinaryRow();
            
            // iterate over rows of structuring element
            for (int yStrel = 0; yStrel < strel.size(1); yStrel++)
            {
                int y2 = yres + yStrel - strelOffset[1];
                // check current row is within the input array
                if (y2 < 0)
                {
                    continue;
                }
                if (y2 > sizeY - 1)
                {
                    break;
                }
                
                // do not compute dilation if any of the rows is empty
                if (strel.isEmptyRow(yStrel) || array.isEmptyRow(y2))
                {
                    continue;
                }
                
                // retrieve the rows to dilate
                BinaryRow arrayRow = array.getRow(y2);
                BinaryRow strelRow = strel.getRow(yStrel);
                
                BinaryRow row = BinaryRows.dilate(arrayRow, strelRow, strelOffset[0]);
                resRow = BinaryRows.union(resRow, row);
            }
            
            if (!resRow.isEmpty())
            {
                // TODO: need to crop?
                res.setRow(yres, resRow);
            }
        }
        
        return res;
    }


	// =============================================================
	// Class fields
    
    /**
     * The rows representing this binary array. Do not keep empty rows.
     */
    HashMap<Integer, BinaryRow> rows;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0 the size of the array in the first dimension
	 * @param size1 the size of the array in the second dimension
	 */
	public RunLengthBinaryArray2D(int size0, int size1)
	{
		super(size0, size1);
		this.rows = new HashMap<>();
	}


	// =============================================================
    // New methods

    // TODO: should return hashmap
	public Collection<BinaryRow> rows()
	{
	    return Collections.unmodifiableCollection(rows.values());
	}
	
    // =============================================================
    // Management of rows
	
    /**
     * @param y
     *            the index of the row
     * @return the row at the specified index, or null if the row at specified
     *         index is empty.
     */
	public BinaryRow getRow(int y)
    {
	    return rows.containsKey(y) ? rows.get(y) : null;
    }

    /**
     * Sets the row at the given y-index.
     * 
     * @param y
     *            the y-index of the row.
     * @param row
     *            the row to store within the array, that can be empty.
     */
    public void setRow(int y, BinaryRow row)
    {
        if (row == null || row.isEmpty())
        {
            rows.remove(y);
        }
        else
        {
            this.rows.put(y, row);
        }
    }
    
    public boolean isEmptyRow(int y)
    {
        return !rows.containsKey(y);
    }
    
    public Collection<Integer> nonEmptyRowIndices()
    {
        return Collections.unmodifiableCollection(rows.keySet());
    }
    
    


	// =============================================================
	// Implementation of the BooleanArray2D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray2D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, int y, boolean state)
    {
        // get row corresponding to current y, or create one if it does not exist
        BinaryRow row = this.rows.get(y);
        if (row == null)
        {
            if (state)
            {
                // create empty row 
                row = new BinaryRow();
                row.set(x, true);
                this.rows.put(y, row);
            }
            
            // if state is false, nothing to do
            return;
        }
        
        // update element at position x
        row.set(x, state);
    }

	/* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
	    BinaryRow row = this.rows.get(pos[1]);
	    if (row == null)
	    {
	        return false;
	    }
	    return row.get(pos[0]);
	}


	// =============================================================
	// Implementation of the Array interface
	
	/* (non-Javadoc)
	 * @see net.sci.array.data.BooleanArray#iterator()
	 */
	@Override
	public net.sci.array.binary.BinaryArray.Iterator iterator()
	{
		return new BooleanIterator();
	}
	
    private class BooleanIterator implements BinaryArray.Iterator
    {
        int x = -1;
        int y = 0;
        
        public BooleanIterator() 
        {
        }
        
        @Override
        public boolean hasNext()
        {
            return x < size0 || y < size1;
        }

        @Override
        public void forward()
        {
            this.x++;
            if (x == size0)
            {
                x = 0;
                y++;
            }
        }

        @Override
        public Binary get()
        {
            return new Binary(RunLengthBinaryArray2D.this.getBoolean(x, y));
        }

        @Override
        public void set(Binary b)
        {
            RunLengthBinaryArray2D.this.setBoolean(x, y, b.getBoolean());
        }
        
        @Override
        public boolean getBoolean()
        {
            return RunLengthBinaryArray2D.this.getBoolean(x, y);
        }

        @Override
        public void setBoolean(boolean b)
        {
            RunLengthBinaryArray2D.this.setBoolean(x, y, b);
        }
    }
}
