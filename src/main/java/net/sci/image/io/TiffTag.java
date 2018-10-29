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
		addAllTags(map, TiffTag.getBaseLineTags());
		
		// Extension tags: less common formats
        addAllTags(map, TiffTag.getExtensionTags());
		
		// TIFF/IT specification 
        addAllTags(map, TiffTag.getTiffITTags());
		
		// ImageJ Tags 
        addAllTags(map, TiffTag.getImageJTags());
		
        // LSM tags
        addAllTags(map, TiffTag.getLSMTags());

        // Some other tag collections may be added in the future.
		
		return map;
	}
	
	private static final void addAllTags(Map<Integer, TiffTag> map, Collection<TiffTag> tagSet)
	{
	    for (TiffTag tag : tagSet)
        {
            map.put(tag.code, tag);
        }
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

		tags.add(new TiffTag(254, "NewSubfileType", "A general indication of the kind of data contained in this subfile")
		{
			public void process(TiffFileInfo info)
			{
				info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
			}
		});

        tags.add(new TiffTag(255, "SubFileType", "(deprecated) A general indication of the kind of data contained in this subfile")
        {
            public void process(TiffFileInfo info)
            {
                System.out.println("Warning: TiffTag with code 255 (SubFileType) is deprecated.");
                info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
            }
        });
		
		tags.add(new TiffTag(256, "ImageWidth", "The number of columns in the image")
		{
			public void process(TiffFileInfo info)
			{
				info.width = value;
			}
		});

		tags.add(new TiffTag(257, "ImageHeight", "The number of rows of pixels in the image")
		{
			public void process(TiffFileInfo info)
			{
				info.height = value;
			}
		});

		tags.add(new TiffTag(258, "BitsPerSample", "Number of bits per component")
		{
			public void init(BinaryDataReader dataReader) throws IOException
			{
				if (count == 1)
				{
					// Scalar type images (grayscale)
					if (value == 8)
						this.content = PixelType.GRAY8;
					else if (value == 16)
					    this.content = PixelType.GRAY16_UNSIGNED;
					else if (value == 32)
					    this.content = PixelType.GRAY32_FLOAT;
					else if (value == 12)
					    this.content = PixelType.GRAY12_UNSIGNED;
					else if (value == 1)
					    this.content = PixelType.BITMAP;
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
					    this.content = PixelType.RGB;
					} else if (bitDepth == 16)
					{
					    this.content = PixelType.RGB48;
					} else
					{
						throw new IOException(
								"Can only open 8 and 16 bit/channel RGB images ("
										+ bitDepth + ")");
					}
				}
			}
			
			public void process(TiffFileInfo info)
			{
			    info.pixelType = (PixelType) this.content;
			}
		});

		tags.add(new TiffTag(259, "CompressionMode", "Compression scheme used on the image data")
		{
			public void process(TiffFileInfo info)
			{
				info.compression = Compression.fromValue(value);
			}
		});

		// No ref for tags 260-261
		
		tags.add(new TiffTag(262, "PhotometricInterpretation", "The color space of the image data")
		{
			public void process(TiffFileInfo info)
			{
				info.photometricInterpretation = value;
				info.whiteIsZero = value == 0;
			}
		});

		tags.add(new TiffTag(263, "Thresholding", 
		        "For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels")
		{
			public void process(TiffFileInfo info)
			{
			}
		});

		// Tags 264-265 refer to half-toning
		
        tags.add(new TiffTag(266, "FillOrder", "The logical order of bits within a byte")
        {
            public void process(TiffFileInfo info)
            {
                if (value == 2)
                {
                    System.err.println("Warning: Can not manage Tiff Files with FillOrder=2");
                }
            }
        });

		tags.add(new TiffTag(270, "ImageDescription", "A string that describes the subject of the image")
		{
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readAscii(dataReader);
            }
            
			public void process(TiffFileInfo info)
			{
				info.imageDescription = (String) this.content;
			}
		});

        tags.add(new TiffTag(271, "Make", "The scanner manufacturer"));

        tags.add(new TiffTag(272, "Model", "The scanner model name or number"));
		
		
		tags.add(new TiffTag(273, "StripOffsets", "For each strip, the byte offset of that strip")
		{
			public void init(BinaryDataReader dataReader) throws IOException
			{
				this.content = readArray(dataReader);
			}
            public void process(TiffFileInfo info)
            {
                info.stripOffsets = (int[]) this.content;
            }
		});

		tags.add(new TiffTag(274, "Orientation", "The orientation of the image with respect to the rows and columns")
		{
			public void process(TiffFileInfo info)
			{
				info.orientation = TiffFileInfo.Orientation.fromValue(value);
			}
		});

		tags.add(new TiffTag(277, "SamplesPerPixel", "The number of components per pixel")
		{
			public void process(TiffFileInfo info)
			{
			    // Eventually update pixel type value
				if (value == 3 && info.pixelType != PixelType.RGB48)
				{
					if (info.pixelType == PixelType.GRAY16_UNSIGNED) 
					    info.pixelType = PixelType.RGB48;
					else
					    info.pixelType = PixelType.RGB;
				}
				else if (value == 4 && info.pixelType == PixelType.GRAY8)
				{
					info.pixelType = PixelType.ARGB;
				}
			}
		});

		tags.add(new TiffTag(278, "RowsPerStrip", "The number of rows per strip")
		{
			public void process(TiffFileInfo info)
			{
				info.rowsPerStrip = value;
			}
		});
		
		tags.add(new TiffTag(279, "StripByteCount", "For each strip, the number of bytes in the strip after compression")
		{
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readArray(dataReader);
            }
            public void process(TiffFileInfo info)
            {
                info.stripLengths = (int[]) this.content;
            }
		});
		
		tags.add(new TiffTag(280, "MinSampleValue", "The minimum component value used")
		{
		});
		
		tags.add(new TiffTag(281, "MaxSampleValue", "The maximum component value used")
		{
		});
		
		tags.add(new TiffTag(282, "XResolution", "The number of pixels per ResolutionUnit in the ImageWidth direction")
		{
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readRational(dataReader);
            }
			public void process(TiffFileInfo info)
			{
				double xScale = (double) this.content;
				if (xScale != 0.0)
					info.pixelWidth = 1.0 / xScale;
			}
		});
		
		tags.add(new TiffTag(283, "YResolution", "The number of pixels per ResolutionUnit in the ImageHeight direction")
		{
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readRational(dataReader);
            }
			public void process(TiffFileInfo info)
			{
				double yScale = (double) this.content;
				if (yScale != 0.0)
					info.pixelHeight = 1.0 / yScale;
			}
		});
		
		tags.add(new TiffTag(284, "PlanarConfiguration", "How the components of each pixel are stored")
		{
			public void process(TiffFileInfo info)
			{
			    if (value == 2 && info.pixelType == PixelType.RGB48)
					info.pixelType = PixelType.GRAY16_UNSIGNED;
				else if (value == 2 && info.pixelType == PixelType.RGB)
					info.pixelType = PixelType.RGB_PLANAR;
				else if (value == 1 && info.pixelType.getSampleNumber() == 4)
					info.pixelType = PixelType.ARGB;
				else if (value != 2
						&& !((info.pixelType.getSampleNumber() == 1) || (info.pixelType.getSampleNumber() == 3)))
				{
					String msg = "Unsupported SamplesPerPixel: " + info.pixelType.getSampleNumber();
					throw new RuntimeException(msg);
				}
			}
		});
		
        tags.add(new TiffTag(288, "FreeOffsets",
                "For each string of contiguous unused bytes in a TIFF file, the byte offset of the string"));
        
        tags.add(new TiffTag(289, "FreeByteCounts",
                "For each string of contiguous unused bytes in a TIFF file, the number of bytes in the string"));
        
        tags.add(new TiffTag(290, "GrayResponseUnit",
                "The precision of the information contained in the GrayResponseCurve"));
        
        /**
         * The 0th value of GrayResponseCurve corresponds to the optical density
         * of a pixel having a value of 0, and so on.
         * 
         * This field may provide useful information for sophisticated
         * applications, but it is currently ignored by most TIFF readers.
         */
        tags.add(new TiffTag(291, "GrayResponseCurve",
                "For grayscale data, the optical density of each possible pixel value")
        {
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readShortArray(dataReader);
            }
           
        });
        
        
		
		tags.add(new TiffTag(296, "ResolutionUnit", "The unit of measurement for XResolution and YResolution")
		{
			public void process(TiffFileInfo info)
			{
			    if (value == 1)
				{
					info.unit = "";
				}
				else if (value == 2)
				{
				    info.unit = "inch";
				}
				else if (value == 3)
				{
					info.unit = "cm";
				}
				else
				{
				    throw new RuntimeException("Illegal value for TiffTag 'ResolutionUnit': " + value);
				}
			}
		});
		
		tags.add(new TiffTag(305, "Software",
				"Name and version number of the software package(s) used to create the image"));
        
		/**
         * The format is: "YYYY:MM:DD HH:MM:SS", with hours like those on a
         * 24-hour clock, and one space character between the date and the time.
         * The length of the string, including the terminating NUL, is 20 bytes.
         */
		tags.add(new TiffTag(306, "DateTime",
                "Date and time of image creation"));

        tags.add(new TiffTag(315, "Artist",
                "Person who created the image"));
        
		tags.add(new TiffTag(316, "HostComputer",
                "The computer and/or operating system in use at the time of image creation"));


		tags.add(new TiffTag(320, "ColorMap", "A color map for palette color images")
		{
			public void init(BinaryDataReader dataReader) throws IOException
			{
				this.content = readColorMap(dataReader, count / 3);
			}
            public void process(TiffFileInfo info)
            {
                info.lut = (int[][]) this.content;
            }
		});
		
		tags.add(new TiffTag(338, "ExtraSamples",
				"Description of extra components"));

		tags.add(new TiffTag(33432, "Copyright",
                "Copyright notice"));

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

        tags.add(new TiffTag(269, "DocumentName", "The name of the document from which this image was scanned"));

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

	/**
     * Management of LSM Tags.
     *
     * References:
     * <ul>
     * <li> <a href="https://fr.mathworks.com/matlabcentral/fileexchange/8412-lsm-file-toolbox"> LSM File Toolbox</a> by Peter Li </li>
     * <li> <a href="https://fr.mathworks.com/matlabcentral/fileexchange/46892-zeiss-laser-scanning-confocal-microscope-lsm-file-reader"> LSM File Reader</a> by Chao-Yuan Yeh</li>
     * </ul>
     *  
     * @return a list of extension tags
     */
    public static final Collection<TiffTag> getLSMTags()
    {
        ArrayList<TiffTag> tags = new ArrayList<>();

        tags.add(new TiffTag(34412, "LSMInfo")
        {
            public void init(BinaryDataReader dataReader) throws IOException
            {
                Map<String, Object> map = new TreeMap<>();
                
                // keep reader pointer
                long pos0 = dataReader.getFilePointer();

                // convert tag value to long offset for reading large buffer
                long offset = ((long) this.value) & 0xffffffffL;
                dataReader.seek(offset+8);

                map.put("dimX", dataReader.readInt());
                map.put("dimY", dataReader.readInt());
                map.put("dimZ", dataReader.readInt());
                map.put("dimC", dataReader.readInt());
                map.put("dimT", dataReader.readInt());
                dataReader.seek(dataReader.getFilePointer() + 12);
                map.put("voxelSizeX", dataReader.readDouble());
                map.put("voxelSizeY", dataReader.readDouble());
                map.put("voxelSizeZ", dataReader.readDouble());
                map.put("specScan", dataReader.readShort() & 0x00FFFF);
                
                // revert reader to initial position
                dataReader.seek(pos0);
                
                this.content = map;
            }           
        });

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
     * The data contained by this tag, that have to be interpreted depending on
     * the type.
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
     * @param dataReader
     *            the instance of DataReader to read optional information from
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
     * Initializes the content field of this tag, based on value and eventually data reader.
     * 
     * @param dataReader
     *            the instance of DataReader used to read information
     * @throws IOException
     *             if an error occurs
     */
	public void init(BinaryDataReader dataReader) throws IOException
	{
	}
	
    /**
     * Updates the specified FileInfo data structure according to the current
     * value of the tag.
     * 
     * @param info
     *            an instance of TiffFileInfo.
     */
    public void process(TiffFileInfo info)
    {
    }
    
	
	// =============================================================
	// protected methods used by subclasses
	
	protected int[][] readColorMap(BinaryDataReader dataReader, int lutLength)
			throws IOException
	{
		// Allocate memory for raw array
	    // (each triplet of components is stored in two bytes)
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
			lut[i][0] = lut16[j] & 0x00FF;
			lut[i][1] = lut16[j + 512] & 0x00FF;
			lut[i][2] = lut16[j + 1024] & 0x00FF;
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
     * 
     * @param dataReader
     *            the instance of BinaryDataReader to read from
     * @return the short content at the specified position, as an integer
     * @throws IOException
     *             if an I/O Exception occurs
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
     * Reads the rational value at the given position, as the ratio of two
     * integers.
     * 
     * @param dataReader
     *            the instance of BinaryDataReader to read from
     * @return the approximated rational content at the specified position, as a
     *         double
     * @throws IOException
     *             if an I/O Exception occurs
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
