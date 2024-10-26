/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import net.sci.array.numeric.Int32Array2D;

/**
 * 
 */
public class RunLengthInt32Array2D extends Int32Array2D
{
    // =============================================================
    // Class fields

    /**
     * The rows representing this array. Does not keep empty rows, and stores a
     * null instead.
     */
    Int32Row[] rows;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new 2D array of Int32 using run-length encoding. All elements are
     * set to <code>zero</code>.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     */
    public RunLengthInt32Array2D(int size0, int size1)
    {
        super(size0, size1);
        this.rows = new Int32Row[size1];
    }

    /**
     * Creates a new 2D array of UInt32 using run-length encoding, initialized with
     * the specified set of rows.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     * @param rows
     *            the indexed rows populating the new array
     */
    public RunLengthInt32Array2D(int size0, int size1, HashMap<Integer, Int32Row> rows)
    {
        super(size0, size1);
        this.rows = new Int32Row[size1];
        for (Entry<Integer, Int32Row> entry : rows.entrySet())
        {
            this.rows[entry.getKey()] = entry.getValue();
        }
    }

    
    // =============================================================
    // Methods implementing the IntArray2D interface

    @Override
    public int getInt(int x, int y)
    {
        Int32Row row = this.rows[y];
        if (row == null)
        {
            return 0;
        }
        return row.get(x);
    }

    @Override
    public void setInt(int x, int y, int value)
    {
        // get row corresponding to current y, or create one if it does not exist
        Int32Row row = this.rows[y];
        if (row == null)
        {
            if (value != 0)
            {
                // create new row initialized with a value at specified position
                row = new Int32Row();
                row.set(x, value);
                this.rows[y] = row;
            }
            
            // if state is false, nothing to do
            return;
        }
        
        // update element at position x
        row.set(x, value);
    }

    /**
     * Fills this array with the specified integer value.
     * 
     * @param value
     *            the value to fill the binary array with.
     */
    public void fillInt(int value)
    {
        // iterate over rows
        for (int y = 0; y < size1; y++)
        {
            this.rows[y] = value != 0 ? new Int32Row(new Int32Run(0, size0 - 1, value)) : null;
        }
    }
    

}
