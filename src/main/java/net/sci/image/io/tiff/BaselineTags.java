/**
 * 
 */
package net.sci.image.io.tiff;

import java.util.HashMap;
import java.util.Map;

/**
 * The list of baseline TIFF Tags.
 * 
 * @see <a href="https://www.awaresystems.be/imaging/tiff/tifftags/baseline.html">https://www.awaresystems.be/imaging/tiff/tifftags/baseline.html</a>
 *
 * @author dlegland
 *
 */
public class BaselineTags implements TagSet
{
    /**
     * 254 - A general indication of the kind of data contained in this subfile.
     */
    public static final class NewSubfileType extends TiffTag
    {
        public static final int CODE = 254;
        public NewSubfileType()
        {
            super(CODE, "NewSubfileType", "A general indication of the kind of data contained in this subfile");
            this.type = Type.LONG;
            this.count = 1;
            this.value = 0;
        }
    }

    
    /**
     * 255 - (deprecated) A general indication of the kind of data contained in this subfile.
     */
    public static final class SubfileType extends TiffTag
    {
        public static final int CODE = 255;
        public SubfileType()
        {
            super(CODE, "SubfileType", "(deprecated) A general indication of the kind of data contained in this subfile");
        }
    }
    
    /**
     * 256 - The number of columns in the image, i.e., the number of pixels per row.
     */
    public static final class ImageWidth extends TiffTag
    {
        public static final int CODE = 256;
        public ImageWidth()
        {
            super(CODE, "ImageWidth", "The number of columns in the image");
            this.type = Type.LONG;
        }
    }
    
    /**
     * 257 - The number of rows of pixels in the image.
     */
    public static final class ImageHeight extends TiffTag
    {
        public static final int CODE = 257;
        public ImageHeight()
        {
            super(CODE, "ImageHeight", "The number of rows of pixels in the image");
            this.type = Type.LONG;
        }
    }

    /**
     * 258 - Number of bits per component, as an array of short with as many
     * elements as the number of pixel elements. Number of pixel elements
     * corresponds to number of channels.
     * 
     * <ul>
     * <li>8 or 16 for gray-scale images</li>
     * <li>32 for floating point images images</li>
     * <li>8 or 16 for color images</li>
     * <li>can also be 1 (for binary images), or 12 or 14 for gray-scale
     * images</li>
     * </ul>
     * 
     * After initialization, the content of the tag is the PixelType.
     */
    public static final class BitsPerSample extends TiffTag
    {
        public static final int CODE = 258;
        public BitsPerSample()
        {
            super(CODE, "BitsPerSample", "Number of bits per component");
            this.type = Type.SHORT;
        }
        
        /**
         * Initializes value or content based on number of samples (per pixel),
         * and number of bits for each sample.
         * 
         * @param samplesPerPixel
         *            the number of samples per pixel
         * @param bitsPerSingleSample
         *            the number of bits used to represent a single sample
         * @return the reference to this tag
         */
        public TiffTag init(int samplesPerPixel, int bitsPerSingleSample)
        {
            if (samplesPerPixel == 1)
            {
                setShortValue((short) bitsPerSingleSample);
            }
            else
            {
                // convert single value into an array of short
                short[] bps = new short[samplesPerPixel];
                for (int c = 0; c < samplesPerPixel; c++)
                {
                    bps[c] = (short) bitsPerSingleSample;
                }
                setValue(bps);
            }
            
            return this;
        }
    }

    /**
     * 259 - Compression scheme used on the image data.
     */
    public static final class Compression extends TiffTag
    {
        public static final int CODE = 259;
        
        public static final int NONE = 1;
        public static final int CCITT = 2;
        public static final int PACKBITS = 32773;
        
        public Compression()
        {
            super(CODE, "CompressionMode", "Compression scheme used on the image data");
            this.type = Type.SHORT;
            this.count = 1;
            this.value = NONE; // no compression
        }
    }

    /**
     * 262 - The color space of the image data. This tag is required, and no
     * default value is specified.
     */
    public static final class PhotometricInterpretation extends TiffTag
    {
        public static final int CODE = 262;
        
