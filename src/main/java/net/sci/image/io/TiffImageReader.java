/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import net.sci.array.Array;
import net.sci.array.data.Array3D;
import net.sci.array.data.scalar2d.BufferedFloatArray2D;
import net.sci.array.data.scalar2d.BufferedInt32Array2D;
import net.sci.array.data.scalar2d.BufferedUInt16Array2D;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar3d.BufferedUInt8Array3D;
import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.image.Image;

/**
 * Provides methods for reading Image files in TIFF Format.
 * 
 * @author David Legland
 *
 */
public class TiffImageReader implements ImageReader
{
	// =============================================================
	// Static
	
	
	// =============================================================
	// Class variables
	
	/**
	 * The file stream from which data are extracted, and which manages data endianness.
	 */
	BinaryDataReader dataReader;

	
	// =============================================================
	// Constructor

	public TiffImageReader(String fileName) throws IOException
	{
		this(new File(fileName));
	}

	public TiffImageReader(File file) throws IOException
	{
		createTiffDataReader(file);
	}

	/**
	 * Reads the beginning of the tiff file. The header is composed of 8 bytes:
	 * <ul>
	 * <li>2 bytes for indicating the byte order</li>
	 * <li>2 bytes for the magic number 42</li>
	 * <li>4 bytes for indicating the offset of the first Image File Directory</li>
	 * </ul>
	 * 
	 * @throws IOException
	 */
	private void createTiffDataReader(File file) throws IOException
	{
		RandomAccessFile inputStream = new RandomAccessFile(file, "r");
		
		// read bytes indicating endianness
		int b1 = inputStream.read();
		int b2 = inputStream.read();
		int byteOrder = ((b2 << 8) + b1);

		// Setup local endianness
		boolean littleEndian = true;
		if (byteOrder == 0x4949) // "II"
		{
			littleEndian = true;
		}
		else if (byteOrder == 0x4d4d) // "MM"
		{
			littleEndian = false;
		}
		else
		{
			String str = Integer.toHexString(b2) + Integer.toHexString(b1);
			inputStream.close();
			throw new RuntimeException(
					"Could not decode endianness of TIFF File: " + str);
		}
		ByteOrder order = littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN; 
		
		this.dataReader = new BinaryDataReader(inputStream, order);
		
		// Read the magic number indicating tiff format
		int magicNumber = dataReader.readShort();
		if (magicNumber != 42)
		{
			inputStream.close();
			throw new RuntimeException(
					"Invalid TIFF file, magic number is different from 42");
		}
	}

	
	// =============================================================
	// Methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see jipl.io.ImageReader#readImage()
	 */
	@Override
	public Image readImage() throws IOException
	{
		// Read the set of image information in the file
		Collection<TiffFileInfo> infoList = readImageFileDirectories();
		if (infoList.size() == 0)
		{
			throw new RuntimeException("Could not read any meta-information from file");
		}

		
		// Read File information of the first image stored in the file
		TiffFileInfo info0 = infoList.iterator().next();
		// info.print();

		// Read image data
		Array<?> data;
		boolean isStack = isStack(infoList);
		if (isStack)
		{
			data = readImageStack(infoList);
		}
		else
		{
			data = readImageData(info0);
		}

		// Create new Image
		Image image = new Image(data);
		
		// Add Image meta-data
		if (info0.lut != null)
		{
			image.setColorMap(info0.lut);
		}
		image.tiffTags = info0.tags;
		
		return image;
	}

	/**
	 * @param infoList a list of TiffFileInfo typically read from a single file
	 * @return true if the list of FileInfo can be seen as a 3D stack
	 */
	private boolean isStack(Collection<TiffFileInfo> infoList)
	{
		// single image is not stack by definition
		if (infoList.size() == 1)
		{
			return false;
		}
		
		// Read File information of the first image stored in the file
		TiffFileInfo info0 = infoList.iterator().next();
				
		// If file contains several images, check if we should read a stack
		// Condition: all images must have same size
		// TODO: detect multi-channel images
		for (TiffFileInfo info : infoList)
		{
			if (info.width != info0.width || info.height != info0.height)
			{
				return false;
			}
		}
		
		// if all items declare the same size, we can load a stack
		return true;
	}
	/**
	 * Closes the stream open by this ImageReader.
	 * 
	 * @throws IOException 
	 */
	public void close() throws IOException
	{
		this.dataReader.close();
	}

