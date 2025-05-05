/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import net.sci.array.Array;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64VectorArray;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.Calibration;
import net.sci.image.Image;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.BaselineTags.PhotometricInterpretation;
import net.sci.image.io.tiff.BaselineTags.PlanarConfiguration;
import net.sci.image.io.tiff.BaselineTags.ResolutionUnit;
import net.sci.image.io.tiff.ExtensionTags.SampleFormat;
import net.sci.image.io.tiff.ImageFileDirectory;
import net.sci.image.io.tiff.TiffTag;

/**
 * Writer for images in TIFF format.
 * 
 * @author dlegland
 *
 */
public class TiffImageWriter implements ImageWriter
{
    /**
     * The number of bytes necessary to write the header.
     */
    static final int HEADER_SIZE = 8;

    /**
     * The number of bytes necessary to write tag / an entry into ad Image File
     * Directory (without tag data).
     */
    static final int ENTRY_SIZE = 12;
    
    boolean useImagejDescription = true;
    
    File file;
    OutputStream out;
    
    /** Byte order to use for writing binary data. Use BIG_ENDIAN as default */
    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    public TiffImageWriter(File file)
    {
        this.file = file;
    }
    
    @Override
    public void writeImage(Image image) throws IOException
    {
        // single image size as number of bytes
        long sliceImageByteCount = computeSliceImageByteCount(image);

        ImageFileDirectory ifd = initImageFileDirectory(image);
        
        // determine size of IFD, in bytes.
        // -> entry count (2 bytes) + 12 bytes per entry + next offset (4 bytes).
        int ifdSize = ifd.byteCount();
        
        // the offset to the beginning of IFD data
        int tagDataOffset = HEADER_SIZE + ifdSize;
        int ifdDataSize = ifd.entryDataByteCount();
        
        // setup the offset of entries with content
        for (TiffTag tag : ifd.entries())
        {
            int size = tag.contentSize();
            if (size > 0)
            {
                tag.value = tagDataOffset;
                tagDataOffset += size;
            }
        }
        
        // determine image offset after IFD data 
        long imageOffset = HEADER_SIZE + ifdSize + ifdDataSize;
        TiffTag imageOffsetTag = ifd.getEntry(BaselineTags.StripOffsets.CODE);
        imageOffsetTag.value = (int) imageOffset;
        
        // compute offset to next IFD
        int nImages = image.getDimension() > 2 ? image.getSize(2) : 1;
        boolean bigTiff = false;
        long nextIFD = 0L;
        if (nImages > 1)
        {
            // in the case of 3D image data, store the whole image data together, 
            // then writes all the additional IFD 
            long stackSize = (long) sliceImageByteCount * nImages;
            nextIFD = imageOffset + stackSize;

            // determine whether the whole image can fit within the 4GB limit
            bigTiff = nextIFD + (nImages - 1) * ifdSize >= 0xffffffffL;
            if (bigTiff)
            {
                nextIFD = 0L;
            }
        }
        ifd.setOffset(nextIFD);
        
        
        // open output stream
        this.out = new FileOutputStream(this.file);
        writeHeader();
        
        // Write current image file directory
        ifd.write(out, byteOrder);

        // Write content of the different tags
        ifd.writeEntryData(out, byteOrder);
                
        // Finally, image data (the whole array)
        writeImageData(image.getData());
        
        
        // Process optional remaining Image File Directories
        if (nextIFD > 0L)
        {
            // for remaining IFD, do not copy all information
            // -> adapt the size of the IFD
            // -> use same pointers within the tags
            ImageFileDirectory ifd2 = duplicate(ifd);
            int ifdSize2 = ifd2.byteCount();
            
            // iterate over remaining image planes
            for (int i = 1; i < nImages; i++)
            {
                nextIFD += ifdSize2;
                if (i == nImages - 1)
                    nextIFD = 0;
                    
                // update entry values of IFD
                imageOffset += sliceImageByteCount;
                imageOffsetTag = ifd2.getEntry(BaselineTags.StripOffsets.CODE);
                imageOffsetTag.value = (int) imageOffset;
                ifd2.setOffset(nextIFD);
                
                ifd2.write(out, byteOrder);
            }
        } 
        else if (bigTiff)
        {
            System.out.println("Stack is larger than 4GB, and most TIFF readers will only open the first image.\nUse this information to open as raw:");
            System.out.println(createImportString(image, ifd));
        }
        
        this.out.close();
    }
    
