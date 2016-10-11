/**
 * 
 */
package net.sci.image.io;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import net.sci.array.Array;
import net.sci.array.data.*;
import net.sci.array.data.scalar2d.BufferedUInt8Array2D;
import net.sci.array.data.scalar3d.BufferedUInt8Array3D;
import net.sci.array.data.scalar3d.UInt8Array3D;
import net.sci.image.Image;
import net.sci.image.io.TiffFileInfo.Compression;
import net.sci.image.io.TiffFileInfo.PixelType;

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

	// Entry type for TIFF Image File Directories
	static final int BYTE_TYPE = 1;
	static final int ASCII_TYPE = 2;
	static final int SHORT_TYPE = 3;
	static final int LONG_TYPE = 4;
	static final int RATIONAL_TYPE = 5;
	static final String[] typeNames = {"None", "Byte", "Ascii", "Short", "Long", "Rational"};

	
	/**
	 * Default method for managing tags.
	 * @return
	 */
	static final Map<Integer, TiffTag> getAllTags()
	{
		TreeMap<Integer, TiffTag> map = new TreeMap<>();
		// Some other tag collections may be added in the future.
		for (TiffTag tag : TiffTag.getBaseLineTags())
		{
			map.put(tag.code, tag);
		}
		for (TiffTag tag : TiffTag.getTiffITTags())
		{
			map.put(tag.code, tag);
		}
		return map;
	}

