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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.sci.algo.AlgoStub;
import net.sci.array.Array;
import net.sci.array.Array2D;
import net.sci.array.Array3D;
import net.sci.array.color.RGB16;
import net.sci.array.color.RGB16Array;
import net.sci.array.color.RGB8;
import net.sci.array.color.RGB8Array;
import net.sci.array.color.RGB8Array2D;
import net.sci.array.numeric.Float32;
import net.sci.array.numeric.Float32Array;
import net.sci.array.numeric.Float32Array2D;
import net.sci.array.numeric.Float32Vector;
import net.sci.array.numeric.Float32VectorArray;
import net.sci.array.numeric.Float64;
import net.sci.array.numeric.Float64Array;
import net.sci.array.numeric.Float64Array2D;
import net.sci.array.numeric.Float64Vector;
import net.sci.array.numeric.Float64VectorArray;
import net.sci.array.numeric.Int16;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.Int16Array2D;
import net.sci.array.numeric.Int32;
import net.sci.array.numeric.Int32Array;
import net.sci.array.numeric.Int32Array2D;
import net.sci.array.numeric.UInt16;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt16Array2D;
import net.sci.array.numeric.UInt8;
import net.sci.array.numeric.UInt8Array;
import net.sci.array.numeric.UInt8Array2D;
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
 * Uses BIG_ENDIAN byte order.
 * 
 * @author dlegland
 *
 */
public class TiffImageWriter extends AlgoStub implements ImageWriter
{
    // =============================================================
    // Constants
    
    /**
     * The number of bytes necessary to write the header.
     */
    static final int HEADER_SIZE = 8;

    /**
     * The number of bytes necessary to write tag / an entry into ad Image File
     * Directory (without tag data).
     */
    static final int ENTRY_SIZE = 12;
    
    
    // =============================================================
    // Class variables
    
    /**
     * The file to write image in.
     */
    File file;
    
    boolean useImagejDescription = true;
    
    /**
     * A list of tags that user can specify, and that will be saved within the
     * Tiff file.
     */
    ArrayList<TiffTag> customTags = new ArrayList<>(4);
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new TiffImageWriter from a file.
     * 
     * @param file
     *            the file to write the Tiff image into.
     */
    public TiffImageWriter(File file)
    {
        this.file = file;
    }
    
    
    // =============================================================
    // Setup methods
    
    /**
     * Specifies whether this writer should use a description that can be
     * interpreted by the ImageJ software.
     * 
     * @param useImagejDescription
     *            a boolean flag
     * @return a reference to this writer
     */
    public TiffImageWriter useImagejDescription(boolean useImagejDescription)
    {
        this.useImagejDescription = useImagejDescription;
        return this;
    }
    
    public TiffImageWriter addCustomTag(TiffTag tag)
    {
        this.customTags.add(tag);
        return this;
    }
    
    
    // =============================================================
    // General methods
    
