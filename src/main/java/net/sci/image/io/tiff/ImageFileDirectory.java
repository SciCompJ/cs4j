/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import net.sci.image.io.PixelType;
import net.sci.image.io.tiff.ExtensionTags.SampleFormat;

/**
 * Data structure used to write image data into Tiff File. Intended to replace
 * the TiffFileInfo class in a future release.
 */
public class ImageFileDirectory
{
    // =============================================================
    // Constants
    
    /**
     * The number of bytes necessary to represent a Tiff Entry (tag) into a
     * file.
     */
    private static final int ENTRY_SIZE = 12;
    
    
    // =============================================================
    // Class variables
    
    /**
     * The byte order of the file containing this ImageFileDirectory.
     * Initialized to BIG_ENDIAN as default.
     */
    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
    
    /**
     * The list of Tiff entries within this image file directory. Use a
     * LinkedHashSet to allow fast indexing while keeping reading order of
     * entries.
     */
    LinkedHashSet<TiffTag> entries = new LinkedHashSet<TiffTag>();
    
    /**
     * The offset to the next IFD.
     */
    long offset;
    
    
    // =============================================================
    // Constructor
    
    /**
     * Creates a new ImageFileDirectory instance.
     */
    public ImageFileDirectory()
    {
    }
    
    
    // =============================================================
    // Accessors and mutators
    
    /**
     * 
     * @return
     */
    public ByteOrder getByteOrder()
    {
        return this.byteOrder;
    }
    
    public ImageFileDirectory setByteOrder(ByteOrder byteOrder)
    {
        this.byteOrder = byteOrder;
        return this;
    }
    
    public void setOffset(long offset)
    {
        this.offset = offset;
    }
    
    
    // =============================================================
    // Management of entries
    
    /**
     * Adds an entry / a tag to the list of entries.
     * 
     * @param tag
     *            the tag to add.
     */
    public void addEntry(TiffTag tag)
    {
        this.entries.add(tag);
    }
    
    public Collection<TiffTag> entries()
    {
        return Collections.unmodifiableCollection(this.entries);
    }
    
    /**
     * Returns the tag specified by the code, or null if the tag does not exist
     * within the directory.
     * 
     * @param tagCode
     *            the code of the tag
     * @return the tag specified by the code
     */
    public TiffTag getEntry(int tagCode)
    {
        for (TiffTag tag : entries)
        {
            if (tag.code == tagCode) 
            {
                return tag;
            }
        }
        return null;
    }
    
    /**
     * Returns the value if the {@code value} field, throwing an exception if
     * the tag does not exist in this directory.
     * 
     * @param tagCode
     *            the code of the tag
     * @throws RuntimeException
     *             if the tag does not exist within this directory
     * @return the integer value of the tag
     */
    public int getValue(int tagCode)
    {
        TiffTag entry = getEntry(tagCode);
        if (entry == null)
        {
            throw new RuntimeException("Requires Image File Directory to contain entry with tag " + tagCode);
        }
        return entry.value;
    }
    
    /**
     * Gets the value of the tag specified by its code as an integer value,
     * using the specified default value if the tag does not exist within this
     * directory.
     * 
     * @param tagCode
     *            the code of the tag
     * @param defaultValue
     *            the default value to use if the tag does not exist in this
     *            directory
     * @return the value of the tag
     */
    public int getIntValue(int tagCode, int defaultValue)
    {
        TiffTag tag = getEntry(tagCode);
        return tag != null ? tag.value : defaultValue;
    }
    
    /**
     * Gets the contents value of the tag specified by its code as a
     * double-pecision floating point value, using the specified default value
     * if the tag does not exist within this directory.
     * 
     * @param tagCode
     *            the code of the tag
     * @param defaultValue
     *            the default value to use if the tag does not exist in this
     *            directory
     * @return the floating point value of the tag stored in the content
     */
    public double getDoubleValue(int tagCode, double defaultValue)
    {
        TiffTag tag = getEntry(tagCode);
        return tag != null ? (double) tag.content : defaultValue;
    }
    
    /**
     * Returns either the value or the content of the tag specified by its code
     * as array of integers, using the specified default value if the tag does
     * not exist within this directory.
     * 
     * @param tagCode
     *            the code of the tag
     * @param defaultValue
     *            the default value to use if the tag does not exist in this
     *            directory
     * @return the value of the tag
     */
    public int[] getIntArrayValue(int tagCode, int[] defaultValue)
    {
        TiffTag tag = getEntry(tagCode);
        if (tag == null) return defaultValue;
        return tag.count == 1 ? new int[] {tag.value} : (int[]) tag.content;
    }
    
