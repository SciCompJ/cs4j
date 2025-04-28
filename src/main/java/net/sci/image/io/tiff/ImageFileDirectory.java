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

/**
 * Data structure used to write image data into Tiff File. Intended to replace
 * the TiffFileInfo class in a future release.
 */
public class ImageFileDirectory
{
    /**
     * The number of bytes necessary to represent a Tiff Entry (tag) into a
     * file.
     */
    private static final int ENTRY_SIZE = 12;
    
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
    
    public ImageFileDirectory()
    {
    }
    
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
    
    public void setOffset(long offset)
    {
        this.offset = offset;
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
     * Write this ImageFileDirectory into the specified stream: first writes the
     * number of entries, then the different tags/entries, and finally the
     * offset to the next ImageFileDirectory.
     * 
     * @param out
     *            the Stream to write in
     * @param order
     *            the ByteOrder of the stream
     * @throws IOException
     *             if a problem occurs
     */
    public void write(OutputStream out, ByteOrder order) throws IOException
    {
        // write number of entries
        writeShort(out, order, entries.size());
        
        // Write list of tags / entries
        for (TiffTag tag : entries)
        {
            tag.writeEntry(out, order);
        }
        
        // write offset to next IFD
        writeInt(out, order, (int) offset);
    }
    
    public void writeEntryData(OutputStream out, ByteOrder order) throws IOException
    {
        for (TiffTag tag : entries)
        {
            tag.writeContent(out, order);
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
