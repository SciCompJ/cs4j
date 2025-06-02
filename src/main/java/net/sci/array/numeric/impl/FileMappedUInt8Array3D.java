/**
 * 
 */
package net.sci.array.numeric.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;

/**
 * Map the content of a binary file onto a 3D array of UInt8.
 * 
 * The data must be contiguous within the file, and not compressed. 
 * 
 * For convenience, the data for the current slice are cached in a byte array.
 * 
 * @author dlegland
 *
 */
public class FileMappedUInt8Array3D extends UInt8Array3D
{
    // =============================================================
    // Class variables
    
    /**
     * The name of the file to read data from.
     */
    String filePath;
    
    /**
     * The position in the file corresponding to the beginning of the data.
     */
    long offset;
    
    /**
     * The file channel used to read the data from the file.
     */
    FileChannel fileChannel = null;
    RandomAccessFile raf = null;
    
    /**
     * The array of byte for the current slice.
     */
    byte[] byteArray;
    
    /**
     * Index of the current slice (updated when reading data at different z-value).
     */
    int currentSliceIndex = -1;
    
    /**
     * The current slice (updated when reading data at different z-value).
     */
    UInt8Array2D currentSlice;
    
    
    // =============================================================
    // Constructor
    
    public FileMappedUInt8Array3D(String filePath, long offset, int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.filePath = filePath;
        this.offset = offset;

        // initialize current cached slice
        this.byteArray = new byte[size0 * size1];
        this.currentSlice = UInt8Array2D.wrap(this.byteArray, size0, size1);
    }

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
    

    private void readCurrentSlice()
    {
        long numel = this.size0 * this.size1;
        ByteBuffer buffer = ByteBuffer.wrap(this.byteArray);
        
        // compute offset of slice beginning
        long start = this.offset;
        if (currentSliceIndex > 0)
        {
            start += numel * ((long) currentSliceIndex);
        }
        
        try
        {
            ensureFileChannelIsOpen();
            this.fileChannel.read(buffer, start);
        }
        catch(IOException ex)
        {
            throw new RuntimeException("Problem occured when reading slice index " + currentSliceIndex, ex);
        }
    }
    
    private void ensureFileChannelIsOpen() throws IOException
    {
        if (this.fileChannel == null)
        {
            this.raf = new RandomAccessFile(this.filePath, "r");
            this.fileChannel = raf.getChannel();
        }
    }
    
    /**
     * Closes the underlying input stream.
     * 
     * @throws IOException
     */
    public void close() throws IOException
    {
        if (this.fileChannel != null)
        {
            this.fileChannel.close();
        }
        if (this.raf != null)
        {
            this.raf.close();
            this.raf = null;
        }
    }
    
    // =============================================================
    // Implementation of the UInt8Array3D interface
    
    /**
     * Updates the cache data with that of the selected slice index, and returns
     * the array corresponding to the slice data.
     * 
     * Note that data will become invalid at the next call to this function. The
     * use of the duplicate() method is encouraged to ensure validity of slice
     * data.
     */
    public UInt8Array2D slice(int sliceIndex)
    {
        ensureCurrentSliceIndex(sliceIndex);
        return this.currentSlice;
    }
    
    @Override
    public byte getByte(int x, int y, int z)
    {
        ensureCurrentSliceIndex(z);
        return this.currentSlice.getByte(x, y);
    }

    @Override
    public void setByte(int x, int y, int z, byte b)
    {
        throw new RuntimeException("Modification of data in FileMappedUInt8Array3D is not available");
    }
}
