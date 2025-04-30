/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array3D;
import net.sci.array.binary.BinaryArray2D;
import net.sci.array.binary.BinaryArray3D;
import net.sci.array.binary.BufferedBinaryArray2D;
import net.sci.array.binary.SlicedBinaryArray3D;
import net.sci.array.color.BufferedPackedByteRGB8Array2D;
import net.sci.array.color.BufferedPackedShortRGB16Array2D;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array2D;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt16Array3D;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array3D;
import net.sci.array.numeric.impl.BufferedFloat32Array2D;
import net.sci.array.numeric.impl.BufferedFloat32Array3D;
import net.sci.array.numeric.impl.BufferedInt32Array2D;
import net.sci.array.numeric.impl.BufferedInt32Array3D;
import net.sci.array.numeric.impl.BufferedUInt16Array2D;
import net.sci.array.numeric.impl.BufferedUInt8Array2D;
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
     * The name of the file to read the data from.
     * Initialized at construction.
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

        // allocate memory for image byte data
        byte[] buffer = new byte[nBytes];
        
        // read data from input stream
        int nRead = 0;
        try (RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r"))
        {
            nRead = readByteBuffer(stream, ifd, buffer);
        }

        // Check all buffer elements have been read
        if (nRead != nBytes)
        {
            throw new IOException("Could read only " + nRead + " bytes over the " + nBytes + " expected");
        }
        
        // Transform raw buffer into interpreted buffer
        if (pixelType == PixelType.UINT8)
        {
            return new BufferedUInt8Array2D(sizeX, sizeY, buffer);
        }
        else if (pixelType == PixelType.UINT12 || pixelType == PixelType.UINT16)
        {
            // Store data as short array
            short[] shortBuffer = convertToShortArray(buffer, this.byteOrder);
            return new BufferedUInt16Array2D(sizeX, sizeY, shortBuffer);
        }
        else if (pixelType == PixelType.INT32)
        {
            int[] intBuffer = convertToIntArray(buffer, this.byteOrder);
            return new BufferedInt32Array2D(sizeX, sizeY, intBuffer);
        }
        else if (pixelType == PixelType.FLOAT32)
        {
            float[] floatBuffer = convertToFloatArray(buffer, this.byteOrder);
            return new BufferedFloat32Array2D(sizeX, sizeY, floatBuffer);
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
                    int r = buffer[index++] & 0x00FF;
                    int g = buffer[index++] & 0x00FF;
                    int b = buffer[index++] & 0x00FF;
                    rgb2d.set(x, y, new RGB8(r, g, b));
                }
            }
            return rgb2d;
        }
        else if (pixelType == PixelType.RGB16)
        {
            RGB16Array2D rgb2d = new BufferedPackedShortRGB16Array2D(sizeX, sizeY);
            
            // fill array with re-ordered buffer content
            int index = 0;
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    int r = convertBytesToShort(buffer[index++], buffer[index++], this.byteOrder) & 0x00FFFF;
                    int g = convertBytesToShort(buffer[index++], buffer[index++], this.byteOrder) & 0x00FFFF;
                    int b = convertBytesToShort(buffer[index++], buffer[index++], this.byteOrder) & 0x00FFFF;
                    rgb2d.set(x, y, new RGB16(r, g, b));
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
        
        // allocate buffer
        byte[] buffer = new byte[nBytes];
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // Read the byte array
        int offset = 0;
        int nRead = 0;
        for (ImageFileDirectory info : ifdList)
        {
            nRead += readByteArray(stream, info, buffer, offset);
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
            int[] intBuffer = convertToIntArray(buffer, this.byteOrder);
            return new BufferedInt32Array3D(sizeX, sizeY, sizeZ, intBuffer);
        }
        else if (pixelType == PixelType.FLOAT32)
        {
            float[] floatBuffer = convertToFloatArray(buffer, this.byteOrder);
            return new BufferedFloat32Array3D(sizeX, sizeY, sizeZ, floatBuffer);
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
            
            arrayList.add(new BufferedUInt8Array2D(sizeX, sizeY, buffer));
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
            arrayList.add(new BufferedUInt16Array2D(sizeX, sizeY, shortBuffer));
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
        BinaryArray2D res = new BufferedBinaryArray2D(sizeX, sizeY, booleanBuffer);
        return res;
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
            default -> throw new RuntimeException("Unsupported code for compression mode: " + compressionCode);
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

    private int readByteArrayPackBits(RandomAccessFile stream, ImageFileDirectory ifd, byte[] buffer)
			throws IOException
	{
        // retrieve strips info
        int[] stripOffsets = entryValueAsIntArray(ifd, BaselineTags.StripOffsets.CODE);
        int[] stripByteCounts = entryValueAsIntArray(ifd, BaselineTags.StripByteCounts.CODE);
        if (stripOffsets.length != stripByteCounts.length)
        {
            throw new RuntimeException("Strip offsets and strip byte counts arrays must have same length");
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

        int nRead = PackBits.uncompressPackBits(compressedBytes, buffer, offset);
        return nRead;
    }

    private static final int[] entryValueAsIntArray(ImageFileDirectory ifd, int tagCode)
    {
        TiffTag tag = ifd.getEntry(tagCode);
        return tag.count == 1 ? new int[] {tag.value} : (int[]) tag.content;
    }

    
    // =============================================================
    // Conversion of byte arrays to other arrays
    
    private final static short[] convertToShortArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nPixels = byteBuffer.length / 2;
        short[] shortBuffer = new short[nPixels];
        
        // convert byte array into short array
        for (int i = 0; i < nPixels; i++)
        {
            int b1 = byteBuffer[2 * i] & 0x00FF;
            int b2 = byteBuffer[2 * i + 1] & 0x00FF;

            // encode bytes to short
            if (order == ByteOrder.LITTLE_ENDIAN)
                shortBuffer[i] = (short) ((b2 << 8) | b1);
            else
                shortBuffer[i] = (short) ((b1 << 8) | b2);
        }
        
        return shortBuffer;
    }

    private final static int[] convertToIntArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nPixels = byteBuffer.length / 4;
        int[] intBuffer = new int[nPixels];
        
        // convert byte array into int array
        for (int i = 0; i < nPixels; i++)
        {
            int b1 = byteBuffer[4 * i + 0] & 0x00FF;
            int b2 = byteBuffer[4 * i + 1] & 0x00FF;
            int b3 = byteBuffer[4 * i + 2] & 0x00FF;
            int b4 = byteBuffer[4 * i + 3] & 0x00FF;

            // encode bytes to short
            if (order == ByteOrder.LITTLE_ENDIAN)
                intBuffer[i] = (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
            else
                intBuffer[i] = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
        }
        
        return intBuffer;
    }

    private final static float[] convertToFloatArray(byte[] byteBuffer, ByteOrder order)
    {
        // Store data as short array
        int nFloats = byteBuffer.length / 4;
        float[] floatBuffer = new float[nFloats];
        
        // convert byte array into float array
        for (int i = 0; i < nFloats; i++)
        {
            int b1 = byteBuffer[4 * i + 0] & 0x00FF;
            int b2 = byteBuffer[4 * i + 1] & 0x00FF;
            int b3 = byteBuffer[4 * i + 2] & 0x00FF;
            int b4 = byteBuffer[4 * i + 3] & 0x00FF;

            // encode bytes to float
            if (order == ByteOrder.LITTLE_ENDIAN)
                floatBuffer[i] = Float.intBitsToFloat((b4 << 24) | (b3 << 16) | (b2 << 8) | b1);
            else
                floatBuffer[i] = Float.intBitsToFloat((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
        }
        
        return floatBuffer;
    }

    private static final short convertBytesToShort(byte b1, byte b2, ByteOrder order)
    {
        int v1 = b1 & 0x00FF;
        int v2 = b2 & 0x00FF;
        
        // encode bytes to short
        if (order == ByteOrder.LITTLE_ENDIAN)
            return (short) ((v2 << 8) | v1);
        else
            return (short) ((v1 << 8) | v2);
    }
}
