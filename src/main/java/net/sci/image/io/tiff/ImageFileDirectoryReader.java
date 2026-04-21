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
import net.sci.image.io.tiff.TiffTag.Type;

/**
 * Read all the instances of {@code ImageFileDirectory} from a Tiff File. These
 * file info can later be used to read image data from the same Tiff file.
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
            int tagCode = dataReader.readShort() & 0x00FFFF;
            
            // read type of tag data
            int typeValue = dataReader.readShort() & 0x00FFFF;
            TiffTag.Type type = TiffTag.Type.getType(typeValue); 
            if (type == Type.UNKNOWN)
            {
                System.out.println(String.format("Tag with code %d has an unknown type value: %d, it will likely be ignored", tagCode, typeValue));
            }
            
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
            readContent(tag);

            if (unknownTag)
            {
                System.out.println("Unknown tag with code " + tagCode + ". Type="
                        + type + ", count=" + count + ", content=" + tag.contentSummary());
            }

            ifd.addEntry(tag);
        }

        // read offset to next IFD
        ifd.offset = ((long) dataReader.readInt()) & 0xffffffffL;
        
        return ifd;
    }

    private int readTagValue(TiffTag.Type type, int count) throws IOException
    {
        int value;
        if (type == TiffTag.Type.SHORT && count == 1)
        {
            value = dataReader.readShort() & 0x00FFFF;
            dataReader.readShort();
        }
        else
        {
            value = dataReader.readInt();
        }
        return value;
    }
    
    /**
     * Initialize the content of the tag from the data reader, given its code
     * and the specified value.
     * 
     * @param dataReader
     *            the instance of DataReader to read optional information from
     * @throws IOException
     *             if tried to read from the file and problem occurred
     */
    private void readContent(TiffTag tag) throws IOException
    {
        tag.content = switch (tag.type)
        {
            case BYTE -> tag.count == 1 ? Integer.valueOf(tag.value) : readByteArray(tag);
            case SHORT -> tag.count == 1 ? Integer.valueOf(tag.value) : readShortArray(tag);
            case LONG -> tag.count == 1 ? Integer.valueOf(tag.value) : readIntArray(tag);
            case ASCII -> readAscii(tag); // Automatically convert byte array to String
            case RATIONAL -> readRational(tag); // Assume only one rational is specified
            case DOUBLE -> readDoubleArray(tag);
            default -> null;
        };
    }
    
    private byte[] readByteArray(TiffTag tag) throws IOException
    {
        // allocate memory for result
        byte[] res = new byte[tag.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // convert tag value to long offset
        long offset = ((long) tag.value) & 0xffffffffL;
        dataReader.seek(offset);
        
        // fill up array
        int nRead = dataReader.readByteArray(res);
        if (nRead != tag.count)
        {
            throw new RuntimeException("Could not read all the required bytes");
        }
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    private String readAscii(TiffTag tag) throws IOException
    {
        // Allocate memory for string buffer
        byte[] data = new byte[tag.count];
        
        // read string buffer
        if (tag.count <= 4)
        {
            // unpack integer
            int value = tag.value;
            for (int i = 0; i < tag.count; i++)
            {
                data[i] = (byte) (value & 0x00FF);
                value = value >> 8;
            }
        }
        else
        {
            // convert state to long offset for reading large buffer
            long offset = ((long) tag.value) & 0xffffffffL;
            
            long pos0 = dataReader.getFilePointer();
            dataReader.seek(offset);
            dataReader.readByteArray(data);
            dataReader.seek(pos0);
        }
        
        return new String(data);
    }
    
    private int[] readShortArray(TiffTag tag) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) tag.value) & 0xffffffffL;
        
        // allocate memory for result
        int[] res = new int[tag.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // fill up array
        dataReader.seek(offset);
        for (int c = 0; c < tag.count; c++)
        {
            res[c] = dataReader.readShort();
        }
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    private int[] readIntArray(TiffTag tag) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) tag.value) & 0xffffffffL;
        
        // allocate memory for result
        int[] res = new int[tag.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // fill up array
        dataReader.seek(offset);
        dataReader.readIntArray(res, 0, tag.count);
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
    }
    
    /**
     * Reads the rational value at the given position, as the ratio of two
     * integers.
     * 
     * @param dataReader
     *            the instance of BinaryDataReader to read from
     * @return the approximated rational content at the specified position, as a
     *         double
     * @throws IOException
     *             if an I/O Exception occurs
     */
    private double readRational(TiffTag tag) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) tag.value) & 0xffffffffL;
        
        long saveLoc = dataReader.getFilePointer();
        dataReader.seek(offset);
        
        int numerator = dataReader.readInt();
        int denominator = dataReader.readInt();
        dataReader.seek(saveLoc);
        
        if (denominator != 0)
            return (double) numerator / denominator;
        else
            return 0.0;
    }
    
    private double[] readDoubleArray(TiffTag tag) throws IOException
    {
        // convert tag value to long offset for reading large buffer
        long offset = ((long) tag.value) & 0xffffffffL;
        
        // allocate memory for result
        double[] res = new double[tag.count];
        
        // save pointer location
        long saveLoc = dataReader.getFilePointer();
        
        // fill up array
        dataReader.seek(offset);
        dataReader.readDoubleArray(res, 0, tag.count);
        
        // restore pointer and return result
        dataReader.seek(saveLoc);
        return res;
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