        public static final int WHITE_IS_ZERO = 0;
        public static final int BLACK_IS_ZERO = 1;
        public static final int RGB = 2;
        public static final int PALETTE_COLOR = 3;
        public static final int TRANSPARENCY_MASK = 4;
        
        public PhotometricInterpretation()
        {
            super(CODE, "PhotometricInterpretation", "The color space of the image data");
            this.type = Type.SHORT;
            this.count = BLACK_IS_ZERO;
        }
    }
    
    /**
     * 263 - For black and white TIFF files that represent shades of gray, the
     * technique used to convert from gray to black and white pixels.
     */
    public static final class Threshholding extends TiffTag
    {
        public static final int CODE = 263;
        public Threshholding()
        {
            super(CODE, "Threshholding", 
                    "For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels");
        }
    }

    /**
     * 264 - The width of the dithering or halftoning matrix used to create a
     * dithered or halftoned bilevel file.
     */

    public static final class CellWidth extends TiffTag
    {
        public static final int CODE = 264;
        public CellWidth()
        {
            super(CODE, "CellWidth", 
                    "The width of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file");
        }
    }
    
    /**
     * 265 - The length of the dithering or halftoning matrix used to create a
     * dithered or halftoned bilevel file.
     */
    public static final class CellHeight extends TiffTag
    {
        public static final int CODE = 265;
        public CellHeight()
        {
            super(CODE, "CellHeight", 
                    "The length of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file");
        }
    }
    
    /**
     * 266 - The logical order of bits within a byte.
     */
    public static final class FillOrder extends TiffTag
    {
        public static final int CODE = 266;
        public FillOrder()
        {
            super(CODE, "FillOrder", 
                    "The logical order of bits within a byte");
        }
    }
    
    /**
     * 270 - A string that describes the subject of the image.
     */
    public static final class ImageDescription extends TiffTag
    {
        public static final int CODE = 270;
        
        public ImageDescription()
        {
            super(CODE, "ImageDescription", 
                    "A string that describes the subject of the image");
        }
        
        public int contentSize()
        {
            return ((String) this.content).length();
        }
    }
    
    /**
     * 271 - The scanner manufacturer.
     */
    public static final class Make extends TiffTag
    {
        public static final int CODE = 271;
        public Make()
        {
            super(CODE, "Make", "The scanner manufacturer");
        }
    }
    
    /**
     * 272 -The scanner model name or number.
     */
    public static final class Model extends TiffTag
    {
        public static final int CODE = 272;
        public Model()
        {
            super(CODE, "Model", "The scanner model name or number");
        }
    }
    
    /**
     * 273 - For each strip, the byte offset of that strip. The value is either
     * a single offset, or an array of offset.
     */
    public static final class StripOffsets extends TiffTag
    {
        public static final int CODE = 273;
        
        public StripOffsets()
        {
            super(CODE, "StripOffsets", "For each strip, the byte offset of that strip");
            this.type = Type.LONG;
            this.count = 1;
        }
    }
    
    /**
     * 274 - The orientation of the image with respect to the rows and columns.
     */
    public static final class Orientation extends TiffTag
    {
        public static final int CODE = 274;
        
        public Orientation()
        {
            super(CODE, "Orientation", "The orientation of the image with respect to the rows and columns");
        }
    }
    
    /**
     * 277 - The number of components per pixel. Type=SHORT, count=1.
     */
    public static final class SamplesPerPixel extends TiffTag
    {
        public static final int CODE = 277;
        public SamplesPerPixel()
        {
            super(CODE, "SamplesPerPixel", "The number of components per pixel");
            this.type = Type.SHORT;
            this.count = 1;
        }
    }

    /**
     * 278 - RowsPerStrip, The number of rows per strip. Type = LONG, count=1.
     * Use of a single strip is not recommended. It is recommended to choose
     * RowsPerStrip such that each strip is about 8K bytes.
     */
    public static final class RowsPerStrip extends TiffTag
    {
        public static final int CODE = 278;
        public RowsPerStrip()
        {
            super(CODE, "RowsPerStrip", "The number of rows per strip");
            this.type = Type.LONG;
            this.count = 1;
        }
    }
    
