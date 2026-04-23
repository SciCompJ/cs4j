/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;

import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.image.io.PackBits;

/**
 * Map the content of a Tiff file onto a 3D array of UInt8. Should allow for
 * compressed data as well.
 * 
 * For convenience, the data for the current slice are cached in a byte array.
 * 
 * @author dlegland
 *
 */
public class TiffFileUInt8Array3D extends UInt8Array3D
{
    // =============================================================
    // Static factory
    
    public static final TiffFileUInt8Array3D open(String filePath, Collection<ImageFileDirectory> ifdList)
    {
        ImageFileDirectory ifd0 = ifdList.iterator().next();
        // Compute image size
        int sizeX = ifd0.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd0.getValue(BaselineTags.ImageHeight.CODE);
        int sizeZ = ifdList.size();

        return new TiffFileUInt8Array3D(filePath, ifdList, sizeX, sizeY, sizeZ);
    }
    
    
    // =============================================================
    // Class variables
    
    /**
     * The name of the file to read data from.
     */
    String filePath;
    
    /**
     * The position in the file corresponding to the beginning of each slide data.
     */
    long[] offsets;
    /**
     * The list of ImageFileDirectory, one for each slice.
     */
    ArrayList<ImageFileDirectory> imageFileDirectories;
    
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
    
    public TiffFileUInt8Array3D(String filePath, Collection<ImageFileDirectory> imageFileDirectories, int size0, int size1, int size2)
    {
        super(size0, size1, size2);
        this.filePath = filePath;
        
        // secure copy of offset array
        this.imageFileDirectories = new ArrayList<>(size2);
        this.imageFileDirectories.addAll(imageFileDirectories);

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
        try
        {
            ensureFileChannelIsOpen();
            ImageFileDirectory ifd = this.imageFileDirectories.get(currentSliceIndex);
            readByteBuffer(this.fileChannel, ifd, this.byteArray);
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
    
    private static int readByteBuffer(FileChannel fileChannel, ImageFileDirectory ifd, byte[] byteArray) throws IOException
    {
        int compressionCode = ifd.getIntValue(BaselineTags.Compression.CODE, BaselineTags.Compression.NONE);

        return switch (compressionCode)
        {
            case BaselineTags.Compression.NONE -> readByteArrayUncompressed(fileChannel, ifd, byteArray);
            case BaselineTags.Compression.PACKBITS -> readByteArrayPackBits(fileChannel, ifd, byteArray);
            default -> throw new RuntimeException(
                    "Unsupported code for compression mode: " + compressionCode);
        };
    }
    
    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private static final int readByteArrayUncompressed(FileChannel fileChannel, ImageFileDirectory ifd, byte[] byteArray)
            throws IOException
    {
        // retrieve strips info
        int[] stripOffsets = ifd.getIntArrayValue(BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = ifd.getIntArrayValue(BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException("Strip offsets and strip byte counts arrays must have same length");
        }
        
        int totalRead = 0;
        int offset = 0;
    
        // read each strip successively
        for (int i = 0; i < stripOffsets.length; i++)
        {
            ByteBuffer buffer = ByteBuffer.wrap(byteArray, offset, stripByteCounts[i]);
            int nRead = fileChannel.read(buffer, stripOffsets[i] & 0xffffffffL);
            offset += nRead;
            totalRead += nRead;
        }
    
        return totalRead;
    }

    private static final int readByteArrayPackBits(FileChannel fileChannel, ImageFileDirectory ifd,
            byte[] byteArray) throws IOException
    {
        // retrieve strips info
        int[] stripOffsets = ifd.getIntArrayValue(BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = ifd.getIntArrayValue(BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException(
                    "Strip offsets and strip byte counts arrays must have same length");
        }

        // Number of strips
        int nStrips = stripOffsets.length;

        // Create a buffer for compressed bytes
        byte[] compressedBytes = new byte[sum(stripByteCounts)];

        // read each compressed strip
        int offset = 0;
        for (int i = 0; i < nStrips; i++)
        {
            ByteBuffer buffer = ByteBuffer.wrap(compressedBytes, offset, stripByteCounts[i]);
            int nRead = fileChannel.read(buffer, stripOffsets[i] & 0xffffffffL);
            offset += nRead;
        }

        // uncompress the data into the byte array
        return PackBits.uncompressPackBits(compressedBytes, byteArray);
    }

    private static final int sum(int[] values)
    {
        int sum = 0;
        for (int v : values)
        {
            sum += v;
        }
        return sum;
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
    
    /**
     * Returns false, as the view can not be modified.
     * 
     * @return false
     */
    public boolean isModifiable()
    {
        return false;
    }    
}
