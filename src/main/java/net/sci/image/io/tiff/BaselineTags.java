/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sci.image.io.BinaryDataReader;
import net.sci.image.io.tiff.TiffFileInfo.Compression;
import net.sci.image.io.tiff.TiffFileInfo.PixelType;

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
        }
    
        public void process(TiffFileInfo info)
        {
            info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
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
    
        public void process(TiffFileInfo info)
        {
            System.out.println("Warning: TiffTag with code 255 (SubFileType) is deprecated.");
            info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
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
        }
    
        public void process(TiffFileInfo info)
        {
            info.width = value;
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
        }
    
        public void process(TiffFileInfo info)
        {
            info.height = value;
        }
    }

    /**
     * 258 - Number of bits per component.
     */
    public static final class BitsPerSample extends TiffTag
    {
        public static final int CODE = 258;
        public BitsPerSample()
        {
            super(CODE, "BitsPerSample", "Number of bits per component");
        }
    
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
    }

    /**
     * 259 - Compression scheme used on the image data.
     */
    public static final class CompressionMode extends TiffTag
    {
        public static final int CODE = 259;
        public CompressionMode()
        {
            super(CODE, "CompressionMode", "Compression scheme used on the image data");
        }
    
        public void process(TiffFileInfo info)
        {
            info.compression = Compression.fromValue(value);
        }
    }

    
    /**
     * 262 - The color space of the image data.
     */
    public static final class PhotometricInterpretation extends TiffTag
    {
        public static final int CODE = 262;
        public PhotometricInterpretation()
        {
            super(CODE, "PhotometricInterpretation", "The color space of the image data");
        }
    
        public void process(TiffFileInfo info)
        {
            info.photometricInterpretation = value;
            info.whiteIsZero = value == 0;
        }
    }
    
    /**
     * 263 - For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels.
     */
    public static final class Thresholding extends TiffTag
    {
        public static final int CODE = 263;
        public Thresholding()
        {
            super(CODE, "Thresholding", 
                    "For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels");
        }
    }

    /**
     * 264 - The width of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file.
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
     * 265 - The length of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file.
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

        public void process(TiffFileInfo info)
        {
            if (value == 2)
            {
                System.err.println("Warning: Can not manage Tiff Files with FillOrder=2");
            }
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
        
        public void init(BinaryDataReader dataReader) throws IOException
        {
            this.content = readAscii(dataReader);
        }
        
        public void process(TiffFileInfo info)
        {
            info.imageDescription = (String) this.content;
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
     * 273 - For each strip, the byte offset of that strip.
     */
    public static final class StripOffsets extends TiffTag
    {
        public static final int CODE = 273;
        
        public StripOffsets()
        {
            super(CODE, "StripOffsets", "For each strip, the byte offset of that strip");
        }
        
        public void init(BinaryDataReader dataReader) throws IOException
        {
            this.content = readArray(dataReader);
        }
        
        public void process(TiffFileInfo info)
        {
            info.stripOffsets = (int[]) this.content;
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

        public void process(TiffFileInfo info)
        {
            info.orientation = TiffFileInfo.Orientation.fromValue(value);
        }
    }
    
    /**
     * 277 - The number of components per pixel.
     */
    public static final class SamplesPerPixel extends TiffTag
    {
        public static final int CODE = 277;
        public SamplesPerPixel()
        {
            super(CODE, "SamplesPerPixel", "The number of components per pixel");
        }
        
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
    }

    /**
     * 278 - RowsPerStrip", "The number of rows per strip.
     */
    public static final class RowsPerStrip extends TiffTag
    {
        public static final int CODE = 278;
        public RowsPerStrip()
        {
            super(CODE, "RowsPerStrip", "The number of rows per strip");
        }
    }
    
    /**
     * 279 - For each strip, the number of bytes in the strip after compression.
     */
    public static final class StripByteCounts extends TiffTag
    {
        public static final int CODE = 279;
        public StripByteCounts()
        {
            super(CODE, "StripByteCounts", "For each strip, the number of bytes in the strip after compression");
        }

        public void init(BinaryDataReader dataReader) throws IOException
        {
            this.content = readArray(dataReader);
        }
        
        public void process(TiffFileInfo info)
        {
            info.stripLengths = (int[]) this.content;
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
        }

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
        }
        
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
        public void init(BinaryDataReader dataReader) throws IOException
        {
            this.content = readShortArray(dataReader);
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
        }
        
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
            super(CODE, "HostComputer", "The precision of the information contained in the GrayResponseCurve");
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
        
        public void init(BinaryDataReader dataReader) throws IOException
        {
            this.content = readColorMap(dataReader, count / 3);
        }
        
        public void process(TiffFileInfo info)
        {
            info.lut = (int[][]) this.content;
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
        add(tags, new CompressionMode());

        add(tags, new PhotometricInterpretation());
        add(tags, new Thresholding());
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
