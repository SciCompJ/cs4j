/**
 * 
 */
package net.sci.image.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

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
		NONE,
		BYTE,
		ASCII,
		SHORT,
		LONG,
		RATIONAL;
		
		public static final Type getType(int typeCode)
		{
			switch (typeCode)
			{
			case 0: return NONE;
			case 1: return BYTE;
			case 2: return ASCII;
			case 3: return SHORT;
			case 4: return LONG;
			case 5: return RATIONAL;
			}
			throw new IllegalArgumentException("Incorrect value for tag type: " + typeCode);
		}
	};

	// =============================================================
	// static methods

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
			public void process(TiffFileInfo info)
			{
				info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
			}
		});

		tags.add(new TiffTag(256, "ImageWidth")
		{
			public void process(TiffFileInfo info)
			{
				info.width = value;
			}
		});

		tags.add(new TiffTag(257, "ImageHeight")
		{
			public void process(TiffFileInfo info)
			{
				info.height = value;
			}
		});

//		tags.add(new TiffTag(258, "BitsPerSample")
//		{
//			public void process(TiffFileInfo info)
//			{
//				if (count == 1)
//				{
//					// Scalar type images (grayscale)
//					if (value == 8)
//						info.fileType = PixelType.GRAY8;
//					else if (value == 16)
//						info.fileType = PixelType.GRAY16_UNSIGNED;
//					else if (value == 32)
//						info.fileType = PixelType.GRAY32_FLOAT;
//					else if (value == 12)
//						info.fileType = PixelType.GRAY12_UNSIGNED;
//					else if (value == 1)
//						info.fileType = PixelType.BITMAP;
//					else
//						throw new IOException(
//								"Unsupported BitsPerSample: " + value);
//				} 
//				else if (count == 3)
//				{
//					// Case of color images stored as 3 bands
//					int bitDepth = readShort(offset);
//
//					if (bitDepth == 8)
//					{
//						info.fileType = PixelType.RGB;
//					} else if (bitDepth == 16)
//					{
//						info.fileType = PixelType.RGB48;
//					} else
//					{
//						throw new IOException(
//								"Can only open 8 and 16 bit/channel RGB images ("
//										+ bitDepth + ")");
//					}
//				}
//			}
//		});

//		case 258:
//			// Bits Per Sample
//			if (count == 1)
//			{
//				// Scalar type images (grayscale)
//				if (value == 8)
//					info.fileType = PixelType.GRAY8;
//				else if (value == 16)
//					info.fileType = PixelType.GRAY16_UNSIGNED;
//				else if (value == 32)
//					info.fileType = PixelType.GRAY32_FLOAT;
//				else if (value == 12)
//					info.fileType = PixelType.GRAY12_UNSIGNED;
//				else if (value == 1)
//					info.fileType = PixelType.BITMAP;
//				else
//					throw new IOException("Unsupported BitsPerSample: "
//							+ value);
//			}
//			else if (count == 3)
//			{
//				// Case of color images stored as 3 bands
//				int bitDepth = readShort(offset);
//	
//				if (bitDepth == 8)
//				{
//					info.fileType = PixelType.RGB;
//				}
//				else if (bitDepth == 16)
//				{
//					info.fileType = PixelType.RGB48;
//				}
//				else
//				{
//					throw new IOException(
//							"Can only open 8 and 16 bit/channel RGB images ("
//									+ bitDepth + ")");
//				}
//			}
//			break;

		tags.add(new TiffTag(259, "CompressionMode")
		{
			public void process(TiffFileInfo info)
			{
				info.compression = Compression.fromValue(value);
			}
		});

		tags.add(new TiffTag(262, "PhotometricInterpretation")
		{
			public void process(TiffFileInfo info)
			{
				info.photometricInterpretation = value;
				info.whiteIsZero = value == 0;
			}
		});

		tags.add(new TiffTag(270, "ImageDescription")
		{
			public void process(TiffFileInfo info)
			{
				info.compression = Compression.fromValue(value);
			}
		});

//		case 270: // Image description
//			info.imageDescription = readAscii(count, value);
//			break;
//	
//		case 273: // Strip Offsets
//			info.stripOffsets = readArray(type, count, value);
//			break;
//	
		tags.add(new TiffTag(274, "Orientation")
		{
			public void process(TiffFileInfo info)
			{
				info.orientation = TiffFileInfo.Orientation.fromValue(value);
			}
		});

		tags.add(new TiffTag(277, "SamplesPerPixel")
		{
			public void process(TiffFileInfo info)
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
			public void process(TiffFileInfo info)
			{
				info.rowsPerStrip = value;
			}
		});
		
//		case 279: // Strip Byte Counts
//			info.stripLengths = readArray(type, count, value);
//			break;
//	
//		case 282: // X Resolution
//			double xScale = readRational(offset);
//			if (xScale != 0.0)
//				info.pixelWidth = 1.0 / xScale;
//			break;
//	
//		case 283: // Y Resolution
//			double yScale = readRational(offset);
//			if (yScale != 0.0)
//				info.pixelHeight = 1.0 / yScale;
//			break;
//	
//		case 284: // Planar Configuration. 1=chunky, 2=planar
//			if (value == 2 && info.fileType == PixelType.RGB48)
//				info.fileType = PixelType.GRAY16_UNSIGNED;
//			else if (value == 2 && info.fileType == PixelType.RGB)
//				info.fileType = PixelType.RGB_PLANAR;
//			else if (value == 1 && info.samplesPerPixel == 4)
//				info.fileType = PixelType.ARGB;
//			else if (value != 2
//					&& !((info.samplesPerPixel == 1) || (info.samplesPerPixel == 3)))
//			{
//				String msg = "Unsupported SamplesPerPixel: "
//						+ info.samplesPerPixel;
//				throw new IOException(msg);
//			}
//			break;
//	
//		case 296: // Resolution unit
//			if (value == 1 && info.unit == null)
//				info.unit = " ";
//			else if (value == 2)
//			{
//				if (info.pixelWidth == 1.0 / 72.0)
//				{
//					info.pixelWidth = 1.0;
//					info.pixelHeight = 1.0;
//				}
//				else
//					info.unit = "inch";
//			}
//			else if (value == 3)
//				info.unit = "cm";
//			break;
//	
//		case 320: // color palette (LUT)
//			int lutLength = (int) Math.pow(2, 8 * info.bytesPerPixel);
//			int expLength = 3 * lutLength;
//			if (count != expLength)
//			{
//				throw new RuntimeException("Tiff Color Palette has "
//						+ count + " elements, while it requires "
//						+ expLength);
//			}
//			
//			info.lut = readColorMap(lutLength, count, offset);
//			break;
		
		
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
		
		tags.add(new TiffTag(339, "SampleFormat"));
//		tags.add(new TiffTag(339, "SampleFormat")
//		{
//			public void process(TiffFileInfo info)
//			{
//				System.out.println("process sample format");
//				info.bytesPerPixel = 4;
//			}
//		});
//		tags.add(new TiffTag(257, "ImageLength"));
//		tags.add(new TiffTag(258, "BitsPerSample"));
		
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
	// specific methods

	/**
	 * Updates the specified FileInfo data structure according to the current value of the tag.
	 * @param info an instance of TiffFileInfo.
	 */
	public void process(TiffFileInfo info)
	{
	}
}
