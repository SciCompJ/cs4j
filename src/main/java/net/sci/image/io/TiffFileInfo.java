/**
 * 
 */
package net.sci.image.io;

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

    public enum PixelType 
	{
        // ---------------------------------------------------------
        // Constant list

        /** 8-bit unsigned integer (0-255). */
        GRAY8(1, 1, false),
        
        /**
         * 16-bit signed integer (-32768-32767). Imported signed images are
         * converted to unsigned by adding 32768.
         */
        GRAY16_SIGNED(2, 1, false),
        
		/** 16-bit unsigned integer (0-65535). */
		GRAY16_UNSIGNED(2, 1, false),
		/**
		 * 32-bit signed integer. Imported 32-bit integer images are converted
		 * to floating-point.
		 */
		GRAY32_INT(4, 1, false),

		/** 32-bit floating-point. */
		GRAY32_FLOAT(4, 1, false),
		
		/** 8-bit unsigned integer with color lookup table. */
		COLOR8(1, 1, true),
		
		/** 24-bit interleaved RGB. Import/export only. */
		RGB(3, 3, true),
		
		/** 24-bit planer RGB. Import only. */
		RGB_PLANAR(3, 3, true),
		
		/** 1-bit black and white. Import only. */
		BITMAP(1, 1, false), 
		
		/** 32-bit interleaved ARGB. Import only. */
		ARGB(4, 4, true), 
		
		/** 24-bit interleaved BGR. Import only. */
		BGR(3, 3, true),
		/**
		 * 32-bit unsigned integer. Imported 32-bit integer images are converted
		 * to floating-point.
		 */
		GRAY32_UNSIGNED(4, 1, false),
		
		/** 48-bit interleaved RGB. */
		RGB48(6, 3, true),
		
		/** 12-bit unsigned integer (0-4095). Import only. */
		GRAY12_UNSIGNED(2, 1, false), 
		
		/** 24-bit unsigned integer. Import only. */
		GRAY24_UNSIGNED(3, 1, false), 
		
		/** 32-bit interleaved BARG (MCID). Import only. */
		BARG(4, 4, true), 
		
		/** 64-bit floating-point. Import only.*/
		GRAY64_FLOAT(8, 1, false), 
		
		/** 48-bit planar RGB. Import only. */
		RGB48_PLANAR(6, 3, true), 
		
		/** 32-bit interleaved ABGR. Import only. */
		ABGR(4, 4, true);
		
        
        // ---------------------------------------------------------
        // enumeration variables
        
        private int byteNumber;
        
        private int sampleNumber;
        
        private boolean color;
        
        
        // ---------------------------------------------------------
        // Constructors

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
	
	// Compression modes
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
	
	// File formats
	public enum FileFormat
	{
		UNKNOWN, 
		RAW, 
		TIFF, 
		GIF_OR_JPG,
		FITS,
		BMP, 
		DICOM, 
		ZIP_ARCHIVE,
		PGM,
		IMAGIO;
	};

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
	
	public int nImages = 1;

	public SubFileType subFileType;
	
	/**
	 * Spatial calibration info 
	 */
	public double pixelWidth = 1;
	public double pixelHeight = 1;
	public String unit = "";
		
	Compression compression = Compression.NONE;
	
	Orientation orientation = Orientation.TOPLEFT;
	
	String imageDescription;
	
	/** Info for reading image buffer */
	int[] stripOffsets;
	int[] stripLengths;
	int rowsPerStrip;
	
	PixelType pixelType;
	
	boolean whiteIsZero; 
	int photometricInterpretation;
	int[][] lut = null;
	
	/**
	 * A list of TiffTag for the additional tags that may be provided in files.
	 */
    Map<Integer,TiffTag> tags = new TreeMap<Integer,TiffTag>();
	
	
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

		if (imageDescription != null) {
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
}
