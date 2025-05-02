/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.SlicedBinaryArray3D;
import net.sci.array.color.BufferedPackedByteRGB8Array2D;
import net.sci.array.color.BufferedPackedShortRGB16Array2D;
import net.sci.array.color.RGB16Array2D;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Array3D;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.Int32Array3D;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt16Array3D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.impl.SlicedUInt16Array3D;
import net.sci.array.numeric.impl.SlicedUInt8Array3D;
import net.sci.image.io.PackBits;
import net.sci.image.io.PixelType;
import net.sci.image.io.TiffImageReader;

/**
 * Read the binary data from a TIFF file based on one or several TiffFileInfo
 * instances.
 * 
 * Manages the conversion from byte arrays to arrays of other types.
 * 
 * @author David Legland
 *
 */
public class TiffImageDataReader extends AlgoStub
{
    // =============================================================
    // Class variables

    /**
     * The name of the file to read the data from. Initialized at construction.
     */
    String filePath;
    
    ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

    
    // =============================================================
    // Constructor

    public TiffImageDataReader(String fileName) throws IOException
    {
        this(new File(fileName));
    }

    public TiffImageDataReader(File file) throws IOException
    {
        this.filePath = file.getPath();
    }

    public TiffImageDataReader(File file, ByteOrder byteOrder) throws IOException
    {
        this.filePath = file.getPath();
        this.byteOrder = byteOrder;
    }
    

    // =============================================================
    // Methods