    /**
     * Returns the number of bytes necessary to write this directory.
     * The number of bytes is computed as:
     * <ul>
     * <li> 2 bytes for the number of entries/li>
     * <li> 12 bytes per entry/li>
     * <li> 4 bytes for the offset to the next Image File Directory/li>
     * </ul>
     * 
     * @return the number of bytes necessary to write this directory.
     */
    public int byteCount()
    {
        return 2 + entries.size() * ENTRY_SIZE + 4;
    }
    
    /**
     * Returns the number of bytes necessary to write the entry data stored
     * within this directory.
     * 
     * @return the size of entry data for this directory.
     */
    public int entryDataByteCount()
    {
        int size = 0;
        for (TiffTag tag : entries)
        {
            size += tag.contentSize();
        }
        return size;
    }
    
    /**
     * Tries to determine pixel type from the value of the tags
     * {@code SamplesPerPixel} and {@code BitsPerSample}, and potentially others.
     * 
     * @return an instance of PixelType compatible with tag values.
     */
    public PixelType determinePixelType()
    {
        // read data type info
        int samplesPerPixel = getValue(BaselineTags.SamplesPerPixel.CODE);
        int[] bitsPerSample = getIntArrayValue(BaselineTags.BitsPerSample.CODE, null);
        if (bitsPerSample.length != samplesPerPixel)
        {
            throw new RuntimeException("Requires content of the \"BitsPerSample\" tag to have number elements consistent with the \"SamplePerElement\" tag");
        }
        int sampleFormat = getIntValue(ExtensionTags.SampleFormat.CODE, 1);
        
        // case of scalar image data
        if (samplesPerPixel == 1)
        {
            return switch (bitsPerSample[0])
            {
                case 1 -> PixelType.BINARY;
                case 8 -> PixelType.UINT8;
                case 12 -> PixelType.UINT12;
                case 16 -> switch (sampleFormat)
                {
                    case SampleFormat.UNSIGNED_INTEGER -> PixelType.UINT16;
                    case SampleFormat.SIGNED_INTEGER -> PixelType.INT16;
                    default -> throw new RuntimeException(
                            "sample format is not managed: " + sampleFormat);
                };
                case 32 -> switch (sampleFormat)
                {
                    case SampleFormat.SIGNED_INTEGER -> PixelType.INT32;
                    case SampleFormat.FLOATING_POINT -> PixelType.FLOAT32;
                    default -> throw new RuntimeException(
                            "sample format is not managed: " + sampleFormat);
                };
                case 64 -> switch (sampleFormat)
                {
                    case SampleFormat.FLOATING_POINT -> PixelType.FLOAT64;
                    default -> throw new RuntimeException(
                            "64-bits image data can only be floating point");
                };
                default -> throw new RuntimeException(
                        "Number of bits per sample for scalar image data is not managed: "
                                + bitsPerSample);
            };
        }
        
        // check for color image data type
        if (samplesPerPixel == 3 && sampleFormat == SampleFormat.UNSIGNED_INTEGER)
        {
            if (bitsPerSample[0] == 8) return PixelType.RGB8;
            if (bitsPerSample[0] == 16) return PixelType.RGB16;
            throw new RuntimeException("In case of 3-sample integer data, bits per samples must be either 8 or 16");
        }
        
        // remaining types are vector data, and are implemented only for floating point data
        if (sampleFormat != SampleFormat.FLOATING_POINT)
        {
            throw new RuntimeException("Image data with several samples must be either color or floating point");
        }
        
        if (bitsPerSample[0] == 32) return new PixelType.Float32Vector(samplesPerPixel);
        if (bitsPerSample[0] == 64) return new PixelType.Float64Vector(samplesPerPixel);

        throw new RuntimeException("Unable to determine pixel type");
    }

    
    /**
     * Write this ImageFileDirectory into the specified stream: first writes the
     * number of entries, then the different tags/entries, and finally the
     * offset to the next ImageFileDirectory.
     * 
     * @param out
     *            the Stream to write in
     * @throws IOException
     *             if a problem occurs
     */
    public void write(OutputStream out) throws IOException
    {
        // write number of entries
        writeShort(out, this.byteOrder, entries.size());
        
        // Write list of tags / entries
        for (TiffTag tag : entries)
        {
            tag.writeEntry(out, this.byteOrder);
        }
        
        // write offset to next IFD
        writeInt(out, this.byteOrder, (int) offset);
    }
    
    public void writeEntryData(OutputStream out) throws IOException
    {
        for (TiffTag tag : entries)
        {
            tag.writeContent(out, this.byteOrder);
        }
    }
    
    private static final void writeShort(OutputStream out, ByteOrder order, int v) throws IOException
    {
        if (order == ByteOrder.LITTLE_ENDIAN)
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

    private static final void writeInt(OutputStream out, ByteOrder order, int v) throws IOException
    {
        if (order == ByteOrder.LITTLE_ENDIAN)
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
}