    @Override
    public void writeImage(Image image) throws IOException
    {
        // single image size as number of bytes
        long sliceImageByteCount = computeSliceImageByteCount(image);

        this.fireStatusChanged(this, "Setup ImageFileDIrectory");
        ImageFileDirectory ifd = initImageFileDirectory(image);
        if (ifd.getByteOrder() != ByteOrder.BIG_ENDIAN)
        {
            throw new RuntimeException("Can only write TIFF file with BIG_ENDIAN byte order");
        }
        
        // add custom tags
        for (TiffTag tag : customTags)
        {
            if (ifd.getEntry(tag.code) != null)
            {
                System.err.println("Duplicate tag with code " + tag.code + ", skipping last entries");
                continue;
            }
            ifd.addEntry(tag);
        }
        
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
        
        this.fireStatusChanged(this, "Setup Image tags data");
        
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
        OutputStream out = new BufferedOutputStream(new FileOutputStream(this.file));
        // Byte order to use for writing binary data. 
        // Use BIG_ENDIAN as default for writing images, initialized at creation of IFD
        ByteOrder byteOrder = ifd.getByteOrder();

        
        // write Tiff ID, and offset to first IFD
        writeHeader(out, byteOrder);
        
        // Write current image file directory
        this.fireStatusChanged(this, "Write IFD entries");
        ifd.write(out);

        // Write content of the different tags
        this.fireStatusChanged(this, "Write IFD entry data");
        ifd.writeEntryData(out);
                
        // Finally, image data (the whole array)
        this.fireStatusChanged(this, "Write Image data");
        writeImageData(out, image.getData());
        
        
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
                
                ifd2.write(out);
            }
        } 
        else if (bigTiff)
        {
            System.out.println("Stack is larger than 4GB, and most TIFF readers will only open the first image.\nUse this information to open as raw:");
            System.out.println(createImportString(image, ifd));
        }
        
        out.close();
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
        
        ImageFileDirectory ifd = new ImageFileDirectory().setByteOrder(ByteOrder.BIG_ENDIAN);
        
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
        
        // photometric interpretation (use tag-specific static factory)
        ifd.addEntry(PhotometricInterpretation.of(image.getData()));
        
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
    private void writeHeader(OutputStream out, ByteOrder order) throws IOException
    {
       if (order == ByteOrder.LITTLE_ENDIAN)
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
    private static final ImageFileDirectory duplicate(ImageFileDirectory ifd)
    {
        ImageFileDirectory newIFD = new ImageFileDirectory()
                .setByteOrder(ifd.getByteOrder());
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
    private void writeImageData(OutputStream out, Array<?> array) throws IOException
    {
        switch (array.dimensionality())
        {
            case 2 -> writeImageData2d(out, Array2D.wrap(array));
            case 3 -> 
            {
                // in case of 3D data, iterate over the 2D slices, notifying each new slice 
                Array3D<?> array3d = Array3D.wrap(array);
                int sizeZ = array.size(2);
                for (int z = 0; z < sizeZ; z++)
                {
                    this.fireProgressChanged(this, z, sizeZ);
                    writeImageData2d(out, array3d.slice(z));
                }
                this.fireProgressChanged(this, 1, 1);
            }
            default ->
            {
                throw new RuntimeException("Unable to manage an array with dimensionality: " + array.dimensionality());
            }
        }
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
    private void writeImageData2d(OutputStream out, Array2D<?> array) throws IOException
    {
        // retrieve array size
        int sizeX = array.size(0);
        int sizeY = array.size(1);
        
        // Create DataOutputStream to write formatted data.
        // DataOutputStream use necessarily BIG_ENDIAN byte order.
        DataOutputStream dos = new DataOutputStream(out);
        
        // Dispatch processing depending on element class
        Class<?> elementClass = array.elementClass();
        if (elementClass == UInt8.class)
        {
            UInt8Array2D array2 = UInt8Array2D.wrap(UInt8Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.write(array2.getByte(x, y));
                }
            }
        }
        else if (elementClass == UInt16.class)
        {
            UInt16Array2D array2 = UInt16Array2D.wrap(UInt16Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.writeShort(array2.getShort(x, y));
                }
            }
        }
        else if (elementClass == Int16.class)
        {
            Int16Array2D array2 = Int16Array2D.wrap(Int16Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.writeShort(array2.getShort(x, y));
                }
            }
        }
        else if (elementClass == Int32.class)
        {
            Int32Array2D array2 = Int32Array2D.wrap(Int32Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.writeInt(array2.getInt(x, y));
                }
            }
        }
        else if (elementClass == Float32.class)
        {
            Float32Array2D array2 = Float32Array2D.wrap(Float32Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.writeFloat(array2.getFloat(x, y));
                }
            }
        }
        else if (elementClass == Float64.class)
        {
            Float64Array2D array2 = Float64Array2D.wrap(Float64Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.writeDouble(array2.getValue(x, y));
                }
            }
        }
        else if (elementClass == RGB8.class)
        {
            RGB8Array2D array2 = RGB8Array2D.wrap(RGB8Array.wrap(array));
            for (int y = 0; y < sizeY; y++)
            {
                for (int x = 0; x < sizeX; x++)
                {
                    dos.write(array2.getSample(x, y, 0));
                    dos.write(array2.getSample(x, y, 1));
                    dos.write(array2.getSample(x, y, 2));
                }
            }
        }
        else if (elementClass == RGB16.class)
        {
            RGB16Array.Iterator iter = RGB16Array.wrap(array).iterator();
            while (iter.hasNext())
            {
                iter.forward();
                dos.writeShort(iter.getSample(0));
                dos.writeShort(iter.getSample(1));
                dos.writeShort(iter.getSample(2));
            }
        }
        else if (elementClass == Float32Vector.class)
        {
            Float32VectorArray vectorArray = Float32VectorArray.wrap(array);
            int nc = vectorArray.channelCount();
            
            Float32VectorArray.Iterator iter = vectorArray.iterator();
            while (iter.hasNext())
            {
                iter.forward();
                for (int c = 0; c < nc; c++)
                {
                    dos.writeFloat((float) iter.getValue(c));
                }
            }
        }
        else if (elementClass == Float64Vector.class)
        {
            Float64VectorArray vectorArray = Float64VectorArray.wrap(array);
            int nc = vectorArray.channelCount();
            
            Float64VectorArray.Iterator iter = vectorArray.iterator();
            while (iter.hasNext())
            {
                iter.forward();
                for (int c = 0; c < nc; c++)
                {
                    dos.writeDouble(iter.getValue(c));
                }
            }
        }
        else
        {
            throw new RuntimeException("Can not manage arrays with class: " + array.getClass()); 
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
        sb.append(", byteOrder=").append(ifd.getByteOrder());
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