    /**
     * Reads the image data from the current stream and specified
     * ImageFileDirectory.
     * 
     * @param ifd
     *            an instance of ImageFileDirectory
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
    public Array<?> readImageData(ImageFileDirectory ifd) throws IOException
    {
        PixelType pixelType = TiffImageReader.determinePixelType(ifd);
        
        // special case of binary images
        if (pixelType == PixelType.BINARY)
        {
            return readBinaryArray2D(ifd);
        }
        
        // determine size of buffer, proportional to pixel number
        int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
        int nPixels = sizeX * sizeY;
        int bytesPerPixel = pixelType.byteCount();
        int nBytes = nPixels * bytesPerPixel;

        // allocate an array of bytes for storing raw data
        byte[] byteArray = new byte[nBytes];
        
        // read data from input stream
        int nRead = 0;
        try (RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r"))
        {
            nRead = readByteBuffer(stream, ifd, byteArray);
        }

        // Check all buffer elements have been read
        if (nRead != nBytes)
        {
            throw new IOException("Could read only " + nRead + " bytes over the " + nBytes + " expected");
        }
        
        // Transform raw buffer into interpreted buffer
        if (pixelType == PixelType.UINT8)
        {
            return UInt8Array2D.wrap(byteArray, sizeX, sizeY);
        }
        else if (pixelType == PixelType.UINT12 || pixelType == PixelType.UINT16)
        {
            // Store data as short array
            short[] shortBuffer = convertToShortArray(byteArray, this.byteOrder);
            return UInt16Array2D.wrap(shortBuffer, sizeX, sizeY);
        }
        else if (pixelType == PixelType.INT32)
        {
            int[] intBuffer = convertToIntArray(byteArray, this.byteOrder);
            return Int32Array2D.wrap(intBuffer, sizeX, sizeY);
        }
        else if (pixelType == PixelType.FLOAT32)
        {
            float[] floatBuffer = convertToFloatArray(byteArray, this.byteOrder);
            return Float32Array2D.wrap(floatBuffer, sizeX, sizeY);
        }
        else if (pixelType == PixelType.RGB8)
        {
            // allocate memory for array
            RGB8Array2D rgb2d = new BufferedPackedByteRGB8Array2D(sizeX, sizeY);
            
            // fill array with re-ordered buffer content
            int index = 0;
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    rgb2d.setSample(x, y, 0, byteArray[index++] & 0x00FF);
                    rgb2d.setSample(x, y, 1, byteArray[index++] & 0x00FF);
                    rgb2d.setSample(x, y, 2, byteArray[index++] & 0x00FF);
                }
            }
            return rgb2d;
        }
        else if (pixelType == PixelType.RGB16)
        {
            RGB16Array2D rgb2d = new BufferedPackedShortRGB16Array2D(sizeX, sizeY);
            
            ShortBuffer shortBuffer = ByteBuffer.wrap(byteArray).order(this.byteOrder).asShortBuffer();
            
            // fill array with re-ordered buffer content
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    rgb2d.setSample(x, y, 0, shortBuffer.get() & 0x00FFFF);
                    rgb2d.setSample(x, y, 1, shortBuffer.get() & 0x00FFFF);
                    rgb2d.setSample(x, y, 2, shortBuffer.get() & 0x00FFFF);
                }
            }
            return rgb2d;
        }
        else
        {
            throw new RuntimeException("Number of samples par pixel must be either 1 or 3.");
        }
    }
    
	/**
     * Reads all the image into this file as a single 3D array. All images must
     * have the same dimensions.
     * 
     * @return an instance of Array3D containing all image data within the file.
     * @throws IOException
     *             if an error occurs.
     */
    public Array3D<?> readImageStack(Collection<ImageFileDirectory> ifdList) throws IOException
    {
        if (ifdList.isEmpty())
        {
            throw new IllegalArgumentException("File info list must contains at least one element.");
        }
        
        // read data type info
        ImageFileDirectory ifd0 = ifdList.iterator().next();
        PixelType pixelType = TiffImageReader.determinePixelType(ifd0);
        
        // When possible, calls a specialized method that uses a "sliced" representation of 3D array
        if (pixelType == PixelType.UINT8) return readImageStack_Gray8(ifdList);
        if (pixelType == PixelType.UINT16 || pixelType == PixelType.UINT12) return readImageStack_Gray16(ifdList);
        if (pixelType == PixelType.BINARY) return readImageStack_Bitmap(ifdList);
        
        // Compute image size
        int sizeX = ifd0.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd0.getValue(BaselineTags.ImageHeight.CODE);
        int sizeZ = ifdList.size();
        
        // Compute size of buffer buffer for each plane
        int pixelsPerPlane = sizeX * sizeY;
        int bytesPerPixels = pixelType.byteCount();
        int bytesPerPlane  = pixelsPerPlane * bytesPerPixels;
        
        // compute total number of expected bytes
        int nBytes = bytesPerPlane * sizeZ;
        if (nBytes < 0)
        {
            throw new RuntimeException("Image data is too large to fit in a single java array");
        }
        
        // allocate an array of bytes for storing raw data
        byte[] byteArray = new byte[nBytes];
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // Read the byte array
        int offset = 0;
        int nRead = 0;
        for (ImageFileDirectory info : ifdList)
        {
            nRead += readByteArray(stream, info, byteArray, offset);
            offset += bytesPerPlane;
        }
        
        stream.close();

        // Check the whole buffer has been read
        if (nRead != nBytes)
        {
            throw new IOException("Could read only " + nRead + " bytes over the " + nBytes + " expected");
        }
        
        // Transform raw buffer into interpreted buffer
        if (pixelType == PixelType.INT32)
        {
            int[] intBuffer = convertToIntArray(byteArray, this.byteOrder);
            return Int32Array3D.wrap(intBuffer, sizeX, sizeY, sizeZ);
        }
        else if (pixelType == PixelType.FLOAT32)
        {
            float[] floatBuffer = convertToFloatArray(byteArray, this.byteOrder);
            return Float32Array3D.wrap(floatBuffer, sizeX, sizeY, sizeZ);
        }
        else
        {
            throw new IOException("Can not read stack with data " + bytesPerPixels + " bytes per pixels");
        }
    }
    
