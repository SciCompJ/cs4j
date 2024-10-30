/**
 * 
 */
package net.sci.array.numeric.impl;

import java.util.HashMap;
import java.util.Map.Entry;

import net.sci.array.numeric.Int32Array3D;

/**
 * 
 */
public class RunLengthInt32Array3D extends Int32Array3D
{
    // =============================================================
    // Class fields
    
    /**
     * The rows representing this binary array. Index first by z, then by y. 
     * Do not keep empty rows.
     */
    Int32Row[][] slices;

    
    // =============================================================
    // Constructors

    /**
     * Creates a new 3D binary array using run-length encoding. All elements are
     * set to <code>false</code>.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     * @param size2
     *            the size of the array in the third dimension
     */
    public RunLengthInt32Array3D(int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.slices = new Int32Row[size2][];
    }

    /**
     * Creates a new 3D binary array using run-length encoding, initialized with
     * the specified set of binary rows.
     * 
     * @param size0
     *            the size of the array in the first dimension
     * @param size1
     *            the size of the array in the second dimension
     * @param size2
     *            the size of the array in the third dimension
     * @param slices
     *            the indexed rows populating the new array
     */
   public RunLengthInt32Array3D(int size0, int size1, int size2, HashMap<Integer, HashMap<Integer, Int32Row>> slices)
   {
       super(size0, size1, size2);
       this.slices = new Int32Row[size2][];
       for (Entry<Integer, HashMap<Integer, Int32Row>> entry : slices.entrySet())
       {
           Int32Row[] slice = new Int32Row[size1];
           for (Entry<Integer, Int32Row> rowEntry : entry.getValue().entrySet())
           {
               slice[rowEntry.getKey()] = rowEntry.getValue();
           }
           this.slices[entry.getKey()] = slice;
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
   public Int32Row getRow(int y, int z)
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
   public void setRow(int y, int z, Int32Row row)
   {
       if (row == null || row.isEmpty())
       {
           removeRow(y, z);
       }
       else
       {
           Int32Row[] slice = slices[z];
           if (slice == null)
           {
               slice = new Int32Row[this.size1];
               slices[z] = slice;
           }
           slice[y] = row;
       }
   }
   
   private void removeRow(int y, int z)
   {
       Int32Row[] slice = slices[z];
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
       Int32Row[] slice = slices[z];
       if (slice == null)
       {
           return true;
       }
       // as we do not allow empty rows, it is enough to check the existence of the key
       if (slice[y] == null) return true;
       return slice[y].isEmpty();
   }
   

   // =============================================================
   // Implementation of the Int32Array3D interface

    @Override
    public int getInt(int x, int y, int z)
    {
        Int32Row[] slice = slices[z];
        if (slice == null)
        {
            return 0;
        }
        
        Int32Row row = slice[y];
        if (row == null)
        {
            return 0;
        }
        
        return row.get(x);
    }

    @Override
    public void setInt(int x, int y, int z, int value)
    {
        Int32Row row = getRow(y, z);
        
        if (row == null)
        {
            if (value != 0)
            {
                row = new Int32Row();
                row.set(x, value);
                setRow(y, z, row);
            }
        }
        else
        {
            row.set(x, value);
            if (row.isEmpty())
            {
                removeRow(y, z);
            }
        }
    }

}
