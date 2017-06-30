/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import net.sci.image.io.TiffFileInfo.Compression;
import net.sci.image.io.TiffFileInfo.PixelType;

/**
 * Manages tag information in TIFF files.
 * 
 * For complete Tiff Tag list, see http://www.awaresystems.be/imaging/tiff/tifftags.html  
 * @author dlegland
 *
 */
public class TiffTag
{
	// =============================================================
	// Static constants

	// Entry type for TIFF Image File Directories
	public static final int BYTE_TYPE = 1;
	public static final int ASCII_TYPE = 2;
	public static final int SHORT_TYPE = 3;
	public static final int LONG_TYPE = 4;
	public static final int RATIONAL_TYPE = 5;

	/**
	 * The type of data stored by a tag.
	 */
	static enum Type 
	{
		UNKNOWN,
		BYTE,
		ASCII,
		SHORT,
		LONG,
		RATIONAL;
		
		public static final Type getType(int typeCode)
		{
			switch (typeCode)
			{
			case 1: return BYTE;
			case 2: return ASCII;
			case 3: return SHORT;
			case 4: return LONG;
			case 5: return RATIONAL;
			default: return UNKNOWN;
			}
		}
	};

	// =============================================================
	// static methods

	/**
	 * Returns a set of know tags indexed by their code.
	 * 
	 * @return
	 */
	static final Map<Integer, TiffTag> getAllTags()
	{
		TreeMap<Integer, TiffTag> map = new TreeMap<>();
		
		// Baseline tags (image size and format)
		for (TiffTag tag : TiffTag.getBaseLineTags())
		{
			map.put(tag.code, tag);
		}
		
		// Extension tags: less common formats
		for (TiffTag tag : TiffTag.getExtensionTags())
		{
			map.put(tag.code, tag);
		}
		
		// TIFF/IT specification 
		for (TiffTag tag : TiffTag.getTiffITTags())
		{
			map.put(tag.code, tag);
		}
		
		// ImageJ Tags 
		for (TiffTag tag : TiffTag.getImageJTags())
		{
			map.put(tag.code, tag);
		}
		
		// Some other tag collections may be added in the future.
		
		return map;
	}

	/**
	 * List of baseline tags. Should be processed directly by the tiff reader.
	 * Uncomplete for now.
	 * 
	 * @return a list of common tags.
	 */
	public static final Collection<TiffTag> getBaseLineTags()
	{
		ArrayList<TiffTag> tags = new ArrayList<>();

		tags.add(new TiffTag(254, "SubFileType")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
			}
		});

		// Tag 255 is deprecated
		
		tags.add(new TiffTag(256, "ImageWidth")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.width = value;
			}
		});

		tags.add(new TiffTag(257, "ImageHeight")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.height = value;
			}
		});

		tags.add(new TiffTag(258, "BitsPerSample")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				if (count == 1)
				{
					// Scalar type images (grayscale)
					if (value == 8)
						info.fileType = PixelType.GRAY8;
					else if (value == 16)
						info.fileType = PixelType.GRAY16_UNSIGNED;
					else if (value == 32)
						info.fileType = PixelType.GRAY32_FLOAT;
					else if (value == 12)
						info.fileType = PixelType.GRAY12_UNSIGNED;
					else if (value == 1)
						info.fileType = PixelType.BITMAP;
					else
						throw new IOException(
								"Unsupported BitsPerSample: " + value);
				} 
				else if (count == 3)
				{
					// Case of color images stored as 3 bands/channels
					int bitDepth = readShort(dataReader);

					if (bitDepth == 8)
					{
						info.fileType = PixelType.RGB;
					} else if (bitDepth == 16)
					{
						info.fileType = PixelType.RGB48;
					} else
					{
						throw new IOException(
								"Can only open 8 and 16 bit/channel RGB images ("
										+ bitDepth + ")");
					}
				}
			}
		});

		tags.add(new TiffTag(259, "CompressionMode")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.compression = Compression.fromValue(value);
			}
		});

		// No ref for tags 260-261
		
		tags.add(new TiffTag(262, "PhotometricInterpretation")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.photometricInterpretation = value;
				info.whiteIsZero = value == 0;
			}
		});

