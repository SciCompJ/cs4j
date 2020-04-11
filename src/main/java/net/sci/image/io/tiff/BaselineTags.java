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
    public static final int NEW_SUBFILE_TYPE = 254;
    
    /**
     * 255 - (deprecated) A general indication of the kind of data contained in this subfile.
     */
    public static final int SUBFILE_TYPE = 255;
    
    /**
     * 256 - The number of columns in the image, i.e., the number of pixels per row.
     */
    public static final int IMAGE_WIDTH = 256;
    
    /**
     * 257 - The number of rows of pixels in the image.
     */
    public static final int IMAGE_HEIGHT = 257;
    
    /**
     * 258 - Number of bits per component.
     */
    public static final int BITS_PER_SAMPLE = 258;
    
    /**
     * 259 - Compression scheme used on the image data.
     */
    public static final int COMPRESSION_MODE = 259;
    
    /**
     * 262 - The color space of the image data.
     */
    public static final int PHOTOMETRIC_INTERPRETATION = 262;
    
    /**
     * 263 - For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels.
     */
    public static final int THRESHOLDING = 263;

    /**
     * 264 - The width of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file.
     */

    public static final int CELL_WIDTH = 264;
    /**
     * 265 - The length of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file.
     */
    public static final int CELL_HEIGHT = 265;
    
    /**
     * 266 - The logical order of bits within a byte.
     */
    public static final int FILL_ORDER = 266;
    
    /**
     * 270 - A string that describes the subject of the image.
     */
    public static final int IMAGE_DESCRIPTION = 270;
    
    /**
     * 271 - The scanner manufacturer.
     */
    public static final int MAKE = 271;
    
    /**
     * 272 -The scanner model name or number.
     */
    public static final int MODEL = 272;
    
    /**
     * 273 - For each strip, the byte offset of that strip.
     */
    public static final int STRIP_OFFSETS = 273;
    
    /**
     * 274 - The orientation of the image with respect to the rows and columns.
     */
    public static final int ORIENTATION = 274;
    
    /**
     * 277 - The number of components per pixel.
     */
    public static final int SAMPLES_PER_PIXEL = 277;
    
    /**
     * 278 - RowsPerStrip", "The number of rows per strip.
     */
    public static final int ROWS_PER_STRIP = 278;
    
    /**
     * 279 - For each strip, the number of bytes in the strip after compression.
     */
    public static final int STRIP_BYTE_COUNTS = 279;
    
    /**
     * 280 - The minimum component value used.
     */
    public static final int MIN_SAMPLE_VALUE = 280;
    
    /**
     * 281 - The maximum component value used.
     */
    public static final int MAX_SAMPLE_VALUE= 281;
    
    /**
     * 282 - The number of pixels per ResolutionUnit in the ImageWidth direction.
     */
    public static final int X_RESOLUTION = 282;
    
    /**
     * 283 - The number of pixels per ResolutionUnit in the ImageHeight direction.
     */
    public static final int Y_RESOLUTION = 283;
    
    /**
     * 284 - How the components of each pixel are stored.
     */
    public static final int PLANAR_CONFIGURATION = 284;
    
    /**
     * 288 - For each string of contiguous unused bytes in a TIFF file, the byte offset of the string.
     */
    public static final int FREE_OFFSETS = 288;
    
    /**
     * 289 - For each string of contiguous unused bytes in a TIFF file, the number of bytes in the string.
     */
    public static final int FREE_BYTE_COUNTS = 289;
    
    /**
     * 290 - The precision of the information contained in the GrayResponseCurve.
     */
    public static final int GRAY_RESPONSE_UNIT = 290;
    
    /**
     * 291 - For grayscale data, the optical density of each possible pixel value.
     */
    public static final int GRAY_RESPONSE_CURVE = 291;
    
    /**
     * 296 - The unit of measurement for XResolution and YResolution.
     */
    public static final int RESOLUTION_UNIT = 296;
    
    /**
     * 305 - Name and version number of the software package(s) used to create the image.
     */
    public static final int SOFTWARE = 305;
    
    /**
     * 306 - Date and time of image creation.
     */
    public static final int DATE_TIME = 306;
    
    /**
     * 315 - Person who created the image.
     */
    public static final int ARTIST = 315;
    
    /**
     * 316 - The computer and/or operating system in use at the time of image creation.
     */
    public static final int HOST_COMPUTER = 316;
    
    /**
     * 320 - A color map for palette color images.
     */
    public static final int COLOR_MAP = 320;
    
    /**
     * 338 - Description of extra components.
     */
    public static final int EXTRA_SAMPLES = 338;
    
    /**
     * 33432 - Copyright notice.
     */
    public static final int COPYRIGHT = 33432;
    
    
    /* (non-Javadoc)
     * @see net.sci.image.io.tiff.TagSet#getTags()
     */
    @Override
    public  Map<Integer, TiffTag> getTags()
    {
        Map<Integer, TiffTag> tags = new HashMap<Integer, TiffTag>(2);
        
        add(tags, new TiffTag(NEW_SUBFILE_TYPE, "NewSubfileType", 
                "A general indication of the kind of data contained in this subfile")
        {
            public void process(TiffFileInfo info)
            {
                info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
            }
        });

        add(tags, new TiffTag(SUBFILE_TYPE, "SubfileType", 
                "(deprecated) A general indication of the kind of data contained in this subfile")
        {
            public void process(TiffFileInfo info)
            {
                System.out.println("Warning: TiffTag with code 255 (SubFileType) is deprecated.");
                info.subFileType = TiffFileInfo.SubFileType.fromValue(value);
            }
        });
        
        add(tags, new TiffTag(IMAGE_WIDTH, "ImageWidth", "The number of columns in the image")
        {
            public void process(TiffFileInfo info)
            {
                info.width = value;
            }
        });

        add(tags, new TiffTag(IMAGE_HEIGHT, "ImageHeight", "The number of rows of pixels in the image")
        {
            public void process(TiffFileInfo info)
            {
                info.height = value;
            }
        });

        add(tags, new TiffTag(BITS_PER_SAMPLE, "BitsPerSample", "Number of bits per component")
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

        add(tags, new TiffTag(COMPRESSION_MODE, "CompressionMode", "Compression scheme used on the image data")
        {
            public void process(TiffFileInfo info)
            {
                info.compression = Compression.fromValue(value);
            }
        });

        add(tags, new TiffTag(PHOTOMETRIC_INTERPRETATION, "PhotometricInterpretation", "The color space of the image data")
        {
            public void process(TiffFileInfo info)
            {
                info.photometricInterpretation = value;
                info.whiteIsZero = value == 0;
            }
        });

        add(tags, new TiffTag(THRESHOLDING, "Thresholding", 
                "For black and white TIFF files that represent shades of gray, the technique used to convert from gray to black and white pixels"));

        add(tags, new TiffTag(CELL_WIDTH, "CellWidth", 
                "The width of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file"));

        add(tags, new TiffTag(CELL_HEIGHT, "CellHeight", 
                "The length of the dithering or halftoning matrix used to create a dithered or halftoned bilevel file"));

       
        add(tags, new TiffTag(FILL_ORDER, "FillOrder", 
                "The logical order of bits within a byte")
        {
            public void process(TiffFileInfo info)
            {
                if (value == 2)
                {
                    System.err.println("Warning: Can not manage Tiff Files with FillOrder=2");
                }
            }
        });

        add(tags, new TiffTag(IMAGE_DESCRIPTION, "ImageDescription",
                "A string that describes the subject of the image")
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

        add(tags, new TiffTag(MAKE, "Make", "The scanner manufacturer"));

        add(tags, new TiffTag(MODEL, "Model", "The scanner model name or number"));
        
        
        add(tags, new TiffTag(STRIP_OFFSETS, "StripOffsets", "For each strip, the byte offset of that strip")
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

        add(tags, new TiffTag(ORIENTATION, "Orientation", "The orientation of the image with respect to the rows and columns")
        {
            public void process(TiffFileInfo info)
            {
                info.orientation = TiffFileInfo.Orientation.fromValue(value);
            }
        });

        add(tags, new TiffTag(SAMPLES_PER_PIXEL, "SamplesPerPixel", "The number of components per pixel")
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

        add(tags, new TiffTag(ROWS_PER_STRIP, "RowsPerStrip", "The number of rows per strip")
        {
            public void process(TiffFileInfo info)
            {
                info.rowsPerStrip = value;
            }
        });
        
        add(tags, new TiffTag(STRIP_BYTE_COUNTS, "StripByteCounts", "For each strip, the number of bytes in the strip after compression")
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
        
        add(tags, new TiffTag(MIN_SAMPLE_VALUE, "MinSampleValue", "The minimum component value used"));
        
        add(tags, new TiffTag(MAX_SAMPLE_VALUE, "MaxSampleValue", "The maximum component value used"));
        
        add(tags, new TiffTag(X_RESOLUTION, "XResolution", "The number of pixels per ResolutionUnit in the ImageWidth direction")
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
        
        add(tags, new TiffTag(Y_RESOLUTION, "YResolution", "The number of pixels per ResolutionUnit in the ImageHeight direction")
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
        
        add(tags, new TiffTag(PLANAR_CONFIGURATION, "PlanarConfiguration", "How the components of each pixel are stored")
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
        
        add(tags, new TiffTag(FREE_OFFSETS, "FreeOffsets",
                "For each string of contiguous unused bytes in a TIFF file, the byte offset of the string"));
        
        add(tags, new TiffTag(FREE_BYTE_COUNTS, "FreeByteCounts",
                "For each string of contiguous unused bytes in a TIFF file, the number of bytes in the string"));
        
        add(tags, new TiffTag(GRAY_RESPONSE_UNIT, "GrayResponseUnit",
                "The precision of the information contained in the GrayResponseCurve"));
        
        /**
         * The 0th value of GrayResponseCurve corresponds to the optical density
         * of a pixel having a value of 0, and so on.
         * 
         * This field may provide useful information for sophisticated
         * applications, but it is currently ignored by most TIFF readers.
         */
        add(tags, new TiffTag(GRAY_RESPONSE_CURVE, "GrayResponseCurve",
                "For grayscale data, the optical density of each possible pixel value")
        {
            public void init(BinaryDataReader dataReader) throws IOException
            {
                this.content = readShortArray(dataReader);
            }
           
        });
        
        
        
        add(tags, new TiffTag(RESOLUTION_UNIT, "ResolutionUnit", "The unit of measurement for XResolution and YResolution")
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
        
        add(tags, new TiffTag(SOFTWARE, "Software",
                "Name and version number of the software package(s) used to create the image"));
        
        /**
         * The format is: "YYYY:MM:DD HH:MM:SS", with hours like those on a
         * 24-hour clock, and one space character between the date and the time.
         * The length of the string, including the terminating NUL, is 20 bytes.
         */
        add(tags, new TiffTag(DATE_TIME, "DateTime",
                "Date and time of image creation"));

        add(tags, new TiffTag(ARTIST, "Artist",
                "Person who created the image"));
        
        add(tags, new TiffTag(HOST_COMPUTER, "HostComputer",
                "The computer and/or operating system in use at the time of image creation"));


        add(tags, new TiffTag(COLOR_MAP, "ColorMap", "A color map for palette color images")
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
        
        add(tags, new TiffTag(EXTRA_SAMPLES, "ExtraSamples",
                "Description of extra components"));

        add(tags, new TiffTag(COPYRIGHT, "Copyright",
                "Copyright notice"));

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
