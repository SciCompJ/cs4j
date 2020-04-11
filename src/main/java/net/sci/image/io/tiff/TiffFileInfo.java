/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

/**
 * Stores all information that are necessary to load an image from a TIFF file. 
 */
public class TiffFileInfo 
{
    // =============================================================
    // Public enumerations
    
    /**
     * The type of pixel within the file. Each type contains information about
     * number of bytes and number of channels of the type.
     */
    public enum PixelType 
	{
        // ---------------------------------------------------------
        // Constant list

        /** 8-bit unsigned integer (0-255). */
        GRAY8(1, 1, false),
        
        /** 16-bit signed integer (-32768-32767). */
        GRAY16_SIGNED(2, 1, false),
        
		/** 16-bit unsigned integer (0-65535). */
		GRAY16_UNSIGNED(2, 1, false),
		
		/** 32-bit signed integer. */
		GRAY32_INT(4, 1, false),

		/** 32-bit floating-point. */
		GRAY32_FLOAT(4, 1, false),
		
		/** 8-bit unsigned integer with color lookup table. */
		COLOR8(1, 1, true),
		
		/** 24-bit interleaved RGB. */
		RGB(3, 3, true),
		
		/** 24-bit planer RGB. */
		RGB_PLANAR(3, 3, true),
		
		/** 1-bit black and white. */
		BITMAP(1, 1, false), 
		
		/** 32-bit interleaved ARGB. */
		ARGB(4, 4, true), 
		
		/** 24-bit interleaved BGR. */
		BGR(3, 3, true),
		
		/** 32-bit unsigned integer. */
		GRAY32_UNSIGNED(4, 1, false),
		
		/** 48-bit interleaved RGB. */
		RGB48(6, 3, true),
		
		/** 12-bit unsigned integer (0-4095). */
		GRAY12_UNSIGNED(2, 1, false), 
		
		/** 24-bit unsigned integer. */
		GRAY24_UNSIGNED(3, 1, false), 
		
		/** 32-bit interleaved BARG (MCID). */
		BARG(4, 4, true), 
		
		/** 64-bit floating-point. */
		GRAY64_FLOAT(8, 1, false), 
		
		/** 48-bit planar RGB. */
		RGB48_PLANAR(6, 3, true), 
		
		/** 32-bit interleaved ABGR. */
		ABGR(4, 4, true);
		
        
        // ---------------------------------------------------------
        // enumeration variables
        
        /** The total number of bytes used to represent this type.*/
        private int byteNumber;
        
        /** The number of samples this type contains.*/
        private int sampleNumber;
        
        /** A logical flag indicating whether this type represents a color. */
        private boolean color;
        
        
        // ---------------------------------------------------------
        // Constructors
        
        /**
         * Creates a new type, by specifying information on how to retrieve
         * pixel info from type.
         * 
         * @param byteNumber
         *            the total number of bytes used to represent this type.
         * @param sampleNumber
         *            the number of samples this type contains.
         * @param color
         *            true is represents a color type.
         */
        private PixelType(int byteNumber, int sampleNumber, boolean color)
        {
            this.byteNumber = byteNumber;
            this.sampleNumber = sampleNumber;
            this.color = color;
        }

        // ---------------------------------------------------------
        // Methods
        
        public int getByteNumber() 
        {
            return this.byteNumber;
        }

        public int getSampleNumber() 
        {
            return this.sampleNumber;
        }

        public boolean isColor()
        {
            return this.color;
        }
	};
	
    /**
     * The different compression modes used in TIFF files. Note that all
     * compression decoders are not necessarily implemented.
     */
	public enum Compression 
	{
		UNKNOWN,
		NONE,
		CCITT_RLE,
		CCITT_GROUP3,
		CCITT_GROUP4,
		LZW, 
		JPEG, 
		JPEG_OLD, 
		PACK_BITS, 
		ZIP;
		
		public static Compression fromValue(int value) 
		{
			switch (value) 
			{
			// First test official values
			case 1: 	return Compression.NONE;
			case 2: 	return Compression.CCITT_RLE;
			case 32773: return Compression.PACK_BITS;
			case 8:
			case 32946: return Compression.ZIP;
			
			// Test also less common values
			case 3: 	return Compression.CCITT_GROUP3;
			case 4: 	return Compression.CCITT_GROUP4;
			case 5: 	return Compression.LZW;
			case 6: 	return Compression.JPEG_OLD;
			case 7: 	return Compression.JPEG;
			default:
				throw new IllegalArgumentException(
						"No Compression type defined for state: " + value);
			}
		}
	};
	
