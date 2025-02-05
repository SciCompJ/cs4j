/**
 * 
 */
package net.sci.image.morphology.filtering;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.Run;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.BinaryMorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

/**
 * Computes dilation of a binary array.
 * 
 * Converts the input into run-length encoded arrays when necessary.
 * 
 * @see BinaryErosion
 * @see BinaryOpening
 * @see BinaryClosing
 * @see Dilation
 * 
 * @author dlegland
 */
public class BinaryDilation extends BinaryMorphologicalFilter
{
    /**
     * Computes the dilation of a row using another row as structuring element.
     * 
     * @param row1
     *            the row to dilate.
     * @param row2
     *            the row to dilate with.
     * @return the result of the dilation of the two rows.
     */
    public static final BinaryRow dilation(BinaryRow row1, BinaryRow row)
    {
        BinaryRow res = row1.duplicate();
        for (Run run : row)
        {
            BinaryRow resDil = dilationLeftRight(row1, -run.left, run.right);
            res = BinaryRow.union(res, resDil);
        }
        return res;
    }
    
    public static final BinaryRow dilationLeftRight(BinaryRow row, int leftDilate, int rightDilate)
    {
        // create result runs
        TreeSet<Run> newRuns = new TreeSet<Run>();
        
        Iterator<Run> iter = row.iterator();
        Run currentRun = iter.hasNext() ? iter.next() : null;
        
        while(currentRun != null)
        {
            // init extremities of new run
            int newLeft = currentRun.left - leftDilate;
            int newRight = currentRun.right + rightDilate;
            
            // switch to next run
            currentRun = iter.hasNext() ? iter.next() : null;
            
            // if leftDilate or rightDilate is negative, this may delete some runs
            // here, we simply iterate
            if (newLeft > newRight)
            {
                continue;
            }
            
            // in case of last run, creates new run and finalize 
            if (currentRun == null)
            {
                newRuns.add(new Run(newLeft, newRight));
                break;
            }
            
            // iterate while right extremity overlap with left extremity of next run
            while (currentRun != null && currentRun.left - leftDilate - 1 <= newRight)
            {
                // overlap -> need to update right extremity
                newRight = currentRun.right + rightDilate;
                // and update current run
                currentRun = iter.hasNext() ? iter.next() : null;
            }
            
            newRuns.add(new Run(newLeft, newRight));
        }
        
        // create the new row from list of runs
        return new BinaryRow(newRuns);
    }
    
    public BinaryDilation(Strel strel)
    {
        super(strel);
    }
    
    /**
     * Performs morphological dilation on a 2D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray2D</code> when
     * necessary.
     * 
     * @see #processBinary3d(BinaryArray3D)
     * @see net.sci.array.binary.RunLengthBinaryArray3D
     * 
     * @param array
     *            the array on which dilation should be applied
     * @return the result of dilation as a new array
     */
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        
        // cast to Strel2D and retrieve size and offset
        Strel2D strel2d = Strel2D.wrap(strel);
        int[] strelSize = strel2d.size();
        int[] strelOffset = strel2d.maskOffset();
        
        // convert to RLE array
        RunLengthBinaryArray2D rleStrel = RunLengthBinaryArray2D.convert(strel2d.binaryMask());
        
        // work on rows shifted along x axis
        HashMap<Integer, BinaryRow> strelRows = shiftRowsToLeft(rleStrel, strelOffset[0]);
        
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
            
            // initialize empty result row
            BinaryRow resRow = new BinaryRow();
            
            // iterate over rows of structuring element
            for (int yStrel = 0; yStrel < strelSize[1]; yStrel++)
            {
                int y2 = y + yStrel - strelOffset[1];
                // check current row is within the input array
                if (y2 < 0 || y2 > sizeY - 1)
                {
                    continue;
                }
                
                // do not compute dilation if row is empty
                if (rleArray.isEmptyRow(y2))
                {
                    continue;
                }
                
                // retrieve the rows to dilate
                BinaryRow arrayRow = rleArray.getRow(y2);
                BinaryRow strelRow = strelRows.get(yStrel);
                
                BinaryRow row = dilation(arrayRow, strelRow);
                resRow = BinaryRow.union(resRow, row);
            }
            