//	/**
//	 * Future set of tags that can be interpreted and added to the list of tags
//	 * of a given image.
//	 */
//	private static TreeMap<Integer, TiffTag> knownTags = createDefaultKnowTAgs();	
//	
//	private static TreeMap<Integer, TiffTag> createDefaultKnowTAgs()
//	{
//		TreeMap<Integer, TiffTag> tags = new TreeMap<Integer, TiffTag>();
//		for (TiffTag tag : new ImageJTiffTags().getTags())
//		{
//			tags.put(tag.code, tag);
//		}
//		return tags;
//	}
//	
//	public static void addKnowTags(Collection<TiffTag> tags)
//	{
//		for (TiffTag tag : new ImageJTiffTags().getTags())
//		{
//			knownTags.put(tag.code, tag);
//		}
//	}

	
	// =============================================================
	// Class variables

	RandomAccessFile inputStream;
	File file;
	
	boolean littleEndian = false;
	
	
	// =============================================================
	// Constructor

	public TiffImageReader(File file) throws IOException
	{
		this.file = file;
		this.inputStream = new RandomAccessFile(file, "r");
		readTiffHeader();
	}

	public TiffImageReader(String fileName) throws IOException
	{
		this.file = new File(fileName);
		this.inputStream = new RandomAccessFile(fileName, "r");
		readTiffHeader();
	}
	
	/**
	 * Read the beginning of the tiff file. The header is composed of 8 bytes:
	 * <ul>
	 * <li>2 bytes for indicating the byte order</li>
	 * <li>2 bytes for the magic number 42</li>
	 * <li>4 bytes for indicating the offset of the first Image File Directory</li>
	 * </ul>
	 * 
	 * @throws IOException
	 */
	private void readTiffHeader() throws IOException
	{
		// read bytes indicating endianness
		int b1 = inputStream.read();
		int b2 = inputStream.read();
		int byteOrder = ((b2 << 8) + b1);

		// Setup local endianness
		if (byteOrder == 0x4949) // "II"
			littleEndian = true;
		else if (byteOrder == 0x4d4d) // "MM"
			littleEndian = false;
		else
		{
			String str = Integer.toHexString(b2) + Integer.toHexString(b1);
			throw new RuntimeException(
					"Could not decode endianness of TIFF File: " + str);
		}
		
		// Read the magic number indicating tiff format
		int magicNumber = readShort(); // should be 42
		if (magicNumber != 42)
		{
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

		// If file contains several images, check if we should read a stack
		// Condition: all images must have same size
		boolean isStack = false;
		if (infoList.size() > 1)
		{
			isStack = true;
			for (TiffFileInfo info : infoList)
			{
				if (info.width != info0.width || info.height != info0.height)
				{
					isStack = false;
					break;
				}
			}
		}

		// Read image data
		Array<?> data;
		if (isStack)
		{
			data = readImageStack(infoList);
		}
		else
		{
			data = readImageData(info0);
		}

		// Create new MetaImage by incorporating meta-buffer
		Image image;
		if (info0.lut == null)
		{
			image = new Image(data);
		}
		else
		{
			image = new Image(data);
			image.setColorMap(info0.lut);
		}

		image.setName(file.getName());
		image.tiffTags = info0.tags;
		
		return image;
	}

	/**
	 * Closes the stream open by this ImageReader.
	 * 
	 * @throws IOException 
	 */
	public void close() throws IOException
	{
		this.inputStream.close();
	}

	/**
	 * Reads the set of image file directories within the TIFF File.
	 */
	public Collection<TiffFileInfo> readImageFileDirectories()
			throws IOException
	{
		// Read file offset of first Image
		inputStream.seek(4);
		long offset = ((long) readInt()) & 0xffffffffL;
		// System.out.println("offset: " + offset);

		if (offset < 0L)
		{
			inputStream.close();
			throw new RuntimeException("Found negative offset in tiff file");
		}

		ArrayList<TiffFileInfo> infoList = new ArrayList<TiffFileInfo>();
		while (offset > 0L)
		{
			inputStream.seek(offset);
			TiffFileInfo info = readImageFileDirectory();
			if (info != null)
			{
				infoList.add(info);
			}

			offset = ((long) readInt()) & 0xffffffffL;
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
		int nEntries = readShort();
		if (nEntries < 1 || nEntries > 1000)
		{
			throw new RuntimeException("Number of entries is out of range: "
					+ nEntries);
		}

		// entry buffer
		int tagCode, fieldType, count;
		int value;

		// create a new FileInfo instance
		TiffFileInfo info = new TiffFileInfo();
		info.intelByteOrder = littleEndian;

		Map<Integer, TiffTag> tagMap = getAllTags();

		// Read each entry
		for (int i = 0; i < nEntries; i++)
		{
			// read tag code
			tagCode = readShort();
			
			// read tag data info
			fieldType = readShort();
			count = readInt();
			value = readValue(fieldType, count);

			// convert state to long offset for reading large buffer
			long offset = ((long) value) & 0xffffffffL;

			switch (tagCode) {
			case 254: // Image subfile type
				info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
				break;

			case 256: // Image Width
				info.width = value;
				break;

			case 257: // Image height
				info.height = value;
				break;

			case 270: // Image description
				info.imageDescription = readAscii(count, value);
				break;

			case 273: // Strip Offsets
				info.stripOffsets = readArray(fieldType, count, value);
				break;

			case 274: // orientation
				info.orientation = TiffFileInfo.Orientation.fromValue(value);
				break;

			case 279: // Strip Byte Counts
				info.stripLengths = readArray(fieldType, count, value);
				break;

			case 262: // Photometric interpretation
				info.photometricInterpretation = value;
				info.whiteIsZero = value == 0;
				break;

			case 258: // Bits Per Sample
				if (count == 1)
				{
					// Scalar type images (grayscale)
					if (value == 8)
						info.fileType = PixelType.GRAY8;
					else if (value == 16)
						info.fileType = PixelType.GRAY16_UNSIGNED;
					else if (value == 32)
						info.fileType = PixelType.GRAY32_INT;
					else if (value == 12)
						info.fileType = PixelType.GRAY12_UNSIGNED;
					else if (value == 1)
						info.fileType = PixelType.BITMAP;
					else
						throw new IOException("Unsupported BitsPerSample: "
								+ value);
				}
				else if (count == 3)
				{
					// Case of color images stored as 3 bands
					int bitDepth = readShort(offset);

					if (bitDepth == 8)
					{
						info.fileType = PixelType.RGB;
					}
					else if (bitDepth == 16)
					{
						info.fileType = PixelType.RGB48;
					}
					else
					{
						throw new IOException(
								"Can only open 8 and 16 bit/channel RGB images ("
										+ bitDepth + ")");
					}
				}
				break;

			case 277: // Samples Per Pixel
				info.samplesPerPixel = value;
				if (value == 3 && info.fileType != PixelType.RGB48)
					info.fileType = info.fileType == PixelType.GRAY16_UNSIGNED ? PixelType.RGB48
							: PixelType.RGB;
				else if (value == 4 && info.fileType == PixelType.GRAY8)
					info.fileType = PixelType.ARGB;
				break;
				
			case 278: // Rows Per Strip
				info.rowsPerStrip = value;
				break;

			case 282: // X Resolution
				double xScale = readRational(offset);
				if (xScale != 0.0)
					info.pixelWidth = 1.0 / xScale;
				break;

			case 283: // Y Resolution
				double yScale = readRational(offset);
				if (yScale != 0.0)
					info.pixelHeight = 1.0 / yScale;
				break;

			case 296: // Resolution unit
				if (value == 1 && info.unit == null)
					info.unit = " ";
				else if (value == 2)
				{
					if (info.pixelWidth == 1.0 / 72.0)
					{
						info.pixelWidth = 1.0;
						info.pixelHeight = 1.0;
					}
					else
						info.unit = "inch";
				}
				else if (value == 3)
					info.unit = "cm";
				break;

			case 284: // Planar Configuration. 1=chunky, 2=planar
				if (value == 2 && info.fileType == PixelType.RGB48)
					info.fileType = PixelType.GRAY16_UNSIGNED;
				else if (value == 2 && info.fileType == PixelType.RGB)
					info.fileType = PixelType.RGB_PLANAR;
				else if (value == 1 && info.samplesPerPixel == 4)
					info.fileType = PixelType.ARGB;
				else if (value != 2
						&& !((info.samplesPerPixel == 1) || (info.samplesPerPixel == 3)))
				{
					String msg = "Unsupported SamplesPerPixel: "
							+ info.samplesPerPixel;
					throw new IOException(msg);
				}
				break;

			case 259: // Compression mode
				info.compression = Compression.fromValue(value);
				break;

			case 320: // color palette (LUT)
				int lutLength = (int) Math.pow(2, 8 * info.bytesPerPixel);
				int expLength = 3 * lutLength;
				if (count != expLength)
				{
					throw new RuntimeException("Tiff Color Palette has "
							+ count + " elements, while it requires "
							+ expLength);
				}
				
				info.lut = readColorMap(lutLength, count, offset);
				break;
				
				
			default: // non-elementary tags
				TiffTag tag = tagMap.get(tagCode);
				
				boolean unknown = false;
				if (tag == null)
				{
					System.out.println("Unknown tag with code " + tagCode + ". Type="
							+ typeNames[fieldType] + ", count=" + count + ", state=" + value);
					tag = new TiffTag(tagCode, "Unknown");
					unknown = true;
				}
				
				// init tag info
				tag.type = fieldType;
				tag.count = count;
				
				setupTag(tag, value);

				if (unknown)
				{
					switch (fieldType)
					{
					case BYTE_TYPE:
					case SHORT_TYPE:
					case LONG_TYPE:
						tag.value = new Integer(value);
						System.out.println(String.format(
								"state of tag %d (%s) is %d", tag.code,
								tag.name, value));
						break;
					case ASCII_TYPE:
						tag.value = readAscii(count, value);
						System.out.println(String.format(
								"state of tag %d (%s) is %s", tag.code,
								tag.name, tag.value));
						break;
					default:
						System.err.println("Could not interpret rational type of tag with code: "
										+ tag.code + " (" + tag.name + ")");
					}
				}
				info.tags.add(tag);

				break;
			} // end of switch(tag)
		}

		return info;
	}
	
	/**
	 * Initialize the value of the tag given its code and the specified value.
	 * 
	 * @param tag
	 *            the tag instance to initialize
	 * @param value
	 *            the value read in the file
	 * @throws IOException
	 *             if tried to read from the file and problem occured
	 */
	private void setupTag(TiffTag tag, int value) throws IOException
	{
		int count = tag.count;
		// parse tag data
		switch (tag.type)
		{
		case BYTE_TYPE:
			tag.value = new Integer(value);
			break;
		case SHORT_TYPE:
			tag.value = new Integer(value);
			break;
		case LONG_TYPE:
			tag.value = new Integer(value);
			break;
		case ASCII_TYPE:
			tag.value = readAscii(count, value);
			break;
		case RATIONAL_TYPE:
			System.err.println("Could not interpret rational with code: "
					+ tag.code + " (" + tag.name + ")");
			break;
		}
	}
	
	private int[][] readColorMap(int lutLength, int count, long offset)
			throws IOException
	{
		// Allocate memory for raw array
		int nBytes = 3 * lutLength * 2;
		byte[] lut16 = new byte[nBytes];
		
		// read the full raw array
		long saveLoc = inputStream.getFilePointer();
		inputStream.seek(offset);
		int nRead = inputStream.read(lut16);
		inputStream.seek(saveLoc);
		if (nRead != nBytes)
		{
			throw new IOException(
					"Could not decode the color palette from TIFF File");
		}
		
		// convert raw array into N-by-3 look-up table
		int[][] lut = new int[lutLength][3];
		int j = 0;
		if (littleEndian)
			j++;
		for (int i = 0; i < lutLength; i++)
		{
			lut[i][0] = lut16[j];
			lut[i][1] = lut16[j + 512];
			lut[i][2] = lut16[j + 1024];
			j += 2;
		}
		return lut;
	}

	int readValue(int fieldType, int count) throws IOException
	{
		int value;
		if (fieldType == SHORT_TYPE && count == 1)
		{
			value = readShort();
			readShort();
		}
		else
			value = readInt();
		return value;
	}

	private String readAscii(int count, int value) throws IOException
	{
		// Allocate memory for string buffer
		byte[] data = new byte[count];

		// read string buffer
		if (count <= 4)
		{
			// unpack integer
			for (int i = 0; i < count; i++)
			{
				data[i] = (byte) (value & 0x00FF);
				value = value >> 8;
			}
		}
		else
		{
			// convert state to long offset for reading large buffer
			long offset = ((long) value) & 0xffffffffL;

			long pos0 = inputStream.getFilePointer();
			inputStream.seek(offset);
			inputStream.read(data);
			inputStream.seek(pos0);
		}

		return new String(data);
	}

	private int[] readArray(int type, int count, int value) throws IOException
	{
		if (count == 1)
		{
			return new int[] { value };
		}

		// convert to long offset for reading large buffer
		long offset = ((long) value) & 0xffffffffL;

		if (type == SHORT_TYPE)
		{
			return readShortArray(count, offset);
		}
		else
		{
			return readIntArray(count, offset);
		}
	}

	private int[] readShortArray(int count, long offset) throws IOException
	{
		// allocate memory for result
		int[] res = new int[count];

		// save pointer location
		long saveLoc = inputStream.getFilePointer();

		// fill up array
		inputStream.seek(offset);
		for (int c = 0; c < count; c++)
			res[c] = readShort();

		// restore pointer and return result
		inputStream.seek(saveLoc);
		return res;
	}

	private int[] readIntArray(int count, long offset) throws IOException
	{
		// allocate memory for result
		int[] res = new int[count];

		// save pointer location
		long saveLoc = inputStream.getFilePointer();

		// fill up array
		inputStream.seek(offset);
		for (int c = 0; c < count; c++)
			res[c] = readInt();

		// restore pointer and return result
		inputStream.seek(saveLoc);
		return res;
	}

	/**
	 * Reads the next integer from the stream.
	 */
	private int readInt() throws IOException
	{
		// read bytes
		int b1 = inputStream.read();
		int b2 = inputStream.read();
		int b3 = inputStream.read();
		int b4 = inputStream.read();

		// encode bytes to integer
		if (littleEndian)
			return ((b4 << 24) + (b3 << 16) + (b2 << 8) + (b1 << 0));
		else
			return ((b1 << 24) + (b2 << 16) + (b3 << 8) + b4);
	}

	/**
	 * Reads the next short state from the stream.
	 */
	private int readShort() throws IOException
	{
		// read bytes
		int b1 = inputStream.read();
		int b2 = inputStream.read();

		// encode bytes to short
		if (littleEndian)
			return ((b2 << 8) + b1);
		else
			return ((b1 << 8) + b2);
	}

	/**
	 * Read the short state stored at the specified position
	 */
	private int readShort(long pos) throws IOException
	{
		long pos0 = inputStream.getFilePointer();
		inputStream.seek(pos);
		int result = readShort();
		inputStream.seek(pos0);
		return result;
	}

	/**
	 * Reads the rationale at the given position, as the ratio of two integers.
	 */
	private double readRational(long loc) throws IOException
	{
		long saveLoc = inputStream.getFilePointer();
		inputStream.seek(loc);
		int numerator = readInt();
		int denominator = readInt();
		inputStream.seek(saveLoc);

		if (denominator != 0)
			return (double) numerator / denominator;
		else
			return 0.0;
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
			return new BufferedUInt8Array2D(info.width, info.height, buffer);
			
//		case GRAY16_SIGNED:
//		case GRAY16_UNSIGNED:
//		case GRAY12_UNSIGNED:
//			pixels = readPixels(info);
//			if (pixels == null)
//				return null;
//			ip = new ShortProcessor(size0, size1, (short[]) pixels, cm);
//			imp = new ImagePlus(fi.fileName, ip);
//			break;
			
//		case GRAY32_INT:
//		case GRAY32_UNSIGNED:
//		case GRAY32_FLOAT:
//			pixels = readPixels(info);
//			if (pixels == null)
//				return null;
//			ip = new FloatProcessor(size0, size1, (float[]) pixels, cm);
//			imp = new ImagePlus(fi.fileName, ip);
//			break;

//		case GRAY24_UNSIGNED:
//		case GRAY64_FLOAT:
//			pixels = readPixels(info);
//			if (pixels == null)
//				return null;
//			ip = new FloatProcessor(size0, size1, (float[]) pixels, cm);
//			imp = new ImagePlus(fi.fileName, ip);
//			break;
			
		case RGB:
		case BGR:
		case ARGB:
		case ABGR:
		case BARG:
		case RGB_PLANAR:
//			RGB8Array2D<?> rgb2d = new RGB8Array2D<?>ByteBuffer(info.width, info.height, buffer);
//			return rgb2d;
//			UInt8Array3D rgb2d = new BufferedUInt8Array3D(3, info.width, info.height, buffer);
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
			inputStream.seek(info.stripOffsets[i]);
			int nRead = inputStream.read(buffer, offset, info.stripLengths[i]);
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
			inputStream.seek(info.stripOffsets[i]);
			int nRead = inputStream.read(buffer, offset, info.stripLengths[i]);
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
			inputStream.seek(info.stripOffsets[i]);
			int nRead = inputStream.read(compressedBytes, offset,
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
			inputStream.seek(info.stripOffsets[i]);
			int nRead = inputStream.read(compressedBytes, offset0,
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
	 * Based on the ImagJ code, which is based on Bio-Formats PackbitsCodec
	 * written by Melissa Linkert.
	 * 
	 * @returns the length of the buffer after decompression
	 */
	private int uncompressPackBits(byte[] input, byte[] output)
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
	 * Based on the ImagJ code, which is based on Bio-Formats PackbitsCodec
	 * written by Melissa Linkert.
	 * 
	 * @returns the length of the buffer after decompression
	 */
	private int uncompressPackBits(byte[] input, byte[] output, int offset)
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
