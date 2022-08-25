/**
 * 
 */
package net.sci.image.morphology.filter;

import java.util.Collection;
import java.util.HashMap;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.ArrayOperator;
import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.strel.IndexedRowsStrel2D;
import net.sci.image.morphology.strel.IndexedRowsStrel3D;

/**
 * Computes erosion of a binary array.
 * 
 * Converts the input into run-length encoded arrays when necessary.
 * 
 * @see BinaryDilation
 * @see BinaryOpening
 * @see BinaryClosing
 * @see Erosion
 * 
 * @author dlegland
 */
public class BallBinaryErosion extends AlgoStub implements ArrayOperator
{
    double radius;
    boolean padding = true;
            
    public BallBinaryErosion(double radius)
    {
        this(radius, true);
    }
    
    public BallBinaryErosion(double radius, boolean padding)
    {
        if (radius <= 0)
        {
            throw new IllegalArgumentException("Requires a positive radius");
        }
        this.radius = radius;
        this.padding = padding;
    }
    
    /**
     * Performs morphological erosion on a 2D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray2D</code> when
     * necessary.
     * 
     * @see #processBinary3d(BinaryArray3D)
     * @see net.sci.array.binary.RunLengthBinaryArray2D
     * 
     * @param array
     *            the array on which erosion should be applied
     * @return the result of erosion as a new array
     */
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
//        // cast to Strel2D and retrieve size and offset
//        Strel2D strel2d = Strel2D.wrap(strel);
//        int[] strelSize = strel2d.size();
//        int[] strelOffset = strel2d.getOffset();
//        
//        // convert structuring element to RLE array
//        RunLengthBinaryArray2D strel2 = RunLengthBinaryArray2D.convert(strel2d.getMask());
//        
//        // work on rows shifted along x axis
//        HashMap<Integer, BinaryRow> strelRows = shiftRows(strel2, -strelOffset[0]);
        
        // ensure input array uses RLE representation (if not already the case)
        fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        
        // create data structure for retrieving rows from rowindex (0 -> N-1)
        IndexedRowsStrel2D strel = IndexedRowsStrel2D.createDisk(radius);
        
        // Initialize data related to strel
        int intRadius = (int) Math.floor(this.radius + 0.5);
        int strelSizeY = 2 * intRadius + 1;
        int strelOffsetY = intRadius;
        
        // Optionally expand the binary rows to better manage borders
        if (this.padding)
        {
            fireStatusChanged(this, "Pad array");
            rleArray = pad(rleArray, intRadius, intRadius);
        }
        
        // initialize a buffer of filtered rows
        FilteredBinaryRowBuffer buffer = new FilteredBinaryRowBuffer(strelSizeY, strel.rows);
        BinaryRow row0 = rleArray.getRow(0);
        for (int iRow = 0; iRow < strelOffsetY + 2; iRow++)
        {
            buffer.update(row0, BinaryRow::erosion);
        }
        for (int iRow = 1 ; iRow < strelSizeY - strelOffsetY - 1; iRow++)
        {
            buffer.update(rleArray.getRow(iRow), BinaryRow::erosion);
        }
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        
        // create result array
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // iterate over rows of result array
        fireStatusChanged(this, "Iterate over rows");
        for (int y = 0; y < sizeY; y++)
        {
            fireProgressChanged(this, y, sizeY);
            
            // shift buffer by using row at index y+sizeY-1-offset
            int index = y + strelSizeY - 1 - strelOffsetY;
            BinaryRow bufferRow = rleArray.getRow(Math.min(index, sizeY - 1));
            buffer.update(bufferRow, BinaryRow::erosion);
            
            // initialize full result row
            BinaryRow resRow = new BinaryRow().complement(sizeX);
            
            // iterate over rows of structuring element
            for (int yStrel = 0; yStrel < strelSizeY; yStrel++)
            {
                int y2 = y + yStrel - strelOffsetY;
                // check current row is within the input array
                if (y2 < 0 || y2 > sizeY - 1)
                {
                    continue;
                }
                // if any of the rows is empty, result of dilation is empty
                if (rleArray.isEmptyRow(y2))
                {
                    resRow = new BinaryRow();
                    break;
                }
                
                // retrieve the result of row erosion with appropriate strel row
                int strelRowIndex = strel.indices[yStrel];
                BinaryRow row = buffer.getFilteredRow(yStrel, strelRowIndex);
                
//                // update result only if necessary
//                if (row != null)
//                {
                    resRow = BinaryRow.intersection(resRow, row);
//                }
            }
            
            if (!resRow.isEmpty())
            {
                res.setRow(y, resRow.crop(0, sizeY - 1));
            }
        }