    private ImageFileDirectory initImageFileDirectory(Image image)
    {
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);
        PixelType pixelType = PixelType.fromImage(image);
        int samplesPerPixel = pixelType.sampleCount();
        int bitsPerSample = pixelType.bitsPerSample();
        
        // image size as number of bytes
        long sliceImageByteCount = computeSliceImageByteCount(image);
        
        ImageFileDirectory ifd = new ImageFileDirectory();
        
        // initialize the collection of tags
        // Depending on the type of images, some of the tags are mandatory

        // file type
        ifd.addEntry(new BaselineTags.NewSubfileType());
        
        // image dimension
        ifd.addEntry(new BaselineTags.ImageWidth().setIntValue(sizeX));
        ifd.addEntry(new BaselineTags.ImageHeight().setIntValue(sizeY));
        
        // number of bits per sample (use tag-specific initialization method)
        ifd.addEntry(new BaselineTags.BitsPerSample().init(samplesPerPixel, bitsPerSample));

        // compression mode (default is none)
        ifd.addEntry(new BaselineTags.Compression());
        
        // photometric interpretation
        TiffTag photometricInterpretationTag = new PhotometricInterpretation();
        if (image.getData() instanceof RGB8Array || image.getData() instanceof RGB16Array)
        {
            photometricInterpretationTag.setShortValue(PhotometricInterpretation.RGB);
        }
        else
        {
            photometricInterpretationTag.setShortValue(PhotometricInterpretation.BLACK_IS_ZERO);
        }
        ifd.addEntry(photometricInterpretationTag);
        
        // create special description string that can be interpreted by ImageJ
        if (useImagejDescription)
        {
            String description = createImagejDescriptionString(image);
            ifd.addEntry(new BaselineTags.ImageDescription().setValue(description));
        }
        
        // the offset to write image data (content initialized later)
        TiffTag imageOffsetTag = new BaselineTags.StripOffsets().initFrom(image);
        ifd.addEntry(imageOffsetTag);
        
        // the number of elements (samples) per pixel. 1 for grayscale, 3 for colors.
        ifd.addEntry(new BaselineTags.SamplesPerPixel().setShortValue((short) samplesPerPixel));

        // determines how to write image data. Data is organized in one or more "strips".
        // Each strip contain data for one or more image rows.
        // Default: only one strip that contains all rows. RowsPerStrip contains row number, 
        // and StripByteCount contains total number of bytes of an image plane.
        ifd.addEntry(new BaselineTags.RowsPerStrip().setIntValue(sizeY));
        ifd.addEntry(new BaselineTags.StripByteCounts().setIntValue((int) sliceImageByteCount));
        
        // save calibration from image
        Calibration calib = image.getCalibration();
        double xspacing = calib.getXAxis().getSpacing();
        double yspacing = calib.getYAxis().getSpacing();
        ifd.addEntry(new BaselineTags.XResolution().setValue(createSpacingRational(xspacing)));
        ifd.addEntry(new BaselineTags.YResolution().setValue(createSpacingRational(yspacing)));
        ifd.addEntry(new BaselineTags.ResolutionUnit().setShortValue(ResolutionUnit.NO_UNIT));

        // planar configuration required only when samples per pixel > 1
        if (samplesPerPixel > 1)
        {
            // init to "Chunky" format (recommended value from specification)
            ifd.addEntry(new BaselineTags.PlanarConfiguration().setShortValue(PlanarConfiguration.CHUNKY));
        }
        
        // --- Extension tags ---
        ifd.addEntry(new SampleFormat().init(pixelType));
        
        // add non-mandatoy tag(s)
        DateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateString = formatter.format(new Date(System.currentTimeMillis()));
        ifd.addEntry(new BaselineTags.DateTime().setValue(dateString));
        