    /**
     * 279 - For each strip, the number of bytes in the strip after compression.
     * Type = LONG, count=1. Corresponds to the product of image elements with
     * byte number per element.
     */
    public static final class StripByteCounts extends TiffTag
    {
        public static final int CODE = 279;
        public StripByteCounts()
        {
            super(CODE, "StripByteCounts", "For each strip, the number of bytes in the strip after compression");
            this.type = Type.LONG;
            this.count = 1;
        }
    }
    
    /**
     * 280 - The minimum component value used.
     */
    public static final class MinSampleValue extends TiffTag
    {
        public static final int CODE = 280;
        public MinSampleValue()
        {
            super(CODE, "MinSampleValue", "The minimum component value used");
        }
    }
    
    /**
     * 281 - The maximum component value used.
     */
    public static final class MaxSampleValue extends TiffTag
    {
        public static final int CODE = 281;
        public MaxSampleValue()
        {
            super(CODE, "MaxSampleValue", "The maximum component value used");
        }
    }
    
    /**
     * 282 - The number of pixels per ResolutionUnit in the ImageWidth direction.
     */
    public static final class XResolution extends TiffTag
    {
        public static final int CODE = 282;
        
        public XResolution()
        {
            super(CODE, "XResolution", "The number of pixels per ResolutionUnit in the ImageWidth direction");
            this.type = Type.RATIONAL;
            this.count = 1;
        }
    }
    
    /**
     * 283 - The number of pixels per ResolutionUnit in the ImageHeight direction.
     */
    public static final class YResolution extends TiffTag
    {
        public static final int CODE = 283;
        
        public YResolution()
        {
            super(CODE, "YResolution", "The number of pixels per ResolutionUnit in the ImageHeight direction");
            this.type = Type.RATIONAL;
            this.count = 1;
        }
    }
    
    /**
     * 284 - How the components of each pixel are stored.
     */
    public static final class PlanarConfiguration extends TiffTag
    {
        public static final int CODE = 284;
        
        public PlanarConfiguration()
        {
            super(CODE, "PlanarConfiguration", "How the components of each pixel are stored");
        }
    }

    /**
     * 288 - For each string of contiguous unused bytes in a TIFF file, the byte offset of the string.
     */
     public static final class FreeOffsets extends TiffTag
    {
        public static final int CODE = 288;
        public FreeOffsets()
        {
            super(CODE, "FreeOffsets", "For each string of contiguous unused bytes in a TIFF file, the byte offset of the string");
        }
    }
    
    /**
     * 289 - For each string of contiguous unused bytes in a TIFF file, the number of bytes in the string.
     */
    public static final class FreeByteCounts extends TiffTag
    {
        public static final int CODE = 289;
        public FreeByteCounts()
        {
            super(CODE, "FreeByteCounts", "For each string of contiguous unused bytes in a TIFF file, the number of bytes in the string");
        }
    }
    
    /**
     * 290 - The precision of the information contained in the GrayResponseCurve.
     */
    public static final class GrayResponseUnit extends TiffTag
    {
        public static final int CODE = 290;
        public GrayResponseUnit()
        {
            super(CODE, "GrayResponseUnit", "The precision of the information contained in the GrayResponseCurve");
        }
    }
    
    /**
     * 291 - For grayscale data, the optical density of each possible pixel value.
     *
     * The 0th value of GrayResponseCurve corresponds to the optical density
     * of a pixel having a value of 0, and so on.
     * 
     * This field may provide useful information for sophisticated
     * applications, but it is currently ignored by most TIFF readers.
     */
    public static final class GrayResponseCurve extends TiffTag
    {
        public static final int CODE = 291;
        public GrayResponseCurve()
        {
            super(CODE, "GrayResponseCurve", "For grayscale data, the optical density of each possible pixel value");
        }
    }
    
    /**
     * 296 - The unit of measurement for XResolution and YResolution.
     */
    public static final class ResolutionUnit extends TiffTag
    {
        public static final int CODE = 296;
        
