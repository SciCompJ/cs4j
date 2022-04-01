/**
 * 
 */
package net.sci.array.binary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.sci.algo.AlgoStub;

/**
 * Concrete implementation of BinaryArray3D that stores inner data in a
 * collection of "run-lengths", defined by a position and a length. Such a
 * storage is expected to be more efficient (requiring less memory) than buffer
 * storage for binary arrays with "compact" regions.
 * 
 * @author dlegland
 *
 */
public class RunLengthBinaryArray3D extends BinaryArray3D
{
    // =============================================================
    // Static methods
    
    /**
     * Converts the input array into a run-length-based binary array, or returns
     * the class-casted input array if it is already an instance of
     * RunLengthBinaryArray3D.
     * 
     * @param array
     *            the input binary array
     * @return a RunLengthBinaryArray3D instance
     */
    public static final RunLengthBinaryArray3D convert(BinaryArray3D array)
    {
        if (array instanceof RunLengthBinaryArray3D)
        {
            return (RunLengthBinaryArray3D) array;
        }
        
        return new Converter().process(array);
    }
    
    
	// =============================================================
	// Class fields
    
    /**
     * The rows representing this binary array. Index first by z, then by y. 
     * Do not keep empty rows.
     */
    BinaryRow[][] slices;

	
	// =============================================================
	// Constructors

	/**
	 * @param size0 the size of the array in the first dimension
     * @param size1 the size of the array in the second dimension
     * @param size2 the size of the array in the third dimension
	 */
	public RunLengthBinaryArray3D(int size0, int size1, int size2)
	{
		super(size0, size1, size2);
		this.slices = new BinaryRow[size2][];
	}

	/**
    * @param size0 the size of the array in the first dimension
    * @param size1 the size of the array in the second dimension
    * @param size2 the size of the array in the third dimension
    * @param slices the indexed rows populating the new array 
    */
   public RunLengthBinaryArray3D(int size0, int size1, int size2, HashMap<Integer, HashMap<Integer, BinaryRow>> slices)
   {
       super(size0, size1, size2);
       this.slices = new BinaryRow[size2][];
       for (Entry<Integer, HashMap<Integer, BinaryRow>> entry : slices.entrySet())
       {
           BinaryRow[] slice = new BinaryRow[size1];
           for (Entry<Integer, BinaryRow> rowEntry : entry.getValue().entrySet())
           {
               slice[rowEntry.getKey()] = rowEntry.getValue();
           }
           this.slices[entry.getKey()] = slice;
       }
   }


	// =============================================================
    // New methods
	
    public Collection<Integer> nonEmptySliceIndices()
    {
        ArrayList<Integer> list = new ArrayList<>(size2);
        for (int z = 0; z < size2; z++)
        {
            if (this.slices[z] != null)
            {
                list.add(z);
            }
        }
        return list;
    }
    
    public Collection<Integer> nonEmptySliceRowIndices(int sliceIndex)
    {
        if (this.slices[sliceIndex] == null)
        {
            // return an empty collection
            return Collections.emptySet();
        }
        
        ArrayList<Integer> list = new ArrayList<>(size1);
        for (int y = 0; y < size1; y++)
        {
            if (this.slices[sliceIndex][y] != null)
            {
                list.add(y);
            }
        }
        return list;
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
        for (int z = 0; z < size2; z++)
        {
            BinaryRow[] slice = slices[z];
            if (slice == null) continue;
            
            for (int y = 0; y < size1; y++)
            {
                BinaryRow row = slice[y];
                if (row != null)
                {
                    count += row.runCount();
                }
            }
        }
        return count;
    }
    
	
    // =============================================================
    // Management of rows
	
    /**
     * @param y
     *            the y-coordinate of the row
     * @param z
     *            the z-coordinate of the row
     * @return the row at the specified (y,z) coordinates, or null if the row at
     *         specified coordinates is empty.
     */
	public BinaryRow getRow(int y, int z)
    {
	    if (slices[z] == null) return null;
	    return slices[z][y];
    }

    /**
     * Sets the row at the given (y,z)-coordinates.
     * 
     * @param y
     *            the y-index of the row.
     * @param z
     *            the z-index of the row.
     * @param row
     *            the row to store within the array, that can be empty.
     */
    public void setRow(int y, int z, BinaryRow row)
    {
        if (row == null || row.isEmpty())
        {
            removeRow(y, z);
        }
        else
        {
            BinaryRow[] slice = slices[z];
            if (slice == null)
            {
                slice = new BinaryRow[this.size1];
                slices[z] = slice;
            }
            slice[y] = row;
        }
    }
    
