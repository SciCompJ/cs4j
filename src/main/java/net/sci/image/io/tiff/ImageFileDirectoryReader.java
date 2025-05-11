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
 * Read all the instances of {@code ImageFileDirectory} from a Tiff File. These
 * file info can later be used to read image data from the same Tiff file.
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
     * The file stream from which data are extracted, and which manages data byte-order.
     */
    BinaryDataReader dataReader;

    
    // =============================================================
    // Constructor

    public ImageFileDirectoryReader(String fileName) throws IOException
    {
        this(new File(fileName));
    }

    public ImageFileDirectoryReader(File file) throws IOException
    {
        this.filePath = file.getPath();
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
        try (RandomAccessFile raf = new RandomAccessFile(this.filePath, "r"))
        {
            // Create binary data reader from input stream
            this.dataReader = new BinaryDataReader(raf, determineByteOrder(raf));

            // check the magic number indicating tiff format
            int magicNumber = dataReader.readShort();
            if (magicNumber != 42)
            { throw new RuntimeException("Invalid TIFF file: magic number is different from 42"); }

            // Read file offset of first IFD
            long offset = ((long) dataReader.readInt()) & 0xffffffffL;
            // System.out.println("offset: " + offset);

            if (offset < 0L)
            { throw new IOException("Found negative offset in tiff file"); }

            ArrayList<ImageFileDirectory> ifdList = new ArrayList<ImageFileDirectory>();
            do
            {
                ImageFileDirectory ifd = readImageFileDirectory(dataReader, offset);
                ifdList.add(ifd);
                offset = ifd.offset;
            } while (offset > 0L);

            dataReader.close();

            return ifdList;
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Impossible to read Image File directory", ex);
        }
    }
    
    /**
     * Reads the the first two bytes of the input stream to determine its byte order.
     * 
     * @throws IOException
     *             if a reading problem occurred
     * @throws RuntimeException
     *             if the byte order could not be decoded
     */
    private ByteOrder determineByteOrder(RandomAccessFile raf) throws IOException
    {
        // read the two bytes indicating endianness
        int b1 = raf.read();
        int b2 = raf.read();
        int byteOrderInfo = ((b2 << 8) + b1);
    
        // associate the two bytes to a byte order
        // If a problem occur, this may be the sign of an file in another format
        return switch (byteOrderInfo)
        {
            case 0x4949 -> ByteOrder.LITTLE_ENDIAN;
            case 0x4d4d -> ByteOrder.BIG_ENDIAN;
            default -> {
                raf.close();
                throw new RuntimeException("Could not decode endianness of TIFF File: " + filePath);
            }
        };
    }
    
    /**
     * Reads the next Image File Directory structure from the input stream.
     * 
     * Reads the main IFD info: the entry number, the different entries, and the
     * offset to the next IFD.
     */
    private ImageFileDirectory readImageFileDirectory(BinaryDataReader dataReader, long offset) throws IOException
    {
    	dataReader.seek(offset);
    	
        // Read and control the number of entries
        int nEntries = dataReader.readShort();
        if (nEntries < 1 || nEntries > 1000)
        {
            throw new RuntimeException("Number of entries is out of range: " + nEntries);
        }

        // create a new ImageFileDirectory instance
        ImageFileDirectory ifd = new ImageFileDirectory();
        // store byte order within ImageFileDirectory
        ifd.setByteOrder(dataReader.getOrder());
        
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
        try 
        {
        	 return TiffTag.Type.getType(typeValue); 
        }
        catch(IllegalArgumentException ex)
        {
            throw new RuntimeException(String.format("Tag with code %d has unknown type value: %d", tagCode, typeValue));
        }
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