            if (!resRow.isEmpty())
            {
                res.setRow(y, resRow.crop(0, sizeX - 1));
            }
        }
        
        return res;
    }
    
    /**
     * Shifts all the runs within the input array by the given amount to the
     * left.
     * 
     * @param array
     *            the array to shift.
     * @param dx
     *            the shift amount (positive to the right)
     * @return the new shifted array
     */
    private static final HashMap<Integer, BinaryRow> shiftRowsToLeft(RunLengthBinaryArray2D array, int dx)
    {
        HashMap<Integer, BinaryRow> resRows = new HashMap<Integer, BinaryRow>();
        for (int y : array.nonEmptyRowIndices())
        {
           resRows.put(y, array.getRow(y).shift(-dx));
        }
        return resRows;
    }

    /**
     * Performs morphological dilation on a 3D binary array. The input is
     * converted into an instance of <code>RunLengthBinaryArray3D</code> when
     * necessary.
     * 
     * @see #processBinary2d(BinaryArray2D)
     * @see net.sci.array.binary.RunLengthBinaryArray2D
     * 
     * @param array
     *            the array on which dilation should be applied
     * @return the result of dilation as a new array
     */
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        RunLengthBinaryArray3D rleArray = RunLengthBinaryArray3D.convert(array);
        
        // cast to Strel2D and retrieve size and offset
        Strel3D strel3d = Strel3D.wrap(strel);
        int[] strelSize = strel3d.size();
        int[] strelOffset = strel3d.maskOffset();
        
        // convert to RLE array
        fireStatusChanged(this, "Prepare input image");
        RunLengthBinaryArray3D strel2 = RunLengthBinaryArray3D.convert(strel3d.binaryMask());

        // prepare strel array: shift each row
        HashMap<Integer, HashMap<Integer, BinaryRow>> strelRows = shiftRows(strel2, -strelOffset[0]);
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        int sizeZ = rleArray.size(2);
        
        // create a map of slices for storing result
        HashMap<Integer, HashMap<Integer, BinaryRow>> slices = new HashMap<>();
        
        // iterate over slices of result array
        fireStatusChanged(this, "Iterate over z-slices");
        for (int z = 0; z < sizeZ; z++)
        {
            fireProgressChanged(this, z, sizeZ);
            HashMap<Integer, BinaryRow> currentSlice = new HashMap<>();
            
            // iterate over rows of current slice
            for (int y = 0; y < sizeY; y++)
            {
                // initialize empty result row
                BinaryRow resRow = new BinaryRow();

                // iterate over rows of structuring element
                for (int zStrel = 0; zStrel < strelSize[2]; zStrel++)
                {
                    int z2 = z + zStrel - strelOffset[2];
                    // check current slice is within the input array
                    if (z2 < 0 || z2 > sizeZ - 1)
                    {
                        continue;
                    }
                    
                    for (int yStrel = 0; yStrel < strelSize[1]; yStrel++)
                    {
                        int y2 = y + yStrel - strelOffset[1];
                        // check current row is within the input array
                        if (y2 < 0 || y2 > sizeY - 1)
                        {
                            continue;
                        }

                        // do not compute dilation if any of the rows is empty
                        if (strel2.isEmptyRow(yStrel, zStrel) || rleArray.isEmptyRow(y2, z2))
                        {
                            continue;
                        }

                        // retrieve the rows to dilate
                        BinaryRow arrayRow = rleArray.getRow(y2, z2);
                        BinaryRow strelRow = strelRows.get(zStrel).get(yStrel);
                        
                        // apply row dilation and combine with result of current row
                        BinaryRow row = dilation(arrayRow, strelRow);
                        resRow = BinaryRow.union(resRow, row);
                    }
                }
                
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
    
    /**
     * Shifts all the runs within the 3D input array by the given amount to the
     * left.
     * 
     * @param array
     *            the array to shift.
     * @param dx
     *            the shift amount (positive to the right)
     * @return the new shifted array
     */
    private static final HashMap<Integer, HashMap<Integer, BinaryRow>> shiftRows(RunLengthBinaryArray3D array, int dx)
    {
        // create array
        HashMap<Integer, HashMap<Integer, BinaryRow>> resRows = new HashMap<Integer, HashMap<Integer, BinaryRow>>();

        // iterate over non-empty slices 
        for (int z : array.nonEmptySliceIndices())
        {
            HashMap<Integer, BinaryRow> sliceRows = new HashMap<Integer, BinaryRow>();
            for (int y : array.nonEmptySliceRowIndices(z))
            {
                sliceRows.put(y, array.getRow(y, z).shift(dx));
            }
            resRows.put(z, sliceRows);
        }

        // return
        return resRows;
    }
    
    @Override
    public BinaryArray processBinary(BinaryArray array)
    {
        int nd = array.dimensionality();
        return switch (nd)
        {
            case 2 -> processBinary2d(BinaryArray2D.wrap(array));
            case 3 -> processBinary3d(BinaryArray3D.wrap(array));
            default -> throw new IllegalArgumentException("Requires an array of dimensionality 2 or 3, not " + nd);
        };
    }
}
