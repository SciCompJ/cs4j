/**
 * 
 */
package net.sci.image.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;

import net.sci.array.Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.Image;
import net.sci.image.io.tiff.BaselineTags;
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
    static final int HEADER_SIZE = 8;
    static final int ENTRY_SIZE = 12;
    static final int BPS_DATA_SIZE = 6;
    static final int MAP_SIZE = 768; // in 16-bit words
    static final int SCALE_DATA_SIZE = 16;
    
    File file;
    OutputStream out;
    boolean littleEndian = true;
    
    public TiffImageWriter(File file)
    {
        this.file = file;
    }
    
    @Override
    public void writeImage(Image image) throws IOException
    {
        // check if image is 3D
        boolean bigTiff = false;
        
        ImageFileDirectory ifd = new ImageFileDirectory();
        
        // initialize the collection of tags
        // Depending on the type of images, some of the tags are mandatory

        // file type
        ifd.addEntry(new BaselineTags.NewSubfileType().initFrom(image));
        
        // image dimension
        ifd.addEntry(new BaselineTags.ImageWidth().initFrom(image));
        ifd.addEntry(new BaselineTags.ImageHeight().initFrom(image));
        
        // number of bits per sample
        ifd.addEntry(new BaselineTags.BitsPerSample().initFrom(image));

        // compression mode (default is none)
        ifd.addEntry(new BaselineTags.CompressionMode().initFrom(image));
        
        // photometric interpretation
        ifd.addEntry(new BaselineTags.PhotometricInterpretation().initFrom(image));
        
        // the offset to write image data (content initialized later)
        TiffTag imageOffsetTag = new BaselineTags.StripOffsets().initFrom(image);
        ifd.addEntry(imageOffsetTag);
        
        // the number of elements (samples) per pixel. 1 for grayscale, 3 for colors.
        ifd.addEntry(new BaselineTags.SamplesPerPixel().initFrom(image));

        // determines how to write image data. Data is organized in one or more "strips".
        // Each strip contain data for one or more image rows.
        // Default: only one strip that contains all rows. RowsPerStrip contains row number, 
        // and StripByteCount contains data size.
        ifd.addEntry(new BaselineTags.RowsPerStrip().initFrom(image));
        ifd.addEntry(new BaselineTags.StripByteCounts().initFrom(image));
        
        // meta-data
        ifd.addEntry(new BaselineTags.XResolution().initFrom(image));
        ifd.addEntry(new BaselineTags.YResolution().initFrom(image));
        ifd.addEntry(new BaselineTags.ResolutionUnit().initFrom(image));
        
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
        imageOffsetTag.value = (int) imageOffset;
        
        // compute offset to next IFD
        long imageSize = computeImageSize(image);
        int nImages = 1;
        long nextIFD = 0L;
        if (image.getDimension() > 2)
        {
            nImages = image.getSize(2);
            // in the case of 3D image data, store all image data together, 
            // then writes all the additional IFD 
            long stackSize = (long) imageSize * nImages;
            nextIFD = imageOffset + stackSize;
        }
        ifd.setOffset(nextIFD);
        
        // open output stream
        this.out = new FileOutputStream(this.file);
        ByteOrder order = littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        writeHeader();
        
        // Write current image file directory
        ifd.write(out, order);

        // Write content of the different tags
        ifd.writeEntryData(out, order);
                
        // Finally, image data (the whole array)
        writeImageData(image.getData());
        
        
        // Process optional remaining images
        if (nextIFD > 0L)
        {
            // for remaining IFD, do not copy all information
            // -> adapt the size of the IFD
            // -> use same pointers within the tags
            ImageFileDirectory ifd2 = duplicate(ifd);
            int ifdSize2 = ifd2.byteCount();
            imageOffsetTag = ifd2.getEntry(BaselineTags.StripOffsets.CODE);
            
            // iterate over remaining image planes
            for (int i = 1; i < nImages; i++)
            {
                
                nextIFD += ifdSize2;
                if (i == nImages - 1)
                    nextIFD = 0;
                    
                imageOffset += imageSize;
                imageOffsetTag.value = (int) imageOffset;
                ifd2.setOffset(nextIFD);
                
                ifd2.write(out, order);
            }
        } 
        else if (bigTiff)
        {
            // TODO: manage case of large TIFF a la ImageJ
            System.out.println("Image is too large to fit within TIFF format.");
        }
        
        this.out.close();
    }
    
    /**
     * Writes the header of the TIFF file, composed of a sequence of eight
     * bytes. The sequence starts either with "II" (Intel byte order, of
     * little-endian) or "MM" (Motorola byte order, of big-endian).
     */
    private void writeHeader() throws IOException
    {
       if (this.littleEndian)
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

    private long computeImageSize(Image image)
    {
        // image size
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);
        
        // image type
        int bytesPerPixel = bytesPerPixel(image);
        
        // image size a number of bytes
        long bytesPerPlane = (long) sizeX * sizeY * bytesPerPixel;
        long imageSize = bytesPerPlane <= 0xffffffffL ? (int) bytesPerPlane : 0;
        return imageSize;   
    }
    
    private int bytesPerPixel(Image image)
    {
        Array<?> array = image.getData();
        
        // use pattern matching
        return switch (array)
        {
            case UInt8Array x -> 1;
            case UInt16Array x -> 2;
            case Int16Array x -> 2;
            case RGB8Array x -> 3;
            default -> throw new RuntimeException(
                    "Unable to determine pixel type for image data with class: " + array.getClass().getName());
        };
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
        // TODO: need to specify byte order?
        BufferedOutputStream bos = new BufferedOutputStream(this.out);
        if (array instanceof UInt8Array array2)
        {
            for (int[] pos : array2.positions())
            {
                bos.write(array2.getByte(pos));
            }
        }
        else if (array instanceof UInt16Array array2)
        {
            for (int[] pos : array2.positions())
            {
                bos.write(array2.getShort(pos));
            }
        }
        else if (array instanceof RGB8Array array2)
        {
            for (int[] pos : array2.positions())
            {
                bos.write((byte) array2.getSample(pos, 0));
                bos.write((byte) array2.getSample(pos, 1));
                bos.write((byte) array2.getSample(pos, 2));
            }
        }
        else
        {
            throw new RuntimeException("Can not manage arays with class: " + array.getClass()); 
        }
        bos.flush();
    }
}
