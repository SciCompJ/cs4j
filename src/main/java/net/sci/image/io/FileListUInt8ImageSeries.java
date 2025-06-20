/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import net.sci.array.Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.Image;

/**
 * An implementation of UInt8Array3D that relies on a series of images stored in
 * files.
 * 
 * The class stores the data corresponding to a single slice. When the current
 * slice is updated, the corresponding file is loaded and the data for current
 * slice are updated.
 * 
 * @see FileListUInt16ImageSeries
 */
public class FileListUInt8ImageSeries extends UInt8Array3D
{
    // =============================================================
    // Class members
    
    /**
     * The list of files corresponding to each slice
     */
    File[] fileList;
    
    /**
     * Index of the current slice (updated when reading data at different z-value).
     */
    int currentSliceIndex = -1;
    
    /**
     * The current slice (updated when reading data at different z-value).
     */
    UInt8Array2D currentSlice;

    /**
     * A boolean flag that toggles the display of messages about the reading
     * process. Default is false (no message display).
     */
    public boolean verbose = false;
    

    // =============================================================
    // Constructor
    
    /**
     * Creates a new array based on a series of files and the slice dimensions.
     * The third dimension of the array is determined by the length of the file
     * array.
     * 
     * @param fileList
     *            the list of files corresponding to each slice
     * @param sizeX
     *            the horizontal size (width) of the slices
     * @param sizeY
     *            the vertical size (height) of the slices
     */
    public FileListUInt8ImageSeries(File[] fileList, int sizeX, int sizeY)
    {
        super(sizeX, sizeY, fileList.length);
        
        this.fileList = fileList;
    }

    
    // =============================================================
    // local processing methods
    
    private void ensureCurrentSliceIndex(int index)
    {
        if (index != this.currentSliceIndex)
        {
            try
            {
                setCurrentSliceIndex(index);
            }
            catch(IOException ex)
            {
                throw new RuntimeException("Problem occured when reading slice index " + index, ex);
            }
        }
    }

    private void setCurrentSliceIndex(int index) throws IOException
    {
        if (index < 0 || index > this.size2)
        {
            throw new IllegalArgumentException("Slice index must be comprised between 0 and " + this.size2);
        }
        
        this.currentSliceIndex = index;
        readCurrentSlice();
    }

    private void readCurrentSlice() throws IOException
    {
        // retrieve current file
        File file = this.fileList[this.currentSliceIndex];
        if (verbose)
        {
            System.out.printf(Locale.ENGLISH, "Read slice %d, file=%s%n", this.currentSliceIndex, file.getName());
        }
        
        // check file existence
        if (!file.exists())
        {
            throw new RuntimeException(
                    String.format("Unable to find file for slice %d (%s)", this.currentSliceIndex, file.getName()));
        }
        
        // read image data for current slice
        Image image = Image.readImage(file);
        Array<?> sliceData = image.getData();
        
        // check type and dimension of slice data
        if (!(sliceData instanceof UInt8Array))
        {
            throw new RuntimeException("Requires an image containing UInt8 data, not " + sliceData.elementClass());
        }
        if (sliceData.dimensionality() != 2)
        {
            throw new RuntimeException("Requires an image containing array with dimensionality 2, not " + sliceData.dimensionality());
        }
        
        // keep data
        this.currentSlice = UInt8Array2D.wrap((UInt8Array) sliceData);
    }
    
    
    // =============================================================
    // Implementation of UInt8Array3D methods
    
    @Override
    public byte getByte(int x, int y, int z)
    {
        ensureCurrentSliceIndex(z);
        return this.currentSlice.getByte(x, y);
    }

    @Override
    public void setByte(int x, int y, int z, byte b)
    {
        throw new RuntimeException("Modification of a FileList view is not allowed");
    }
    
    
    // =============================================================
    // Override object methods

    @Override
    public String toString()
    {
        return(String.format("FileListUInt16ImageSeries with size %dx%dx%d, current slice: %d", this.size0, this.size1, this.size2, this.currentSliceIndex));
    }
}
