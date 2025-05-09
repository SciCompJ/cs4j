/**
 * 
 */
package net.sci.image.io.tiff;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Map;

import net.sci.image.io.BinaryDataReader;

/**
 * Read one or several instances of {@code ImageFileDirectory} from a Tiff File.
 * These file info can later be used to read image data from the same Tiff file.
 * 
 * 
 */
public class ImageFileDirectoryReader
{
    // =============================================================
    // Class variables
    
    /**
     * The name of the file to read the data from.
     * Initialized at construction.
     */
    String filePath;
    
    /**
     * The file stream from which data are extracted, and which manages data endianness.
     */
    BinaryDataReader dataReader;

    /**
     * The byte order used within the open stream.
     */
    ByteOrder byteOrder;
    
    
    // =============================================================
    // Constructor

    public ImageFileDirectoryReader(String fileName, ByteOrder byteOrder) throws IOException
    {
        this(new File(fileName), byteOrder);
    }

    public ImageFileDirectoryReader(File file, ByteOrder byteOrder) throws IOException
    {
        this.filePath = file.getPath();
        this.byteOrder = byteOrder;
    }
    
    
    // =============================================================
    // Methods
    
    /**
     * Reads the set of image file directories within this TIFF File.
     * 
     * @return the collection of ImageFileDirectory stored within this file.
     * @throws IOException
     *             if an error occurs
     */
    public ArrayList<ImageFileDirectory> readImageFileDirectories() throws IOException
    {
        // open the stream from file name
        RandomAccessFile inputStream = new RandomAccessFile(this.filePath, "r");
        // Create binary data reader from input stream
        this.dataReader = new BinaryDataReader(inputStream, this.byteOrder);
        
        // Read file offset of first Image
        dataReader.seek(4);
        long offset = ((long) dataReader.readInt()) & 0xffffffffL;
        // System.out.println("offset: " + offset);

        if (offset < 0L)
        {
            dataReader.close();
            throw new RuntimeException("Found negative offset in tiff file");
        }

        ArrayList<ImageFileDirectory> ifdList = new ArrayList<ImageFileDirectory>();
        while (offset > 0L)
        {
            dataReader.seek(offset);
            ImageFileDirectory ifd = readNextImageFileDirectory();
            ifdList.add(ifd);
            offset = ifd.offset;
        }
        dataReader.close();
        
        return ifdList;
    }
    
    /**
     * Reads the next Image File Directory structure from the input stream.
     * 
     * Reads the main IFD info: the entry number, the different entries, and the
     * offset to the next IFD.
     */
    private ImageFileDirectory readNextImageFileDirectory() throws IOException
    {
        // Read and control the number of entries
        int nEntries = dataReader.readShort();
        if (nEntries < 1 || nEntries > 1000)
        {
            throw new RuntimeException("Number of entries is out of range: " + nEntries);
        }

        // create a new ImageFileDirectory instance
        ImageFileDirectory ifd = new ImageFileDirectory();
        // store byte order within ImageFileDirectory
        ifd.setByteOrder(byteOrder);
        
        // retrieve the list of Tiff Tags that can be interpreted
        Map<Integer, TiffTag> tagMap = TiffTag.getAllTags();

        // Read each entry
        for (int i = 0; i < nEntries; i++)
        {
            // read tag code
            int tagCode = dataReader.readShort();
            
            // read type of tag data
            TiffTag.Type type = readTagType(tagCode);
            
            // reader number of data and value / offset
            int count = dataReader.readInt();
            int value = readTagValue(type, count);

            TiffTag tag = tagMap.get(tagCode);
            
            // if tag was not found, create a default empty tag
            boolean unknownTag = tag == null;
            if (unknownTag)
            {
                tag = new TiffTag(tagCode, "Unknown");
            }
            
            // init tag info
            tag.init(type, count, value);
            tag.readContent(dataReader);

            if (unknownTag)
            {
                System.out.println("Unknown tag with code " + tagCode + ". Type="
                        + type + ", count=" + count + ", value=" + tag.content);
            }

            ifd.addEntry(tag);
        }

        // read offset to next IFD
        ifd.offset = ((long) dataReader.readInt()) & 0xffffffffL;
        
        return ifd;
    }

    private TiffTag.Type readTagType(int tagCode) throws IOException
    {
        // read tag data info
        int typeValue = dataReader.readShort();
        TiffTag.Type type;
        try 
        {
            type = TiffTag.Type.getType(typeValue); 
        }
        catch(IllegalArgumentException ex)
        {
            throw new RuntimeException(String.format("Tag with code %d has incorrect type value: %d", tagCode, typeValue));
        }
        
        return type;
    }

    private int readTagValue(TiffTag.Type type, int count) throws IOException
    {
        int value;
        if (type == TiffTag.Type.SHORT && count == 1)
        {
            value = dataReader.readShort();
            dataReader.readShort();
        }
        else
        {
            value = dataReader.readInt();
        }
        return value;
    }
    

    // =============================================================
    // Methods
    
    /**
     * Closes the stream open by this class.
     * 
     * @throws IOException
     *             if an error occurs
     */
    public void close() throws IOException
    {
        this.dataReader.close();
    }
}
