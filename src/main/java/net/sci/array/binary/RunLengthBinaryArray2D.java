/**
 * 
 */
package net.sci.array.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sci.algo.AlgoStub;

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
        return new Converter().process(array);
    }
    
    
    // =============================================================
    // Class fields

    /**
     * The rows representing this binary array. Does not keep empty rows, and
     * stores a null instead.
     */
    BinaryRow[] rows;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new 2D binary array using run-length encoding. All elements are
     * set to <code>false</code>.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     */
    public RunLengthBinaryArray2D(int size0, int size1)
    {
        super(size0, size1);
        this.rows = new BinaryRow[size1];
    }

    /**
     * Creates a new 2D binary array using run-length encoding, initialized with
     * the specified set of rows.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     * @param rows
     *            the indexed rows populating the new array
     */
    public RunLengthBinaryArray2D(int size0, int size1, HashMap<Integer, BinaryRow> rows)
    {
        super(size0, size1);
        this.rows = new BinaryRow[size1];
        for (Entry<Integer, BinaryRow> entry : rows.entrySet())
        {
            this.rows[entry.getKey()] = entry.getValue();
        }
    }


	// =============================================================
    // New methods
	
    /**
     * Counts the number of runs within this array.
     * 
     * @return the number of runs within this array.
     */
    public int runCount()
    {
        int count = 0;
        for (int y = 0; y < size1; y++)
        {
            BinaryRow row = rows[y];
            if (row != null)
            {
                count += row.runCount();
            }
        }
        return count;
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
	    return rows[y];
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
        if (row != null)
        {
            if (!row.isEmpty())
            {
                rows[y] = row;
                return;
            }
        }
        
        // if row is null OR is empty, do not keep reference
        rows[y] = null;
    }
    
    public boolean isEmptyRow(int y)
    {
        if (rows[y] == null) return true;
        return rows[y].isEmpty();
    }
    
    public Collection<Integer> nonEmptyRowIndices()
    {
        ArrayList<Integer> list = new ArrayList<>(size1);
        for (int y = 0; y < size1; y++)
        {
            if (this.rows[y] != null)
            {
                list.add(y);
            }
        }
        return list;
    }
    

	// =============================================================
	// Implementation of the BinaryArray interface

    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray2D#getBoolean(int, int)
     */
    @Override
    public boolean getBoolean(int x, int y)
    {
        BinaryRow row = this.rows[y];
        if (row == null)
        {
            return false;
        }
        return row.get(x);
    }

    /* (non-Javadoc)
     * @see net.sci.array.binary.BinaryArray2D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, int y, boolean state)
    {
        // get row corresponding to current y, or create one if it does not exist
        BinaryRow row = this.rows[y];
        if (row == null)
        {
            if (state)
            {
                // create empty row 
                row = new BinaryRow();
                row.set(x, true);
                this.rows[y] = row;
            }
            
            // if state is false, nothing to do
            return;
        }
        
        // update element at position x
        row.set(x, state);
    }
    
    
    // =============================================================
    // Specialization of the BinaryArray interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray#complement()
     */
    @Override
    public RunLengthBinaryArray2D complement()
    {
        RunLengthBinaryArray2D result = new RunLengthBinaryArray2D(size(0), size(1));
        for (int y = 0; y < size1; y++)
        {
            BinaryRow row = this.rows[y];
            result.rows[y] = row == null ? new BinaryRow(new Run(0, size0 - 1)) : row.complement(size0);
            if (result.rows[y].isEmpty())
            {
                result.rows[y] = null;
            }
        }
        return result;
    }
    
    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param state
     *            the value to fill the binary array with.
     */
    public void fill(boolean state)
    {
        // iterate over rows
        for (int y = 0; y < size1; y++)
        {
            this.rows[y] = state ? new BinaryRow(new Run(0, size0 - 1)) : null;
        }
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
        
        for (int y = 0; y < sizeY; y++)
        {
            BinaryRow row = this.rows[y]; 
            if (row != null)
            {
                res.rows[y] = row.duplicate();
            }
        }
        return res;
    }
    
    /**
     * Utility class that converts a 2D binary array into an instance of
     * RunLengthBinaryArray2D.
     * 
     * @see RunLengthBinaryArray2D.#convert(BinaryArray2D)
     */
    public static class Converter extends AlgoStub
    {
        public RunLengthBinaryArray2D process(BinaryArray2D array)
        {
            if (array instanceof RunLengthBinaryArray2D)
            {
                return (RunLengthBinaryArray2D) array;
            }
            
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
            
            // iterate over rows
            for (int y = 0; y < sizeY; y++)
            {
                this.fireProgressChanged(this, y, sizeY);

                TreeMap<Integer,Run> runs = new TreeMap<Integer,Run>(); 

                int x1 = 0;
                currentRow:
                while (true)
                {
                    // find beginning of first run
                    while(!array.getBoolean(x1, y))
                    {
                        if (++x1 == sizeX) break currentRow;
                    }

                    // find the end of current run
                    int x2 = x1;
                    while (array.getBoolean(x2, y))
                    {
                        if (++x2 == sizeX) break;
                    }

                    // keep current run, and look for next one
                    runs.put(x1, new Run(x1, x2 - 1));
                    if (x2 == sizeX) break;
                    x1 = x2;
                }

                // put row in target array.
                if (!runs.isEmpty())
                {
                    res.setRow(y, new BinaryRow(runs));
                }
            }
            this.fireProgressChanged(this, sizeY, sizeY);
            
            return res;
        }
    }
}