	/**
	 * The type of subfile, used to identify the type of image.
	 */
	public enum SubFileType 
	{
		IMAGE,
		REDUCEDIMAGE,
		PAGE,
		MASK; 
			
		public static SubFileType fromValue(int value)
		{
			switch (value) 
			{
			case 0: return SubFileType.IMAGE;
			case 1: return SubFileType.REDUCEDIMAGE;
			case 2: return SubFileType.PAGE;
			case 4: return SubFileType.MASK;
			default:
				throw new IllegalArgumentException(
						"Not SubFileType defined for state " + value);
			}
		}
	}
	
    /**
     * The orientation of the image.
     */
	public enum Orientation
	{
		TOPLEFT,
		TOPRIGHT,
		BOTRIGHT,
		BOTLEFT,
		LEFTTOP,
		RIGHTTOP,
		RIGHTBOT,
		LEFTBOT; 
		
		public static Orientation fromValue(int value)
		{
			switch (value) {
			case 1: return Orientation.TOPLEFT;
			case 2: return Orientation.TOPRIGHT;
			case 3: return Orientation.BOTRIGHT;
			case 4: return Orientation.BOTLEFT;
			case 5: return Orientation.LEFTTOP;
			case 6: return Orientation.RIGHTTOP;
			case 7: return Orientation.RIGHTBOT;
			case 8: return Orientation.LEFTBOT;
			default:
				throw new IllegalArgumentException("No orientation defined for state: " + value);
			}
		}
	};
	
	
    // =============================================================
    // Class variables
	
	/**
	 * Size of the image
	 */
	public int width; 
	public int height; 
	
	public SubFileType subFileType;
	
	/**
	 * Spatial calibration info 
	 */
	public double pixelWidth = 1;
	public double pixelHeight = 1;
	public String unit = "";
		
	public Compression compression = Compression.NONE;
	
	public Orientation orientation = Orientation.TOPLEFT;
	
	public String imageDescription;
	
	/** Info for reading image buffer */
	public int[] stripOffsets;
	public int[] stripLengths;
	public int rowsPerStrip;
	
	public PixelType pixelType;
	
	public boolean whiteIsZero; 
	public int photometricInterpretation;
	public int[][] lut = null;
	
	/**
	 * A list of TiffTag for the additional tags that may be provided in files.
	 */
	public Map<Integer,TiffTag> tags = new TreeMap<Integer,TiffTag>();
	
	
    // =============================================================
    // Methods

	/**
	 * Display the content of the image file directory to the console.  
	 */
	public void print() 
	{
		print(System.out);
	}

	/**
     * Display the content of the image file directory to the specified print
     * stream.
     * 
     * @param out
     *            the the stream used to display information
     */
	public void print(PrintStream out)
	{
		out.println("--- Tiff File Info Description ---");
		out.println("file type: " + pixelType);
		out.println("size0: " + width);
		out.println("size1: " + height);
//		out.println("intel byte order: " + intelByteOrder);
		out.println("compression: " + compression);
//		out.println("offset: " + offset);

		out.println("samples per pixel: " + pixelType.sampleNumber);
		out.println("bytes per pixel: " + pixelType.byteNumber);
		out.println("Photometric intrepretation: " + photometricInterpretation);

		if (imageDescription != null) 
		{
			out.println("image description: " + imageDescription);
		}
		
		out.print("strip offsets:");
		for (int i = 0; i < stripOffsets.length; i++)
			out.print(" " + stripOffsets[i]);
		out.println();

		out.print("strip lengths:");
		for (int i = 0; i < stripLengths.length; i++)
			out.print(" " + stripLengths[i]);
		out.println();
		
		out.println("rowsPerStrip: " + rowsPerStrip);
	}
	
	public boolean hasSameTags(TiffFileInfo that)
	{
	    for (int key : tags.keySet())
	    {
	        if (!that.tags.containsKey(key))
	        {
	            return false;
	        }
	        
	        // do not compare the tags corresponding to the way the data are stored in the file
            if (key == BaselineTags.STRIP_OFFSETS || key == BaselineTags.ROWS_PER_STRIP
                    || key == BaselineTags.STRIP_BYTE_COUNTS || key == BaselineTags.FREE_OFFSETS
                    || key == BaselineTags.FREE_BYTE_COUNTS)
	        {
	            continue;
	        }
	        
	        TiffTag tag = tags.get(key);
            TiffTag tag2 = that.tags.get(key);
            if (!tag.equals(tag2))
            {
                return false;
            }
	    }
	    return true;
	}
}