        return ifd;
    }
    
    /**
     * Creates a string that can be interpreted by the ImageJ software to read
     * TIFF images, in particular for reading files larger than 4GB.
     * 
     * In the current implementation, only management of 3D XYZ images is
     * implemented.
     * 
     * @param image
     *            the image that need to be saved
     * @return a string instance that will be saved into the "description" entry
     *         of the Tiff file.
     */
    private String createImagejDescriptionString(Image image) 
    {
        int sizeZ = image.getDimension() > 2 ? image.getSize(2) : 1;
        
        StringBuffer sb = new StringBuffer(100);
        
        // use an arbitrary version of ImageJ
        sb.append("ImageJ=1.54m\n");
        
        if (image.getDimension() > 2)
        {
            sb.append("images=" + image.getSize(2) + "\n");
        }
        if (sizeZ > 1)
        {
            sb.append("slices=" + sizeZ + "\n");
        }
        
        // add calibration info
        Calibration cal = image.getCalibration();
        if (cal.isCalibrated() && cal.getXAxis().getUnitName() != null)
        {
            addEscapedString(sb, "unit=" + cal.getXAxis().getUnitName() + "\n");
        }
        if (sizeZ > 1) 
        {
            if (cal.isCalibrated() && cal.getXAxis().getUnitName() != null)
            {
                sb.append("spacing=" + cal.getZAxis().getSpacing() + "\n");
            }
            sb.append("loop=" + "false" + "\n");
        }
        
        sb.append((char)0);
        return new String(sb);
    }
     
    /**
     * Appends a string to the specified StringBuffer, by escaping special
     * character.
     * 
     * @param sb
     *            the StringBuffer to update
     * @param str
     *            the String to append
     */
    private static final void addEscapedString(StringBuffer sb, String str)
    {
        for (int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);
            if (c >= 0x20 && c < 0x7f && c != '\\')
            {
                // classical character
                sb.append(c);
            }
            else if (c <= 0xffff)
            { 
                // (supplementary unicode characters >0xffff unsupported)
                sb.append("\\u");
                sb.append(int2hex(c, 4));
            }
        }
    }
    
    private static final long computeSliceImageByteCount(Image image)
    {
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);
        PixelType pixelType = PixelType.fromImage(image);
        int samplesPerPixel = pixelType.sampleCount();
        int bitsPerSample = pixelType.bitsPerSample();
        
        // image size as number of bytes
        return ((long) sizeX) * sizeY * samplesPerPixel * (bitsPerSample / 8);
    }
    
    private static final int[] createSpacingRational(double spacing)
    {
        // store calibration as 1_000_000 over spacing (IJ default behavior)
        double value = 1.0 / spacing;
        double denom = 1_000_000.0;
        if (value * denom > Integer.MAX_VALUE)
        {
            denom /= Integer.MAX_VALUE;
        }
        return new int[] { (int) (value * denom), (int) denom };
    }

    /**
     * Writes the header of the TIFF file, composed of a sequence of eight
     * bytes. The sequence starts either with "II" (Intel byte order, of
     * little-endian) or "MM" (Motorola byte order, of big-endian).
     */
    private void writeHeader() throws IOException
    {
       if (this.byteOrder == ByteOrder.LITTLE_ENDIAN)
       {
           // Start with "II" (Intel byte order, little-endian)
           // Then magic number "42" as a short, 
           // then offset to first IFD, equal to 8, as 32-bits integer.
           out.write(new byte[] {73, 73, 42, 0, 8, 0, 0, 0});
       }
       else
       {
           // Start with "MM" (Motorola byte order, big-endian)
           // Then magic number "42" as a short, 
           // then offset to first IFD, equal to 8, as 32-bits integer.
           out.write(new byte[] {77, 77, 0, 42, 0, 0, 0, 8});
       }
    }
    
    /**
     * Duplicates this ImageFileDirectory, to facilitate writing of a new image
     * with nearly the same entries.
     * 
     * @return a new instance of {@code ImageFileDirectory} initialized with
     *         same entries.
     */
    private ImageFileDirectory duplicate(ImageFileDirectory ifd)
    {
        ImageFileDirectory newIFD = new ImageFileDirectory();
        for (TiffTag tag : ifd.entries())
        {
            newIFD.addEntry(tag);
        }
        return newIFD;
    }
    
    /**
     * Writes all the data within the array, using the natural position iterator
     * of the array.
     * 
     * @param array
     *            the array to write into the file.
     * @throws IOException
     *             if an I/O Exception occurred
     */
    public void writeImageData(Array<?> array) throws IOException
    {
        // Create DataOutputStream to write formatted data.
        // DataOutputStream use necessarily BIG_ENDIAN byte order.
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(this.out));
        if (array instanceof UInt8Array array2)
        {
            for (int[] pos : array2.positions())
            {
                dos.write(array2.getByte(pos));
            }
        }
        else if (array instanceof UInt16Array array2)
        {
            for (int[] pos : array2.positions())
            {
                dos.writeShort(array2.getShort(pos));
            }
        }
        else if (array instanceof Float32Array array2)
        {
            for (int[] pos : array2.positions())
            {
                dos.writeFloat(array2.getFloat(pos));
            }
        }
        else if (array instanceof Float64Array array2)
        {
            for (int[] pos : array2.positions())
            {
                dos.writeDouble(array2.getValue(pos));
            }
        }
        else if (array instanceof RGB8Array rgbArray)
        {
            for (int[] pos : rgbArray.positions())
            {
                dos.write((byte) rgbArray.getSample(pos, 0));
                dos.write((byte) rgbArray.getSample(pos, 1));
                dos.write((byte) rgbArray.getSample(pos, 2));
            }
        }
        else if (array instanceof Float32VectorArray vectorArray)
        {
            int nc = vectorArray.channelCount();
            for (int[] pos : vectorArray.positions())
            {
                for (int c = 0; c < nc; c++)
                {
                    dos.writeFloat(vectorArray.getFloat(pos, c));
                }
            }
        }
        else if (array instanceof Float64VectorArray vectorArray)
        {
            int nc = vectorArray.channelCount();
            for (int[] pos : vectorArray.positions())
            {
                for (int c = 0; c < nc; c++)
                {
                    dos.writeDouble(vectorArray.getValue(pos, c));
                }
            }
        }
        else
        {
            throw new RuntimeException("Can not manage arays with class: " + array.getClass()); 
        }
        dos.flush();
    }

    private String createImportString(Image image, ImageFileDirectory ifd)
    {
        int nImages = image.getDimension() > 2 ? image.getSize(2) : 1;
        PixelType pixelType = PixelType.fromImage(image);
        
        StringBuffer sb = new StringBuffer();
        sb.append("name=").append(file.getName());
        sb.append(", path=").append(file.getParent());
        sb.append(", width=").append(image.getSize(0));
        sb.append(", height=").append(image.getSize(1));
        sb.append(", nImages=").append(nImages);
        long offset = ifd.getIntArrayValue(BaselineTags.StripOffsets.CODE, null)[0];
        sb.append(", offset=").append(offset);
        sb.append(", type=").append(image.getData().elementClass().getSimpleName());
        sb.append(", byteOrder=").append(this.byteOrder);
        sb.append(", format=").append("tif");
        sb.append(", samples=").append(pixelType.sampleCount());
        return sb.toString();
    }
    

    /** The 16 hexadecimal digits from '0' to 'F' */
    private static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
            'F' };

    /**
     * Converts an integer into a zero-padded hex string of fixed length given
     * by the {@code digits} parameter. If the number is too large, it is
     * truncated by keeping only the lowest digits.
     * 
     * @param i
     *            the value to convert
     * @param nDigits
     *            the number of digits of the result
     * @return an hexadecimal representation of the integer
     */
    private static final String int2hex(int i, int nDigits)
    {
        char[] buf = new char[nDigits];
        for (int pos = nDigits - 1; pos >= 0; pos--)
        {
            buf[pos] = hexDigits[i & 0xf];
            i >>>= 4;
        }
        return new String(buf);
    }
}
