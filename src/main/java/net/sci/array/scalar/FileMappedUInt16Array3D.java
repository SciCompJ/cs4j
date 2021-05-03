/**
 * 
 */
package net.sci.array.scalar;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

/**
 * Map the content of a binary file onto a 3D array of Float32.
 * 
 * The data must be contiguous within the file, and not compressed. 
 * 
 * For convenience, the data for the current slice are cached in a float buffer.
 * 
 * @author dlegland
 *
 */
public class FileMappedUInt16Array3D extends UInt16Array3D
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
     * The buffer of bytes for the current slice. A Float buffer based on this
     * buffer is created when constructor is invoked.
     */
    ByteBuffer byteBuffer;
    
    /**
     * Index of the current slice (updated when reading data at different z-value).
     */
    int currentSliceIndex = -1;
    
    /**
     * The current slice (updated when reading data at different z-value).
     */
    UInt16Array2D currentSlice;

    
    
    // =============================================================
    // Constructor
    
    public FileMappedUInt16Array3D(String filePath, long offset, int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.filePath = filePath;
        this.offset = offset;

        // initialize byte buffer for storing current slice data
        byte[] byteArray = new byte[size0 * size1 * 2];
        this.byteBuffer = ByteBuffer.wrap(byteArray);
        
        // wrap the byte buffer into a Float32Array2D
        ShortBuffer buffer = this.byteBuffer.asShortBuffer();
        this.currentSlice = new ShortBufferUInt16Array2D(size0, size1, buffer);
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
        // compute offset of slice beginning
        long numel = this.size0 * this.size1 * 2;
        long start = this.offset;
        if (currentSliceIndex > 0)
        {
            start += numel * ((long) currentSliceIndex);
        }
        
        try
        {
            ensureFileChannelIsOpen();

            // reset position of inner byte buffer
            this.byteBuffer.position(0);

            // read data
            this.fileChannel.read(this.byteBuffer, start);
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
    // Implementation of the Float32Array3D interface
    
    /**
     * Updates the cache data with that of the selected slice index, and returns
     * the array corresponding to the slice data.
     * 
     * Note that data will become invalid at the next call to this function. The
     * use of the duplicate() method is encouraged to ensure validity of slice
     * data.
     */
    public UInt16Array2D slice(int sliceIndex)
    {
        ensureCurrentSliceIndex(sliceIndex);
        return this.currentSlice;
    }
    
    @Override
    public short getShort(int... pos)
    {
        ensureCurrentSliceIndex(pos[2]);
        return this.currentSlice.getShort(pos[0], pos[1]);
    }

    @Override
    public void setShort(int x, int y, int z, short f)
    {
        throw new RuntimeException("Modification of data in FileMappedUInt16Array3D is not available");
    }

    @Override
    public double getValue(int... pos)
    {
        ensureCurrentSliceIndex(pos[2]);
        return this.currentSlice.getShort(pos[0], pos[1]);
    }
}