        fireProgressChanged(this, 1, 1);
        return res;
    }
    
    private RunLengthBinaryArray2D pad(RunLengthBinaryArray2D array, int leftPad, int rightPad)
    {
        // array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // create result array
        HashMap<Integer, BinaryRow> resRows = new HashMap<Integer, BinaryRow>();
        
        // iterate over row indices
        Collection<Integer> indices = array.nonEmptyRowIndices();
        int nInds = indices.size();
        for (int yRow : indices)
        {
            fireProgressChanged(this, yRow, nInds);
            if (!array.isEmptyRow(yRow))
            {
                BinaryRow row = array.getRow(yRow);
                row = pad(row, sizeX, leftPad, rightPad);
                resRows.put(yRow, row);
            }
        }
        
        // create result array
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY, resRows);
        return res;
    }
    
    /**
     * Performs morphological erosion on a 3D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray3D</code> when
     * necessary.
     * 
     * @see #processBinary2d(BinaryArray2D)
     * @see net.sci.array.binary.RunLengthBinaryArray3D
     * 
     * @param array
     *            the array on which erosion should be applied
     * @return the result of erosion as a new array
     */
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
//        // cast to Strel2D and retrieve size and offset
//        Strel3D strel3d = Strel3D.wrap(strel);
//        int[] strelSize = strel3d.size();
//        int[] strelOffset = strel3d.getOffset();
//        
//        // convert structuring element to RLE array
//        RunLengthBinaryArray3D rleStrel = RunLengthBinaryArray3D.convert(strel3d.getMask());
//
//        // prepare strel array: shift each row
//        HashMap<Integer, HashMap<Integer, BinaryRow>> strelRows = shiftRows(rleStrel, -strelOffset[0]);
        
        // ensure input array uses RLE representation (if not already the case)
        this.fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray3D rleArray = RunLengthBinaryArray3D.convert(array);
        
        // Initialize data related to strel
        int intRadius = (int) Math.floor(this.radius + 0.5);
        int strelSizeZ = 2 * intRadius + 1;
        int strelOffsetZ = intRadius;

        // Create data structure to index strel rows
        IndexedRowsStrel3D strel = IndexedRowsStrel3D.createBall(radius);

        // Optionally expand the binary rows to better manage borders
        if (this.padding)
        {
            fireStatusChanged(this, "Pad array");
            rleArray = pad(rleArray, intRadius, intRadius);
        }
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        int sizeZ = rleArray.size(2);
        
        // create buffer
        int[] bufferSize = new int[] {sizeY, strelSizeZ};
        FilteredBinaryRowBuffer2D buffer = new FilteredBinaryRowBuffer2D(bufferSize, strel.rows);
        
        // initialize buffer with upper slices of array
        BinaryRow[] sliceRows = new BinaryRow[sizeY];
        copySliceRows(rleArray, 0, sliceRows);
        for (int iSlice = 0; iSlice < strelOffsetZ + 2; iSlice++)
        {
            buffer.update(sliceRows, BinaryRow::erosion);
        }
        for (int iSlice = 1 ; iSlice < strelSizeZ - strelOffsetZ - 1; iSlice++)
        {
            copySliceRows(rleArray, iSlice, sliceRows);
            buffer.update(sliceRows, BinaryRow::erosion);
        }
        
        
        // create a map of slices for storing result
        HashMap<Integer, HashMap<Integer, BinaryRow>> slices = new HashMap<>();
        
        // iterate over rows of result array
        this.fireStatusChanged(this, "Iterate over z-slices");
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            HashMap<Integer, BinaryRow> currentSlice = new HashMap<>();
            
            // shift buffer by using row at index z+sizeZ-1-offsetZ
            int zNextSlice = Math.min(z + strelSizeZ - 1 - strelOffsetZ, sizeZ - 1);
            copySliceRows(rleArray, zNextSlice, sliceRows);
            buffer.update(sliceRows, BinaryRow::dilation);
            
            for (int y = 0; y < sizeY; y++)
            {
                // initialize full result row
                BinaryRow resRow = new BinaryRow().complement(sizeX);

                // iterate over rows of structuring element
                for (int zStrel : strel.indices.keySet())
                {
                    int z2 = z + zStrel;
                    // check current slice is within the input array
                    if (z2 < 0 || z2 > sizeZ - 1)
                    {
                        continue;
                    }
                    
                    HashMap<Integer, Integer> strelSlice = strel.indices.get(zStrel); 
                    for (int yStrel : strelSlice.keySet())
                    {
                        int y2 = y + yStrel;
                        if (y2 < 0 || y2 > sizeY - 1)
                        {
                            continue;
                        }
                        
                        // if input row is empty, result of row erosion is empty, and
                        // hence the result of intersection with previous result
                        if (rleArray.isEmptyRow(y2, z2))
                        {
                            resRow = new BinaryRow();
                            break;
                        }
                        else
                        {
                            // retrieve the result of row dilation with appropriate strel row
                            int index = strelSlice.get(yStrel);
                            int zBuffer = zStrel + intRadius;
                            BinaryRow row = buffer.getFilteredRow(y2, zBuffer, index);

//                            // update result only if necessary
//                            if (row != null)
//                            {
                                resRow = BinaryRow.intersection(resRow, row);
//                            }
                        }
//                        // retrieve the result of row dilation with appropriate strel row
//                        int index = strelSlice.get(yStrel);
//                        int zBuffer = zStrel + intRadius;
//                        BinaryRow row = buffer.getFilteredRow(y2, zBuffer, index);
//                        
//                        // combine result with current result row
//                        resRow = BinaryRow.union(resRow, row);
//                        
//                        // update result only if necessary
//                        if (row != null)
//                        {
//                            resRow = BinaryRow.intersection(resRow, row);
//                        }
//                        else
//                        {
//                            resRow = new BinaryRow();
//                        }
                    }
                }
                
//                // iterate over rows of structuring element
//                for (int zStrel = 0; zStrel < strelSize[2]; zStrel++)
//                {
//                    int z2 = z + zStrel - strelOffset[2];
//                    // check current row is within the input array
//                    if (z2 < 0 || z2 > sizeZ - 1)
//                    {
//                        continue;
//                    }
//
//                    for (int yStrel = 0; yStrel < strelSize[1]; yStrel++)
//                    {
//                        int y2 = y + yStrel - strelOffset[1];
//                        // check current row is within the input array
//                        if (y2 < 0 || y2 > sizeY - 1)
//                        {
//                            continue;
//                        }
//
//                        // if input row is empty, result of row erosion is empty, and
//                        // hence the result of intersection with previous result
//                        if (rleArray.isEmptyRow(y2, z2))
//                        {
//                            resRow = new BinaryRow();
//                            break;
//                        }
//                        else
//                        {
//                            // retrieve the rows to dilate
//                            BinaryRow arrayRow = rleArray.getRow(y2, z2);
//                            BinaryRow strelRow = strelRows.get(zStrel).get(yStrel);
//                            
//                            // update result only if necessary
//                            if (strelRow != null)
//                            {
//                                resRow = BinaryRow.intersection(resRow, BinaryRow.erosion(arrayRow, strelRow));
//                            }
//                        }
//                    }
//                }
                
                if (!resRow.isEmpty())
                {
                    currentSlice.put(y, resRow.crop(0, sizeX - 1));
                }
            }
            
            if (!currentSlice.isEmpty())
            {
                slices.put(z, currentSlice);
            }
        }
        
        fireProgressChanged(this, 1, 1);
        
        // create result array
        return new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ, slices);
    }
    
    private RunLengthBinaryArray3D pad(RunLengthBinaryArray3D array, int leftPad, int rightPad)
    {
        // array dimensions
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        int sizeZ = array.size(2);
        
        // create result array
        HashMap<Integer, HashMap<Integer, BinaryRow>> resSlices = new HashMap<Integer, HashMap<Integer, BinaryRow>>();
                
        // iterate over slice indices
        Collection<Integer> zSliceIndices = array.nonEmptySliceIndices();
        int nSlices = zSliceIndices.size();
        for (int zSlice : zSliceIndices)
        {
            fireProgressChanged(this, zSlice, nSlices);
            
            Collection<Integer> yRowIndices = array.nonEmptySliceRowIndices(zSlice);
            
            // prepare a map for storing result
            HashMap<Integer, BinaryRow> sliceRows = new HashMap<Integer, BinaryRow>(yRowIndices.size());
            
            // iterate over row indices within current slice
            for (int yRow : yRowIndices)
            {
                BinaryRow row = array.getRow(yRow, zSlice);
                row = pad(row, sizeX, leftPad, rightPad);
                sliceRows.put(yRow, row);
            }
            
            // store rows within slice
            resSlices.put(zSlice, sliceRows);
        }
        
        // create result array
        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ, resSlices);
        return res;
    }
    
    private static final void copySliceRows(RunLengthBinaryArray3D array, int sliceIndex, BinaryRow[] rows)
    {
        int sizeY = array.size(1);
        for (int y = 0; y < sizeY; y++)
        {
            rows[y] = array.getRow(y, sliceIndex);
        }
    }
    