    private void removeRow(int y, int z)
    {
        BinaryRow[] slice = slices[z];
        if (slice != null)
        {
            if (slice[y] != null)
            {
                slice[y] = null;
            }
        }
    }
    
    public boolean isEmptyRow(int y, int z)
    {
        BinaryRow[] slice = slices[z];
        if (slice == null)
        {
            return true;
        }
        // as we do not allow empty rows, it is enough to check the existence of the key
        if (slice[y] == null) return true;
        return slice[y].isEmpty();
    }
    
    
	// =============================================================
	// Implementation of the BinaryArray3D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray3D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, int y, int z, boolean state)
    {
        BinaryRow row = getRow(y, z);
        
        if (row == null)
        {
            if (state)
            {
                row = new BinaryRow();
                row.set(x, true);
                setRow(y, z, row);
            }
        }
        else
        {
            row.set(x, state);
            if (row.isEmpty())
            {
                removeRow(y, z);
            }
        }
    }
    

    // =============================================================
    // Implementation of the BinaryArray interface
    
    /**
     * Fills this binary array with the specified boolean value.
     * 
     * @param state
     *            the value to fill the binary array with.
     */
    public void fill(boolean state)
    {
        if (state)
        {
            // iterate over slices
            for (int z = 0; z < size2; z++)
            {
                // ensure current slice is not empty
                if (slices[z] == null) 
                {
                    slices[z] = new BinaryRow[size1];
                }
                
                // fill current slice
                for (int y = 0; y < size1; y++)
                {
                    slices[z][y] = new BinaryRow(new Run(0, size0 - 1));
                }
            }
        }
        else
        {
            // iterate over slices
            for (int z = 0; z < size1; z++)
            {
                slices[z] = null;
            }
        }
    }
    
    /* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
        BinaryRow[] slice = slices[pos[2]];
	    if (slice == null)
	    {
	        return false;
	    }
	    
        BinaryRow row = slice[pos[1]];
        if (row == null)
        {
            return false;
        }
	    
	    return row.get(pos[0]);
	}
	
	
	// =============================================================
	// Implementation of the Array interface
	
    /**
     * @return a new instance of RunLengthBinaryArray3D
     */
    @Override
    public RunLengthBinaryArray3D duplicate()
    {
        // create array
        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(size0, size1, size2);
        
        // iterate over slices
        for (int z = 0; z < size2; z++)
        {
            // duplicate only non empty slices
            if (slices[z] != null)
            {
                res.slices[z] = duplicate(slices[z]);
            }
        }
        
        return res;
    }
    
    private BinaryRow[] duplicate(BinaryRow[] slice)
    {
        BinaryRow[] res = new BinaryRow[slice.length];
        for (int y = 0; y < slice.length; y++)
        {
            if (slice[y] != null)
            {
                res[y] = slice[y].duplicate();
            }
        }
        return res;
    }
     
    public static class Converter extends AlgoStub
    {
        public RunLengthBinaryArray3D process(BinaryArray3D array)
        {
            if (array instanceof RunLengthBinaryArray3D)
            {
                return (RunLengthBinaryArray3D) array;
            }
            
            int sizeX = array.size(0);
            int sizeY = array.size(1);
            int sizeZ = array.size(2);
            RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
            
            // iterate over slices
            for (int z = 0; z < sizeZ; z++)
            {
                this.fireProgressChanged(this, z, sizeZ);
                
                for (int y = 0; y < sizeY; y++)
                {
                    TreeMap<Integer,Run> runs = new TreeMap<Integer,Run>(); 
                    
                    int x1 = 0;
                    currentRow:
                    while (true)
                    {
                        // find beginning of first run
                        while(!array.getBoolean(x1, y, z))
                        {
                            if (++x1 == sizeX) break currentRow;
                        }
                        
                        // find the end of current run
                        int x2 = x1;
                        while (array.getBoolean(x2, y, z))
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
                        res.setRow(y, z, new BinaryRow(runs));
                    }
                }
            }
            this.fireProgressChanged(this, sizeZ, sizeZ);
            
            return res;
        }
    }
}
