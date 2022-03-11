/**
 * 
 */
package net.sci.array.binary;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    HashMap<Integer, HashMap<Integer, BinaryRow>> slices;

	
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
		this.slices = new HashMap<>(size2);
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
       this.slices = slices;
   }


	// =============================================================
    // New methods
	
    public Collection<Integer> nonEmptySliceIndices()
    {
        return Collections.unmodifiableCollection(slices.keySet());
    }
    
    public Collection<Integer> nonEmptySliceRowIndices(int sliceIndex)
    {
        if (this.slices.containsKey(sliceIndex))
        {
            return Collections.unmodifiableCollection(slices.get(sliceIndex).keySet());
        }
        else
        {
            // return an empty collection
            return Collections.emptySet();
        }
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
	    if (!slices.containsKey(z))
	    {
	        return null;
	    }
	    HashMap<Integer, BinaryRow> slice = slices.get(z);
	    return slice.containsKey(y) ? slice.get(y) : null;
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
            HashMap<Integer, BinaryRow> slice = slices.get(z);
            if (slice == null)
            {
                slice = new HashMap<Integer, BinaryRow>(this.size1);
                slices.put(z, slice);
            }
            slice.put(y, row);
        }
    }
    
    private void removeRow(int y, int z)
    {
        if (slices.containsKey(z))
        {
            HashMap<Integer, BinaryRow> slice = slices.get(z);
            if (slice.containsKey(y))
            {
                slice.remove(y);
                if (slice.isEmpty())
                {
                    slices.remove(z);
                }
            }
        }
    }
    
    public boolean isEmptyRow(int y, int z)
    {
        if (!slices.containsKey(z))
        {
            return true;
        }
        HashMap<Integer, BinaryRow> slice = slices.get(z);
        // as we do not allow empty rows, it is enough to check the existence of the key
        return !slice.containsKey(y);
    }
    
    
	// =============================================================
	// Implementation of the BinaryArray3D interface

    /* (non-Javadoc)
     * @see net.sci.array.scalar.BinaryArray3D#setBoolean(int, int, boolean)
     */
    @Override
    public void setBoolean(int x, int y, int z, boolean state)
    {
        BinaryRow row = null;
        
        HashMap<Integer, BinaryRow> slice = slices.get(z);
        if (slice != null)
        {
            row = slice.get(y);
        }
        
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
        // in any case, clear the inner map
        this.slices.clear();
        
        if (state)
        {
            // iterate over slices
            for (int z = 0; z < size1; z++)
            {
                // fill current slice
                HashMap<Integer, BinaryRow> slice = new HashMap<>(size1);
                for (int y = 0; y < size1; y++)
                {
                    slice.put(y, new BinaryRow(new Run(0, size0 - 1)));
                }
                this.slices.put(z, slice);
            }
        }
    }
    
    /* (non-Javadoc)
	 * @see net.sci.array.scalar.BinaryArray#getBoolean(int[])
	 */
	@Override
	public boolean getBoolean(int... pos)
	{
        HashMap<Integer, BinaryRow> slice = slices.get(pos[2]);
	    if (slice == null)
	    {
	        return false;
	    }
	    
        BinaryRow row = slice.get(pos[1]);
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
        HashMap<Integer, HashMap<Integer, BinaryRow>> resSlices = new HashMap<>(size2);
        
        // add copies of each slice
        for (Map.Entry<Integer, HashMap<Integer, BinaryRow>> entry : this.slices.entrySet())
        {
            resSlices.put(entry.getKey(), duplicate(entry.getValue()));
        }
        
        // create array
        return new RunLengthBinaryArray3D(size0, size1, size2, resSlices);
    }
    
    private HashMap<Integer, BinaryRow> duplicate(HashMap<Integer, BinaryRow> slice)
    {
        HashMap<Integer, BinaryRow> res = new HashMap<>(this.size1); 
        for (Map.Entry<Integer, BinaryRow> entry : slice.entrySet())
        {
            res.put(entry.getKey(), entry.getValue().duplicate());
        }
        return res;
    }
    
    
    // =============================================================
    // Implementation of the Iterator
    
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
        int z = 0;
        
        public BooleanIterator() 
        {
        }
        
        @Override
        public boolean hasNext()
        {
            return x < size0 - 1 || y < size1 - 1 || z < size2 - 1;
        }

        @Override
        public void forward()
        {
            this.x++;
            if (x == size0)
            {
                x = 0;
                y++;
                
                if (y == size1)
                {
                    y = 0;
                    z++;
                }
            }
        }

        @Override
        public Binary get()
        {
            return new Binary(RunLengthBinaryArray3D.this.getBoolean(x, y, z));
        }

        @Override
        public void set(Binary b)
        {
            RunLengthBinaryArray3D.this.setBoolean(x, y, z, b.getBoolean());
        }
        
        @Override
        public boolean getBoolean()
        {
            return RunLengthBinaryArray3D.this.getBoolean(x, y, z);
        }

        @Override
        public void setBoolean(boolean b)
        {
            RunLengthBinaryArray3D.this.setBoolean(x, y, z, b);
        }
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
            
            // start with naive algorithm
            for (int z = 0; z < sizeZ; z++)
            {
                for (int y = 0; y < sizeY; y++)
                {
                    for (int x = 0; x < sizeX; x++)
                    {
                        if (array.getBoolean(x, y, z))
                        {
                            res.setBoolean(x, y, z, true);
                        }
                    }
                }
            }
            
            return res;
        }
    }
}
