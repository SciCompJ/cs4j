/**
 * 
 */
package net.sci.array.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * @param size0 the size of the array in the first dimension
     * @param size1 the size of the array in the second dimension
     * @param rows the indexed rows populating the new array 
     */
    public RunLengthBinaryArray2D(int size0, int size1, HashMap<Integer, BinaryRow> rows)
    {
        super(size0, size1);
        this.rows = rows;
    }


	// =============================================================
    // New methods

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
	
    /**
     * @return a new instance of RunLengthBinaryArray2D
     */
    @Override
    public RunLengthBinaryArray2D duplicate()
    {
        // retrieve array size
        int sizeX = this.size(0);
        int sizeY = this.size(1);
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // add copies of each row
        for (Map.Entry<Integer, BinaryRow> entry : this.rows.entrySet())
        {
            int index = entry.getKey();
            res.rows.put(index, entry.getValue().duplicate());
        }
        return res;
    }

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
            return x < size0 - 1|| y < size1 - 1;
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
