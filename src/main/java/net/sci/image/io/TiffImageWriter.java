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
import java.util.ArrayList;

import net.sci.array.Array;
import net.sci.array.color.RGB8Array;
import net.sci.array.numeric.Int16Array;
import net.sci.array.numeric.UInt16Array;
import net.sci.array.numeric.UInt8Array;
import net.sci.image.Image;
import net.sci.image.io.tiff.BaselineTags;
import net.sci.image.io.tiff.TiffTag;

import static net.sci.image.io.tiff.TiffFileInfo.PixelType;

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
        
        // initialize the collection of tags
        ArrayList<TiffTag> tags = new ArrayList<TiffTag>();
        
        // count the number of entries. Nine are mandatory:
        // 1. new type file
        // 2. width
        // 3. height
        // 4. bits per sample
        // 5. photometric interpretation
        // 6. strip offsets
        // 7. samples per pixel
        // 8. rows per strip
        // 9. strip byte counts
        // other are optional.

        // file type
        tags.add(new BaselineTags.NewSubfileType().init(image));
        
        // image dimension
        tags.add(new BaselineTags.ImageWidth().init(image));
        tags.add(new BaselineTags.ImageHeight().init(image));
        
        // sample size
        tags.add(new BaselineTags.BitsPerSample().init(image));

        // compression mode (default is none)
        tags.add(new BaselineTags.CompressionMode().init(image));
        
        // photometric interpretation
        tags.add(new BaselineTags.PhotometricInterpretation().init(image));
        
        // the offset to write image data (content initialized later)
        TiffTag imageOffsetTag = new BaselineTags.StripOffsets().init(image);
        tags.add(imageOffsetTag);
        
        // the number of elements (samples) per pixel. 1 for grayscale, 3 for colors.
        tags.add(new BaselineTags.SamplesPerPixel().init(image));

        // determines how to write image data. Data is organized in one or more "strips".
        // Each strip contain data for one or more image rows.
        // Default: only one strip that contains all rows. RowsPerStrip contains row number, 
        // and StripByteCount contains data size.
        tags.add(new BaselineTags.RowsPerStrip().init(image));
        tags.add(new BaselineTags.StripByteCounts().init(image));
        
        // meta-data
        tags.add(new BaselineTags.XResolution().init(image));
        tags.add(new BaselineTags.YResolution().init(image));
        tags.add(new BaselineTags.ResolutionUnit().init(image));
        
        // determine size of IFD, in bytes.
        // -> entry count (2 bytes) + 12 bytes per entry + next offset (4 bytes).
        int ifdSize = 2 + tags.size() * ENTRY_SIZE + 4;
        
        // the offset to the beginning of IFD data
        int tagDataOffset = HEADER_SIZE + ifdSize;
        int ifdDataSize = 0;
        
        // setup offset of tags with content (except the "strip offsets" tag)
        for (TiffTag tag : tags)
        {
            if (tag.code == BaselineTags.StripOffsets.CODE) continue;
            int size = tag.contentSize();
            if (size > 0)
            {
                tag.value = tagDataOffset;
                tagDataOffset += size;
                ifdDataSize += size;
            }
        }
        
        // determine image offset after IFD data 
        long imageOffset = HEADER_SIZE + ifdSize + ifdDataSize;
        imageOffsetTag.value = (int) imageOffset;
        
        // compute offset to next IFD
        PixelType pixelType = determinePixelType(image);
        long imageSize = computeImageSize(image, pixelType);
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
        
        // open output stream
        this.out = new FileOutputStream(this.file);
        ByteOrder order = littleEndian ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
        writeHeader();
        
        // Write current image file directory
        
        // write number of entries
        writeShort(tags.size());
        
        // Write list of tags / entries
        for (TiffTag tag : tags)
        {
            tag.writeEntry(out, order);
        }
        
        // write offset to next IFD
        writeInt((int) nextIFD);
        // (end of IFD)

        // Write content of the different tags
        // (need to use the same order as the one used to define tag data offset
        for (TiffTag tag : tags)
        {
            tag.writeContent(out, order);
        }
                
        // Finally, image data (the whole array)
        writeImageData(image.getData());
        
        // Process optional remaining images
        // TODO: manage case of large TIFF a la ImageJ
        if (nextIFD > 0L)
        {
            // for remaining IFD, do not copy all information
            // -> adapt the size of the IFD
            // -> use same pointers within the tags
            int ifdSize2 = ifdSize;
//            if (metaDataSize > 0)
//            {
//                metaDataSize = 0;
//                nEntries -= 2;
//                ifdSize2 -= 2 * 12;
//            }
            
            // iterate over remaining image planes
            for (int i = 1; i < nImages; i++)
            {
                nextIFD += ifdSize2;
                if (i == nImages - 1)
                    nextIFD = 0;
                    
                imageOffset += imageSize;
                imageOffsetTag.value = (int) imageOffset;
                
                // write new IFD
                // write number of entries
                writeShort(tags.size());
                // Write list of tags / entries
                for (TiffTag tag : tags)
                {
                    tag.writeEntry(out, order);
                }
                writeInt((int) nextIFD);
            }
        } 
        else if (bigTiff)
        {
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

    private PixelType determinePixelType(Image image)
    {
        // sample size
        Array<?> array = image.getData();
        if (array instanceof UInt8Array)
        {
            return PixelType.GRAY8;
        }
        else if (array instanceof UInt16Array)
        {
            return PixelType.GRAY16_UNSIGNED;
        }
        else if (array instanceof Int16Array)
        {
            return PixelType.GRAY16_SIGNED;
        }
        else if (array instanceof RGB8Array)
        {
            return PixelType.RGB;
        }
        else
        {
            throw new RuntimeException("Unable to save tiff file for image data with class: " + array.getClass().getName());
        }
    }
    
    private long computeImageSize(Image image, PixelType pixelType)
    {
        // image size
        int sizeX = image.getSize(0);
        int sizeY = image.getSize(1);
        int bytesPerPixel = pixelType.getByteNumber();;
        long bytesPerPlane = (long) sizeX * sizeY * bytesPerPixel;
        long imageSize = bytesPerPlane <= 0xffffffffL ? (int) bytesPerPlane : 0;
        
        return imageSize;   
    }
    
    private final void writeShort(int v) throws IOException
    {
        if (littleEndian)
        {
            out.write(v & 255);
            out.write((v >>> 8) & 255);
        }
        else
        {
            out.write((v >>> 8) & 255);
            out.write(v & 255);
        }
    }

    private final void writeInt(int v) throws IOException
    {
        if (littleEndian)
        {
            out.write(v & 255);
            out.write((v >>> 8) & 255);
            out.write((v >>> 16) & 255);
            out.write((v >>> 24) & 255);
        }
        else
        {
            out.write((v >>> 24) & 255);
            out.write((v >>> 16) & 255);
            out.write((v >>> 8) & 255);
            out.write(v & 255);
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