    private UInt8Array3D readImageStack_Gray8(Collection<ImageFileDirectory> fileInfoList) throws IOException
    {
        // read data type info
        ImageFileDirectory ifd0 = fileInfoList.iterator().next();
        int samplesPerPixel = ifd0.getValue(BaselineTags.SamplesPerPixel.CODE);
        int[] bitsPerSample = entryValueAsIntArray(ifd0, BaselineTags.BitsPerSample.CODE);
        
        // check IFD entries validity
        if(samplesPerPixel != 1 || bitsPerSample[0] != 8) 
        {
            throw new RuntimeException("Can only process UInt8 arrays");
        }
        
        // Compute image size
        int sizeX = ifd0.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd0.getValue(BaselineTags.ImageHeight.CODE);
        int sizeZ = fileInfoList.size();
        
        // Compute size of buffer buffer for each plane
        int pixelsPerPlane = sizeX * sizeY;
        int bytesPerPlane  = pixelsPerPlane;

        // create the container
        ArrayList<UInt8Array> arrayList = new ArrayList<>(sizeZ);
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // iterate over slices to create each 2D array
        int nSlices = fileInfoList.size();
        int currentSliceIndex = 0;
        for (ImageFileDirectory info : fileInfoList)
        {
            this.fireProgressChanged(this, currentSliceIndex++, nSlices);
            
            byte[] buffer = new byte[bytesPerPlane];
            int nRead = readByteBuffer(stream, info, buffer);

            // Check the whole buffer has been read
            if (nRead != bytesPerPlane)
            {
                throw new IOException("Could read only " + nRead + " bytes over the " + bytesPerPlane + " expected");
            }
            
            arrayList.add(UInt8Array2D.wrap(buffer, sizeX, sizeY));
        }
        
        stream.close();
        
        // create a new instance of 3D array that stores each slice
        this.fireProgressChanged(this, nSlices, nSlices);
        return new SlicedUInt8Array3D(arrayList);
    }
    
    private UInt16Array3D readImageStack_Gray16(Collection<ImageFileDirectory> fileInfoList) throws IOException
    {
        // read data type info
        ImageFileDirectory ifd0 = fileInfoList.iterator().next();
        int samplesPerPixel = ifd0.getValue(BaselineTags.SamplesPerPixel.CODE);
        int[] bitsPerSample = entryValueAsIntArray(ifd0, BaselineTags.BitsPerSample.CODE);
        
        // check IFD entries validity
        if(bitsPerSample[0] != 16 || samplesPerPixel != 1) 
        {
            throw new RuntimeException("Can only process UInt16 arrays");
        }
        
        // Compute image size
        int sizeX = ifd0.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd0.getValue(BaselineTags.ImageHeight.CODE);
        int sizeZ = fileInfoList.size();
        
        // Compute size of buffer buffer for each plane
        int pixelsPerPlane = sizeX * sizeY;
        int bytesPerPixels = 2;
        int bytesPerPlane  = pixelsPerPlane * bytesPerPixels;

        // create the container
        ArrayList<UInt16Array> arrayList = new ArrayList<>(sizeZ);
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // iterate over slices to create each 2D array
        int nSlices = fileInfoList.size();
        int currentSliceIndex = 0;
        for (ImageFileDirectory info : fileInfoList)
        {
            this.fireProgressChanged(this, currentSliceIndex++, nSlices);
            
            byte[] buffer = new byte[bytesPerPlane];
            int nRead = readByteBuffer(stream, info, buffer);

            // Check the whole buffer has been read
            if (nRead != bytesPerPlane)
            {
                throw new IOException(String.format("Could read only %d bytes over the %d expected", nRead, bytesPerPlane));
            }
            
            short[] shortBuffer = convertToShortArray(buffer, this.byteOrder);
            arrayList.add(UInt16Array2D.wrap(shortBuffer, sizeX, sizeY));
        }
        
        stream.close();
        
        // create a new instance of 3D array that stores each slice
        this.fireProgressChanged(this, nSlices, nSlices);
        return new SlicedUInt16Array3D(arrayList);
    }
    