	/**
	 * Reads the set of image file directories within this TIFF File.
	 */
	public Collection<TiffFileInfo> readImageFileDirectories()
			throws IOException
	{
		// Read file offset of first Image
		dataReader.seek(4);
		long offset = ((long) dataReader.readInt()) & 0xffffffffL;
		// System.out.println("offset: " + offset);

		if (offset < 0L)
		{
			dataReader.close();
			throw new RuntimeException("Found negative offset in tiff file");
		}

		ArrayList<TiffFileInfo> infoList = new ArrayList<TiffFileInfo>();
		while (offset > 0L)
		{
			dataReader.seek(offset);
			TiffFileInfo info = readImageFileDirectory();
			if (info != null)
			{
				infoList.add(info);
			}

			offset = ((long) dataReader.readInt()) & 0xffffffffL;
		}

		if (infoList.size() == 0)
		{
			throw new RuntimeException("Could not read any image info in file");
		}
		
		return infoList;
	}
	
	/**
	 * Reads the next Image File Directory structure from the input stream.
	 */
	private TiffFileInfo readImageFileDirectory() throws IOException
	{
		// Read and control the number of entries
		int nEntries = dataReader.readShort();
		if (nEntries < 1 || nEntries > 1000)
		{
			throw new RuntimeException("Number of entries is out of range: "
					+ nEntries);
		}

		// create a new FileInfo instance
		TiffFileInfo info = new TiffFileInfo();
		info.intelByteOrder = dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN;

		Map<Integer, TiffTag> tagMap = TiffTag.getAllTags();

		// Read each entry
		for (int i = 0; i < nEntries; i++)
		{
			// read tag code
			int tagCode = dataReader.readShort();
			
			// read type of tag data
			TiffTag.Type type = readTagType(tagCode);
			
			// reader number of data and value / offset
			int count = dataReader.readInt();
			int value = readTagValue(type, count);

			TiffTag tag = tagMap.get(tagCode);
			
			// if tag was not found, create a default empty tag
			if (tag == null)
			{
				System.out.println("Unknown tag with code " + tagCode + ". Type="
						+ type + ", count=" + count + ", value=" + value);
				tag = new TiffTag(tagCode, "Unknown");
			}
			
			// init tag info
			tag.type = type;
			tag.count = count;
			tag.value = value;
			tag.readContent(dataReader);

			// call the initialization procedure specific to tag
			tag.process(info, this.dataReader);

			info.tags.add(tag);
		}

		// Adjust the number of bytes per pixel for some specific formats 
		switch (info.fileType)
		{
		case GRAY32_INT:
		case GRAY32_FLOAT:
			info.bytesPerPixel = 4;
			break;
		default:
		}

		return info;
	}

	private TiffTag.Type readTagType(int tagCode) throws IOException
	{
		// read tag data info
		int typeValue = dataReader.readShort();
		TiffTag.Type type;
		try 
		{
			type = TiffTag.Type.getType(typeValue); 
		}
		catch(IllegalArgumentException ex)
		{
			throw new RuntimeException(String.format("Tag with code %d has incorrect type value: %d", tagCode, typeValue));
		}
		
		return type;
	}

	private int readTagValue(TiffTag.Type type, int count) throws IOException
	{
		int value;
		if (type == TiffTag.Type.SHORT && count == 1)
		{
			value = dataReader.readShort();
			dataReader.readShort();
		}
		else
		{
			value = dataReader.readInt();
		}
		return value;
	}


	public Array3D<?> readImageStack(Collection<TiffFileInfo> infos)
			throws IOException
	{
		// Compute image size
		TiffFileInfo info0 = infos.iterator().next();
		int width = info0.width;
		int height = info0.height;
		int depth = infos.size();
		
		// Compute size of buffer buffer for each plane
		int pixelsPerPlane = width * height;
		int bytesPerPlane  = pixelsPerPlane * info0.getBytesPerPixel();
		int nBytes = bytesPerPlane * depth;
		
		// Allocate buffer array
		byte[] buffer = new byte[nBytes];
		
		// Read the byte array
		int offset = 0;
		int nRead = 0;
		for (TiffFileInfo info : infos)
		{
			nRead += readByteArray(buffer, info, offset);
			offset += bytesPerPlane;
		}

		// Check all buffer have been read
		if (nRead != nBytes)
		{
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}

		// Transform raw buffer into interpreted buffer
		switch (info0.fileType) {
		case GRAY8:
		case COLOR8:
		case BITMAP:
			return new BufferedUInt8Array3D(width, height, depth, buffer);
		default:
			throw new IOException("Can not process file info of type "
					+ info0.fileType);
		}
	}
	