//		tags.add(new TiffTag(263, "Threshholding")
//		{
//			public void process(TiffFileInfo info, BinaryDataReader dataReader)
//			{
//			}
//		});

		// Tags 264-265 refer to half-toning
		
		// Tag 266 refer to logical order of bits within a byte
		
		tags.add(new TiffTag(270, "ImageDescription")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				info.imageDescription = readAscii(dataReader);
			}
		});

		// Tags 271-272 refer to scanner model name and manufacturer
		
		tags.add(new TiffTag(273, "StripOffsets")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				info.stripOffsets = readArray(dataReader);
			}
		});

		tags.add(new TiffTag(274, "Orientation")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.orientation = TiffFileInfo.Orientation.fromValue(value);
			}
		});

		tags.add(new TiffTag(277, "SamplesPerPixel")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.samplesPerPixel = value;
				if (value == 3 && info.fileType != PixelType.RGB48)
					info.fileType = info.fileType == PixelType.GRAY16_UNSIGNED ? PixelType.RGB48
							: PixelType.RGB;
				else if (value == 4 && info.fileType == PixelType.GRAY8)
					info.fileType = PixelType.ARGB;
			}
		});

		tags.add(new TiffTag(278, "RowsPerStrip")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader)
			{
				info.rowsPerStrip = value;
			}
		});
		
		tags.add(new TiffTag(279, "StripByteCount")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				info.stripLengths = readArray(dataReader);
			}
		});
		
		tags.add(new TiffTag(280, "MinSampleValue", "The minimum component value used")
		{
			//TODO: add processing
		});
		
		tags.add(new TiffTag(281, "MaxSampleValue", "The maximum component value used")
		{
			//TODO: add processing
		});
		
		tags.add(new TiffTag(282, "XResolution")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				double xScale = readRational(dataReader);
				if (xScale != 0.0)
					info.pixelWidth = 1.0 / xScale;
			}
		});
		
		tags.add(new TiffTag(283, "YResolution")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				double yScale = readRational(dataReader);
				if (yScale != 0.0)
					info.pixelHeight = 1.0 / yScale;
			}
		});
		
		tags.add(new TiffTag(284, "PlanarConfiguration")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				if (value == 2 && info.fileType == PixelType.RGB48)
					info.fileType = PixelType.GRAY16_UNSIGNED;
				else if (value == 2 && info.fileType == PixelType.RGB)
					info.fileType = PixelType.RGB_PLANAR;
				else if (value == 1 && info.samplesPerPixel == 4)
					info.fileType = PixelType.ARGB;
				else if (value != 2
						&& !((info.samplesPerPixel == 1) || (info.samplesPerPixel == 3)))
				{
					String msg = "Unsupported SamplesPerPixel: " + info.samplesPerPixel;
					throw new IOException(msg);
				}
			}
		});
		
		tags.add(new TiffTag(296, "ResolutionUnit")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				if (value == 1 && info.unit == null)
				{
					info.unit = "";
				}
				else if (value == 2)
				{
					if (info.pixelWidth == 1.0 / 72.0)
					{
						info.pixelWidth = 1.0;
						info.pixelHeight = 1.0;
					}
					else
					{
						info.unit = "inch";
					}
				}
				else if (value == 3)
				{
					info.unit = "cm";
				}
			}
		});
		
		tags.add(new TiffTag(305, "Software",
				"Name and version number of the software package(s) used to create the image."));

		tags.add(new TiffTag(320, "ColorMap")
		{
			public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
			{
				int lutLength = (int) Math.pow(2, 8 * info.bytesPerPixel);
				int expLength = 3 * lutLength;
				if (count != expLength)
				{
					throw new RuntimeException("Tiff Color Palette has "
							+ count + " elements, while it requires "
							+ expLength);
				}
				
				info.lut = readColorMap(dataReader, lutLength);
			}
		});
		
		tags.add(new TiffTag(338, "ExtraSamples",
				"Description of extra components."));

		return tags;
	}

	
	/**
	 * List of extension tags. 
	 *  
	 * @return a list of extension tags
	 */
	public static final Collection<TiffTag> getExtensionTags()
	{
		ArrayList<TiffTag> tags = new ArrayList<>();

		tags.add(new TiffTag(317, "Predictor", "A mathematical operator that is applied to the image data before an encoding scheme is applied"));
		tags.add(new TiffTag(339, "SampleFormat", "Specifies how to interpret each data sample in a pixel"));
		
		return tags;
	}

	public static final Collection<TiffTag> getTiffITTags()
	{
		ArrayList<TiffTag> tags = new ArrayList<>();
		
		tags.add(new TiffTag(34016, "Site"));
		tags.add(new TiffTag(34017, "ColorSequence"));
		tags.add(new TiffTag(34018, "IT8Header"));
		tags.add(new TiffTag(34019, "RasterPadding"));
		tags.add(new TiffTag(34020, "BitsPerRunLength"));
		tags.add(new TiffTag(34021, "BitsPerExtendedRunLength"));
		tags.add(new TiffTag(34022, "ColorTable"));
		tags.add(new TiffTag(34023, "ImageColorIndicator"));
		tags.add(new TiffTag(34024, "BackgroundColorIndicator"));
		tags.add(new TiffTag(34025, "ImageColorValue"));
		tags.add(new TiffTag(34026, "BackgroundColorValue"));
		tags.add(new TiffTag(34027, "PixelIntensityRange"));
		tags.add(new TiffTag(34028, "TransparencyIndicator"));
		tags.add(new TiffTag(34029, "ColorCharacterization"));
		tags.add(new TiffTag(34030, "HCUsage"));
		tags.add(new TiffTag(34031, "TrapIndicator"));
		tags.add(new TiffTag(34032, "CMYKEquivalent"));
				
		return tags;
	}
	
	/**
	 * List of ImageJ Tags. They seem to correspond to ROI management.
	 *  
	 * @return a list of extension tags
	 */
	public static final Collection<TiffTag> getImageJTags()
	{
		ArrayList<TiffTag> tags = new ArrayList<>();

		tags.add(new TiffTag(50838, "ImageJMetaDataCounts"));
		tags.add(new TiffTag(50839, "ImageJMetaData"));

		return tags;
	}

	// =============================================================
	// Class variables

	/**
	 * The integer code used to identify this tag.
	 */
	public int code;
	
	/** The name of this tag, for easy identification in GUI */
	public String name;

	/** An optional description of this tag */
	public String description = null;
	
	/**
	 * The type of value stored by this tag.
	 */
	public Type type;
	
	/**
	 * The number of data stored by this tag.
	 */
	public int count;
	
	/**
	 * The integer value associated to this tag, that may have a different
	 * meaning depending on tag type.
	 */
	public int value;
	
	/**
	 * The data contained by this tag, that have to be interpreted depending on the type.
	 */
	public Object content;
	
	// =============================================================
	// Constructor

	public TiffTag(int code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
	public TiffTag(int code, String name, String description)
	{
		this.code = code;
		this.name = name;
		this.description = description;
	}
	
	
	// =============================================================
	// public new methods

	/**
	 * Initialize the content of the tag from the data reader, given its code
	 * and the specified value.
	 * 
	 * @param dataReader the instance of DataReader to read optional information from
	 * @throws IOException
	 *             if tried to read from the file and problem occurred
	 */
	public void readContent(BinaryDataReader dataReader) throws IOException
	{
		// parse tag data
		switch (this.type)
		{
		case BYTE:
			this.content = new Integer(value);
			break;
		case SHORT:
			this.content = new Integer(value);
			break;
		case LONG:
			this.content = new Integer(value);
			break;
		case ASCII:
			this.content = readAscii(dataReader);
			break;
		case RATIONAL:
			// convert tag value to long offset for reading large buffer
			this.content = readRational(dataReader);
			break;
			
		case UNKNOWN:
			System.err.println("Could not interpret tag with code: "
					+ this.code + " (" + this.name + ")");
			break;
		}		
	}
	
    /**
     * Updates the specified FileInfo data structure according to the current
     * value of the tag.
     * 
     * @param info
     *            an instance of TiffFileInfo.
     * @param dataReader
     *            the instance of DataReader used to read information
     * @throws IOException
     *             if an error occurs
     */
	public void process(TiffFileInfo info, BinaryDataReader dataReader) throws IOException
	{
	}
	
	
	// =============================================================
	// protected methods used by subclasses

	protected int[][] readColorMap(BinaryDataReader dataReader, int lutLength)
			throws IOException
	{
		// Allocate memory for raw array
		int nBytes = 3 * lutLength * 2;
		byte[] lut16 = new byte[nBytes];
		
		// convert state to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		// read the full raw array
		long saveLoc = dataReader.getFilePointer();
		dataReader.seek(offset);
		int nRead = dataReader.read(lut16);
		dataReader.seek(saveLoc);
		if (nRead != nBytes)
		{
			throw new IOException(
					"Could not decode the color palette from TIFF File");
		}
		
		// convert raw array into N-by-3 look-up table
		int[][] lut = new int[lutLength][3];
		int j = 0;
		if (dataReader.getOrder() == ByteOrder.LITTLE_ENDIAN)
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

	protected String readAscii(BinaryDataReader dataReader) throws IOException
	{
		// Allocate memory for string buffer
		byte[] data = new byte[this.count];

		// read string buffer
		if (this.count <= 4)
		{
			// unpack integer
			int value = this.value;
			for (int i = 0; i < this.count; i++)
			{
				data[i] = (byte) (value & 0x00FF);
				value = value >> 8;
			}
		}
		else
		{
			// convert state to long offset for reading large buffer
			long offset = ((long) this.value) & 0xffffffffL;

			long pos0 = dataReader.getFilePointer();
			dataReader.seek(offset);
			dataReader.read(data);
			dataReader.seek(pos0);
		}

		return new String(data);
	}

	protected int[] readArray(BinaryDataReader dataReader) throws IOException
	{
		if (this.count == 1)
		{
			return new int[] { this.value };
		}

		if (this.type == TiffTag.Type.SHORT)
		{
			return readShortArray(dataReader);
		}
		else
		{
			return readIntArray(dataReader);
		}
	}

	protected int[] readShortArray(BinaryDataReader dataReader) throws IOException
	{
		// convert tag value to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		// allocate memory for result
		int[] res = new int[this.count];

		// save pointer location
		long saveLoc = dataReader.getFilePointer();

		// fill up array
		dataReader.seek(offset);
		for (int c = 0; c < this.count; c++)
		{
			res[c] = dataReader.readShort();
		}
		
		// restore pointer and return result
		dataReader.seek(saveLoc);
		return res;
	}

	protected int[] readIntArray(BinaryDataReader dataReader) throws IOException
	{
		// convert tag value to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		// allocate memory for result
		int[] res = new int[this.count];

		// save pointer location
		long saveLoc = dataReader.getFilePointer();

		// fill up array
		dataReader.seek(offset);
		for (int c = 0; c < this.count; c++)
		{
			res[c] = dataReader.readInt();
		}
		
		// restore pointer and return result
		dataReader.seek(saveLoc);
		return res;
	}

	/**
	 * Read the short state stored at the specified position
	 */
	protected int readShort(BinaryDataReader dataReader) throws IOException
	{
		// convert tag value to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		long pos0 = dataReader.getFilePointer();
		dataReader.seek(offset);
		int result = dataReader.readShort();
		dataReader.seek(pos0);
		return result;
	}

	/**
	 * Reads the rationale at the given position, as the ratio of two integers.
	 */
	protected double readRational(BinaryDataReader dataReader) throws IOException
	{
		// convert tag value to long offset for reading large buffer
		long offset = ((long) this.value) & 0xffffffffL;

		long saveLoc = dataReader.getFilePointer();
		dataReader.seek(offset);
		
		int numerator = dataReader.readInt();
		int denominator = dataReader.readInt();
		dataReader.seek(saveLoc);

		if (denominator != 0)
			return (double) numerator / denominator;
		else
			return 0.0;
	}

}
