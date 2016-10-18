/**
 * 
 */
package net.sci.image.io;

import java.util.ArrayList;
import java.util.Collection;

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
		
//		tags.add(new TiffTag(256, "ImageWidth"));
//		tags.add(new TiffTag(257, "ImageLength"));
//		tags.add(new TiffTag(258, "BitsPerSample"));
		
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
	
	/**
	 * The type of state stored by this tag.
	 */
	public int type;
	
	/**
	 * The number of data stored by this tag.
	 */
	public int count;
	
	/**
	 * The data contained by this tag, that have to be interpreted depending on the type.
	 */
	public Object value;
	
	
//	public String description = null;
	
	// =============================================================
	// Constructor

	public TiffTag(int code, String name)
	{
		this.code = code;
		this.name = name;
	}
	
	// =============================================================
	// specific methods

//	/**
//	 * Updates the specified FileInfo data structure according to the current value of the tag.
//	 * @param info an instance of TiffFileInfo.
//	 */
//	public void process(TiffFileInfo info)
//	{
//	}
}