	/**
	 * Reads the buffer from the current stream and specified info. 
	 * @throws IOException 
	 */
	public Array<?> readImageData(TiffFileInfo info) throws IOException
	{
		// Size of image and of buffer buffer
		int nPixels = info.width * info.height;

		// compute size of buffer
		int nBytes = nPixels * info.getBytesPerPixel();

		// Read the byte array
		byte[] buffer = new byte[nBytes];
		int nRead = readByteArray(buffer, info);

		// Check all buffer have been read
		if (nRead != nBytes)
		{
			throw new IOException("Could read only " + nRead
					+ " bytes over the " + nBytes + " expected");
		}

		
		// Transform raw buffer into interpreted buffer
		switch (info.fileType) {
		case GRAY8:
		case COLOR8:
		case BITMAP:
		{
			// Case of data coded with 8 bits
			return new BufferedUInt8Array2D(info.width, info.height, buffer);
		}
		
		case GRAY16_UNSIGNED:
		case GRAY12_UNSIGNED:
		{
			// Store data as short array
			short[] shortBuffer = new short[nPixels];
			
			// convert byte array into sort array
			for (int i = 0; i < nPixels; i++)
			{
				int b1 = buffer[2 * i] & 0x00FF;
				int b2 = buffer[2 * i + 1] & 0x00FF;
				
				// encode bytes to short
				if (dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN)
					shortBuffer[i] = (short) ((b2 << 8) + b1);
				else
					shortBuffer[i] = (short) ((b1 << 8) + b2);
			}
			
			return new BufferedUInt16Array2D(info.width, info.height, shortBuffer);
		}	

		case GRAY32_INT:
		{
			// Store data as short array
			int[] intBuffer = new int[nPixels];
			
			// convert byte array into sort array
			for (int i = 0; i < nPixels; i++)
			{
				int b1 = buffer[4 * i] & 0x00FF;
				int b2 = buffer[4 * i + 1] & 0x00FF;
				int b3 = buffer[4 * i + 2] & 0x00FF;
				int b4 = buffer[4 * i + 3] & 0x00FF;
				
				// encode bytes to short
				if (dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN)
					intBuffer[i] = ((b4 << 24) | (b3 << 16) | (b2 << 8) | b1);
				else
					intBuffer[i] = ((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
			}
			
			return new BufferedInt32Array2D(info.width, info.height, intBuffer);
		}	
		
		
		case GRAY32_FLOAT:
		{
			// Store data as short array
			float[] floatBuffer = new float[nPixels];
			
			// convert byte array into sort array
			for (int i = 0; i < nPixels; i++)
			{
				int b1 = buffer[4 * i] & 0x00FF;
				int b2 = buffer[4 * i + 1] & 0x00FF;
				int b3 = buffer[4 * i + 2] & 0x00FF;
				int b4 = buffer[4 * i + 3] & 0x00FF;
				
				// encode bytes to short
				if (dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN)
					floatBuffer[i] = Float.intBitsToFloat((b4 << 24) | (b3 << 16) | (b2 << 8) | b1);
				else
					floatBuffer[i] = Float.intBitsToFloat((b1 << 24) | (b2 << 16) | (b3 << 8) | b4);
			}
			
			return new BufferedFloatArray2D(info.width, info.height, floatBuffer);
		}	

		case RGB:
		case BGR:
		case ARGB:
		case ABGR:
		case BARG:
		case RGB_PLANAR:
			// allocate memory for array
			UInt8Array3D rgb2d = new BufferedUInt8Array3D(info.width, info.height, 3);
			
			// fill array with re-ordered buffer content
			int index = 0;
			for (int y = 0; y < info.height; y++)
			{
				for (int x = 0; x < info.width; x++)
				{
					rgb2d.setByte(x, y, 0, buffer[index++]);
					rgb2d.setByte(x, y, 1, buffer[index++]);
					rgb2d.setByte(x, y, 2, buffer[index++]);
				}
			}
			return rgb2d;
			
//			case GRAY16_SIGNED:
//			case GRAY24_UNSIGNED:
			
//			case GRAY32_UNSIGNED:
			
//			case GRAY64_FLOAT:
//				pixels = readPixels(info);
//				if (pixels == null)
//					return null;
//				ip = new FloatProcessor(size0, size1, (float[]) pixels, cm);
//				imp = new ImagePlus(fi.fileName, ip);
//				break;
		default:
			throw new IOException("Can not process file info of type " + info.fileType);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArray(byte[] buffer, TiffFileInfo info)
			throws IOException
	{
		switch (info.compression) {
		case NONE:
			return readByteArrayUncompressed(buffer, info);
		case PACK_BITS:
			return readByteArrayPackBits(buffer, info);
		default:
			throw new RuntimeException("Unsupported compression mode: "
					+ info.compression);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArray(byte[] buffer, TiffFileInfo info, int offset)
			throws IOException
	{
		switch (info.compression) {
		case NONE:
			return readByteArrayUncompressed(buffer, info, offset);
		case PACK_BITS:
			return readByteArrayPackBits(buffer, info, offset);
		default:
			throw new RuntimeException("Unsupported compression mode: "
					+ info.compression);
		}
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArrayUncompressed(byte[] buffer, TiffFileInfo info)
			throws IOException
	{
		int totalRead = 0;
		int offset = 0;

		// read each strip successively
		int nStrips = info.stripOffsets.length;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i]);
			int nRead = dataReader.read(buffer, offset, info.stripLengths[i]);
			offset += nRead;
			totalRead += nRead;
		}

		return totalRead;
	}

	/**
	 * Read an array of bytes into a pre-allocated buffer, by iterating over the
	 * strips, and returns the number of bytes read.
	 */
	private int readByteArrayUncompressed(byte[] buffer, TiffFileInfo info,
			int offset) throws IOException
	{
		int totalRead = 0;

		// read each strip successively
		int nStrips = info.stripOffsets.length;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i]);
			int nRead = dataReader.read(buffer, offset, info.stripLengths[i]);
			offset += nRead;
			totalRead += nRead;
		}

		return totalRead;
	}

	private int readByteArrayPackBits(byte[] buffer, TiffFileInfo info)
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
			dataReader.seek(info.stripOffsets[i]);
			int nRead = dataReader.read(compressedBytes, offset,
					info.stripLengths[i]);
			offset += nRead;
		}

