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
	// Class fields
    
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

//	/* (non-Javadoc)
//	 * @see net.sci.array.data.scalar2d.BooleanArray2D#duplicate()
//	 */
//	@Override
//	public BinaryArray2D duplicate()
//	{
//		int n = this.size0 * this.size1;
//		boolean[] buffer2 = new boolean[n];
//		System.arraycopy(this.buffer, 0, buffer2, 0, n);
//		return new RunLengthBinaryArray2D(this.size0, this.size1, buffer2);
//	}

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
