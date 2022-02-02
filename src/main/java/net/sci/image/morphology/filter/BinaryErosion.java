/**
 * 
 */
package net.sci.image.morphology.filter;

import java.util.HashMap;

import net.sci.array.binary.BinaryArray;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BinaryRow;
import net.sci.array.binary.RunLengthBinaryArray2D;
import net.sci.array.binary.RunLengthBinaryArray3D;
import net.sci.image.morphology.BinaryMorphologicalFilter;
import net.sci.image.morphology.Strel;
import net.sci.image.morphology.strel.Strel2D;
import net.sci.image.morphology.strel.Strel3D;

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
public class BinaryErosion extends BinaryMorphologicalFilter
{
    public BinaryErosion(Strel strel)
    {
        super(strel);
    }
    
    public BinaryArray2D processBinary2d(BinaryArray2D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        RunLengthBinaryArray2D rleArray = RunLengthBinaryArray2D.convert(array);
        
        // cast to Strel2D and retrieve size and offset
        Strel2D strel2d = Strel2D.wrap(strel);
        int[] strelSize = strel2d.size();
        int[] strelOffset = strel2d.getOffset();
        
        // convert to RLE array
        RunLengthBinaryArray2D strel2 = RunLengthBinaryArray2D.convert(strel2d.getMask());
        
        // work on rows shifted along x axis
        HashMap<Integer, BinaryRow> strelRows = shiftRows(strel2, -strelOffset[0]);
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        
        // create result array
        RunLengthBinaryArray2D res = new RunLengthBinaryArray2D(sizeX, sizeY);
        
        // iterate over rows of result array
        for (int yres = 0; yres < sizeY; yres++)
        {
            fireProgressChanged(this, yres, sizeY);
            
            // initialize full result row
            BinaryRow resRow = new BinaryRow().complement(sizeX);
            
            // iterate over rows of structuring element
            for (int yStrel = 0; yStrel < strelSize[1]; yStrel++)
            {
                int y2 = yres + yStrel - strelOffset[1];
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
                else
                {
                    // retrieve the rows to erode
                    BinaryRow arrayRow = rleArray.getRow(y2);
                    BinaryRow strelRow = strelRows.get(yStrel);
                    
                    // update result only if necessary
                    if (strelRow != null)
                    {
                        resRow = resRow.intersection(arrayRow.erosion(strelRow));
                    }
                }
            }
            
            if (!resRow.isEmpty())
            {
                res.setRow(yres, resRow.crop(0, sizeY - 1));
            }
        }

        fireProgressChanged(this, 1, 1);
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
    private HashMap<Integer, BinaryRow> shiftRows(RunLengthBinaryArray2D array, int dx)
    {
        HashMap<Integer, BinaryRow> resRows = new HashMap<Integer, BinaryRow>();
        for (int y : array.nonEmptyRowIndices())
        {
           resRows.put(y, array.getRow(y).shift(dx));
        }
        return resRows;
    }
    
    public BinaryArray3D processBinary3d(BinaryArray3D array)
    {
        // ensure input array uses RLE representation (if not already the case)
        RunLengthBinaryArray3D rleArray = RunLengthBinaryArray3D.convert(array);
        
        // cast to Strel2D and retrieve size and offset
        Strel3D strel3d = Strel3D.wrap(strel);
        int[] strelSize = strel3d.size();
        int[] strelOffset = strel3d.getOffset();
        
        // convert to RLE array
        RunLengthBinaryArray3D rleStrel = RunLengthBinaryArray3D.convert(strel3d.getMask());

        // prepare strel array: shift each row
        HashMap<Integer, HashMap<Integer, BinaryRow>> strelRows = shiftRows(rleStrel, -strelOffset[0]);
        
        // array dimensions
        int sizeX = rleArray.size(0);
        int sizeY = rleArray.size(1);
        int sizeZ = rleArray.size(2);
        
        // create result array
        RunLengthBinaryArray3D res = new RunLengthBinaryArray3D(sizeX, sizeY, sizeZ);
        
        // iterate over rows of result array
        for (int zres = 0; zres < sizeZ; zres++)
        {
            fireProgressChanged(this, zres, sizeZ);
            
            for (int yres = 0; yres < sizeY; yres++)
            {
                // initialize full result row
                BinaryRow resRow = new BinaryRow().complement(sizeX);

                // iterate over rows of structuring element
                for (int zStrel = 0; zStrel < strelSize[2]; zStrel++)
                {
                    int z2 = zres + zStrel - strelOffset[2];
                    // check current row is within the input array
                    if (z2 < 0 || z2 > sizeZ - 1)
                    {
                        continue;
                    }

                    for (int yStrel = 0; yStrel < strelSize[1]; yStrel++)
                    {
                        int y2 = yres + yStrel - strelOffset[1];
                        // check current row is within the input array
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
                            // retrieve the rows to dilate
                            BinaryRow arrayRow = rleArray.getRow(y2, z2);
                            BinaryRow strelRow = strelRows.get(zStrel).get(yStrel);
                            
                            // update result only if necessary
                            if (strelRow != null)
                            {
                                resRow = resRow.intersection(arrayRow.erosion(strelRow));
                            }
                        }
                    }
                }
                
                if (!resRow.isEmpty())
                {
                    res.setRow(yres, zres, resRow.crop(0, sizeX - 1));
                }
            }
        }
        
        fireProgressChanged(this, 1, 1);
        return res;
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
    private HashMap<Integer, HashMap<Integer, BinaryRow>> shiftRows(RunLengthBinaryArray3D array, int dx)
    {
        // create
        HashMap<Integer, HashMap<Integer, BinaryRow>> resRows = new HashMap<Integer, HashMap<Integer, BinaryRow>>();
        
        // iterate 
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
        switch (nd)
        {
            case 2: return processBinary2d(BinaryArray2D.wrap(array));
            case 3: return processBinary3d(BinaryArray3D.wrap(array));
            default:
                throw new IllegalArgumentException("Requires an array of dimensionality 2, not " + nd);
        }
    }
}