    private BinaryArray3D readImageStack_Bitmap(Collection<ImageFileDirectory> fileInfoList) throws IOException
    {
        // read data type info
        ImageFileDirectory ifd0 = fileInfoList.iterator().next();
        int samplesPerPixel = ifd0.getValue(BaselineTags.SamplesPerPixel.CODE);
        int[] bitsPerSample = entryValueAsIntArray(ifd0, BaselineTags.BitsPerSample.CODE);

        // check IFD entries validity
        if(samplesPerPixel != 1 || bitsPerSample[0] != 1) 
        {
            throw new RuntimeException("Can only process BITMAP arrays");
        }
        
        // Compute image size
        int nSlices = fileInfoList.size();
        
        // create the container
        ArrayList<BinaryArray2D> arrayList = new ArrayList<>(nSlices);
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // iterate over slices to create each 2D array
        int currentSliceIndex = 0;
        for (ImageFileDirectory info : fileInfoList)
        {
            this.fireProgressChanged(this, currentSliceIndex++, nSlices);
            BinaryArray2D sliceData = readBinaryArray2D(info);
            arrayList.add(sliceData);
        }
        
        stream.close();
        
        // create a new instance of 3D array that stores each slice
        this.fireProgressChanged(this, nSlices, nSlices);
        return new SlicedBinaryArray3D(arrayList);
    }
    