//    /**
//     * Shifts all the runs within the 3D input array by the given amount to the
//     * left.
//     * 
//     * @param array
//     *            the array to shift.
//     * @param dx
//     *            the shift amount (positive to the right)
//     * @return the new shifted array
//     */
//    private HashMap<Integer, HashMap<Integer, BinaryRow>> shiftRows(RunLengthBinaryArray3D array, int dx)
//    {
//        // create
//        HashMap<Integer, HashMap<Integer, BinaryRow>> resRows = new HashMap<Integer, HashMap<Integer, BinaryRow>>();
//
//        // iterate over non-empty slices 
//        for (int z : array.nonEmptySliceIndices())
//        {
//            HashMap<Integer, BinaryRow> sliceRows = new HashMap<Integer, BinaryRow>();
//            for (int y : array.nonEmptySliceRowIndices(z))
//            {
//              sliceRows.put(y, array.getRow(y, z).shift(dx));
//          }
//          resRows.put(z, sliceRows);
//      }
//        
//        // return
//        return resRows;
//    }
    
    /**
     * Ensures the input row can be considered as larger row, by optionally
     * adding <code>true</code> elements before first element and after first
     * element when the extremities of the row are set to <code>true</code>.
     * When an extremity (either to 0 one or the length-1 one) contains zero,
     * then it is not necessary to pad.
     * 
     * @param row
     *            the row to pad (assumed to start at zero)
     * @param length
     *            the length of the row
     * @param leftPad
     *            the pad length before first element
     * @param rightPad
     *            the pad length after last element
     * @return the padded row
     */
    private BinaryRow pad(BinaryRow row, int length, int leftPad, int rightPad)
    {
        if (row.get(0))
        {
            Run run = new Run(-leftPad, -1);
            row = BinaryRow.union(row, new BinaryRow(run));
        }

        if (row.get(length - 1))
        {
            Run run = new Run(length, length + rightPad - 1);
            row = BinaryRow.union(row, new BinaryRow(run));
        }
        
        return row;
    }
    
    public BinaryArray processBinary(BinaryArray array)
    {
        int nd = array.dimensionality();
        switch (nd)
        {
            case 2: return processBinary2d(BinaryArray2D.wrap(array));
            case 3: return processBinary3d(BinaryArray3D.wrap(array));
            default:
                throw new IllegalArgumentException("Requires an array of dimensionality 2, not " + nd);
        }
    }
    /**
     * Default implementation that case the input array to a BinaryArray, and
     * calls the processBinary method.
     * 
     * @param array
     *            the array to process
     * @throws RuntimeException
     *             if the input array is not an instance of BinaryArray
     */
    @Override
    public <T> BinaryArray process(Array<T> array)
    {
        if (array instanceof BinaryArray)
        {
            return processBinary((BinaryArray) array);
        }
        else
        {
            throw new RuntimeException(
                    "Requires an instance of BinaryArray");
        }
    }
    
}
