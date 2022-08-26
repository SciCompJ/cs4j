/**
 * 
 */
package net.sci.image.morphology.strel;

import java.util.ArrayList;
import java.util.HashMap;
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
public class IndexedRowsStrel3D
{
    public static final IndexedRowsStrel3D createBall(double radius)
    {
        // Initialize data related to strel
        int intRadius = (int) Math.floor(radius + 0.5);
        int sizeZ = 2 * intRadius + 1;
        
        // compute x-offsets of the XY-projection,
        // that corresponds to the z offsets of the YZ projection
        int[] yOffsets2d = computeOffsets2d(intRadius);
        
        // the square of the radius (including center pixel)
        double sqRadius = (radius + 0.5) * (radius + 0.5);
        
        // First iteration over (y,z) pairs to identify the set of unique x values
        TreeSet<Integer> intRadiusSet = new TreeSet<Integer>();
        for (int iz = 0; iz < sizeZ; iz++)
        {
            int z = iz - intRadius;
            int dy = yOffsets2d[iz];
            for (int y = -dy; y <= +dy; y++)
            {
                int dx = (int) Math.floor(Math.sqrt(sqRadius - y * y - z * z));
                intRadiusSet.add(dx);
            }
        }
        
        // convert TreeSet to sorted array, and build the array of binary rows
        int uniqueRadiusCount = intRadiusSet.size();
        ArrayList<Integer> array2 = new ArrayList<Integer>(uniqueRadiusCount);
        BinaryRow[] uniqueRows = new BinaryRow[uniqueRadiusCount];
        int rowIndex = 0;
        for (int xRadius : intRadiusSet)
        {
            uniqueRows[rowIndex++] = new BinaryRow(new Run(-xRadius, xRadius));
            array2.add(xRadius);
        }
        
        // Second iteration to create the "map of maps to indices".
        HashMap<Integer, HashMap<Integer, Integer>> indices = new HashMap<>();
        for (int iz = 0; iz < sizeZ; iz++)
        {
            int z = iz - intRadius;
            int dy = yOffsets2d[iz];
            HashMap<Integer, Integer> sliceIndices = new HashMap<>();
            
            for (int y = -dy; y <= +dy; y++)
            {
                int dx = (int) Math.floor(Math.sqrt(sqRadius - y * y - z * z));
                int index = array2.indexOf(dx);
                sliceIndices.put(y, index);
            }
            indices.put(z, sliceIndices);
        }
        
        return new IndexedRowsStrel3D(indices, uniqueRows);
    }
    
    /**
     * @return the set of offset in x for each y-offset. The array length is
     *         odd, corresponding to 2*intRadius+1.
     */
    private static final int[] computeOffsets2d(double radius)
    {
        int intRadius = (int) Math.floor(radius + 0.5);
        
        // allocate arrays
        int nOffsets = 2 * intRadius + 1;
        int[] xOffsets = new int[nOffsets];
        
        // initialize each row
        double r2 = (radius + 0.5) * ((radius + 0.5));
        for (int i = 0; i < nOffsets; i++)
        {
            int dy = i - intRadius;
            xOffsets[i] = (int) Math.floor(Math.sqrt(r2 - dy * dy));
        }
        
        return xOffsets;
    }
    
    /**
     * The array of indices that map array (y,z) indices to index of unique row within
     * the <code>rows</code> array.
     * 
     * The size of the containing array is given by the size of the
     * <code>indices</code> array.
     */
    public HashMap<Integer, HashMap<Integer, Integer>> indices;
    
    /**
     * The array of indexed rows. The size must be at least equal to the largest index.
     */
    public BinaryRow[] rows;
    
    
    int[] size;
    int[] offset;
    
    
    public IndexedRowsStrel3D(HashMap<Integer, HashMap<Integer, Integer>> rowIndices, BinaryRow[] uniqueRows)
    {
        this.indices = rowIndices;
        this.rows = uniqueRows;
        
        initSizeAndOffset();
    }
    
    private void initSizeAndOffset()
    {
        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;
        int zMin = Integer.MAX_VALUE;
        int zMax = Integer.MIN_VALUE;
        
        for (int z : indices.keySet())
        {
            zMin = Math.min(zMin, z);
            zMax = Math.max(zMax, z);
            
            var slice = indices.get(z);
            for (int y : slice.values())
            {
                yMin = Math.min(yMin, y);
                yMax = Math.max(yMax, y);
                
                BinaryRow row = rows[slice.get(y)];
                Run run = row.runs().iterator().next();
                xMin = Math.min(xMin, run.left);
                xMax = Math.max(xMax, run.right);
            }
        }
        
        this.size = new int[] {xMax - xMin + 1, yMax - yMin + 1, zMax - zMin + 1};
        this.offset = new int[] {-xMin, -yMin, -zMin};
    }
    
    public int[] size()
    {
        return this.size;
    }

    public int[] offset()
    {
        return this.offset;
    }
    
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Strel with indexed Rows:");
        for (int z : indices.keySet())
        {
            buffer.append("\nslice " + z + ":");
            
            HashMap<Integer, Integer> sliceIndices = indices.get(z);
            for (int y : sliceIndices.keySet())
            {
                int index = sliceIndices.get(y);
                buffer.append(String.format(Locale.ENGLISH, "\n  row (% d,% d), index #%d: %s", y, z, index, rows[index].runs().iterator().next()));
            }
        }
        return buffer.toString();
    }

}
