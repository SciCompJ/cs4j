/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;

/**
 * A structuring element based on run-length encoding, that use indices of the
 * BinaryRow instances representing element. Expected to be more efficient for
 * structuring elements with a large number of similar rows.
 * 
 * The structuring element is anchored at a point with x-coordinate equal to 0.
 * 
 * @author dlegland
 */
public class IndexedRowsStrel2D
{
    public static final IndexedRowsStrel2D createDisk(double radius)
    {
        // Initialize data related to strel
        int intRadius = (int) Math.floor(radius + 0.5);
        int sizeY = 2 * intRadius + 1;
        int[] xIntRadiusArray = new int[sizeY];
        TreeSet<Integer> intRadiusSet = new TreeSet<Integer>();
        
        // the square of the radius (including center pixel)
        double sqRadius = (radius + 0.5) * (radius + 0.5);
        
        // compute x-radius for each row, and build the set of unique integer radius
        for (int y = 0; y < sizeY; y++)
        {
            int dy = y - intRadius;
            int x2 = (int) Math.floor(Math.sqrt(sqRadius - dy * dy));
            xIntRadiusArray[y] = x2;
            intRadiusSet.add(x2);
        }
        
        // convert tree to sorted array, and build the array of binary rows
        int uniqueRadiusCount = intRadiusSet.size();
        ArrayList<Integer> array2 = new ArrayList<Integer>(uniqueRadiusCount);
        BinaryRow[] uniqueRows = new BinaryRow[uniqueRadiusCount];
        int rowIndex = 0;
        for (int xRadius : intRadiusSet)
        {
            uniqueRows[rowIndex++] = new BinaryRow(new Run(-xRadius, xRadius));
            array2.add(xRadius);
        }
        
        // create the index of unique row for each regular row
        int[] uniqueRowIndices = new int[sizeY];
        for (int y = 0; y < sizeY; y++)
        {
            int rowRadius = xIntRadiusArray[y];
            int index = array2.indexOf(rowRadius);
            uniqueRowIndices[y] = index;
        }
        
        return new IndexedRowsStrel2D(uniqueRowIndices, uniqueRows);
    }
    
    /**
     * The array of indices that map array index to index of unique row within
     * the <code>rows</code> array.
     * 
     * The size of the containing array is given by the size of the
     * <code>indices</code> array.
     */
    public int[] indices;
    
    /**
     * The array of indexed rows. The size must be at least equal to the largest index.
     */
    public BinaryRow[] rows;
    
    int[] size;
    int[] offset;

    
    public IndexedRowsStrel2D(int[] rowIndices, BinaryRow[] uniqueRows)
    {
        this.indices = rowIndices;
        this.rows = uniqueRows;
        
        initSizeAndOffset();
    }
    
    private void initSizeAndOffset()
    {
        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        
        for (BinaryRow row : rows)
        {
            Run run = row.runs().iterator().next();
            xMin = Math.min(xMin, run.left);
            xMax = Math.max(xMax, run.right);
        }
        
        int offsetY = (indices.length - 1) / 2;

        
        this.size = new int[] {xMax - xMin + 1, indices.length};
        this.offset = new int[] {-xMin, offsetY};
    }
    
    
    public int[] size()
    {
        return this.size;
    }

    public int[] offset()
    {
        return this.offset;
    }
    
    
    /**
     * Returns the binary row that correspond to a given index.
     * 
     * @param index
     *            the index of the row, between 0 and rowCount()
     * @return the corresponding row
     */
    public BinaryRow getRow(int index)
    {
        return rows[indices[index]];
    }
    
    /**
     * 
     * @param index
     *            the index of the row within the unique row array, between 0
     *            and uniqueRowCount()
     * @return the corresponding row
     */
    public BinaryRow getIndexedRow(int index)
    {
        return rows[index];
    }
    
    /**
     * @return the total number of rows within this array.
     */
    public int rowCount()
    {
        return indices.length;
    }
    
    /**
     * @return the number of unique rows within this array.
     */
    public int uniqueRowCount()
    {
        return rows.length;
    }
    
    /**
     * @return the number of unique rows within this array.
     */
    public int uniqueRowIndex(int row)
    {
        return indices[row];
    }
    
    public BinaryRow[] uniqueRows()
    {
        return this.rows;
    }
    
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Strel with indexed Rows:");
        for (int y = 0; y < indices.length; y++)
        {
            int index = indices[y];
            buffer.append(String.format(Locale.ENGLISH, "\nrow #%d (index = %d): %s", y, index, rows[index].runs().iterator().next()));
        }
        return buffer.toString();
    }
    
    public static final void main(String... args)
    {
        IndexedRowsStrel2D array = IndexedRowsStrel2D.createDisk(2);
        System.out.println(array);
    }
}
