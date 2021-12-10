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
import net.sci.array.binary.BufferedBinaryArray2D;
import net.sci.array.binary.SlicedBinaryArray3D;
import net.sci.array.color.BufferedPackedByteRGB8Array2D;
import net.sci.array.color.BufferedPackedShortRGB16Array2D;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array2D;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.scalar.BufferedFloat32Array2D;
import net.sci.array.scalar.BufferedFloat32Array3D;
import net.sci.array.scalar.BufferedInt32Array2D;
import net.sci.array.scalar.BufferedUInt16Array2D;
import net.sci.array.scalar.BufferedUInt16Array3D;
import net.sci.array.scalar.BufferedUInt8Array2D;
import net.sci.array.scalar.BufferedUInt8Array3D;
import net.sci.array.scalar.SlicedUInt8Array3D;
import net.sci.array.scalar.UInt8Array;
import net.sci.image.io.PackBits;
import net.sci.image.io.tiff.TiffFileInfo.PixelType;

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

	
	// =============================================================
	// Methods
	
	/**
     * Reads the image data from the current stream and specified info.
     * 
     * @param info
     *            an instance of TiffFileInfo
     * @return the data array corresponding to the specified TiffFileInfo
     * @throws IOException
     *             if an error occurs
     */
	public Array<?> readImageData(TiffFileInfo info) throws IOException
	{
        // special case of binary images
		if (info.pixelType == PixelType.BITMAP)
		{
		    return readBinaryArray2D(info);
		}
		
        // size of buffer is proportional to pixel number
        int nPixels = info.width * info.height;
        int nBytes = nPixels * info.pixelType.getByteNumber();

        // allocate memory for image byte data
		byte[] buffer = new byte[nBytes];
		
        // read data from input stream
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");
		int nRead = readByteBuffer(stream, info, buffer);
        stream.close();

		// Check all buffer elements have been read
		if (nRead != nBytes)
		{
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}

		// Transform raw buffer into interpreted buffer
		switch (info.pixelType) {
		case GRAY8:
		case COLOR8:
		{
			// Case of data coded with 8 bits
			return new BufferedUInt8Array2D(info.width, info.height, buffer);
		}

		case BITMAP:
            boolean[] booleanBuffer = convertToBooleanArray(buffer);
            return new BufferedBinaryArray2D(info.width, info.height, booleanBuffer);
        
		case GRAY16_UNSIGNED:
		case GRAY12_UNSIGNED:
		{
			// Store data as short array
			short[] shortBuffer = convertToShortArray(buffer, info.byteOrder);
			return new BufferedUInt16Array2D(info.width, info.height, shortBuffer);
		}	

		case GRAY32_INT:
		{
			// Store data as int array
			int[] intBuffer = convertToIntArray(buffer, info.byteOrder);
            return new BufferedInt32Array2D(info.width, info.height, intBuffer);
		}	
		
		case GRAY32_FLOAT:
		{
			// Store data as short array
			float[] floatBuffer = convertToFloatArray(buffer, info.byteOrder);
			return new BufferedFloat32Array2D(info.width, info.height, floatBuffer);
		}	

		case RGB:
		case BGR:
		case ARGB:
		case ABGR:
		case BARG:
		case RGB_PLANAR:
		{
			// allocate memory for array
			RGB8Array2D rgb2d = new BufferedPackedByteRGB8Array2D(info.width, info.height);
			
			// fill array with re-ordered buffer content
			int index = 0;
			for (int y = 0; y < info.height; y++)
			{
				for (int x = 0; x < info.width; x++)
				{
					int r = buffer[index++] & 0x00FF;
					int g = buffer[index++] & 0x00FF;
					int b = buffer[index++] & 0x00FF;
					rgb2d.set(x, y, new RGB8(r, g, b));
				}
			}
			return rgb2d;
		}
		
		case RGB48:
		{
		    RGB16Array2D rgb2d = new BufferedPackedShortRGB16Array2D(info.width, info.height);
		    
		    ByteOrder order = info.byteOrder;
		    // fill array with re-ordered buffer content
		    int index = 0;
		    for (int y = 0; y < info.height; y++)
		    {
		        for (int x = 0; x < info.width; x++)
		        {
                    int r = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
                    int g = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
                    int b = convertBytesToShort(buffer[index++], buffer[index++], order) & 0x00FFFF;
		            rgb2d.set(x, y, new RGB16(r, g, b));
		        }
		    }
		    return rgb2d;
		}

		default:
			throw new IOException("Can not process file info of type " + info.pixelType);
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
    public Array3D<?> readImageStack(Collection<TiffFileInfo> fileInfoList)
            throws IOException
    {
        if (fileInfoList.isEmpty())
        {
            throw new IllegalArgumentException("File info list must contains at least one element.");
        }
        
        // Compute image size
        TiffFileInfo info0 = fileInfoList.iterator().next();
        int sizeX = info0.width;
        int sizeY = info0.height;
        int sizeZ = fileInfoList.size();
        
        // Compute size of buffer buffer for each plane
        int pixelsPerPlane = sizeX * sizeY;
        int bytesPerPlane  = pixelsPerPlane * info0.pixelType.getByteNumber();
        
        // When possible, creates a sliced array instance
        
        // Read 3D UInt8 data as SlicedUInt8Array3D
        if (info0.pixelType == PixelType.GRAY8)
        {
            System.out.println("Choose sliced array data representation...");
            // check type limit
            if(info0.pixelType != TiffFileInfo.PixelType.GRAY8) 
            {
                throw new RuntimeException("Can only process UInt8 arrays");
            }
            
            // create the container
            ArrayList<UInt8Array> arrayList = new ArrayList<>(sizeZ);
            
            RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

            // iterate over slices to create each 2D array
            int nSlices = fileInfoList.size();
            int currentSliceIndex = 0;
            for (TiffFileInfo info : fileInfoList)
            {
                this.fireProgressChanged(this, currentSliceIndex++, nSlices);
                
                byte[] buffer = new byte[bytesPerPlane];
                int nRead = readByteBuffer(stream, info, buffer);

                // Check the whole buffer has been read
                if (nRead != bytesPerPlane)
                {
                    throw new IOException("Could read only " + nRead
                            + " bytes over the " + bytesPerPlane + " expected");
                }
                
                arrayList.add(new BufferedUInt8Array2D(sizeX, sizeY, buffer));
            }
            
            stream.close();
            
            // create a new instance of 3D array that stores each slice
            this.fireProgressChanged(this, nSlices, nSlices);
            System.out.println("create 3D array");
            return new SlicedUInt8Array3D(arrayList);
        }
        
        // Read 3D BITMAP data as SlicedBinaryArray3D
        if (info0.pixelType == PixelType.BITMAP)
        {
            System.out.println("Choose sliced binary array data representation...");
            
            // create the container
            ArrayList<BinaryArray2D> arrayList = new ArrayList<>(sizeZ);
            
            RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

            // iterate over slices to create each 2D array
            int nSlices = fileInfoList.size();
            int currentSliceIndex = 0;
            for (TiffFileInfo info : fileInfoList)
            {
                this.fireProgressChanged(this, currentSliceIndex++, nSlices);
                BinaryArray2D sliceData = readBinaryArray2D(info);
                arrayList.add(sliceData);
            }
            
            stream.close();
            
            // create a new instance of 3D array that stores each slice
            this.fireProgressChanged(this, nSlices, nSlices);
            System.out.println("create 3D array");
            return new SlicedBinaryArray3D(arrayList);
        }
        
        // compute total number of expected bytes
        int nBytes = bytesPerPlane * sizeZ;
        if (nBytes < 0)
        {
            throw new RuntimeException("Image data is too large to fit in a single array");
        }
        
        // allocate buffer
        byte[] buffer = new byte[nBytes];
        
        RandomAccessFile stream = new RandomAccessFile(new File(this.filePath), "r");

        // Read the byte array
        int offset = 0;
        int nRead = 0;
        for (TiffFileInfo info : fileInfoList)
        {
            nRead += readByteArray(stream, info, buffer, offset);
            offset += bytesPerPlane;
        }
        
        stream.close();

        // Check the whole buffer has been read
        if (nRead != nBytes)
        {
            throw new IOException("Could read only " + nRead
                    + " bytes over the " + nBytes + " expected");
        }
        
        // Transform raw buffer into interpreted buffer
        switch (info0.pixelType) {
        case GRAY8:
        case COLOR8:
            return new BufferedUInt8Array3D(sizeX, sizeY, sizeZ, buffer);
        case GRAY16_UNSIGNED:
        case GRAY12_UNSIGNED:
        {
            // Store data as short array
            short[] shortBuffer = convertToShortArray(buffer, info0.byteOrder);
            return new BufferedUInt16Array3D(sizeX, sizeY, sizeZ, shortBuffer);
        }   

        case GRAY32_FLOAT:
        {
            float[] floatBuffer = convertToFloatArray(buffer, info0.byteOrder);
            return new BufferedFloat32Array3D(sizeX, sizeY, sizeZ, floatBuffer);
        }

        case BITMAP:
            throw new RuntimeException("Reading Bitmap Tiff files not supported");
            
        default:
            throw new IOException("Can not read stack with data type "
                    + info0.pixelType);
        }
    }
    
	/**
     * Reads a boolean array from the stream, using the specified FileInfo.
     * 
     */
    private BinaryArray2D readBinaryArray2D(TiffFileInfo info)
            throws IOException
    {
        // image size
        int sizeX = info.width;
        int sizeY = info.height;
        
        // need to adapt scan size
        int scanLength = (int) Math.ceil(sizeX / 8.0);
        int bufferLength = scanLength * sizeY;
        byte[] buffer = new byte[bufferLength];
        
        // read byte buffer containing binary data
        File file = new File(this.filePath);
        RandomAccessFile stream = new RandomAccessFile(file, "r");
        int nRead = readByteBuffer(stream, info, buffer);
        stream.close();
        
        // Check all buffer elements have been read
        if (nRead != bufferLength)
        {
            throw new IOException("Could read only " + nRead
                    + " bytes over the " + bufferLength + " expected");
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
	private int readByteBuffer(RandomAccessFile stream, TiffFileInfo info, byte[] buffer)
			throws IOException
	{
		switch (info.compression) {
		case NONE:
			return readByteArrayUncompressed(stream, info, buffer);
		case PACK_BITS:
			return readByteArrayPackBits(stream, info, buffer);
		default:
			throw new RuntimeException("Unsupported compression mode: "
					+ info.compression);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArrayUncompressed(RandomAccessFile stream, TiffFileInfo info, byte[] buffer)
			throws IOException
	{
		int totalRead = 0;
		int offset = 0;

		// read each strip successively
		int nStrips = info.stripOffsets.length;
		for (int i = 0; i < nStrips; i++)
		{
			stream.seek(info.stripOffsets[i] & 0xffffffffL);
			int nRead = stream.read(buffer, offset, info.stripLengths[i]);
			offset += nRead;
			totalRead += nRead;
		}

		return totalRead;
	}

	private int readByteArrayPackBits(RandomAccessFile stream, TiffFileInfo info, byte[] buffer)
			throws IOException
	{
		// Number of strips
		int nStrips = info.stripOffsets.length;

		// Compute the number of bytes per strip
		int nBytes = 0;
		for (int i = 0; i < nStrips; i++)
			nBytes += info.stripLengths[i];
		byte[] compressedBytes = new byte[nBytes];

		// read each compressed strip
		int offset = 0;
		for (int i = 0; i < nStrips; i++)
		{
			stream.seek(info.stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(compressedBytes, offset, info.stripLengths[i]);
			offset += nRead;
		}

		int nRead = PackBits.uncompressPackBits(compressedBytes, buffer);
		return nRead;
	}
    

	// =============================================================
    // utility functions for reading 3D array into contiguous memory block
    
    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteArray(RandomAccessFile stream, TiffFileInfo info, byte[] buffer, int offset)
            throws IOException
    {
        switch (info.compression) {
        case NONE:
            return readByteArrayUncompressed(stream, info, buffer, offset);
        case PACK_BITS:
            return readByteArrayPackBits(stream, info, buffer, offset);
        default:
            throw new RuntimeException("Unsupported compression mode: "
                    + info.compression);
        }
    }

    /**
     * Read an array of bytes into a pre-allocated buffer, by iterating over the
     * strips, and returns the number of bytes read.
     */
    private int readByteArrayUncompressed(RandomAccessFile stream, TiffFileInfo info, byte[] buffer,
            int offset) throws IOException
    {
        int totalRead = 0;

        // read each strip successively
        int nStrips = info.stripOffsets.length;
        for (int i = 0; i < nStrips; i++)
        {
            stream.seek(info.stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(buffer, offset, info.stripLengths[i]);
            offset += nRead;
            totalRead += nRead;
        }

        return totalRead;
    }

    private int readByteArrayPackBits(RandomAccessFile stream, TiffFileInfo info, byte[] buffer, int offset)
            throws IOException
    {
        // Number of strips
        int nStrips = info.stripOffsets.length;

        // Compute the number of bytes per strip
        int nBytes = 0;
        for (int i = 0; i < nStrips; i++)
        {
            nBytes += info.stripLengths[i];
        }
        byte[] compressedBytes = new byte[nBytes];

        // read each compressed strip
        int offset0 = 0;
        for (int i = 0; i < nStrips; i++)
        {
            stream.seek(info.stripOffsets[i] & 0xffffffffL);
            int nRead = stream.read(compressedBytes, offset0, info.stripLengths[i]);
            offset0 += nRead;
        }

        int nRead = PackBits.uncompressPackBits(compressedBytes, buffer, offset);
        return nRead;
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

    private final static boolean[] convertToBooleanArray(byte[] byteBuffer)
    {
        // Store data as short array
        int nPixels = byteBuffer.length * 8;
        boolean[] booleanBuffer = new boolean[nPixels];
        
        // convert byte array into boolean array
        int index = 0;
        for (int i = 0; i < byteBuffer.length; i++)
        {
            byte currentByte = byteBuffer[i];
            for (int j = 0; j < 8; j++)
            {
                booleanBuffer[index++] = (currentByte & 0x01) > 0;
                currentByte >>= 1;
            }
        }
        
        return booleanBuffer;
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