        public ResolutionUnit()
        {
            super(CODE, "ResolutionUnit", "The unit of measurement for XResolution and YResolution");
            this.type = Type.SHORT;
            this.count = 1;
            this.value = 1; // default: no unit
        }
    }
    
    /**
     * 305 - Name and version number of the software package(s) used to create the image.
     */
    public static final class Software extends TiffTag
    {
        public static final int CODE = 305;
        public Software()
        {
            super(CODE, "Software", "Name and version number of the software package(s) used to create the image");
            this.type = Type.ASCII;
        }
    }
    
    /**
     * 306 - Date and time of image creation.
     * 
     * The format is: "YYYY:MM:DD HH:MM:SS", with hours like those on a
     * 24-hour clock, and one space character between the date and the time.
     * The length of the string, including the terminating NUL, is 20 bytes.
     */
    public static final class DateTime extends TiffTag
    {
        public static final int CODE = 306;
        public DateTime()
        {
            super(CODE, "DateTime", "Date and time of image creation");
            this.type = Type.ASCII;
        }
    }
    
    /**
     * 315 - Person who created the image.
     */
    public static final class Artist extends TiffTag
    {
        public static final int CODE = 315;
        public Artist()
        {
            super(CODE, "Artist", "Person who created the image");
            this.type = Type.ASCII;
        }
    }
    
    /**
     * 316 - The computer and/or operating system in use at the time of image creation.
     */
    public static final class HostComputer extends TiffTag
    {
        public static final int CODE = 316;
        public HostComputer()
        {
            super(CODE, "HostComputer", "The computer and/or operating system in use at the time of image creation");
            this.type = Type.ASCII;
        }
    }
    
    /**
     * 320 - A color map for palette color images.
     */
    public static final class ColorMap extends TiffTag
    {
        public static final int CODE = 320;
        
        public ColorMap()
        {
            super(CODE, "ColorMap", "A color map for palette color images");
        }
    }
    
    /**
     * 338 - Description of extra components.
     */
    public static final class ExtraSamples extends TiffTag
    {
        public static final int CODE = 338;
        public ExtraSamples()
        {
            super(CODE, "ExtraSamples", "Description of extra components");
        }
    }
    
    /**
     * 33432 - Copyright notice.
     */
    public static final class Copyright extends TiffTag
    {
        public static final int CODE = 33432;
        public Copyright()
        {
            super(CODE, "Copyright", "Copyright notice");
        }
    }
    
    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(40);
        
        add(tags, new NewSubfileType());
        add(tags, new SubfileType());
        
        add(tags, new ImageWidth());
        add(tags, new ImageHeight());
        add(tags, new BitsPerSample());
        add(tags, new Compression());

        add(tags, new PhotometricInterpretation());
        add(tags, new Threshholding());
        add(tags, new CellWidth());
        add(tags, new CellHeight());
        add(tags, new FillOrder());

        add(tags, new ImageDescription());
        add(tags, new Make());
        add(tags, new Model());

        add(tags, new StripOffsets());
        add(tags, new Orientation());
        add(tags, new SamplesPerPixel());
        add(tags, new RowsPerStrip());
        add(tags, new StripByteCounts());
        
        add(tags, new MinSampleValue());
        add(tags, new MaxSampleValue());
        add(tags, new XResolution());
        add(tags, new YResolution());

        add(tags, new PlanarConfiguration());
        add(tags, new FreeOffsets());
        add(tags, new FreeByteCounts());
        
        add(tags, new GrayResponseUnit());
        add(tags, new GrayResponseCurve());
        add(tags, new ResolutionUnit());
        
        add(tags, new Software());
        add(tags, new DateTime());
        add(tags, new Artist());
        add(tags, new HostComputer());
        add(tags, new ColorMap());
        add(tags, new ExtraSamples());
        add(tags, new Copyright());

        return tags;
    }
    
    /**
     * Adds a tag into a map by indexing it with its key.
     * 
     * @param map
     *            the map to populate.
     * @param tag
     *            the tag to add.
     */
    private void add(Map<Integer, TiffTag> map, TiffTag tag)
    {
        tag.tagSet = this;
        map.put(tag.code, tag);
    }

    @Override
    public String getName()
    {
        return "Baseline";
    }
}