		int nRead = uncompressPackBits(compressedBytes, buffer);
		return nRead;
	}

	private int readByteArrayPackBits(byte[] buffer, TiffFileInfo info,
			int offset) throws IOException
	{
		// Number of strips
		int nStrips = info.stripOffsets.length;

		// Compute the number of bytes per strip
		int nBytes = 0;
		for (int i = 0; i < nStrips; i++)
			nBytes += info.stripLengths[i];
		byte[] compressedBytes = new byte[nBytes];

		// read each compressed strip
		int offset0 = 0;
		for (int i = 0; i < nStrips; i++)
		{
			dataReader.seek(info.stripOffsets[i]);
			int nRead = dataReader.read(compressedBytes, offset0,
					info.stripLengths[i]);
			offset0 += nRead;
		}

		int nRead = uncompressPackBits(compressedBytes, buffer, offset);
		return nRead;
	}

	/**
	 * Uncompress byte array into a pre-allocated result byte array, using
	 * Packbits compression.
	 * 
	 * Based on the ImageJ code, which is based on Bio-Formats PackbitsCodec
	 * written by Melissa Linkert.
	 * 
	 * @returns the length of the buffer after decompression
	 */
	private static int uncompressPackBits(byte[] input, byte[] output)
	{
		int index = 0;
		int index2 = 0;
		while (index < input.length && index2 < output.length)
		{
			// read the compression code
			byte n = input[index++];
			if (n >= 0)
			{
				// copy the next n+1 bytes literally
				for (int i = 0; i < n + 1; i++)
					output[index2++] = input[index++];
			}
			else if (n != -128)
			{
				// copy the next byte state -n+1 times
				int count = -n + 1;
				byte value = input[index++];
				for (int i = 0; i < count; i++)
					output[index2++] = value;
			}
		}

		return index2;
	}

	/**
	 * Uncompress byte array into a pre-allocated result byte array, using
	 * Packbits compression.
	 * 
	 * Based on the ImageJ code, which is based on Bio-Formats PackbitsCodec
	 * written by Melissa Linkert.
	 * 
	 * @returns the length of the buffer after decompression
	 */
	private static int uncompressPackBits(byte[] input, byte[] output, int offset)
	{
		int index = 0;
		int index2 = offset;
		while (index < input.length && index2 < output.length)
		{
			// read the compression code
			byte n = input[index++];
			if (n >= 0)
			{
				// copy the next n+1 bytes literally
				for (int i = 0; i < n + 1; i++)
					output[index2++] = input[index++];
			}
			else if (n != -128)
			{
				// copy the next byte state -n+1 times
				int count = -n + 1;
				byte value = input[index++];
				for (int i = 0; i < count; i++)
					output[index2++] = value;
			}
		}

		return index2 - offset;
	}
}