    /**
     * Reads a boolean array from the stream, using the specified FileInfo.
     * 
     */
    private BinaryArray2D readBinaryArray2D(ImageFileDirectory ifd) throws IOException
    {
        // image size
        int sizeX = ifd.getValue(BaselineTags.ImageWidth.CODE);
        int sizeY = ifd.getValue(BaselineTags.ImageHeight.CODE);
        
        // need to adapt scan size
        int scanLength = (int) Math.ceil(sizeX / 8.0);
        int bufferLength = scanLength * sizeY;
        byte[] buffer = new byte[bufferLength];
        
        // read byte buffer containing binary data
        int nRead = 0;
        try (RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");)
        {
            nRead = readByteBuffer(stream, ifd, buffer);
        }
        
        // Check all buffer elements have been read
        if (nRead != bufferLength)
        {
            throw new IOException(String.format("Could read only %d bytes over the %d expected", nRead, bufferLength));
        }
        
        // convert byte buffer into boolean buffer
        boolean[] booleanBuffer = new boolean[sizeX * sizeY];
        for (int y = 0; y < sizeY; y++)
        {
            // position within byte buffer 
            int offset = y * scanLength;
            int byteIndex = 0;
            
            // read current line data
            for (int scanIndex = 0; scanIndex < scanLength; scanIndex++)
            {
                // the byte value encapsulating the eight boolean values
                int byteValue = buffer[offset + scanIndex] & 0xFF;
                
                for (int i = 7; i >= 0; i--)
                {
                    if (byteIndex < sizeX)
                    {
                        boolean b = (byteValue & (1 << i)) > 0;
                        booleanBuffer[y * sizeX + byteIndex++] = b;
                    }
                }
            }
        }

        // create result 2D array
        return BinaryArray2D.wrap(booleanBuffer, sizeX, sizeY);
    }

    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteBuffer(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer)
            throws IOException
    {
        TiffTag compressionTag = ifd.getEntry(BaselineTags.CompressionMode.CODE);
        int compressionCode = compressionTag != null ? compressionTag.value : 1;

        return switch (compressionCode)
        {
            case 1 -> readByteArrayUncompressed(stream, ifd, buffer);
            case 32773 -> readByteArrayPackBits(stream, ifd, buffer);
            default -> throw new RuntimeException(
                    "Unsupported code for compression mode: " + compressionCode);
        };
    }

    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteArrayUncompressed(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer)
            throws IOException
    {
        // retrieve strips info
        int[] stripOffsets = entryValueAsIntArray(ifd, BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = entryValueAsIntArray(ifd, BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException("Strip offsets and strip byte counts arrays must have same length");
        }
        
        int totalRead = 0;
        int offset = 0;
    
        // read each strip successively
        for (int i = 0; i < stripOffsets.length; i++)
        {
            stream.seek(stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(buffer, offset, stripByteCounts[i]);
            offset += nRead;
            totalRead += nRead;
        }
    
        return totalRead;
    }

    private int readByteArrayPackBits(RandomAccessFile stream, ImageFileDirectory ifd,
            byte[] buffer) throws IOException
    {
        // retrieve strips info
        int[] stripOffsets = entryValueAsIntArray(ifd, BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = entryValueAsIntArray(ifd, BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException(
                    "Strip offsets and strip byte counts arrays must have same length");
        }

        // Number of strips
        int nStrips = stripOffsets.length;

        // Compute the number of bytes per strip
        int nBytes = 0;
        for (int i = 0; i < nStrips; i++)
            nBytes += stripByteCounts[i];
        byte[] compressedBytes = new byte[nBytes];

        // read each compressed strip
        int offset = 0;
        for (int i = 0; i < nStrips; i++)
        {
            stream.seek(stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(compressedBytes, offset, stripByteCounts[i]);
            offset += nRead;
        }

        int nRead = PackBits.uncompressPackBits(compressedBytes, buffer);
        return nRead;
    }

    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteArray(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer, int offset)
            throws IOException
    {
        TiffTag compressionTag = ifd.getEntry(BaselineTags.CompressionMode.CODE);
        int compressionCode = compressionTag != null ? compressionTag.value : 1;
        
        return switch (compressionCode)
        {
            case 1 -> readByteArrayUncompressed(stream, ifd, buffer, offset);
            case 32773 -> readByteArrayPackBits(stream, ifd, buffer, offset);
            default -> throw new RuntimeException("Unsupported compression mode: " + compressionCode);
        };
    }

    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteArrayUncompressed(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer,
            int offset) throws IOException
    {
        int[] stripOffsets = entryValueAsIntArray(ifd, BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = entryValueAsIntArray(ifd, BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException("Strip offsets and strip byte counts arrays must have same length");
        }

        int totalRead = 0;

        // read each strip successively
        for (int i = 0; i < stripOffsets.length; i++)
        {
            stream.seek(stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(buffer, offset, stripByteCounts[i]);
            offset += nRead;
            totalRead += nRead;
        }

        return totalRead;
    }

    private int readByteArrayPackBits(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer, int offset)
            throws IOException
    {
        int[] stripOffsets = entryValueAsIntArray(ifd, BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = entryValueAsIntArray(ifd, BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException("Strip offsets and strip byte counts arrays must have same length");
        }

        // Compute the number of bytes per strip
        int nBytes = 0;
        for (int i = 0; i < stripOffsets.length; i++)
        {
            nBytes += stripByteCounts[i];
        }
        byte[] compressedBytes = new byte[nBytes];

        // read each compressed strip
        int offset0 = 0;
        for (int i = 0; i < stripOffsets.length; i++)
        {
            stream.seek(stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(compressedBytes, offset0, stripByteCounts[i]);
            offset0 += nRead;
        }

        return PackBits.uncompressPackBits(compressedBytes, buffer, offset);
    }

    private static final int[] entryValueAsIntArray(ImageFileDirectory ifd, int tagCode)
    {
        TiffTag tag = ifd.getEntry(tagCode);
        return tag.count == 1 ? new int[] {tag.value} : (int[]) tag.content;
    }

    
    // =============================================================
    // Conversion of byte arrays to other arrays
    
    private final static short[] convertToShortArray(byte[] byteArray, ByteOrder order)
    {
        short[] shortArray = new short[byteArray.length / 2];
        ByteBuffer.wrap(byteArray).order(order).asShortBuffer().get(shortArray);
        return shortArray;
    }

    private final static int[] convertToIntArray(byte[] byteArray, ByteOrder order)
    {
        int[] intArray = new int[byteArray.length / 4];
        ByteBuffer.wrap(byteArray).order(order).asIntBuffer().put(intArray);
        return intArray;
    }

    private final static float[] convertToFloatArray(byte[] byteArray, ByteOrder order)
    {
        float[] floatArray = new float[byteArray.length / 4];
        ByteBuffer.wrap(byteArray).order(order).asFloatBuffer().get(floatArray);
        return floatArray;
    }
}
